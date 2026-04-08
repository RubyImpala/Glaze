package com.rubyimpala.features.commandkeybinds;

import com.google.gson.*;
import com.rubyimpala.features.commandkeybinds.models.CommandKeybind;
import com.rubyimpala.features.commandkeybinds.models.KeybindContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.rubyimpala.util.GlazeConstants.CONFIG_DIR;

public class CommandKeybindStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger("GlazeMod");
    private static final Path PATH = CONFIG_DIR.resolve("command_keybinds.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static List<CommandKeybind> load() {
        if (!Files.exists(PATH)) {
            save(new ArrayList<>());
            return new ArrayList<>();
        }
        try {
            String raw = Files.readString(PATH);
            JsonArray array = JsonParser.parseString(raw).getAsJsonArray();
            List<CommandKeybind> binds = new ArrayList<>();
            for (JsonElement element : array) {
                JsonObject obj = element.getAsJsonObject();
                binds.add(new CommandKeybind(
                        obj.get("name").getAsString(),
                        obj.get("command").getAsString(),
                        obj.get("keybind").getAsString(),
                        obj.get("enabled").getAsBoolean(),
                        obj.get("client_command").getAsBoolean(),
                        KeybindContext.valueOf(obj.get("context").getAsString())
                ));
            }
            return binds;
        } catch (IOException e) {
            LOGGER.error("[Glaze] Failed to load command keybinds", e);
            return new ArrayList<>();
        }
    }

    public static void save(List<CommandKeybind> binds) {
        try {
            Files.createDirectories(CONFIG_DIR);
            JsonArray array = new JsonArray();
            for (CommandKeybind bind : binds) {
                JsonObject obj = new JsonObject();
                obj.addProperty("name", bind.name);
                obj.addProperty("command", bind.command);
                obj.addProperty("keybind", bind.keybind);
                obj.addProperty("enabled", bind.enabled);
                obj.addProperty("client_command", bind.clientCommand);
                obj.addProperty("context", bind.context.name());
                array.add(obj);
            }
            Files.writeString(PATH, GSON.toJson(array));
        } catch (IOException e) {
            LOGGER.error("[Glaze] Failed to save command keybinds", e);
        }
    }
}