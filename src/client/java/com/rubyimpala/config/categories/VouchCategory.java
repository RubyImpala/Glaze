package com.rubyimpala.config.categories;

import com.rubyimpala.config.GlazeSettings;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.network.chat.Component;

public class VouchCategory {

    public static ConfigCategory build() {
        var vouchCategoryBuilder = ConfigCategory.createBuilder()
                .name(Component.literal("Vouch System"))
                .tooltip(Component.literal("Config settings for the vouch system"));

        vouchCategoryBuilder.option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Enable Vouch System"))
                        .description(OptionDescription.of(Component.literal(
                                "Enables the /glaze vouch commands.")))
                        .binding(GlazeSettings.getDefaults().vouchSystemEnabled,
                                () -> GlazeSettings.CONFIG().vouchSystemEnabled,
                                v -> GlazeSettings.CONFIG().vouchSystemEnabled = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build());

        return vouchCategoryBuilder.build();
    }
}