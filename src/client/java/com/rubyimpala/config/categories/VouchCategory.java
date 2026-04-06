package com.rubyimpala.config.categories;

import com.rubyimpala.config.GlazeSettings;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class VouchCategory {

    public static ConfigCategory build(Screen parent){
        return ConfigCategory.createBuilder()
                .name(Component.literal("Vouch System"))

                .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Enable Vouch System"))
                        .description(OptionDescription.of(Component.literal(
                                "Enables the /glaze vouch commands.")))
                        .binding(true,
                                () -> GlazeSettings.vouchSystemEnabled,
                                v -> GlazeSettings.vouchSystemEnabled = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .build();
    }
}
