package com.rubyimpala.features.auction.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.rubyimpala.config.GlazeConfig;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class ApiCommands {

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildApiBranch() {
        return literal("api")
                .then(buildDeleteNode())
                .then(buildSetNode())
                .then(buildViewNode());
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> buildDeleteNode() {
        return literal("delete")
                .executes(ApiCommands::deleteKey);
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> buildViewNode(){
       return literal("view")
               .executes(ApiCommands::viewKey);
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> buildSetNode() {
        return literal("set")
                .then(argument("key", StringArgumentType.string())
                        .executes(ApiCommands::setKey));
    }

    private static int setKey(CommandContext<FabricClientCommandSource> context) {
        String key = StringArgumentType.getString(context, "key");
        GlazeConfig.Auth.updateToken(key);
        context.getSource().sendFeedback(Component.literal("§6[Glaze] §aKey saved!"));
        return 1;
    }

    private static int viewKey(CommandContext<FabricClientCommandSource> context){
        String rawKey = GlazeConfig.Auth.getToken();

        boolean hasKey = (rawKey != null && !rawKey.isEmpty());

        if (hasKey) {
            context.getSource().sendFeedback(Component.literal("§c§l⚠ SECURITY: §cDo not share this key with anyone!"));
        }

        String displayText = hasKey ? rawKey : "No key exists";
        ChatFormatting color = hasKey ? ChatFormatting.BLUE : ChatFormatting.RED;
        MutableComponent message = Component.literal("§6[Glaze] §aCurrent key (Click to copy): ");

        MutableComponent keyComponent = Component.literal(displayText)
                .withStyle(style -> {
                    style = style.withColor(color);
                    // Only add click/hover events if the key actually exists!
                    if (hasKey) {
                        style = style.withClickEvent(new ClickEvent.CopyToClipboard(rawKey))
                                .withHoverEvent(new HoverEvent.ShowText(Component.literal("§eClick to copy")));
                    }
                    return style;
                });

        context.getSource().sendFeedback(message.append(keyComponent));        return 1;
    }

    private static int deleteKey(CommandContext<FabricClientCommandSource> context) {
        GlazeConfig.Auth.updateToken("");
        context.getSource().sendFeedback(Component.literal("§6[Glaze] §cKey deleted!"));
        return 1;
    }
}