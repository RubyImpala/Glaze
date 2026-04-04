package com.rubyimpala.features.auction;

public class AuctionHoverState {
    // The item ID the player is currently hovering over.
    // Written by TooltipEvents, read by InputEvents.
    public static String lastHoveredItemId = null;
}