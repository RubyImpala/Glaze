package com.rubyimpala.features.vouch;

import com.google.gson.*;
import com.rubyimpala.features.vouch.models.PlayerVouches;
import com.rubyimpala.features.vouch.models.VouchRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static com.rubyimpala.config.GlazeConstants.CONFIG_DIR;

public class VouchStorage implements VouchRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger("GlazeMod");
    private static final Path VOUCHES_PATH = CONFIG_DIR.resolve("glaze_vouches.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // In-memory cache of all vouch data — loaded once on startup
    private final Map<String, PlayerVouches> data = new HashMap<>();

    public VouchStorage() {
        load();
    }

    @Override
    public void addVouch(String targetName, VouchRecord record) {
        // Get or create the PlayerVouches entry for this target
        data.computeIfAbsent(targetName.toLowerCase(), k -> new PlayerVouches(targetName))
                .addVouch(record);
        save();
    }

    @Override
    public PlayerVouches getVouches(String targetName) {
        return data.getOrDefault(targetName.toLowerCase(), new PlayerVouches(targetName));
    }

    @Override
    public long getLastVouchTime(String targetName, String voucher) {
        return getVouches(targetName).lastVouchTimeBy(voucher);
    }

    // Loads vouch data from glaze_vouches.json into memory
    private void load() {
        if (!Files.exists(VOUCHES_PATH)) return;

        try {
            String raw = Files.readString(VOUCHES_PATH);
            JsonObject root = JsonParser.parseString(raw).getAsJsonObject();

            for (Map.Entry<String, JsonElement> playerEntry : root.entrySet()) {
                String targetName = playerEntry.getKey();
                PlayerVouches playerVouches = new PlayerVouches(targetName);
                JsonArray vouchArray = playerEntry.getValue().getAsJsonArray();

                for (JsonElement vouchElement : vouchArray) {
                    JsonObject vouchObj = vouchElement.getAsJsonObject();
                    String voucher = vouchObj.get("voucher").getAsString();
                    long timestamp = vouchObj.get("timestamp").getAsLong();
                    playerVouches.addVouch(new VouchRecord(voucher, timestamp));
                }

                data.put(targetName.toLowerCase(), playerVouches);
            }

        } catch (IOException e) {
            LOGGER.error("[Glaze] Failed to load vouch data", e);
        }
    }

    // Saves all in-memory vouch data back to glaze_vouches.json
    private void save() {
        try {
            Files.createDirectories(CONFIG_DIR);
            JsonObject root = new JsonObject();

            for (Map.Entry<String, PlayerVouches> entry : data.entrySet()) {
                JsonArray vouchArray = new JsonArray();

                for (VouchRecord record : entry.getValue().getVouches()) {
                    JsonObject vouchObj = new JsonObject();
                    vouchObj.addProperty("voucher", record.voucher());
                    vouchObj.addProperty("timestamp", record.timestamp());
                    vouchArray.add(vouchObj);
                }

                root.add(entry.getKey(), vouchArray);
            }

            Files.writeString(VOUCHES_PATH, GSON.toJson(root));

        } catch (IOException e) {
            LOGGER.error("[Glaze] Failed to save vouch data", e);
        }
    }

    @Override
    public boolean removeVouch(String targetName, String voucher) {
        PlayerVouches vouches = data.get(targetName.toLowerCase());
        if (vouches == null) return false;
        boolean removed = vouches.removeVouchesBy(voucher);
        if (removed) save();
        return removed;
    }
}