package com.rubyimpala;

import com.rubyimpala.commands.GlazeCommands;
import com.rubyimpala.config.GlazeConfig;
import com.rubyimpala.events.ChatEvents;
import com.rubyimpala.events.ServerJoinEvents;
import com.rubyimpala.features.chatrules.ChatRuleService;
import com.rubyimpala.features.commandkeybinds.CommandKeybindService;
import com.rubyimpala.features.pricing.events.TooltipEvents;
import com.rubyimpala.features.ahsearch.input.AhSearchKeybinds;

import net.fabricmc.api.ClientModInitializer;

public class GlazeClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
        GlazeCommands.register();
		TooltipEvents.register();
		ChatEvents.register();
		GlazeConfig.load();
		AhSearchKeybinds.register();
		ChatRuleService.load();
		ServerJoinEvents.register();
		CommandKeybindService.load();
		CommandKeybindService.register();
	}
}