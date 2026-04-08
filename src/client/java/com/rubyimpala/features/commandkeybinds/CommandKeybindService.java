package com.rubyimpala.features.commandkeybinds;

import com.rubyimpala.features.commandkeybinds.models.CommandKeybind;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandKeybindService {

    private static List<CommandKeybind> binds = new ArrayList<>();
    // Track previous press state per keybind to avoid repeat firing
    private static final Map<String, Boolean> prevState = new HashMap<>();

    public static void load() {
        binds = CommandKeybindStorage.load();
    }

    public static void save() {
        CommandKeybindStorage.save(binds);
    }

    public static List<CommandKeybind> getBinds() { return binds; }
    public static void setBinds(List<CommandKeybind> b) { binds = b; }

    public static void addBind(CommandKeybind bind) {
        binds.add(bind);
        save();
    }

    public static void removeBind(CommandKeybind bind) {
        binds.remove(bind);
        save();
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            long window = Minecraft.getInstance().getWindow().handle();
            boolean screenOpen = client.screen != null;
            boolean inventoryOpen = client.screen instanceof AbstractContainerScreen;

            for (CommandKeybind bind : binds) {
                if (!bind.enabled) continue;

                // Check context
                switch (bind.context) {
                    case IN_GAME_ONLY -> { if (screenOpen) continue; }
                    case INVENTORY_ONLY -> { if (!inventoryOpen) continue; }
                    case EVERYWHERE -> {} // no restriction
                }

                KeybindParser.ParsedKeybind parsed = KeybindParser.parse(bind.keybind);
                if (!parsed.isValid()) continue;

                boolean pressed = KeybindParser.isPressed(window, parsed);
                boolean wasPressed = prevState.getOrDefault(bind.name + bind.keybind, false);

                if (pressed && !wasPressed) {
                    executeCommand(bind, client);
                }
                prevState.put(bind.name + bind.keybind, pressed);
            }
        });
    }

    private static void executeCommand(CommandKeybind bind, Minecraft client) {
        String cmd = bind.command;
        if (bind.clientCommand) {
            // Run as client command
            if (cmd.startsWith("/")) cmd = cmd.substring(1);
            client.player.connection.sendCommand(cmd); // Will be intercepted by client dispatcher
        } else {
            // Send to server
            if (cmd.startsWith("/")) {
                client.player.connection.sendCommand(cmd.substring(1));
            } else {
                client.player.connection.sendChat(cmd);
            }
        }
    }
}