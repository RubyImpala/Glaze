package com.rubyimpala.features.vouch.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.rubyimpala.features.vouch.VouchService;
import com.rubyimpala.features.vouch.models.PlayerVouches;
import com.rubyimpala.features.vouch.models.VouchRecord;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class VouchCommands {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildVouchBranch() {
        return literal("vouch")
                .then(buildAddNode())
                .then(buildRemoveNode())
                .then(buildViewNode());
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> buildAddNode() {
        return literal("add")
                .then(argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> suggestOnlinePlayers(context, builder))
                        .executes(VouchCommands::addVouch));
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> buildRemoveNode() {
        return literal("remove")
                .then(argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> suggestOnlinePlayers(context, builder))
                        .executes(VouchCommands::removeVouch));
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> buildViewNode() {
        return literal("view")
                .executes(VouchCommands::viewGivenVouches)
                .then(argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> suggestOnlinePlayers(context, builder))
                        .executes(VouchCommands::viewVouches));
    }

    private static int viewGivenVouches(CommandContext<FabricClientCommandSource> context) {
        String myName = Minecraft.getInstance().getUser().getName();

        context.getSource().sendFeedback(
                Component.literal("§6[Glaze] §7Fetching vouches you have given..."));

        CompletableFuture.runAsync(() -> {
            PlayerVouches vouches = VouchService.getGivenVouches();
            List<VouchRecord> records = vouches.getVouches();

            Minecraft.getInstance().execute(() -> {
                if (records.isEmpty()) {
                    context.getSource().sendFeedback(
                            Component.literal("§6[Glaze] §aYou have not vouched for anyone yet."));
                    return;
                }

                context.getSource().sendFeedback(
                        Component.literal("§6[Glaze] §aYou have vouched for §e" +
                                records.size() + " §aplayer(s):"));

                for (VouchRecord record : records) {
                    String date = DATE_FORMAT.format(Instant.ofEpochMilli(record.timestamp()));
                    context.getSource().sendFeedback(
                            Component.literal("§7  • §e" + record.voucher() + " §7(" + date + ")"));
                }
            });
        });

        return 1;
    }

    // Suggests all online player names for tab completion
    private static java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggestOnlinePlayers(
            CommandContext<FabricClientCommandSource> context,
            com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {

        var connection = net.minecraft.client.Minecraft.getInstance().getConnection();
        if (connection != null) {
            connection.getOnlinePlayers().stream()
                    .map(p -> p.getProfile().name())
                    .filter(name -> name.toLowerCase().startsWith(builder.getRemaining().toLowerCase()))
                    .forEach(builder::suggest);
        }
        return builder.buildFuture();
    }

    private static int addVouch(CommandContext<FabricClientCommandSource> context) {
        String typed = StringArgumentType.getString(context, "player");
        String target = VouchService.resolveExactName(typed);
        String voucher = context.getSource().getPlayer().getName().getString();

        // Use typed name in not-online message since we couldn't resolve it
        if (target == null) {
            context.getSource().sendFeedback(
                    Component.literal("§6[Glaze] §c" + typed + " is not online right now."));
            return 1;
        }

        VouchService.VouchResult result = VouchService.addVouch(target, voucher);

        switch (result) {
            case SUCCESS ->
                    context.getSource().sendFeedback(
                            Component.literal("§6[Glaze] §7vouching §e" + target + "§7..."));

            case ON_COOLDOWN -> {
                long seconds = VouchService.getCooldownSecondsRemaining(target, voucher);
                context.getSource().sendFeedback(
                        Component.literal("§6[Glaze] §cYou can vouch for §e" + target +
                                "§c again in §e" + seconds + "s§c."));
            }

            case SELF_VOUCH ->
                    context.getSource().sendFeedback(
                            Component.literal("§6[Glaze] §cYou cannot vouch for yourself."));
        }
        return 1;
    }

    private static int removeVouch(CommandContext<FabricClientCommandSource> context) {
        String typed = StringArgumentType.getString(context, "player");
        String target = VouchService.resolveExactName(typed) != null
                ? VouchService.resolveExactName(typed)
                : typed;
        String voucher = context.getSource().getPlayer().getName().getString();

        boolean removed = VouchService.removeVouch(target, voucher);

        if (removed) {
            context.getSource().sendFeedback(
                    Component.literal("§6[Glaze] §aYour vouch for §e" + target + "§a has been removed."));
        } else {
            context.getSource().sendFeedback(
                    Component.literal("§6[Glaze] §cYou have not vouched for §e" + target + "§c."));
        }
        return 1;
    }


    private static int viewVouches(CommandContext<FabricClientCommandSource> context) {
        String typed = StringArgumentType.getString(context, "player");
        String target = VouchService.resolveExactName(typed) != null
                ? VouchService.resolveExactName(typed)
                : typed;

        context.getSource().sendFeedback(
                Component.literal("§6[Glaze] §7Fetching vouches for §e" + target + "§7..."));

        CompletableFuture.runAsync(() -> {
            PlayerVouches vouches = VouchService.getVouches(target);
            List<VouchRecord> records = vouches.getVouches();

            Minecraft.getInstance().execute(() -> {
                if (records.isEmpty()) {
                    context.getSource().sendFeedback(
                            Component.literal("§6[Glaze] §e" + target + " §ahas no vouches yet."));
                    return;
                }

                context.getSource().sendFeedback(
                        Component.literal("§6[Glaze] §e" + target +
                                " §ahas §e" + records.size() + " §avouch(es):"));

                for (VouchRecord record : records) {
                    String date = DATE_FORMAT.format(Instant.ofEpochMilli(record.timestamp()));
                    context.getSource().sendFeedback(
                            Component.literal("§7  • §e" + record.voucher() + " §7(" + date + ")"));
                }
            });
        });

        return 1;
    }
}