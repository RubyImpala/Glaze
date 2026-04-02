package com.rubyimpala.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.rubyimpala.data.DonutPriceManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class GlazeCommands {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            // Build the main '/glaze' trunk
            var glazeRoot = literal("glaze");

            // Attach the branches from other classes
            glazeRoot.then(ApiCommands.buildApiBranch());

            // Register the whole tree at once
            dispatcher.register(glazeRoot);
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            client.execute(() -> {
                if (client.player != null) {
                    if (DonutPriceManager.getAuthToken().isEmpty()) {
                        client.player.sendSystemMessage(Component.literal("§6[Glaze] §eNo API Key found!")
                                .withStyle(ChatFormatting.YELLOW));
                        client.player.sendSystemMessage(Component.literal("§7Type §f/api key §7on DonutSMP, then run §f/glaze api {key}")
                                .withStyle(ChatFormatting.GRAY));
                    } else {
                        // If they have a key, just a small "active" message
                        client.player.sendSystemMessage(Component.literal("§6[Glaze] §aPrice fetching active."));
                    }
                }
            });
        });
    }
}