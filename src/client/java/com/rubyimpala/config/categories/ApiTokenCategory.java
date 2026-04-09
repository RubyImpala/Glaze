package com.rubyimpala.config.categories;

import com.rubyimpala.config.GlazeSettings;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.network.chat.Component;

public class ApiTokenCategory {

    public static ConfigCategory build(){
        var apiTokenCategoryBuilder = ConfigCategory.createBuilder()
                .name(Component.literal("API & Token"))
                .tooltip(Component.literal("Config settings for the api token"));

        apiTokenCategoryBuilder.option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Auto-detect Token from Server Chat"))
                        .description(OptionDescription.of(Component.literal(
                                "Automatically saves your API token when the server sends it in chat.")))
                        .binding(GlazeSettings.getDefaults().autoTokenDetection,
                                () -> GlazeSettings.CONFIG().autoTokenDetection,
                                v -> GlazeSettings.CONFIG().autoTokenDetection = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build());

        apiTokenCategoryBuilder.option(Option.<String>createBuilder()
                        .name(Component.literal("API Token"))
                        .description(OptionDescription.of(Component.literal(
                                "Your DonutSMP API token. Run /api on the server to get it.")))
                        .binding(GlazeSettings.getDefaults().apiToken,
                                () -> GlazeSettings.CONFIG().apiToken,
                                v -> GlazeSettings.CONFIG().apiToken = v)
                        .controller(StringControllerBuilder::create)
                        .build());

        return apiTokenCategoryBuilder.build();
    }
}
