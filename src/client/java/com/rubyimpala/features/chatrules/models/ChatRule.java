package com.rubyimpala.features.chatrules.models;

public class ChatRule {
    public String name;      // Display name in the config screen
    public String input;
    public String output;
    public boolean enabled;
    public boolean prefixMatch;

    public ChatRule(String name, String input, String output,
                    boolean enabled, boolean prefixMatch) {
        this.name = name;
        this.input = input;
        this.output = output;
        this.enabled = enabled;
        this.prefixMatch = prefixMatch;
    }
}