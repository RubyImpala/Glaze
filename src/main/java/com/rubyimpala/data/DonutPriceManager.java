package com.rubyimpala.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DonutPriceManager {
    // Must be static and public for GSON to access it correctly
    public static class PriceEntry {
        public final int price;
        public final long timestamp;

        public PriceEntry(int price, long timestamp) {
            this.price = price;
            this.timestamp = timestamp;
        }
    }

    public static final Logger LOGGER = LoggerFactory.getLogger("GlazeMod");
    private static final String API_URL = "https://api.donutsmp.net/v1/auction/list/1";
    private static final Path CONFIG_DIR = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("glaze");
    private static final Path PROPERTIES_PATH = CONFIG_DIR.resolve("glaze.properties");
    private static final Path CACHE_PATH = CONFIG_DIR.resolve("glaze_prices.json");

    private static String authToken = "";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Map: Item ID -> List of historical PriceEntries
    private static final Map<String, List<PriceEntry>> PRICES = new ConcurrentHashMap<>();
    private static final Set<String> PENDING_REQUESTS = Collections.synchronizedSet(new HashSet<>());

    // Shielding fields
    private static final AtomicInteger REQUEST_COUNT = new AtomicInteger(0);
    private static long blockUntil = 0;

    // Background saver
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    public static void init() {
        loadConfig();
        loadCache();
        fetchFromApi("");

        // Periodically save the JSON to disk every 5 minutes to avoid lag during hovers
        SCHEDULER.scheduleAtFixedRate(DonutPriceManager::saveCache, 5, 5, TimeUnit.MINUTES);
    }

    public static Integer getPrice(String itemId) {
        List<PriceEntry> history = PRICES.get(itemId);
        if (history == null || history.isEmpty()) return null;

        PriceEntry latest = history.get(history.size() - 1);
        long now = System.currentTimeMillis();

        if (latest.price == -1) {
            if (now - latest.timestamp > 60000) return null;
            return -1;
        }

        // 5 minute refresh check
        if (now - latest.timestamp > 300000) {
            if (!PENDING_REQUESTS.contains(itemId)) return null;
        }

        // AVERAGING LOGIC: Use the last 5 valid results from history
        double sum = 0;
        int count = 0;
        for (int i = history.size() - 1; i >= 0 && count < 5; i--) {
            int p = history.get(i).price;
            if (p > 0) {
                sum += p;
                count++;
            }
        }

        return count > 0 ? (int)(sum / count) : latest.price;
    }

    public static void fetchSpecificItem(String itemId) {
        long now = System.currentTimeMillis();
        if (PENDING_REQUESTS.contains(itemId) || now < blockUntil) return;

        // Rate limit shield (250 requests)
        if (REQUEST_COUNT.get() >= 250) {
            LOGGER.warn("[GlazeMod] Hit 250 limit. Cooling down for 60s.");
            blockUntil = now + 60000;
            REQUEST_COUNT.set(0);
            return;
        }

        PENDING_REQUESTS.add(itemId);
        REQUEST_COUNT.incrementAndGet();

        CompletableFuture.runAsync(() -> {
            try {
                fetchFromApi(itemId);
            } finally {
                PENDING_REQUESTS.remove(itemId);
            }
        });
    }

    private static void fetchFromApi(String searchTerm) {
        try {
            URL url = java.net.URI.create(API_URL).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", authToken);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{\"search\": \"" + searchTerm + "\", \"sort\": \"lowest_price\"}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInputString.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {
                try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
                    DonutApiResponse data = GSON.fromJson(reader, DonutApiResponse.class);
                    processResults(searchTerm, data);
                }
            } else if (conn.getResponseCode() == 429) {
                blockUntil = System.currentTimeMillis() + 60000;
                LOGGER.error("[GlazeMod] 429 detected. Blocking for 60s.");
            }
        } catch (Exception e) {
            LOGGER.error("API Fetch Failed: " + e.getMessage());
        }
    }

    private static void processResults(String searchTerm, DonutApiResponse data) {
        long now = System.currentTimeMillis();

        // 1. Update the map for items found in results
        if (data != null && data.result != null) {
            for (DonutApiResponse.AuctionListing listing : data.result) {
                if (listing == null || listing.item == null) continue;
                String id = formatId(listing.item.id);
                if (listing.price > 0) {
                    int p = (int)(listing.price / Math.max(1, (double)listing.item.count));
                    addToHistory(id, p, now);
                }
            }
        }

        // 2. If the specific search returned nothing, mark it as -1
        if (!searchTerm.isEmpty() && (data == null || data.result == null || data.result.isEmpty())) {
            addToHistory(searchTerm, -1, now);
        }
    }

    private static void addToHistory(String id, int price, long timestamp) {
        PRICES.compute(id, (k, list) -> {
            if (list == null) list = new ArrayList<>();
            list.add(new PriceEntry(price, timestamp));
            if (list.size() > 10) list.remove(0); // Only keep last 10 for JSON size
            return list;
        });
    }

    // --- Persistence ---

    private static void loadCache() {
        try {
            if (Files.exists(CACHE_PATH)) {
                try (Reader reader = Files.newBufferedReader(CACHE_PATH)) {
                    java.lang.reflect.Type type = new TypeToken<Map<String, List<PriceEntry>>>(){}.getType();
                    Map<String, List<PriceEntry>> loaded = GSON.fromJson(reader, type);
                    if (loaded != null) PRICES.putAll(loaded);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Cache load failed", e);
        }
    }

    public static void saveCache() {
        try {
            Files.createDirectories(CONFIG_DIR);
            // Snapshot the map to avoid ConcurrentModificationException during saving
            Map<String, List<PriceEntry>> snapshot = new HashMap<>(PRICES);
            try (Writer writer = Files.newBufferedWriter(CACHE_PATH)) {
                GSON.toJson(snapshot, writer);
            }
        } catch (Exception e) {
            LOGGER.error("Cache save failed", e);
        }
    }

    public static void loadConfig() {
        try {
            if (Files.exists(PROPERTIES_PATH)) {
                Properties prop = new Properties();
                prop.load(Files.newInputStream(PROPERTIES_PATH));
                authToken = prop.getProperty("auth_token", "");
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load config", e);
        }
    }

    public static String formatId(String id) {
        if (id == null) return "";
        return id.replace("minecraft:", "").replace("_", " ");
    }
    public static void updateAuthToken(String token) {
        authToken = token;
        saveConfig(); // Persist to glaze.properties immediately
    }
    public static String getAuthToken() {
        return authToken;
    }

    private static void saveConfig() {
        try {
            Files.createDirectories(CONFIG_DIR);
            Properties prop = new Properties();
            prop.setProperty("auth_token", authToken);
            try (OutputStream os = Files.newOutputStream(PROPERTIES_PATH)) {
                prop.store(os, "Glaze Configuration");
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }
}