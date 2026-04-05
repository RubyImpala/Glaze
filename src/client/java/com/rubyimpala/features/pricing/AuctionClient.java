package com.rubyimpala.features.pricing;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rubyimpala.config.GlazeConfig;
import com.rubyimpala.features.pricing.models.PriceEntry;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import static com.rubyimpala.Glaze.LOGGER;
import static com.rubyimpala.config.GlazeConstants.AUCTION_API_URL;

public class AuctionClient {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static JsonElement fetchRaw(String query) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("search", query);
            body.addProperty("sort", "lowest_price");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AUCTION_API_URL))
                    .header("Authorization", GlazeConfig.Auth.getToken())
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return JsonParser.parseString(response.body());
            }

            LOGGER.warn("API returned status: " + response.statusCode());
        } catch (Exception e) {
            LOGGER.error("API Fetch Failed: " + e.getMessage());
        }
        return null;
    }

    public static List<PriceEntry> flattenResults(JsonElement json) {
        List<PriceEntry> ahListings = new ArrayList<>();
        long now = System.currentTimeMillis();

        if (json == null || !json.isJsonObject()) return ahListings;

        JsonObject obj = json.getAsJsonObject();
        if (!obj.has("result")) return ahListings;

        JsonArray results = obj.getAsJsonArray("result");

        for (JsonElement element : results) {
            JsonObject entry = element.getAsJsonObject();
            JsonObject item = entry.getAsJsonObject("item");

            Map<String, Integer> enchantsMap = new HashMap<>();
            if (item.has("enchants") && !item.get("enchants").isJsonNull()) {
                JsonObject enchantsObj = item.getAsJsonObject("enchants");

                if (enchantsObj.has("enchantments") && !enchantsObj.get("enchantments").isJsonNull()) {
                    JsonObject enchantments = enchantsObj.getAsJsonObject("enchantments");

                    if (enchantments.has("levels") && !enchantments.get("levels").isJsonNull()) {
                        JsonObject levelsObj = enchantments.getAsJsonObject("levels");

                        for (Map.Entry<String, JsonElement> en : levelsObj.entrySet()) {
                            enchantsMap.put(en.getKey(), en.getValue().getAsInt());
                        }
                    }
                }
            }

            String id = item.get("id").getAsString();
            long totalPrice = entry.get("price").getAsLong();
            int count = item.get("count").getAsInt();

            ahListings.add(new PriceEntry(id, totalPrice, count, now, enchantsMap));
        }
        return ahListings;
    }
}
