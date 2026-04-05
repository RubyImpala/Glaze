package com.rubyimpala.util;

import com.rubyimpala.config.GlazeSettings;
import com.rubyimpala.features.auction.models.ItemValueEntry;
import com.rubyimpala.features.auction.models.ShulkerValueResult;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

public class HintComponents {

    public static void addLoadingTooltip(List<Component> lines){
        if (!GlazeSettings.showPriceTooltips) return;
        lines.add(Component.literal("Price: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal("Loading...").withStyle(ChatFormatting.GRAY)));
    }

    public static void addPriceTooltip(List<Component> lines, long unitPrice) {
        if (!GlazeSettings.showPriceTooltips) return;
        lines.add(Component.literal("Price: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(DisplayUtils.formatPrice(unitPrice))
                        .withStyle(ChatFormatting.GREEN)));
    }

    public static void addNoListingsTooltip(List<Component> lines) {
        if (!GlazeSettings.showPriceTooltips) return;
        lines.add(Component.literal("Price: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal("No listings").withStyle(ChatFormatting.GRAY)));
        addReloadHint(lines);
    }

    public static void addStackPriceTooltip(List<Component> lines, long stackPrice, int maxStackSize) {
        if (!GlazeSettings.showPriceTooltips) return;
        lines.add(Component.literal("Price (x" + maxStackSize + "): ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(DisplayUtils.formatPrice(stackPrice))
                        .withStyle(ChatFormatting.GREEN)));
    }

    public static void addReloadHint(List<Component> lines) {
        if (!GlazeSettings.showReloadHint) return;
        lines.add(Component.literal("[R to reload]")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    public static void addStackPriceHint(List<Component> lines) {
        if (!GlazeSettings.showStackPriceHint) return;
        lines.add(Component.literal("[Shift for stack price]")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    public static void addUnshiftHint(List<Component> lines) {
        if (!GlazeSettings.showStackPriceHint) return;
        lines.add(Component.literal("[Unshift for unit price]")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    public static void addShulkerBreakdownHint(List<Component> lines) {
        if (!GlazeSettings.showShulkerBreakdownHint) return;
        lines.add(Component.literal("[Shift for breakdown]")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    public static void addShulkerSummary(List<Component> lines, long totalPrice, boolean hasLoading) {
        if (!GlazeSettings.showShulkerValuation) return;
        lines.add(Component.literal("Price: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(DisplayUtils.formatPrice(totalPrice))
                        .withStyle(ChatFormatting.GREEN))
                .append(loadingSuffix(hasLoading)));
        addShulkerBreakdownHint(lines);
        addReloadHint(lines);
    }

    public static void addShulkerBreakdown(List<Component> lines, ShulkerValueResult result) {
        if (!GlazeSettings.showShulkerValuation) return;
        lines.add(Component.literal("━━ Price Breakdown ━━").withStyle(ChatFormatting.GOLD));
        for (ItemValueEntry entry : result.entries()) {
            lines.add(buildEntryLine(entry));
        }
        lines.add(Component.literal("  Total: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(DisplayUtils.formatPrice(result.totalPrice()))
                        .withStyle(ChatFormatting.GREEN))
                .append(loadingSuffix(result.hasLoading())));
    }

    private static Component buildEntryLine(ItemValueEntry entry) {
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
}