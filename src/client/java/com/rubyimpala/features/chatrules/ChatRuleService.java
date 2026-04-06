package com.rubyimpala.features.chatrules;

import com.rubyimpala.features.chatrules.models.ChatRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatRuleService {

    private static List<ChatRule> rules = new ArrayList<>();

    public static void load() {
        rules = ChatRuleStorage.load();
    }

    public static void save() {
        ChatRuleStorage.save(rules);
    }

    public static List<ChatRule> getRules() {
        return rules;
    }

    public static void setRules(List<ChatRule> newRules) {
        rules = newRules;
    }

    public static void addRule(ChatRule rule) {
        rules.add(rule);
        save();
    }

    public static void removeRule(ChatRule rule) {
        rules.remove(rule);
        save();
    }

    public static Optional<String> apply(String message) {
        for (ChatRule rule : rules) {
            if (!rule.enabled) continue;
            if (rule.prefixMatch) {
                if (message.startsWith(rule.input)) {
                    String suffix = message.substring(rule.input.length());
                    return Optional.of(rule.output + suffix);
                }
            } else {
                if (message.equals(rule.input)) {
                    return Optional.of(rule.output);
                }
            }
        }
        return Optional.empty();
    }
}