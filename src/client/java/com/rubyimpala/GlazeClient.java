package com.rubyimpala;

import com.rubyimpala.data.DonutPriceManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;


public class GlazeClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		// CRITICAL: You must start the manager or the API never gets called!
		DonutPriceManager.init();

		ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
			// Standardize EVERYTHING to the "spaced" version immediately
			String rawPath = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
			String spacedId = DonutPriceManager.formatId(rawPath);

			Integer price = DonutPriceManager.getPrice(spacedId);

			if (price == null) {
				// STATE: Never searched or timeout expired
				DonutPriceManager.fetchSpecificItem(spacedId);
				lines.add(Component.literal("Fetching price...")
						.withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
			} else if (price == -1) {
				// STATE: Searched recently but found nothing
				lines.add(Component.literal("No listings found")
						.withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC));
			} else {
				// STATE: Success!
				lines.add(Component.literal("Avg AH Price: " + price + " coins")
						.withStyle(ChatFormatting.GOLD));
			}
		});
	}

}