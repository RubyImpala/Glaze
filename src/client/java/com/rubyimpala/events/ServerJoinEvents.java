package com.rubyimpala.events;

import com.rubyimpala.config.GlazeSettings;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ServerJoinEvents {

    public static void register() {
        ClientPlayConnectionEvents.JOIN.register((_, _, client) -> client.execute(() -> {
            if (client.player != null) {
                if (GlazeSettings.CONFIG().apiToken.isEmpty()) {
                    client.player.sendSystemMessage(Component.literal("§6[Glaze] §eNo API Key found!")
                            .withStyle(ChatFormatting.YELLOW));
                    client.player.sendSystemMessage(Component.literal("§7Type §f/api §7on DonutSMP (If running it didn't do anything run this §f/glaze api set <key>")
                            .withStyle(ChatFormatting.GRAY));
                } else {
                    // If they have a key, just a small "active" message
                    client.player.sendSystemMessage(Component.literal("§6[Glaze] §aPrice fetching active."));
                }
            }
        }));
    }
}
