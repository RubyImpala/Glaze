package com.rubyimpala.data;

import com.google.gson.Gson;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;


public class DonutPriceManager {
    private static class PriceEntry {
        public final int price;
        public final long timestamp;

        public PriceEntry(int price, long timestamp) {
            this.price = price;
            this.timestamp = timestamp;
        }
    }

    public static final Logger LOGGER = LoggerFactory.getLogger("GlazeMod");

    private static final Map<String, PriceEntry> PRICES = new HashMap<>();
    private static final String API_URL = "https://api.donutsmp.net/v1/auction/list/1";
    private static String authToken = "";
    private static final Path CONFIG_PATH = Paths.get("config", "glaze.properties");

    public static void init() {
        loadConfig();
        fetchFromApi("");
    }

    public static Integer getPrice(String itemId) {
        PriceEntry entry = PRICES.get(itemId);
        if (entry == null) return null;

        long currentTime = System.currentTimeMillis();

        if (entry.price == -1) {
            long oneMinute = 60 * 1000;
            if (currentTime - entry.timestamp > oneMinute) {
                return null; // One minute passed, try searching again
            }
            return -1; // Still in the 1-minute timeout
        }

        if (currentTime - entry.timestamp > (5 * 60 * 1000)) {
            // Only return null if we aren't already trying to update it
            if (!PENDING_REQUESTS.contains(itemId)) {
                return null;
            }
        }
        return entry.price;
    }

    private static void fetchFromApi(String searchTerm) {
        try {
            // Use URI to handle the string, then convert to URL
            URL url = java.net.URI.create(API_URL).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", authToken);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("accept", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{\"search\": \"" + searchTerm + "\", \"sort\": \"lowest_price, recently_listed\"}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            LOGGER.info("[DonutDebug] Searching for: {}", searchTerm);

            if (conn.getResponseCode() == 200) {
                try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
                    DonutApiResponse data = new Gson().fromJson(reader, DonutApiResponse.class);

                    // CHECK: Did the API return an empty result for our specific search?
                    if (data == null || data.result == null || data.result.isEmpty()) {
                        if (!searchTerm.isEmpty()) {
                            // Set the 1-minute timeout by putting -1 in the map
                            PRICES.put(searchTerm, new PriceEntry(-1, System.currentTimeMillis()));
                            LOGGER.info("[DonutDebug] No listings for '{}'. Cooldown started.", searchTerm);
                        }
                    } else {
                        updateMap(data);
                        LOGGER.info("[DonutDebug] Successfully updated prices for search: {}", searchTerm);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateMap(DonutApiResponse data) {
        if (data == null || data.result == null || data.result.isEmpty()) return;

        Map<String, List<Double>> grouped = new HashMap<>();
        for (DonutApiResponse.AuctionListing listing : data.result) {
            if (listing == null || listing.item == null || listing.item.id == null) continue;

            String id = formatId(listing.item.id);

            // Only collect data if the price and count are valid
            if (listing.price > 0) {
                double count = (listing.item.count > 0) ? listing.item.count : 1;
                double pricePerItem = listing.price / count;
                grouped.computeIfAbsent(id, k -> new ArrayList<>()).add(pricePerItem);
            }
        }

        long now = System.currentTimeMillis();
        for (Map.Entry<String, List<Double>> entry : grouped.entrySet()) {
            List<Double> prices = entry.getValue();
            if (prices.isEmpty()) continue;

            Collections.sort(prices);
            int count = Math.min(prices.size(), 5);
            double sum = 0;
            for (int i = 0; i < count; i++) {
                sum += prices.get(i);
            }
            int average = (int) (sum / count);

            // Save the valid price
            PRICES.put(entry.getKey(), new PriceEntry(average, now));
        }
    }

    private static final Set<String> PENDING_REQUESTS = Collections.synchronizedSet(new HashSet<>());

    public static void fetchSpecificItem(String itemId) {
        if (PENDING_REQUESTS.contains(itemId)) return;

        // FIX: Check if we already have a result (Price or -1) that isn't expired
        if (getPrice(itemId) != null) return;


        CompletableFuture.runAsync(() -> {
            PENDING_REQUESTS.add(itemId);
            try {
                // We pass the ID so fetchFromApi knows what to mark as -1 if it fails
                fetchFromApi(itemId);
            } finally {
                PENDING_REQUESTS.remove(itemId);
            }
        });
    }

    public static void loadConfig() {
        try {
            if (Files.notExists(CONFIG_PATH)) {
                Files.createDirectories(CONFIG_PATH.getParent());
                // Create a template file so you know where to put the token
                Files.writeString(CONFIG_PATH, "auth_token=YOUR_TOKEN_HERE");
                LOGGER.warn("Config file created at {}. Please add your token!", CONFIG_PATH);
                return;
            }

            Properties prop = new Properties();
            prop.load(Files.newInputStream(CONFIG_PATH));
            authToken = prop.getProperty("auth_token", "");

        } catch (IOException e) {
            LOGGER.error("Failed to load config", e);
        }
    }
    public static String formatId(String id) {
        if (id == null) return "";

        // 1. Remove the namespace
        String term = id.replace("minecraft:", "");

        // 2. Replace underscores with spaces
        term = term.replace("_", " ");

        return term;
    }
}