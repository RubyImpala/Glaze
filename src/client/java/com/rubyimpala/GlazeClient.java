package com.rubyimpala;

import com.rubyimpala.commands.GlazeCommands;
import com.rubyimpala.models.DonutPriceManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

// Static imports make the command builder much cleaner in 26.1


public class GlazeClient implements ClientModInitializer {

	private String lastHoveredId = "";
	private long hoverStartTime = 0;

	@Override
	public void onInitializeClient() {
		DonutPriceManager.init();

        GlazeCommands.register();

		ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
			String rawPath = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
			String spacedId = DonutPriceManager.formatId(rawPath);
			Integer price = DonutPriceManager.getPrice(spacedId);

			if (price == null) {
				long now = System.currentTimeMillis();
				if (!spacedId.equals(lastHoveredId)) {
					lastHoveredId = spacedId;
					hoverStartTime = now;
				}
				if (now - hoverStartTime > 150) {
					DonutPriceManager.fetchSpecificItem(spacedId);
				}
				lines.add(Component.literal("Fetching price...").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
			} else if (price == -1) {
				lines.add(Component.literal("No listings found").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
			} else {
				lines.add(Component.literal("Avg AH Price: " + price + " coins").withStyle(ChatFormatting.GOLD));
			}
		});
	}
}