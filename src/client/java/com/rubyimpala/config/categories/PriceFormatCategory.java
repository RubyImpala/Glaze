package com.rubyimpala.config.categories;

import com.rubyimpala.config.GlazeSettings;
import com.rubyimpala.config.PriceFormat;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PriceFormatCategory {

    public static ConfigCategory build(Screen parent){
        return ConfigCategory.createBuilder()
                .name(Component.literal("Price Format"))

                .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Show Dollar Sign ($)"))
                        .binding(true,
                                () -> GlazeSettings.showDollarSign,
                                v -> GlazeSettings.showDollarSign = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .option(Option.<PriceFormat>createBuilder()
                        .name(Component.literal("Price Format"))
                        .description(OptionDescription.of(Component.literal(
                                "FORMATTED: $1.5K   RAW: $1500   COMMA: $1,500")))
                        .binding(PriceFormat.FORMATTED,
                                () -> GlazeSettings.priceFormat,
                                v -> GlazeSettings.priceFormat = v)
                        .controller(opt -> EnumControllerBuilder.create(opt)
                                .enumClass(PriceFormat.class)
                                .formatValue(f -> Component.literal(switch (f) {
                                    case FORMATTED -> "Formatted (1.5K)";
                                    case RAW ->       "Raw (1500)";
                                    case COMMA ->     "Comma (1,500)";
                                })))
                        .build())

                .build();
    }
}
