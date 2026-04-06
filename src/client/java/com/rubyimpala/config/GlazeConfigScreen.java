package com.rubyimpala.config;

import com.rubyimpala.config.categories.*;
import com.rubyimpala.features.chatrules.ChatRuleService;
import com.rubyimpala.features.chatrules.ChatRuleStorage;
import com.rubyimpala.features.chatrules.models.ChatRule;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class GlazeConfigScreen {

    public static Screen build(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Glaze Settings"))
                .save(GlazeSettings::save)

                // ── Auction House ──────────────────────────────
                .category(AuctionHouseCategory.build(parent))

                // ── Price Format ───────────────────────────────
                .category(PriceFormatCategory.build(parent))

                // ── API & Token ────────────────────────────────
                .category(ApiTokenCategory.build(parent))

                // ── Vouch System ───────────────────────────────
                .category(VouchCategory.build(parent))

                // ── Chat Rules ─────────────────────────────────
                .category(ChatRulesCategory.build(parent))

                .build()
                .generateScreen(parent);
    }
}