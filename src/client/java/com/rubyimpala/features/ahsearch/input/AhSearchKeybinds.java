package com.rubyimpala.features.ahsearch.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.rubyimpala.features.ahsearch.AhSearchService;
import com.rubyimpala.mixin.client.AbstractContainerScreenAccessor;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class AhSearchKeybinds {

    // Create the category object — shows as "Glaze" in Controls screen
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("glaze", "glaze")
    );

    public static KeyMapping searchAh;
    public static KeyMapping searchAhEnchants;

    public static void register() {
        searchAh = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.glaze.search_ah",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_Y,
                CATEGORY
        ));

        searchAhEnchants = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.glaze.search_ah_enchants",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_U,
                CATEGORY
        ));
    }

    private static ItemStack getHoveredItem(Minecraft client) {
        if (client.screen instanceof AbstractContainerScreen<?> screen) {
            var hoveredSlot = ((AbstractContainerScreenAccessor) screen).getHoveredSlot();
            if (hoveredSlot != null) return hoveredSlot.getItem();
        }

        // CreativeModeInventoryScreen is separate in 26.1
        if (client.screen instanceof net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen creativeScreen) {
            var hoveredSlot = ((AbstractContainerScreenAccessor) creativeScreen).getHoveredSlot();
            if (hoveredSlot != null) return hoveredSlot.getItem();
        }

        return null;
    }

    public static void onKeyPressed(KeyEvent event) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        ItemStack hovered = getHoveredItem(client);
        if (hovered == null || hovered.isEmpty()) return;

        if (searchAh.matches(event)) {
            AhSearchService.searchItem(hovered, false);
        } else if (searchAhEnchants.matches(event)) {
            AhSearchService.searchItem(hovered, true);
        }
    }
}