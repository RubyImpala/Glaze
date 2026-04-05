package com.rubyimpala.features.pricing;

import com.rubyimpala.config.GlazeConfig;
import com.rubyimpala.features.pricing.models.ItemValueEntry;
import com.rubyimpala.features.pricing.models.PriceEntry;
import com.rubyimpala.features.pricing.models.ShulkerValueResult;
import com.rubyimpala.util.StringUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

public class AuctionService {

    private static final Logger LOGGER = LoggerFactory.getLogger("GlazeMod");

    // A single background thread dedicated to API requests.
    // "daemon = true" means it won't stop the game from closing.
    private static final ExecutorService PRIORITY_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "glaze-auction-priority");
        t.setDaemon(true);
        return t;
    });

    private static final ExecutorService BACKGROUND_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "glaze-auction-background");
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
    public static Optional<Long> getLowestPrice(String itemId) {
        // Don't do anything if the user hasn't set their API key yet
        if (GlazeConfig.Auth.getToken().isEmpty()) {
            return Optional.empty();
        }

        // If data is stale and we're not already fetching, kick off a background fetch
        if (AuctionCache.isStale(itemId) && !AuctionCache.isPending(itemId)) {
            fetchPriority(itemId);
        }

        // If we have never fetched OR the cache is stale (mid-refresh), show nothing yet
        if (!AuctionCache.hasFetched(itemId)) {
            return Optional.empty();
        }

        // Find the lowest priced listing that exactly matches this item ID
        List<PriceEntry> entries = AuctionCache.get(itemId);
        return entries.stream()
                .filter(e -> e.id().equals(itemId))
                .min(Comparator.comparingLong(PriceEntry::getUnitPrice))
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

    // Hovered items — goes to priority executor
    public static void fetchPriority(String itemId) {
        AuctionCache.markPending(itemId);
        PRIORITY_EXECUTOR.submit(() -> doFetch(itemId));
    }

    // Background fetches — goes to background executor
    private static void fetchAsync(String itemId) {
        AuctionCache.markPending(itemId);
        BACKGROUND_EXECUTOR.submit(() -> doFetch(itemId));
    }

    // The actual fetch logic, shared by both:
    private static void doFetch(String itemId) {
        try {
            String searchTerm = StringUtils.idToSearchTerm(itemId);

            var json = AuctionClient.fetchRaw(searchTerm);
            var allEntries = AuctionClient.flattenResults(json);

            List<PriceEntry> filtered = allEntries.stream()
                    .filter(e -> e.id().equals(itemId))
                    .toList();

            AuctionCache.put(itemId, filtered);
            LOGGER.info("[Glaze] Cached {} listing(s) for {}", filtered.size(), itemId);

        } catch (Exception e) {
            LOGGER.error("[Glaze] Failed to fetch prices for {}: {}", itemId, e.getMessage());
            AuctionCache.markFailed(itemId);
        }
    }

    /**
     * Calculates the total AH value of all items inside a shulker box.
     * Returns null if the stack isn't a shulker or has no contents component.
     */
    public static ShulkerValueResult getShulkerBreakdown(ItemStack stack) {
        // Get the items stored inside the shulker
        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
        if (contents == null) return null;

        List<ItemValueEntry> entries = new ArrayList<>();
        long total = 0;
        boolean hasLoading = false;
        boolean hasUnpriced = false;

        // Price the shulker box itself
        String shulkerId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        Optional<Long> shulkerPrice = getLowestPrice(shulkerId);
        if (!AuctionCache.hasFetched(shulkerId) || isLoading(shulkerId)) {
            hasLoading = true;
        } else if (shulkerPrice.isPresent()) {
            total += shulkerPrice.get();
        }

        for (ItemStack item : contents.nonEmptyItemCopyStream().toList()) {
            String itemId = BuiltInRegistries.ITEM.getKey(item.getItem()).toString();
            String displayName = item.getHoverName().getString();
            int count = item.getCount();

            // This triggers a background fetch if the item isn't cached yet
            Optional<Long> price = getLowestPrice(itemId);

            if (!AuctionCache.hasFetched(itemId) || isLoading(itemId)) {
                // Fetch is still running
                entries.add(new ItemValueEntry(displayName, count, -1, false, true));
                hasLoading = true;
            } else if (price.isEmpty()) {
                // Fetched but no listings on the AH
                entries.add(new ItemValueEntry(displayName, count, -1, true, false));
                hasUnpriced = true;
            } else {
                Long unitPrice = price.get();
                entries.add(new ItemValueEntry(displayName, count, unitPrice, false, false));
                total += unitPrice * count;
            }
        }

        return new ShulkerValueResult(total, hasLoading, hasUnpriced, entries);
    }
}