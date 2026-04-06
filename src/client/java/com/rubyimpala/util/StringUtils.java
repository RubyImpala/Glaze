package com.rubyimpala.util;

import java.util.HashMap;
import java.util.Map;

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

    // Enchant definitions
    // Base enchants
    public static final String UNB3 = "unbreaking 3";
    public static final String MENDING = "mending";
    public static final String SILK_TOUCH = "silk touch";
    public static final String FORTUNE3 = "fortune 3";
    public static final String EFFICIENCY5 = "efficiency 5";
    public static final String PROT4 = "protection 4";
    public static final String DEPTH_STRIDER3 = "depth strider 3";
    public static final String FEATHER_FALLING4 = "feather falling 4";
    public static final String SOUL_SPEED3 = "soul speed 3";
    public static final String SHARPNESS5 = "sharpness 5";
    public static final String LOOTING3 = "looting 3";
    public static final String SWEEPING3 = "sweeping edge 3";
    public static final String POWER5 = "power 5";
    public static final String FLAME = "flame";
    public static final String INFINITY = "infinity";
    public static final String THORNS3 = "thorns 3";
    public static final String AQUA_AFF3 = "aqua affinity";
    public static final String RESPIRATION3 = "respiration 3";
    public static final String BLAST_PROT4 = "blast protection 4";
    public static final String FIRE_PROT4 = "fire protection 4";
    public static final String PROJ_PROT4 = "projectile protection 4";
    public static final String SWIFT_SNEAK3 = "swift sneak 3";

    // Combined enchant groups
    public static final String GENERAL = enchants(UNB3, MENDING);
    public static final String TOOLS_ENCHS = enchants(EFFICIENCY5, UNB3, MENDING);
    public static final String SILK = enchants(EFFICIENCY5, SILK_TOUCH, UNB3, MENDING);
    public static final String FORTUNE = enchants(EFFICIENCY5, FORTUNE3, UNB3, MENDING);
    public static final String SWORD_ENCHS = enchants(SHARPNESS5, LOOTING3, SWEEPING3, UNB3, MENDING);
    public static final String BOW_ENCHS = enchants(POWER5, FLAME, INFINITY, UNB3);

    // Armor pieces
    public static final String HELMET_ENCHS = enchants(PROT4, AQUA_AFF3, RESPIRATION3, UNB3, MENDING);
    public static final String CHESTPLATE_ENCHS = enchants(PROT4, UNB3, MENDING);
    public static final String LEGGINGS_ENCHS = enchants(PROT4, SWIFT_SNEAK3, UNB3, MENDING);
    public static final String BOOTS_ENCHS = enchants(PROT4, DEPTH_STRIDER3, FEATHER_FALLING4, UNB3, MENDING);


    // Materials
    public static final String NETHERITE = "netherite";
    public static final String DIAMOND = "diamond";
    public static final String IRON = "iron";
    public static final String GOLD = "gold";
    public static final String CHAIN = "chainmail";
    public static final String STONE = "stone";
    public static final String WOOD = "wooden";
    public static final String LEATHER = "leather";

    // Tool Types
    public static final String SWORD = "sword";
    public static final String PICKAXE = "pickaxe";
    public static final String AXE = "axe";
    public static final String SHOVEL = "shovel";
    public static final String HOE = "hoe";
    public static final String SHEARS = "shears";
    public static final String BRUSH = "brush";

    // Armor Types
    public static final String HELMET = "helmet";
    public static final String CHESTPLATE = "chestplate";
    public static final String LEGGINGS = "leggings";
    public static final String BOOTS = "boots";

    // Utility Types
    public static final String ELYTRA = "elytra";
    public static final String SHIELD = "shield";
    public static final String BOW = "bow";
    public static final String CROSSBOW = "crossbow";
    public static final String TRIDENT = "trident";
    public static final String MACE = "mace";

    // Materials Array
    public static final String[] MATERIALS = {
            NETHERITE, DIAMOND, IRON, GOLD, CHAIN, STONE, WOOD, LEATHER
    };

    // All Tools Array
    public static final String[] TOOLS = {
            SWORD, PICKAXE, AXE, SHOVEL, HOE, SHEARS, BRUSH
    };

    // All Armor Array
    public static final String[] ARMOR = {
            HELMET, CHESTPLATE, LEGGINGS, BOOTS
    };

    // Combat & Ranged Array
    public static final String[] COMBAT = {
            BOW, CROSSBOW, TRIDENT, MACE, SHIELD, ELYTRA
    };

    // Master Array (Everything)
    public static final String[] ALL_ITEMS = {
            SWORD, PICKAXE, AXE, SHOVEL, HOE, SHEARS, BRUSH,
            HELMET, CHESTPLATE, LEGGINGS, BOOTS,
            ELYTRA, SHIELD, BOW, CROSSBOW, TRIDENT, MACE
    };

    public static final Map<String, String> ITEM_ENCHANTS = new HashMap<>();

    static {
        // Tools
        ITEM_ENCHANTS.put(PICKAXE, FORTUNE); // Defaulting to Fortune, or use SILK
        ITEM_ENCHANTS.put(SHOVEL, SILK);
        ITEM_ENCHANTS.put(AXE, TOOLS_ENCHS);
        ITEM_ENCHANTS.put(HOE, TOOLS_ENCHS);

        // Combat
        ITEM_ENCHANTS.put(SWORD, SWORD_ENCHS);
        ITEM_ENCHANTS.put(BOW, BOW_ENCHS);

        // Armor
        ITEM_ENCHANTS.put(HELMET, HELMET_ENCHS);
        ITEM_ENCHANTS.put(CHESTPLATE, CHESTPLATE_ENCHS);
        ITEM_ENCHANTS.put(LEGGINGS, LEGGINGS_ENCHS);
        ITEM_ENCHANTS.put(BOOTS, BOOTS_ENCHS);
    }

    private static String enchants(String... enchants) {
        return " " + String.join(" ", enchants);
    }
}