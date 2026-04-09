package com.rubyimpala.util;

import java.nio.file.Path;

public record GlazeConstants() {

    public static final String MOD_ID = "glaze";

    public static final String AUCTION_API_URL = "https://api.donutsmp.net/v1/auction/list/1";

//    public static final Path ROOT_DIR = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);

    public static final Path CONFIG_DIR = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);

    public static String SETTINGS_FILENAME = "glaze_settings.json5";
}