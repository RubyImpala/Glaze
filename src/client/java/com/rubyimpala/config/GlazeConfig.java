package com.rubyimpala.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Properties;

import static com.rubyimpala.config.GlazeConstants.CONFIG_DIR;
import static com.rubyimpala.config.GlazeConstants.CONFIG_PATH;

public class GlazeConfig {

    public static final Logger LOGGER = LoggerFactory.getLogger("GlazeMod");


    public static void load() {
        // Load token
        if (Files.exists(CONFIG_PATH)) {
            try (var is = Files.newInputStream(CONFIG_PATH)) {
                Properties prop = new Properties();
                prop.load(is);
                Auth.setToken(prop.getProperty("auth_token", ""));
            } catch (IOException e) {
                LOGGER.error("[Glaze] Failed to load token config", e);
            }
        }
        // Load settings
        GlazeSettings.load();
    }

    private static void save() {
        try {
            Files.createDirectories(CONFIG_DIR);
            Properties prop = new Properties();
            prop.setProperty("auth_token", Auth.getToken());
            try (OutputStream os = Files.newOutputStream(CONFIG_PATH)) {
                prop.store(os, "Glaze Configuration");
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save config", e);
        }
    }
    public static class Auth{
        public static String token = "";

        public static String getToken() {
            return token;
        }
        public static void setToken(String newToken){
            token = newToken;
        }

        public static void updateToken(String newToken) {
            setToken(newToken);
            save(); // Persist to glaze.properties immediately
        }
    }
}
