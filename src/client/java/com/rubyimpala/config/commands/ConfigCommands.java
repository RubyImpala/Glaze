package com.rubyimpala.config.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.rubyimpala.config.GlazeConfigScreen;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class ConfigCommands {
    public static LiteralArgumentBuilder<FabricClientCommandSource> buildConfigNode() {
        return literal("config")
                .executes(context -> {
                    Minecraft minecraft = Minecraft.getInstance();
                    minecraft.execute(() -> minecraft.setScreen(GlazeConfigScreen.build(null)));
                    return 1;
                });
    }
}
