package com.rubyimpala.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Properties;

import static com.rubyimpala.util.GlazeConstants.CONFIG_DIR;
import static com.rubyimpala.util.GlazeConstants.CONFIG_PATH_SETTINGS;

public class GlazeSettings {

    private static final Logger LOGGER = LoggerFactory.getLogger("GlazeMod");

    // --- Price Tooltips ---
    public static boolean showPriceTooltips = true;
    public static boolean showShulkerValuation = true;
    public static boolean showReloadHint = true;
    public static boolean showStackPriceHint = true;
    public static boolean showShulkerBreakdownHint = true;
    public static boolean showDollarSign = true;
    public static PriceFormat priceFormat = PriceFormat.FORMATTED;
    public static boolean showRedirectNotification = true;

    // --- Cache ---
    public static int cacheTtlMinutes = 5;

    // --- Vouch ---
    public static boolean vouchSystemEnabled = true;

    // --- API ---
    public static boolean autoTokenDetection = true;

    public static void load() {
        if (!Files.exists(CONFIG_PATH_SETTINGS)) {
            save(); // Write defaults on first launch
            return;
        }
        try (var is = Files.newInputStream(CONFIG_PATH_SETTINGS)) {
            Properties prop = new Properties();
            prop.load(is);

            showPriceTooltips       = bool(prop, "show_price_tooltips", true);
            showShulkerValuation    = bool(prop, "show_shulker_valuation", true);
            showReloadHint          = bool(prop, "show_reload_hint", true);
            showStackPriceHint      = bool(prop, "show_stack_price_hint", true);
            showShulkerBreakdownHint= bool(prop, "show_shulker_breakdown_hint", true);
            showDollarSign          = bool(prop, "show_dollar_sign", true);
            vouchSystemEnabled      = bool(prop, "vouch_system_enabled", true);
            autoTokenDetection      = bool(prop, "auto_token_detection", true);
            cacheTtlMinutes         = integer(prop, "cache_ttl_minutes", 5, 1, 10);
            priceFormat             = parseEnum(prop, "price_format", PriceFormat.FORMATTED);
            showRedirectNotification = bool(prop, "show_redirect_notification", true);

        } catch (IOException e) {
            LOGGER.error("[Glaze] Failed to load settings", e);
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_DIR);
            Properties prop = new Properties();

            prop.setProperty("show_price_tooltips",         String.valueOf(showPriceTooltips));
            prop.setProperty("show_shulker_valuation",      String.valueOf(showShulkerValuation));
            prop.setProperty("show_reload_hint",            String.valueOf(showReloadHint));
            prop.setProperty("show_stack_price_hint",       String.valueOf(showStackPriceHint));
            prop.setProperty("show_shulker_breakdown_hint", String.valueOf(showShulkerBreakdownHint));
            prop.setProperty("show_dollar_sign",            String.valueOf(showDollarSign));
            prop.setProperty("vouch_system_enabled",        String.valueOf(vouchSystemEnabled));
            prop.setProperty("auto_token_detection",        String.valueOf(autoTokenDetection));
            prop.setProperty("cache_ttl_minutes",           String.valueOf(cacheTtlMinutes));
            prop.setProperty("price_format",                priceFormat.name());
            prop.setProperty("show_redirect_notification", String.valueOf(showRedirectNotification));

            try (OutputStream os = Files.newOutputStream(CONFIG_PATH_SETTINGS)) {
                prop.store(os, "Glaze Settings");
            }
        } catch (IOException e) {
            LOGGER.error("[Glaze] Failed to save settings", e);
        }
    }

    // --- Helpers ---
    private static boolean bool(Properties p, String key, boolean def) {
        return Boolean.parseBoolean(p.getProperty(key, String.valueOf(def)));
    }

    private static int integer(Properties p, String key, int def, int min, int max) {
        try {
            int val = Integer.parseInt(p.getProperty(key, String.valueOf(def)));
            return Math.max(min, Math.min(max, val));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static PriceFormat parseEnum(Properties p, String key, PriceFormat def) {
        try {
            return PriceFormat.valueOf(p.getProperty(key, def.name()));
        } catch (IllegalArgumentException e) {
            return def;
        }
    }
}