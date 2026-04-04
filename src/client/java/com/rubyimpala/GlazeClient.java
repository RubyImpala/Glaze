package com.rubyimpala;

import com.rubyimpala.commands.GlazeCommands;
import com.rubyimpala.config.GlazeConfig;
import com.rubyimpala.features.auction.events.TooltipEvents;

import net.fabricmc.api.ClientModInitializer;

public class GlazeClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
        GlazeCommands.register();
		TooltipEvents.register();
		GlazeConfig.load();
	}
}