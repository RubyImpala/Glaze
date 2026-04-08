package com.rubyimpala.config.categories;

import com.rubyimpala.config.GlazeConfigScreen;
import com.rubyimpala.features.commandkeybinds.CommandKeybindService;
import com.rubyimpala.features.commandkeybinds.models.CommandKeybind;
import com.rubyimpala.features.commandkeybinds.models.KeybindContext;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CommandKeybindsCategory {

    private static boolean justRegenerated = false;

    public static ConfigCategory build(Screen parent) {
        var categoryBuilder = ConfigCategory.createBuilder()
                .name(Component.literal("Command Keybinds"))
                .tooltip(Component.literal(
                        "Bind commands to keys. Format: Y, CTRL+Y, SHIFT+F5, etc."));

        categoryBuilder.option(ButtonOption.createBuilder()
                .name(Component.literal("Add New Keybind"))
                .text(Component.literal("Add"))
                .action((screen, opt) -> {
                    String defaultName = "Keybind " + (CommandKeybindService.getBinds().size() + 1);
                    CommandKeybindService.addBind(new CommandKeybind(
                            defaultName, "", "", true, false, KeybindContext.EVERYWHERE));
                    justRegenerated = true;
                    Minecraft.getInstance().execute(() ->
                            Minecraft.getInstance().setScreen(
                                    GlazeConfigScreen.build(parent)));
                })
                .build());

        for (CommandKeybind bind : CommandKeybindService.getBinds()) {
            categoryBuilder.group(OptionGroup.createBuilder()
                    .name(Component.literal(bind.name))
                    .collapsed(true)

                    .option(Option.<String>createBuilder()
                            .name(Component.literal("Name"))
                            .binding("Keybind",
                                    () -> bind.name,
                                    v -> { bind.name = v; CommandKeybindService.save(); })
                            .controller(opt -> StringControllerBuilder.create(opt))
                            .build())

                    .option(Option.<Boolean>createBuilder()
                            .name(Component.literal("Enabled"))
                            .binding(true,
                                    () -> bind.enabled,
                                    v -> { bind.enabled = v; CommandKeybindService.save(); })
                            .controller(TickBoxControllerBuilder::create)
                            .build())

                    .option(Option.<String>createBuilder()
                            .name(Component.literal("Key"))
                            .description(OptionDescription.of(Component.literal(
                                    "Examples: Y, CTRL+Y, SHIFT+F5, ALT+G")))
                            .binding("",
                                    () -> bind.keybind,
                                    v -> { bind.keybind = v; CommandKeybindService.save(); })
                            .controller(opt -> StringControllerBuilder.create(opt))
                            .build())

                    .option(Option.<String>createBuilder()
                            .name(Component.literal("Command"))
                            .description(OptionDescription.of(Component.literal(
                                    "The command or message to run. Use / for server commands.")))
                            .binding("",
                                    () -> bind.command,
                                    v -> { bind.command = v; CommandKeybindService.save(); })
                            .controller(opt -> StringControllerBuilder.create(opt))
                            .build())

                    .option(Option.<Boolean>createBuilder()
                            .name(Component.literal("Client Command"))
                            .description(OptionDescription.of(Component.literal(
                                    "If enabled, runs as a mod client command instead of sending to server.")))
                            .binding(false,
                                    () -> bind.clientCommand,
                                    v -> { bind.clientCommand = v; CommandKeybindService.save(); })
                            .controller(TickBoxControllerBuilder::create)
                            .build())

                    // Context enum
                    .option(Option.<KeybindContext>createBuilder()
                            .name(Component.literal("Context"))
                            .description(OptionDescription.of(Component.literal(
                                    "When should this keybind be active?")))
                            .binding(KeybindContext.EVERYWHERE,
                                    () -> bind.context,
                                    v -> { bind.context = v; CommandKeybindService.save(); })
                            .controller(opt -> EnumControllerBuilder.create(opt)
                                    .enumClass(KeybindContext.class)
                                    .formatValue(c -> Component.literal(switch (c) {
                                        case EVERYWHERE -> "Everywhere";
                                        case IN_GAME_ONLY -> "In Game Only";
                                        case INVENTORY_ONLY -> "Inventory Only";
                                    })))
                            .build())

                    .option(ButtonOption.createBuilder()
                            .name(Component.literal("Delete Keybind"))
                            .text(Component.literal("§cDelete"))
                            .action((screen, opt) -> {
                                CommandKeybindService.removeBind(bind);
                                justRegenerated = true;
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