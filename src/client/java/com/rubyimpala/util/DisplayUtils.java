package com.rubyimpala.util;

import com.rubyimpala.config.GlazeSettings;

public class DisplayUtils {

    /**
     * Formats a price integer into a readable string with a $ prefix.
     * Examples:
     *   999        -> "$999"
     *   1000       -> "$1K"
     *   15500      -> "$15.5K"
     *   1000000    -> "$1M"
     *   2500000    -> "$2.5M"
     *   1000000000 -> "$1B"
     */
    public static String formatPrice(long price) {
        String formatted = switch (GlazeSettings.priceFormat) {
            case FORMATTED -> formatSuffixed(price);
            case RAW ->       String.valueOf(price);
            case COMMA ->     String.format("%,d", price);
        };
        return GlazeSettings.showDollarSign ? "$" + formatted : formatted;
    }

    private static String formatSuffixed(long price) {
        if (price >= 1_000_000_000_000L)
            return formatDecimal(price / 1_000_000_000_000.0) + "T";
        if (price >= 1_000_000_000L)
            return formatDecimal(price / 1_000_000_000.0) + "B";
        if (price >= 1_000_000)
            return formatDecimal(price / 1_000_000.0) + "M";
        if (price >= 1_000)
            return formatDecimal(price / 1_000.0) + "K";
        return String.valueOf(price);
    }

    // Strips unnecessary trailing zeros: 1.0 -> "1", 1.5 -> "1.5"
    private static String formatDecimal(double value) {
        if (value == Math.floor(value)) {
            return String.valueOf((int) value);
        }
        // Round to 1 decimal place
        return String.valueOf(Math.round(value * 10) / 10.0);
    }
}