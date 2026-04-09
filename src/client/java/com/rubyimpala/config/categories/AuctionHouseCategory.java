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
import net.minecraft.network.chat.Component;

public class AuctionHouseCategory {

    public static ConfigCategory build() {

        var auctionHouseCategoryBuilder = ConfigCategory.createBuilder()
                .name(Component.literal("Auction House"))
                .tooltip(Component.literal("Settings for price tooltips and caching."));

        // Price Tooltip group
        auctionHouseCategoryBuilder.group(priceTooltipGroupBuilder());

        auctionHouseCategoryBuilder.group(priceFormatOptionGroupBuilder());

        return auctionHouseCategoryBuilder.build();
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
                .binding(GlazeSettings.getDefaults().showPriceTooltips,
                        () -> GlazeSettings.CONFIG().showPriceTooltips,
                        v -> GlazeSettings.CONFIG().showPriceTooltips = v)
                .controller(TickBoxControllerBuilder::create)
                .build());

        priceTooltipOptionGroup.option(Option.<Boolean>createBuilder()
                .name(Component.literal("Show Shulker Valuation"))
                .description(OptionDescription.of(Component.literal(
                        "Show total AH value when hovering over shulker boxes.")))
                .binding(GlazeSettings.getDefaults().showShulkerValuation,
                        () -> GlazeSettings.CONFIG().showShulkerValuation,
                        v -> GlazeSettings.CONFIG().showShulkerValuation = v)
                .controller(TickBoxControllerBuilder::create)
                .build());

        priceTooltipOptionGroup.option(Option.<Boolean>createBuilder()
                .name(Component.literal("Show [R to Reload] Hint"))
                .binding(GlazeSettings.getDefaults().showReloadHint,
                        () -> GlazeSettings.CONFIG().showReloadHint,
                        v -> GlazeSettings.CONFIG().showReloadHint = v)
                .controller(TickBoxControllerBuilder::create)
                .build());

        priceTooltipOptionGroup.option(Option.<Boolean>createBuilder()
                .name(Component.literal("Show [Shift for Stack Price] Hint"))
                .binding(GlazeSettings.getDefaults().showStackPriceHint,
                        () -> GlazeSettings.CONFIG().showStackPriceHint,
                        v -> GlazeSettings.CONFIG().showStackPriceHint = v)
                .controller(TickBoxControllerBuilder::create)
                .build());

        priceTooltipOptionGroup.option(Option.<Boolean>createBuilder()
                .name(Component.literal("Show [Shift for Breakdown] Hint"))
                .binding(GlazeSettings.getDefaults().showShulkerBreakdownHint,
                        () -> GlazeSettings.CONFIG().showShulkerBreakdownHint,
                        v -> GlazeSettings.CONFIG().showShulkerBreakdownHint = v)
                .controller(TickBoxControllerBuilder::create)
                .build());

        priceTooltipOptionGroup.option(Option.<Integer>createBuilder()
                .name(Component.literal("Price Cache Duration (minutes)"))
                .description(OptionDescription.of(Component.literal(
                        "How long prices are cached before being re-fetched.")))
                .binding(GlazeSettings.getDefaults().cacheTtlMinutes,
                        () -> GlazeSettings.CONFIG().cacheTtlMinutes,
                        v -> GlazeSettings.CONFIG().cacheTtlMinutes = v)
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
                .binding(GlazeSettings.getDefaults().showDollarSign,
                        () -> GlazeSettings.CONFIG().showDollarSign,
                        v -> GlazeSettings.CONFIG().showDollarSign = v)
                .controller(TickBoxControllerBuilder::create)
                .build());

        priceFormatOptionGroup.option(Option.<PriceFormat>createBuilder()
                .name(Component.literal("Price Format"))
                .description(OptionDescription.of(Component.literal(
                        "FORMATTED: $1.5K   RAW: $1500   COMMA: $1,500")))
                .binding(GlazeSettings.getDefaults().priceFormat,
                        () -> GlazeSettings.CONFIG().priceFormat,
                        v -> GlazeSettings.CONFIG().priceFormat = v)
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
