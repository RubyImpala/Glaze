package com.rubyimpala.features.auction.models;

public record ItemValueEntry(
        String displayName,
        int count,
        long unitPrice, // -1 if unpriced or loading
        boolean unpriced,
        boolean loading
) {
    // Total value of this stack (e.g. 64 diamonds at $100 each = $6400)
    public long stackTotal() {
        return unitPrice * count;
    }
}