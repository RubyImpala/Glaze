package com.rubyimpala.events;

import com.rubyimpala.config.GlazeSettings;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import static com.rubyimpala.GlazeClient.LOGGER;

public class ChatEvents {

    private static final String TOKEN_PREFIX = "Your API Token is:";

    public static void register() {
        // This automatically detects when the server sends an API token and saves it to the config
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            // overlay = true means it's an actionbar message, we don't want those
            if (overlay) return;

            String raw = message.getString();

            // Checks if token detection is enabled in the config
            if (!GlazeSettings.CONFIG().autoTokenDetection) return;

            if (raw.startsWith(TOKEN_PREFIX)) {
                GlazeSettings.CONFIG().apiToken = raw.substring(TOKEN_PREFIX.length()).trim();
                LOGGER.info("[Glaze] API token automatically saved from server message.");


                Minecraft.getInstance().gui.getChat().addServerSystemMessage(
                        Component.literal("§6[Glaze] §aAPI token automatically saved!")
                );
            }
        });
    }
}