package com.rubyimpala.util;

public class StringUtils {

    /**
     * Converts a Minecraft item ID to a human-readable search term.
     * "minecraft:diamond_sword" -> "diamond sword"
     * "minecraft:oak_log"       -> "oak log"
     */
    public static String idToSearchTerm(String id) {
        return id.contains(":")
                ? id.split(":")[1].replace("_", " ")
                : id.replace("_", " ");
    }
}