package com.rubyimpala.features.commandkeybinds.models;

public class CommandKeybind {
    public String name;
    public String command;
    public String keybind;      // e.g. "Y", "CTRL+Y", "SHIFT+F5"
    public boolean enabled;
    public boolean clientCommand; // if true, runs as client command
    public KeybindContext context;

    public CommandKeybind(String name, String command, String keybind,
                          boolean enabled, boolean clientCommand, KeybindContext context) {
        this.name = name;
        this.command = command;
        this.keybind = keybind;
        this.enabled = enabled;
        this.clientCommand = clientCommand;
        this.context = context;
    }
}