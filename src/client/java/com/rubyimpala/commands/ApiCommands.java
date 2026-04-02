package com.rubyimpala.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.rubyimpala.data.DonutPriceManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
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
        DonutPriceManager.updateAuthToken(key);
        context.getSource().sendFeedback(Component.literal("§6[Glaze] §aKey saved!"));
        return 1;
    }

    private static int viewKey(CommandContext<FabricClientCommandSource> context){
        context.getSource().sendFeedback(Component.literal("§c§l⚠ SECURITY: §cDo not share this key with anyone!"));

        String key = DonutPriceManager.getAuthToken();

        MutableComponent message = Component.literal("§6[Glaze] §aCurrent key (Click to copy): ");

        MutableComponent keyComponent = Component.literal("§9" + key)
                .withStyle(style -> style
                        .withClickEvent(new ClickEvent.CopyToClipboard(key))
                        .withHoverEvent(new HoverEvent.ShowText(Component.literal("§eClick to copy to clipboard")))
                );

        // Join them and send
        context.getSource().sendFeedback(message.append(keyComponent));        return 1;
    }

    private static int deleteKey(CommandContext<FabricClientCommandSource> context) {
        DonutPriceManager.updateAuthToken("");
        context.getSource().sendFeedback(Component.literal("§6[Glaze] §cKey deleted!"));
        return 1;
    }
}