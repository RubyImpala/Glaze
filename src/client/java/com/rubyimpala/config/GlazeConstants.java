package com.rubyimpala.config;

import java.nio.file.Path;

public record GlazeConstants() {

    public static final String AUCTION_API_URL = "https://api.donutsmp.net/v1/auction/list/1";

    public static final Path ROOT_DIR = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("glaze");

    public static final Path CONFIG_DIR = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("glaze");
    public static final Path CONFIG_PATH = CONFIG_DIR.resolve("glaze.properties");

    public static final Path CACHE_PATH = CONFIG_DIR.resolve("glaze_prices.json");

    public static final Path CONFIG_PATH_SETTINGS = CONFIG_DIR.resolve("glaze_config.properties");
}