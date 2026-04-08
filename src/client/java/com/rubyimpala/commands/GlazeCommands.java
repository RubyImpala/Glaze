package com.rubyimpala.commands;

import com.rubyimpala.config.commands.ConfigCommands;
import com.rubyimpala.features.pricing.commands.ApiCommands;
import com.rubyimpala.features.vouch.commands.VouchCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class GlazeCommands {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            var glazeRoot = literal("glaze");

            // Attach the branches from other classes
            glazeRoot.then(ApiCommands.buildApiBranch())
                    .then(VouchCommands.buildVouchBranch())
                    .then(ConfigCommands.buildConfigNode());

            // Register the whole tree at once
            dispatcher.register(glazeRoot);
        });
    }
}