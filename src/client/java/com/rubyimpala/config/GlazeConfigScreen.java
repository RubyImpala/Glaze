package com.rubyimpala.config;

import com.rubyimpala.config.categories.*;
import dev.isxander.yacl3.api.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GlazeConfigScreen {

    public static Screen build(Screen parent) {
        var configScreen = YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Glaze Settings"))
                .save(GlazeSettings::save);

        // ── Auction House ─────────────────────────────────────────
        configScreen.category(AuctionHouseCategory.build());

        // ── API & Token ───────────────────────────────────────────
        configScreen.category(ApiTokenCategory.build());

        // ── Vouch System ──────────────────────────────────────────
        configScreen.category(VouchCategory.build());

        // ── Chat Rules ────────────────────────────────────────────
        configScreen.category(ChatRulesCategory.build(parent));

        // ── Command Keybinds ──────────────────────────────────────
        configScreen.category(CommandKeybindsCategory.build(parent));

        return configScreen.build().generateScreen(parent);
    }
}