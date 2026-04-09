package com.rubyimpala.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

import static com.rubyimpala.util.GlazeConstants.MOD_ID;
import static com.rubyimpala.util.GlazeConstants.SETTINGS_FILENAME;

public class GlazeSettings {

    public static ConfigClassHandler<GlazeSettings> HANDLER = ConfigClassHandler.createBuilder(GlazeSettings.class)
            .id(Identifier.fromNamespaceAndPath(MOD_ID, SETTINGS_FILENAME))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve(SETTINGS_FILENAME))
                    .setJson5(true)
                    .build())
            .build();

    public static GlazeSettings CONFIG() {
        return HANDLER.instance();
    }

    public static GlazeSettings getDefaults() {
        return  HANDLER.defaults();
    }

    public static void save() {
        HANDLER.save();
    }

    public static void load() {
        HANDLER.load();
    }


    // --- Price Tooltips ---
    @SerialEntry
    public boolean showPriceTooltips = true;

    @SerialEntry
    public boolean showShulkerValuation = true;

    @SerialEntry
    public boolean showReloadHint = true;

    @SerialEntry
    public boolean showStackPriceHint = true;

    @SerialEntry
    public boolean showShulkerBreakdownHint = true;

    @SerialEntry
    public boolean showDollarSign = true;
    @SerialEntry
    public PriceFormat priceFormat = PriceFormat.FORMATTED;

    @SerialEntry
    public boolean showRedirectNotification = true;

    // --- Cache ---
    @SerialEntry
    public int cacheTtlMinutes = 5;

    // --- Vouch ---
    @SerialEntry
    public boolean vouchSystemEnabled = true;

    // --- API ---
    @SerialEntry
    public boolean autoTokenDetection = true;

    @SerialEntry
    public String apiToken = "";
}