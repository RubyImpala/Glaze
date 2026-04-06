package com.rubyimpala.config.categories;

import com.rubyimpala.config.GlazeConfigScreen;
import com.rubyimpala.config.GlazeSettings;
import com.rubyimpala.features.chatrules.ChatRuleService;
import com.rubyimpala.features.chatrules.ChatRuleStorage;
import com.rubyimpala.features.chatrules.models.ChatRule;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class ChatRulesCategory {

    private static boolean resetConfirmPending = false;

    public static ConfigCategory build(Screen parent){

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
