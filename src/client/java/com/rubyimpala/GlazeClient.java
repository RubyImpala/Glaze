package com.rubyimpala;

import com.rubyimpala.data.DonutPriceManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class GlazeClient implements ClientModInitializer {

	private String lastHoveredId = "";
	private long hoverStartTime = 0;

	@Override
	public void onInitializeClient() {
		DonutPriceManager.init();

		ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
			String rawPath = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
			String spacedId = DonutPriceManager.formatId(rawPath);

			Integer price = DonutPriceManager.getPrice(spacedId);

			if (price == null) {
				// DEBOUNCING logic
				long now = System.currentTimeMillis();
				if (!spacedId.equals(lastHoveredId)) {
					lastHoveredId = spacedId;
					hoverStartTime = now;
				}

				// Only fetch if mouse stayed on this item for more than 150ms
				if (now - hoverStartTime > 150) {
					DonutPriceManager.fetchSpecificItem(spacedId);
				}

				lines.add(Component.literal("Fetching price...")
						.withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
			} else if (price == -1) {
				lines.add(Component.literal("No listings found")
						.withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC));
			} else {
				lines.add(Component.literal("Avg AH Price: " + price + " coins")
						.withStyle(ChatFormatting.GOLD));
			}
		});
	}
}