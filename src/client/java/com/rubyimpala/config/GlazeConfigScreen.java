package com.rubyimpala.config;

import com.rubyimpala.features.chatrules.ChatRuleService;
import com.rubyimpala.features.chatrules.ChatRuleStorage;
import com.rubyimpala.features.chatrules.models.ChatRule;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class GlazeConfigScreen {
    private static boolean resetConfirmPending = false;

    public static Screen build(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Glaze Settings"))
                .save(GlazeSettings::save)

                // ── Auction House ──────────────────────────────
                .category(ConfigCategory.createBuilder()
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

                        .build())

                // ── Price Format ───────────────────────────────
                .category(ConfigCategory.createBuilder()
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

                        .build())

                // ── API & Token ────────────────────────────────
                .category(ConfigCategory.createBuilder()
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

                        .build())

                // ── Vouch System ───────────────────────────────
                .category(ConfigCategory.createBuilder()
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

                        .build())

                // ── Chat Rules ─────────────────────────────────
                .category(buildChatRulesCategory(parent))

                .build()
                .generateScreen(parent);
    }

    private static ConfigCategory buildChatRulesCategory(Screen parent) {
        var categoryBuilder = ConfigCategory.createBuilder()
                .name(Component.literal("Chat Rules"));

        // Show redirect notification toggle
        categoryBuilder.option(Option.<Boolean>createBuilder()
                .name(Component.literal("Show Redirect Notification"))
                .description(OptionDescription.of(Component.literal(
                        "Show a message when a chat rule redirects your input.")))
                .binding(true,
                        () -> GlazeSettings.showRedirectNotification,
                        v -> GlazeSettings.showRedirectNotification = v)
                .controller(TickBoxControllerBuilder::create)
                .build());

        // Show reset to defaul button
        categoryBuilder.option(ButtonOption.createBuilder()
                .name(Component.literal(resetConfirmPending
                        ? "§cClick again to confirm reset!"
                        : "§4Reset Rules To Default"))
                .text(Component.literal(resetConfirmPending ? "§cConfirm" : "Reset"))
                .action((screen, opt) -> {
                    if (!resetConfirmPending) {
                        resetConfirmPending = true;
                        // Regenerate screen to show confirm state
                        Minecraft.getInstance().execute(() ->
                                Minecraft.getInstance().setScreen(
                                        GlazeConfigScreen.build(parent)));
                    } else {
                        resetConfirmPending = false;
                        ChatRuleService.setRules(new ArrayList<>(ChatRuleStorage.getDefaults()));
                        ChatRuleService.save();
                        Minecraft.getInstance().execute(() ->
                                Minecraft.getInstance().setScreen(
                                        GlazeConfigScreen.build(parent)));
                    }
                })
                .build());

        // Add Rule button — opens a small screen to name the new rule
        categoryBuilder.option(ButtonOption.createBuilder()
                .name(Component.literal("Add New Rule"))
                .text(Component.literal("Add"))
                .action((screen, opt) -> {
                    // Generate a default name like "Rule 1", "Rule 2" etc.
                    String defaultName = "Rule " + (ChatRuleService.getRules().size() + 1);
                    ChatRuleService.addRule(new ChatRule(
                            defaultName, "", "", true, true));
                    Minecraft.getInstance().execute(() ->
                            Minecraft.getInstance().setScreen(
                                    GlazeConfigScreen.build(parent)));
                })
                .build());

        // One group per rule
        for (ChatRule rule : ChatRuleService.getRules()) {
            categoryBuilder.group(OptionGroup.createBuilder()
                    .name(Component.literal(rule.name))
                    .description(OptionDescription.of(Component.literal(
                            rule.input.isEmpty() ? "No input set" : rule.input + " → " + rule.output)))
                    .collapsed(true)

                    .option(Option.<String>createBuilder()
                            .name(Component.literal("Rule Name"))
                            .binding("Rule",
                                    () -> rule.name,
                                    v -> { rule.name = v; ChatRuleService.save(); })
                            .controller(opt -> StringControllerBuilder.create(opt))
                            .build())

                    .option(Option.<Boolean>createBuilder()
                            .name(Component.literal("Enabled"))
                            .binding(true,
                                    () -> rule.enabled,
                                    v -> { rule.enabled = v; ChatRuleService.save(); })
                            .controller(TickBoxControllerBuilder::create)
                            .build())

                    .option(Option.<Boolean>createBuilder()
                            .name(Component.literal("Prefix Match"))
                            .description(OptionDescription.of(Component.literal(
                                    "If on, matches anything starting with the input and appends the rest to the output.")))
                            .binding(true,
                                    () -> rule.prefixMatch,
                                    v -> { rule.prefixMatch = v; ChatRuleService.save(); })
                            .controller(TickBoxControllerBuilder::create)
                            .build())

                    .option(Option.<String>createBuilder()
                            .name(Component.literal("Input"))
                            .description(OptionDescription.of(Component.literal(
                                    "The command or message to intercept.")))
                            .binding("",
                                    () -> rule.input,
                                    v -> { rule.input = v; ChatRuleService.save(); })
                            .controller(opt -> StringControllerBuilder.create(opt))
                            .build())

                    .option(Option.<String>createBuilder()
                            .name(Component.literal("Output"))
                            .description(OptionDescription.of(Component.literal(
                                    "The command or message to send instead.")))
                            .binding("",
                                    () -> rule.output,
                                    v -> { rule.output = v; ChatRuleService.save(); })
                            .controller(opt -> StringControllerBuilder.create(opt))
                            .build())

                    .option(ButtonOption.createBuilder()
                            .name(Component.literal("Delete Rule"))
                            .text(Component.literal("§cDelete"))
                            .action((screen, opt) -> {
                                ChatRuleService.removeRule(rule);
                                // Regenerate screen to reflect deletion
                                Minecraft.getInstance().execute(() ->
                                        Minecraft.getInstance().setScreen(
                                                GlazeConfigScreen.build(parent)));
                            })
                            .build())

                    .build());
        }

        return categoryBuilder.build();
    }
}