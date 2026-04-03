package com.rubyimpala.features.auction;

import com.rubyimpala.features.auction.models.PriceEntry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionCache {

    // How long before we consider cached data "old" and re-fetch (5 minutes)
    private static final long TTL_MS = 5 * 60 * 1000;

    // Maps item ID (e.g. "minecraft:diamond") -> its list of AH listings
    private static final Map<String, List<PriceEntry>> cache = new ConcurrentHashMap<>();

    // Maps item ID -> when we last fetched it (milliseconds)
    private static final Map<String, Long> timestamps = new ConcurrentHashMap<>();

    // Item IDs that are currently being fetched in the background
    // This prevents sending 10 requests for the same item if you hover fast
    private static final Set<String> pending = ConcurrentHashMap.newKeySet();

    /** Returns true if we have no data, or if the data is older than 5 minutes */
    public static boolean isStale(String itemId) {
        Long ts = timestamps.get(itemId);
        if (ts == null) return true; // Never fetched
        return (System.currentTimeMillis() - ts) > TTL_MS;
    }

    /** Returns true if a fetch is already running for this item */
    public static boolean isPending(String itemId) {
        return pending.contains(itemId);
    }

    /** Call this right before starting a background fetch */
    public static void markPending(String itemId) {
        pending.add(itemId);
    }

    /** Call this when a fetch succeeds — stores the results and clears the pending flag */
    public static void put(String itemId, List<PriceEntry> entries) {
        cache.put(itemId, entries);
        timestamps.put(itemId, System.currentTimeMillis());
        pending.remove(itemId); // Fetch is done
    }

    /** Call this when a fetch fails — clears pending and stores empty so we wait TTL before retrying */
    public static void markFailed(String itemId) {
        pending.remove(itemId);
        cache.put(itemId, Collections.emptyList());
        timestamps.put(itemId, System.currentTimeMillis());
    }

    /** Returns the cached listings, or an empty list if nothing is cached yet */
    public static List<PriceEntry> get(String itemId) {
        return cache.getOrDefault(itemId, Collections.emptyList());
    }

    /** Returns true if we have EVER successfully fetched this item (even if the result was empty listings) */
    public static boolean hasFetched(String itemId) {
        return cache.containsKey(itemId);
    }
}