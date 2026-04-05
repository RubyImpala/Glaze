package com.rubyimpala.features.auction.events.renderers;

import com.rubyimpala.config.GlazeSettings;
import com.rubyimpala.features.auction.AuctionService;
import com.rubyimpala.util.DisplayUtils;
import com.rubyimpala.util.HintComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ItemTooltipRenderer {

    public static void render(ItemStack stack, List<Component> lines) {
        // Checks if it's enabled in the settings
        if (!GlazeSettings.showPriceTooltips) return;

        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        int maxStackSize = stack.getMaxStackSize();
        boolean isStackable = maxStackSize > 1;
        boolean shiftDown = isShiftDown();

        if (AuctionService.isLoading(itemId)) {
            renderLoading(lines);
        } else if (AuctionService.hasNoListings(itemId)) {
            renderNoListings(lines);
        } else {
            AuctionService.getLowestPrice(itemId).ifPresent(unitPrice -> {
                if (shiftDown && isStackable) {
                    renderStackPrice(lines, unitPrice, maxStackSize);
                } else {
                    renderUnitPrice(lines, unitPrice, isStackable);
                }
            });
        }
    }

    private static void renderLoading(List<Component> lines) {
        HintComponents.addLoadingTooltip(lines);
    }

    private static void renderNoListings(List<Component> lines) {
        HintComponents.addNoListingsTooltip(lines);
    }

    private static void renderUnitPrice(List<Component> lines, long unitPrice, boolean isStackable) {
        HintComponents.addPriceTooltip(lines, unitPrice);
        if (isStackable) {
            HintComponents.addStackPriceHint(lines);
        }
        HintComponents.addReloadHint(lines);
    }

    private static void renderStackPrice(List<Component> lines, long unitPrice, int maxStackSize) {
        long stackPrice = unitPrice * maxStackSize;
        HintComponents.addStackPriceTooltip(lines, stackPrice, maxStackSize);
        HintComponents.addUnshiftHint(lines);
        HintComponents.addReloadHint(lines);
    }

    private static boolean isShiftDown() {
        long window = Minecraft.getInstance().getWindow().handle();
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
    }
}