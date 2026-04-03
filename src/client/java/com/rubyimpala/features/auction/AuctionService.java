package com.rubyimpala.features.auction;

import com.rubyimpala.config.GlazeConfig;
import com.rubyimpala.features.auction.models.PriceEntry;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.rubyimpala.config.GlazeConfig.LOGGER;

public class AuctionService {

    // A single background thread dedicated to API requests.
    // "daemon = true" means it won't stop the game from closing.
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "glaze-auction-fetch");
        t.setDaemon(true);
        return t;
    });

    /**
     * The main method you'll call from the tooltip.
     *
     * Returns the lowest AH price for this item if we have it cached.
     * If the cache is stale, it kicks off a background fetch automatically.
     *
     * @param itemId  e.g. "minecraft:diamond"
     * @return  the lowest total listing price, or empty if loading / no listings / no API key
     */
    public static Optional<Integer> getLowestPrice(String itemId) {
        // Don't do anything if the user hasn't set their API key yet
        if (GlazeConfig.Auth.getToken().isEmpty()) {
            return Optional.empty();
        }

        // If data is stale and we're not already fetching, kick off a background fetch
        if (AuctionCache.isStale(itemId) && !AuctionCache.isPending(itemId)) {
            fetchAsync(itemId);
        }

        // If we have never fetched OR the cache is stale (mid-refresh), show nothing yet
        if (!AuctionCache.hasFetched(itemId)) {
            return Optional.empty();
        }

        // Find the lowest priced listing that exactly matches this item ID
        List<PriceEntry> entries = AuctionCache.get(itemId);
        return entries.stream()
                .filter(e -> e.id().equals(itemId))
                .min(Comparator.comparingInt(PriceEntry::totalPrice))
                .map(PriceEntry::getUnitPrice);
    }

    /** Returns true if a fetch is currently running for this item (used to show "Loading...") */
    public static boolean isLoading(String itemId) {
        return AuctionCache.isPending(itemId);
    }

    /** Returns true if we've fetched before but found zero listings */
    public static boolean hasNoListings(String itemId) {
        return AuctionCache.hasFetched(itemId) && AuctionCache.get(itemId).isEmpty();
    }

    // Runs the API fetch on the background thread
    private static void fetchAsync(String itemId) {
        AuctionCache.markPending(itemId);

        EXECUTOR.submit(() -> {
            try {
                // Convert "minecraft:diamond" -> "diamond" for the search query
                // Also replace underscores: "iron_sword" -> "iron sword"
                String searchTerm = itemId.contains(":")
                        ? itemId.split(":")[1].replace("_", " ")
                        : itemId.replace("_", " ");

                var json = AuctionClient.fetchRaw(searchTerm);
                var allEntries = AuctionClient.flattenResults(json);

                // The API search returns anything matching the keyword,
                // so we filter down to only exact item ID matches
                List<PriceEntry> filtered = allEntries.stream()
                        .filter(e -> e.id().equals(itemId))
                        .toList();

                AuctionCache.put(itemId, filtered);
                LOGGER.info("[Glaze] Cached {} listing(s) for {}", filtered.size(), itemId);

            } catch (Exception e) {
                LOGGER.error("[Glaze] Failed to fetch prices for {}: {}", itemId, e.getMessage());
                AuctionCache.markFailed(itemId);
            }
        });
    }
}