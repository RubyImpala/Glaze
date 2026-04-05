package com.rubyimpala.features.ahsearch;

import com.rubyimpala.util.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.core.component.DataComponents;

public class AhSearchService {

    /**
     * Sends /ah <searchTerm> in chat based on the hovered item.
     * If withEnchants is true, appends all enchant names to the search.
     */
    public static void searchItem(ItemStack stack, boolean withEnchants) {
        String searchTerm = buildSearchTerm(stack, withEnchants);
        Minecraft minecraft = Minecraft.getInstance();

        minecraft.player.sendSystemMessage(
                Component.literal("§6[Glaze] §aSearching Auction House for §e" + searchTerm + "§a...")
        );

        minecraft.player.connection.sendCommand("ah " + searchTerm);
    }

    private static String buildSearchTerm(ItemStack stack, boolean withEnchants) {
        // Convert item ID to readable name e.g. "minecraft:diamond_sword" -> "diamond sword"
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        String baseName = StringUtils.idToSearchTerm(itemId);


        if (!withEnchants) return baseName;

        // Append enchant names if requested
        ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
        if (enchantments == null || enchantments.isEmpty()) return baseName;

        StringBuilder sb = new StringBuilder(baseName);
        enchantments.keySet().forEach(enchantment -> {
            String enchantName = enchantment.unwrapKey()
                    .map(key -> key.identifier().getPath().replace("_", " "))
                    .orElse("");
            int level = enchantments.getLevel(enchantment);
            if (!enchantName.isEmpty()) {
                sb.append(" ").append(enchantName);
                if (level > 1) sb.append(" ").append(level); // only append level if > 1
            }
        });

        return sb.toString();
    }
}