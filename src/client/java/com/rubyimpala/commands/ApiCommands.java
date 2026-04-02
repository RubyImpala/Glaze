package com.rubyimpala.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.rubyimpala.data.DonutPriceManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class ApiCommands {

    public static LiteralArgumentBuilder<FabricClientCommandSource> buildApiBranch() {
        return literal("api")
                .then(buildDeleteNode())
                .then(buildKeyNode());
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> buildDeleteNode() {
        return literal("delete")
                .executes(ApiCommands::deleteKey);
    }

    private static RequiredArgumentBuilder<FabricClientCommandSource, String> buildKeyNode() {
        return argument("key", StringArgumentType.string())
                .executes(ApiCommands::setKey);
    }

    private static int setKey(CommandContext<FabricClientCommandSource> context) {
        String key = StringArgumentType.getString(context, "key");
        DonutPriceManager.updateAuthToken(key);
        context.getSource().sendFeedback(Component.literal("§6[Glaze] §aKey saved!"));
        return 1;
    }

    private static int deleteKey(CommandContext<FabricClientCommandSource> context) {
        DonutPriceManager.updateAuthToken("");
        context.getSource().sendFeedback(Component.literal("§6[Glaze] §cKey deleted!"));
        return 1;
    }
}