package com.rubyimpala.features.auction.events.renderers;

import com.rubyimpala.features.auction.AuctionService;
import com.rubyimpala.util.DisplayUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ItemTooltipRenderer {

    public static void render(ItemStack stack, List<Component> lines) {
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
        lines.add(Component.literal("Price: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal("Loading...").withStyle(ChatFormatting.GRAY)));
    }

    private static void renderNoListings(List<Component> lines) {
        lines.add(Component.literal("Price: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal("No listings").withStyle(ChatFormatting.GRAY)));
        lines.add(Component.literal("[R to reload]")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    private static void renderUnitPrice(List<Component> lines, long unitPrice, boolean isStackable) {
        lines.add(Component.literal("Price: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(DisplayUtils.formatPrice(unitPrice))
                        .withStyle(ChatFormatting.GREEN)));
        if (isStackable) {
            lines.add(Component.literal("[Shift for stack price]")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
        lines.add(Component.literal("[R to reload]")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    private static void renderStackPrice(List<Component> lines, long unitPrice, int maxStackSize) {
        long stackPrice = unitPrice * maxStackSize;
        lines.add(Component.literal("Price (x" + maxStackSize + "): ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(DisplayUtils.formatPrice(stackPrice))
                        .withStyle(ChatFormatting.GREEN)));
        lines.add(Component.literal("[Unshift for unit price]")
                .withStyle(ChatFormatting.DARK_GRAY));
        lines.add(Component.literal("[R to reload]")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    private static boolean isShiftDown() {
        long window = Minecraft.getInstance().getWindow().handle();
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
    }
}