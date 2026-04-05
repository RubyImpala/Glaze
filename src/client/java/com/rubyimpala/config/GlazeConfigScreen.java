package com.rubyimpala.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;

public class GlazeConfigScreen {

    public static Screen build(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Glaze Settings"))
                .setSavingRunnable(GlazeSettings::save);

        ConfigEntryBuilder entry = builder.entryBuilder();

        // ── Auction House ──────────────────────────────────────────
        ConfigCategory ah = builder.getOrCreateCategory(
                Component.literal("Auction House"));

        ah.addEntry(entry.startBooleanToggle(
                        Component.literal("Show Price Tooltips"),
                        GlazeSettings.showPriceTooltips)
                .setTooltip(Component.literal("Show AH prices when hovering over items."))
                .setDefaultValue(true)
                .setSaveConsumer(v -> GlazeSettings.showPriceTooltips = v)
                .build());

        ah.addEntry(entry.startBooleanToggle(
                        Component.literal("Show Shulker Valuation"),
                        GlazeSettings.showShulkerValuation)
                .setTooltip(Component.literal("Show total AH value when hovering over shulker boxes."))
                .setDefaultValue(true)
                .setSaveConsumer(v -> GlazeSettings.showShulkerValuation = v)
                .build());

        ah.addEntry(entry.startBooleanToggle(
                        Component.literal("Show [R to Reload] Hint"),
                        GlazeSettings.showReloadHint)
                .setDefaultValue(true)
                .setSaveConsumer(v -> GlazeSettings.showReloadHint = v)
                .build());

        ah.addEntry(entry.startBooleanToggle(
                        Component.literal("Show [Shift for Stack Price] Hint"),
                        GlazeSettings.showStackPriceHint)
                .setDefaultValue(true)
                .setSaveConsumer(v -> GlazeSettings.showStackPriceHint = v)
                .build());

        ah.addEntry(entry.startBooleanToggle(
                        Component.literal("Show [Shift for Breakdown] Hint on Shulkers"),
                        GlazeSettings.showShulkerBreakdownHint)
                .setDefaultValue(true)
                .setSaveConsumer(v -> GlazeSettings.showShulkerBreakdownHint = v)
                .build());

        ah.addEntry(entry.startIntSlider(
                        Component.literal("Price Cache Duration (minutes)"),
                        GlazeSettings.cacheTtlMinutes, 1, 10)
                .setTooltip(Component.literal("How long prices are cached before being re-fetched."))
                .setDefaultValue(5)
                .setSaveConsumer(v -> GlazeSettings.cacheTtlMinutes = v)
                .build());

        // ── Price Format ───────────────────────────────────────────
        ConfigCategory format = builder.getOrCreateCategory(
                Component.literal("Price Format"));

        format.addEntry(entry.startBooleanToggle(
                        Component.literal("Show Dollar Sign ($)"),
                        GlazeSettings.showDollarSign)
                .setDefaultValue(true)
                .setSaveConsumer(v -> GlazeSettings.showDollarSign = v)
                .build());

        List<PriceFormat> formats = Arrays.asList(PriceFormat.values());
        format.addEntry(entry.startSelector(
                        Component.literal("Price Format"),
                        formats.toArray(),
                        GlazeSettings.priceFormat)
                .setTooltip(
                        Component.literal("FORMATTED: $1.5K   RAW: $1500   COMMA: $1,500"))
                .setDefaultValue(PriceFormat.FORMATTED)
                .setNameProvider(o -> Component.literal(switch ((PriceFormat) o) {
                    case FORMATTED -> "Formatted (1.5K)";
                    case RAW ->       "Raw (1500)";
                    case COMMA ->     "Comma (1,500)";
                }))
                .setSaveConsumer(v -> GlazeSettings.priceFormat = (PriceFormat) v)
                .build());

        // ── API & Token ────────────────────────────────────────────
        ConfigCategory api = builder.getOrCreateCategory(
                Component.literal("API & Token"));

        api.addEntry(entry.startBooleanToggle(
                        Component.literal("Auto-detect Token from Server Chat"),
                        GlazeSettings.autoTokenDetection)
                .setTooltip(Component.literal(
                        "Automatically saves your API token when the server sends it in chat."))
                .setDefaultValue(true)
                .setSaveConsumer(v -> GlazeSettings.autoTokenDetection = v)
                .build());

        // Token field — reads and writes directly to GlazeConfig.Auth
        api.addEntry(entry.startStrField(
                        Component.literal("API Token"),
                        GlazeConfig.Auth.getToken())
                .setTooltip(Component.literal(
                        "Your DonutSMP API token. Run /api on the server to get it."))
                .setDefaultValue("")
                .setSaveConsumer(v -> {
                    if (v.isBlank()) {
                        GlazeConfig.Auth.updateToken("");
                    } else {
                        GlazeConfig.Auth.updateToken(v);
                    }
                })
                .build());

        // ── Vouch System ───────────────────────────────────────────
        ConfigCategory vouch = builder.getOrCreateCategory(
                Component.literal("Vouch System"));

        vouch.addEntry(entry.startBooleanToggle(
                        Component.literal("Enable Vouch System"),
                        GlazeSettings.vouchSystemEnabled)
                .setTooltip(Component.literal(
                        "Enables the /glaze vouch commands."))
                .setDefaultValue(true)
                .setSaveConsumer(v -> GlazeSettings.vouchSystemEnabled = v)
                .build());

        return builder.build();
    }
}