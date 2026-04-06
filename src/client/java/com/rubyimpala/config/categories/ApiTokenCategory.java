package com.rubyimpala.config.categories;

import com.rubyimpala.config.GlazeConfig;
import com.rubyimpala.config.GlazeSettings;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ApiTokenCategory {

    public static ConfigCategory build(Screen parent){
        return ConfigCategory.createBuilder()
                .name(Component.literal("API & Token"))

                .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Auto-detect Token from Server Chat"))
                        .description(OptionDescription.of(Component.literal(
                                "Automatically saves your API token when the server sends it in chat.")))
                        .binding(true,
                                () -> GlazeSettings.autoTokenDetection,
                                v -> GlazeSettings.autoTokenDetection = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .option(Option.<String>createBuilder()
                        .name(Component.literal("API Token"))
                        .description(OptionDescription.of(Component.literal(
                                "Your DonutSMP API token. Run /api on the server to get it.")))
                        .binding("",
                                () -> GlazeConfig.Auth.getToken(),
                                v -> GlazeConfig.Auth.updateToken(v))
                        .controller(opt -> StringControllerBuilder.create(opt))
                        .build())

                .build();
    }
}
