package com.rubyimpala.features.auction.models;

import java.util.List;

public record ShulkerValueResult(
        long totalPrice,      // Sum of all priced items
        boolean hasLoading,  // At least one item is still being fetched
        boolean hasUnpriced, // At least one item has no AH listings
        List<ItemValueEntry> entries
) {}