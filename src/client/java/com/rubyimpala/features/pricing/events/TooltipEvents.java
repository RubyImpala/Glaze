package com.rubyimpala.features.pricing.events;

import com.rubyimpala.features.pricing.AuctionCache;
import com.rubyimpala.features.pricing.AuctionService;
import com.rubyimpala.features.pricing.events.renderers.ItemTooltipRenderer;
import com.rubyimpala.features.pricing.events.renderers.ShulkerTooltipRenderer;
import com.rubyimpala.mixin.client.AbstractContainerScreenAccessor;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.lwjgl.glfw.GLFW;

public class TooltipEvents {

    private static boolean rWasPressed = false;

    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, tooltipType, lines) -> {
            if (stack.isEmpty()) return;
            if (!isActuallyHovered(stack)) return;

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

    private static boolean isActuallyHovered(ItemStack stack) {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            Slot hovered = ((AbstractContainerScreenAccessor) containerScreen).getHoveredSlot();
            return hovered != null && ItemStack.isSameItem(hovered.getItem(), stack);
        }
        return false;
    }
}