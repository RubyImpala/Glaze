package com.rubyimpala.features.auction.events.renderers;

import com.rubyimpala.config.GlazeSettings;
import com.rubyimpala.features.auction.AuctionService;
import com.rubyimpala.features.auction.models.ItemValueEntry;
import com.rubyimpala.features.auction.models.ShulkerValueResult;
import com.rubyimpala.util.DisplayUtils;
import com.rubyimpala.util.HintComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ShulkerTooltipRenderer {

    public static void render(ItemStack stack, List<Component> lines) {
        // Checks if it's enabled in the settings
        if (!GlazeSettings.showShulkerValuation) return;

        ShulkerValueResult result = AuctionService.getShulkerBreakdown(stack);
        if (result == null) return;

        if (isShiftDown()) {
            renderBreakdown(result, lines);
        } else {
            renderSummary(result, lines);
        }
    }

    private static void renderSummary(ShulkerValueResult result, List<Component> lines) {
        HintComponents.addShulkerSummary(lines, result.totalPrice(), result.hasLoading());
    }

    private static void renderBreakdown(ShulkerValueResult result, List<Component> lines) {
        HintComponents.addShulkerBreakdown(lines, result);
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