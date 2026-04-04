package com.rubyimpala.features.auction.events;

import com.rubyimpala.features.auction.AuctionCache;
import com.rubyimpala.features.auction.AuctionHoverState;
import com.rubyimpala.features.auction.AuctionService;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class InputEvents {

    private static boolean rWasPressed = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.screen != null) return;
            if (AuctionHoverState.lastHoveredItemId == null) return;

            long window = Minecraft.getInstance().getWindow().handle();
            boolean rPressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_R) == GLFW.GLFW_PRESS;

            if (rPressed && !rWasPressed) {
                AuctionCache.invalidate(AuctionHoverState.lastHoveredItemId);
                AuctionService.getLowestPrice(AuctionHoverState.lastHoveredItemId);
            }
            rWasPressed = rPressed;
        });
    }
}