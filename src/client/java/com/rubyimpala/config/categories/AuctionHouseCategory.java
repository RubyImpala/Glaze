package com.rubyimpala.config.categories;

import com.rubyimpala.config.GlazeSettings;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AuctionHouseCategory {

    public static ConfigCategory build(Screen parent) {
        return ConfigCategory.createBuilder()
                .name(Component.literal("Auction House"))
                .tooltip(Component.literal("Settings for price tooltips and caching."))

                .option(Option.<Boolean>createBuilder()
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
                        .build())

                .build();
    }
}
