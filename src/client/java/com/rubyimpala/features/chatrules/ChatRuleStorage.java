package com.rubyimpala.features.chatrules;

import com.google.gson.*;
import com.rubyimpala.features.chatrules.models.ChatRule;
import com.rubyimpala.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.rubyimpala.config.GlazeConstants.CONFIG_DIR;

public class ChatRuleStorage {

    public static List<ChatRule> defaults = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger("GlazeMod");
    private static final Path RULES_PATH = CONFIG_DIR.resolve("chat_rules.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static List<ChatRule> load() {
        if (defaults.isEmpty()){
            defaults = getDefaults();
        }

        if (!Files.exists(RULES_PATH)) {
            save(defaults);
            return defaults;
        }
        try {
            String raw = Files.readString(RULES_PATH);
            JsonArray array = JsonParser.parseString(raw).getAsJsonArray();
            List<ChatRule> rules = new ArrayList<>();
            for (JsonElement element : array) {
                JsonObject obj = element.getAsJsonObject();
                rules.add(new ChatRule(
                        obj.get("name").getAsString(),
                        obj.get("input").getAsString(),
                        obj.get("output").getAsString(),
                        obj.get("enabled").getAsBoolean(),
                        obj.get("prefix_match").getAsBoolean()
                ));
            }
            return rules;
        } catch (IOException e) {
            LOGGER.error("[Glaze] Failed to load chat rules", e);
            return new ArrayList<>();
        }
    }

    public static void save(List<ChatRule> rules) {
        try {
            Files.createDirectories(CONFIG_DIR);
            JsonArray array = new JsonArray();
            for (ChatRule rule : rules) {
                JsonObject obj = new JsonObject();
                obj.addProperty("name", rule.name);
                obj.addProperty("input", rule.input);
                obj.addProperty("output", rule.output);
                obj.addProperty("enabled", rule.enabled);
                obj.addProperty("prefix_match", rule.prefixMatch);
                array.add(obj);
            }
            Files.writeString(RULES_PATH, GSON.toJson(array));
        } catch (IOException e) {
            LOGGER.error("[Glaze] Failed to save chat rules", e);
        }
    }

    public static List<ChatRule> getDefaults() {
        defaults.clear();

//        for (String material : StringUtils.MATERIALS) {
            for (String itemType : StringUtils.ALL_ITEMS) {
                // Check if we have enchants defined for this specific item type
                if (StringUtils.ITEM_ENCHANTS.containsKey(itemType)) {
//                    String fullName = material + " " + itemType;
                    String enchants = StringUtils.ITEM_ENCHANTS.get(itemType);
//                    String fullNameAndEnchants = fullName + "  " + enchants;
                    String command = "/ah";
                    String commandAndFullNameAndEnchants = command + " " + itemType + " " + enchants/*fullNameAndEnchants*/;

                    defaults.add(new ChatRule("Maxed " + itemType/*fullName*/, "/ah maxed " + itemType, commandAndFullNameAndEnchants, true, false));
                }
            }
//        }

        return  defaults;
    }
}