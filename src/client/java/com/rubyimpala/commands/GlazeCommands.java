package com.rubyimpala.commands;

import com.rubyimpala.config.GlazeConfig;
import com.rubyimpala.config.commands.ConfigCommands;
import com.rubyimpala.features.pricing.commands.ApiCommands;
import com.rubyimpala.features.vouch.commands.VouchCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class GlazeCommands {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            var glazeRoot = literal("glaze");

            // Attach the branches from other classes
            glazeRoot.then(ApiCommands.buildApiBranch())
                    .then(VouchCommands.buildVouchBranch())
                    .then(ConfigCommands.buildConfigNode());

            // Register the whole tree at once
            dispatcher.register(glazeRoot);
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            client.execute(() -> {
                if (client.player != null) {
                    if (GlazeConfig.Auth.getToken().isEmpty()) {
                        client.player.sendSystemMessage(Component.literal("§6[Glaze] §eNo API Key found!")
                                .withStyle(ChatFormatting.YELLOW));
                        client.player.sendSystemMessage(Component.literal("§7Type §f/api key §7on DonutSMP (If running it didn't do anything run this §f/glaze api set <key>")
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