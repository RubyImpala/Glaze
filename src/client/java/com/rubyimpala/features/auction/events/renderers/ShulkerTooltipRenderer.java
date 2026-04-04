package com.rubyimpala.features.auction.events.renderers;

import com.rubyimpala.features.auction.AuctionService;
import com.rubyimpala.features.auction.models.ItemValueEntry;
import com.rubyimpala.features.auction.models.ShulkerValueResult;
import com.rubyimpala.util.DisplayUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ShulkerTooltipRenderer {

    public static void render(ItemStack stack, List<Component> lines) {
        ShulkerValueResult result = AuctionService.getShulkerBreakdown(stack);
        if (result == null) return;

        if (isShiftDown()) {
            renderBreakdown(result, lines);
        } else {
            renderSummary(result, lines);
        }
    }

    private static void renderSummary(ShulkerValueResult result, List<Component> lines) {
        lines.add(Component.literal("Price: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(DisplayUtils.formatPrice(result.totalPrice()))
                        .withStyle(ChatFormatting.GREEN))
                .append(loadingSuffix(result.hasLoading())));

        lines.add(Component.literal("[Shift for breakdown]")
                .withStyle(ChatFormatting.DARK_GRAY));
        lines.add(Component.literal("[R to reload]")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    private static void renderBreakdown(ShulkerValueResult result, List<Component> lines) {
        lines.add(Component.literal("━━ Price Breakdown ━━").withStyle(ChatFormatting.GOLD));

        for (ItemValueEntry entry : result.entries()) {
            lines.add(renderEntry(entry));
        }

        lines.add(Component.literal("  Total: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(DisplayUtils.formatPrice(result.totalPrice()))
                        .withStyle(ChatFormatting.GREEN))
                .append(loadingSuffix(result.hasLoading())));
    }

    private static Component renderEntry(ItemValueEntry entry) {
        String prefix = "  " + entry.count() + "x " + entry.displayName() + ": ";

        if (entry.loading()) {
            return Component.literal(prefix).withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("Loading...").withStyle(ChatFormatting.DARK_GRAY));
        } else if (entry.unpriced()) {
            return Component.literal(prefix).withStyle(ChatFormatting.GRAY)
                    .append(Component.literal("Unpriced").withStyle(ChatFormatting.RED));
        } else {
            return Component.literal(prefix).withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(DisplayUtils.formatPrice(entry.stackTotal()))
                            .withStyle(ChatFormatting.GREEN));
        }
    }

    private static Component loadingSuffix(boolean hasLoading) {
        return hasLoading
                ? Component.literal(" (+ loading...)").withStyle(ChatFormatting.DARK_GRAY)
                : Component.empty();
    }

    private static boolean isShiftDown() {
        long window = Minecraft.getInstance().getWindow().handle();
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
    }
}