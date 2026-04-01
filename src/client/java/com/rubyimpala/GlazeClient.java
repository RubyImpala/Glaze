package com.rubyimpala;

import com.rubyimpala.data.DonutPriceManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands; // Renamed from ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import com.mojang.brigadier.arguments.StringArgumentType;

// Static imports make the command builder much cleaner in 26.1
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;

public class GlazeClient implements ClientModInitializer {

	private String lastHoveredId = "";
	private long hoverStartTime = 0;

	@Override
	public void onInitializeClient() {
		DonutPriceManager.init();

		// Register the /glaze api <key> command
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(literal("glaze")
					.then(literal("api")
							// Subcommand: /glaze api delete
							.then(literal("delete")
									.executes(context -> {
										DonutPriceManager.updateAuthToken(""); // Clear the token
										context.getSource().sendFeedback(Component.literal("§6[Glaze] §cAPI Key deleted from config."));
										return 1;
									})
							)
							// Existing argument: /glaze api <key>
							.then(argument("key", StringArgumentType.string())
									.executes(context -> {
										String newKey = StringArgumentType.getString(context, "key");
										DonutPriceManager.updateAuthToken(newKey);
										context.getSource().sendFeedback(Component.literal("§6[Glaze] §aAPI Key updated and saved!"));
										return 1;
									})
							)
					)
			);
		});

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			// We use execute to run this on the next client tick so the player exists
			client.execute(() -> {
				if (client.player != null) {
					if (DonutPriceManager.getAuthToken().isEmpty()) {
						client.player.sendSystemMessage(Component.literal("§6[Glaze] §eNo API Key found!")
								.withStyle(ChatFormatting.YELLOW));
						client.player.sendSystemMessage(Component.literal("§7Type §f/api key §7on DonutSMP, then run §f/glaze api {key}")
								.withStyle(ChatFormatting.GRAY));
					} else {
						// If they have a key, just a small "active" message
						client.player.sendSystemMessage(Component.literal("§6[Glaze] §aPrice fetching active."));
					}
				}
			});
		});

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