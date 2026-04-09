package com.rubyimpala;

import com.rubyimpala.commands.GlazeCommands;
import com.rubyimpala.config.GlazeSettings;
import com.rubyimpala.events.ChatEvents;
import com.rubyimpala.events.ServerJoinEvents;
import com.rubyimpala.features.chatrules.ChatRuleService;
import com.rubyimpala.features.commandkeybinds.CommandKeybindService;
import com.rubyimpala.features.pricing.events.TooltipEvents;
import com.rubyimpala.features.ahsearch.input.AhSearchKeybinds;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.rubyimpala.util.GlazeConstants.MOD_ID;

public class GlazeClient implements ClientModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
        GlazeCommands.register();
		TooltipEvents.register();
		ChatEvents.register();
		GlazeSettings.load();
		AhSearchKeybinds.register();
		ChatRuleService.load();
		ServerJoinEvents.register();
		CommandKeybindService.load();
		CommandKeybindService.register();
	}
}