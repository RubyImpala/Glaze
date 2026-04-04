package com.rubyimpala.features.auction.events;

import com.rubyimpala.features.auction.AuctionService;
import com.rubyimpala.util.DisplayUtils;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;

public class TooltipEvents {

    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, tooltipType, lines) -> {
            // Don't do anything for empty slots
            if (stack.isEmpty()) return;

            // Get the item's full registry ID, e.g. "minecraft:diamond"
            String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();

            if (AuctionService.isLoading(itemId)) {
                // First hover — fetch is running in background
                lines.add(
                        Component.literal("AH: ").withStyle(ChatFormatting.GOLD)
                                .append(Component.literal("Loading...").withStyle(ChatFormatting.GRAY))
                );

            } else if (AuctionService.hasNoListings(itemId)) {
                // Fetched successfully but nothing is listed on the AH
                lines.add(
                        Component.literal("AH: ").withStyle(ChatFormatting.GOLD)
                                .append(Component.literal("No listings").withStyle(ChatFormatting.GRAY))
                );

            } else {
                // We have a price!
                AuctionService.getLowestPrice(itemId).ifPresent(price -> {
                    lines.add(
                            Component.literal("AH: ").withStyle(ChatFormatting.GOLD)
                                    .append(Component.literal(DisplayUtils.formatPrice(price)).withStyle(ChatFormatting.GREEN))
                    );
                });
            }
        });
    }
}