package com.rubyimpala.config.categories;

import com.rubyimpala.config.GlazeSettings;
import com.rubyimpala.config.PriceFormat;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AuctionHouseCategory {

    public static ConfigCategory build(Screen parent) {

        var categoryBuilder = ConfigCategory.createBuilder()
                .name(Component.literal("Auction House"))
                .tooltip(Component.literal("Settings for price tooltips and caching."));

        // Price Tooltip group
        categoryBuilder.group(priceTooltipGroupBuilder());

        categoryBuilder.group(priceFormatOptionGroupBuilder());

        return categoryBuilder.build();
    }

    public static OptionGroup priceTooltipGroupBuilder() {

        var priceTooltipOptionGroup = OptionGroup.createBuilder()
                .name(Component.literal("Price Tooltip"))
                .description(OptionDescription.of(Component.literal("Settings for price tooltip")))
                .collapsed(true);



                priceTooltipOptionGroup.option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Show Price Tooltips"))
                        .description(OptionDescription.of(Component.literal(
                                "Show AH prices when hovering over items.")))
                        .binding(true,
                                () -> GlazeSettings.showPriceTooltips,
                                v -> GlazeSettings.showPriceTooltips = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Show Shulker Valuation"))
                        .description(OptionDescription.of(Component.literal(
                                "Show total AH value when hovering over shulker boxes.")))
                        .binding(true,
                                () -> GlazeSettings.showShulkerValuation,
                                v -> GlazeSettings.showShulkerValuation = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Show [R to Reload] Hint"))
                        .binding(true,
                                () -> GlazeSettings.showReloadHint,
                                v -> GlazeSettings.showReloadHint = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Show [Shift for Stack Price] Hint"))
                        .binding(true,
                                () -> GlazeSettings.showStackPriceHint,
                                v -> GlazeSettings.showStackPriceHint = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Show [Shift for Breakdown] Hint"))
                        .binding(true,
                                () -> GlazeSettings.showShulkerBreakdownHint,
                                v -> GlazeSettings.showShulkerBreakdownHint = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())

                .option(Option.<Integer>createBuilder()
                        .name(Component.literal("Price Cache Duration (minutes)"))
                        .description(OptionDescription.of(Component.literal(
                                "How long prices are cached before being re-fetched.")))
                        .binding(5,
                                () -> GlazeSettings.cacheTtlMinutes,
                                v -> GlazeSettings.cacheTtlMinutes = v)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(1, 10)
                                .step(1))
                        .build());

        return priceTooltipOptionGroup.build();
    }

    public static OptionGroup priceFormatOptionGroupBuilder() {

        var priceFormatOptionGroup = OptionGroup.createBuilder()
                .name(Component.literal("Price Format"))
                .description(OptionDescription.of(Component.literal("Settings for price format")))
                .collapsed(true);

        priceFormatOptionGroup.option(Option.<Boolean>createBuilder()
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
                        .build());

        return priceFormatOptionGroup.build();
    }
}
