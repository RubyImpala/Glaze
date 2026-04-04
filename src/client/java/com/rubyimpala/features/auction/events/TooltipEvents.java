package com.rubyimpala.features.auction.events;

import com.rubyimpala.features.auction.AuctionCache;
import com.rubyimpala.features.auction.AuctionHoverState;
import com.rubyimpala.features.auction.AuctionService;
import com.rubyimpala.features.auction.events.renderers.ItemTooltipRenderer;
import com.rubyimpala.features.auction.events.renderers.ShulkerTooltipRenderer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.lwjgl.glfw.GLFW;

public class TooltipEvents {

    private static boolean rWasPressed = false;

    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, tooltipType, lines) -> {
            if (stack.isEmpty()) return;

            String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();

            // R key reload — only fires while actively hovering this item
            long window = Minecraft.getInstance().getWindow().handle();
            boolean rPressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_R) == GLFW.GLFW_PRESS;
            if (rPressed && !rWasPressed) {
                AuctionCache.invalidate(itemId);
                AuctionService.getLowestPrice(itemId);
            }
            rWasPressed = rPressed;

            boolean isShulker = stack.getItem() instanceof BlockItem bi
                    && bi.getBlock() instanceof ShulkerBoxBlock;

            if (isShulker) {
                ShulkerTooltipRenderer.render(stack, lines);
            } else {
                ItemTooltipRenderer.render(stack, lines);
            }
        });
    }
}