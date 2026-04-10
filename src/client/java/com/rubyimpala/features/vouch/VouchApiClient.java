package com.rubyimpala.features.vouch;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rubyimpala.features.vouch.models.PlayerVouches;
import com.rubyimpala.features.vouch.models.VouchRecord;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.rubyimpala.GlazeClient.LOGGER;

public class VouchApiClient implements VouchRepository {

    private static final String BASE_URL = "https://glaze-vouches-api.rubyimpala.workers.dev";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    // ── GET /vouches/{playerUuid} ──────────────────────────────────────────
    @Override
    public PlayerVouches getVouches(String playerName) {
        // Resolve UUID from tab list
        UUID uuid = resolveUuid(playerName);
        if (uuid == null) return new PlayerVouches(playerName);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/vouches/" + uuid))
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                LOGGER.warn("[Glaze] Failed to get vouches: {}", response.statusCode());
                return new PlayerVouches(playerName);
            }

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            PlayerVouches vouches = new PlayerVouches(playerName);
            JsonArray vouchArray = json.getAsJsonArray("vouches");

            for (JsonElement element : vouchArray) {
                JsonObject obj = element.getAsJsonObject();
                vouches.addVouch(new VouchRecord(
                        obj.get("voucher_name").getAsString(),
                        obj.get("timestamp").getAsLong()
                ));
            }

            return vouches;

        } catch (Exception e) {
            LOGGER.error("[Glaze] Error fetching vouches: {}", e.getMessage());
            return new PlayerVouches(playerName);
        }
    }

    // ── POST /vouches/{targetUuid} ─────────────────────────────────────────
    @Override
    public void addVouch(String targetName, VouchRecord record) {
        UUID targetUuid = resolveUuid(targetName);

        if (targetUuid == null) {
            LOGGER.warn("[Glaze] Could not resolve UUID for {}", targetName);
            return;
        }

        String token = Minecraft.getInstance().getUser().getAccessToken();

        // Send target name and uuid in body so Worker doesn't need to look it up
        JsonObject body = new JsonObject();
        body.addProperty("target_name", targetName);
        body.addProperty("target_uuid", targetUuid.toString());

        CompletableFuture.runAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/vouches/" + targetUuid))
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .build();

                HttpResponse<String> response = CLIENT.send(request,
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    LOGGER.info("[Glaze] Successfully vouched for {}", targetName);
                    if (Minecraft.getInstance().player != null) Minecraft.getInstance().execute(() ->
                            Minecraft.getInstance().player.sendSystemMessage(
                                    Component.literal("§6[Glaze] §aSuccessfully vouched for §e" + targetName + "§a!")));

                } else {
                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    String error = json.has("error") ? json.get("error").getAsString() : "Unknown error";
                    LOGGER.warn("[Glaze] Failed to vouch: {}", error);
                    // Send feedback to player
                    if (Minecraft.getInstance().player != null) Minecraft.getInstance().execute(() ->
                            Minecraft.getInstance().player.sendSystemMessage(
                                    net.minecraft.network.chat.Component.literal(
                                            "§6[Glaze] §c" + error)));
                }

            } catch (Exception e) {
                LOGGER.error("[Glaze] Error adding vouch: {}", e.getMessage());
            }
        });
    }

    // ── DELETE /vouches/{targetUuid} ───────────────────────────────────────
    @Override
    public boolean removeVouch(String targetName, String voucher) {
        // Try tab list first (player online)
        UUID targetUuid = resolveUuid(targetName);

        CompletableFuture.runAsync(() -> {
            UUID finalUuid = targetUuid;

            // If not online, look up UUID from our given vouches
            if (finalUuid == null) {
                finalUuid = resolveUuidFromGivenVouches(targetName);
            }

            if (finalUuid == null) {
                Minecraft.getInstance().execute(() ->
                        Minecraft.getInstance().player.sendSystemMessage(
                                Component.literal("§6[Glaze] §cCould not find §e" + targetName +
                                        "§c. Try when they are online if you have never vouched them before.")));
                return;
            }

            try {
                String token = Minecraft.getInstance().getUser().getAccessToken();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/vouches/" + finalUuid))
                        .header("Authorization", "Bearer " + token)
                        .DELETE()
                        .build();

                HttpResponse<String> response = CLIENT.send(request,
                        HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    Minecraft.getInstance().execute(() ->
                            Minecraft.getInstance().player.sendSystemMessage(
                                    Component.literal("§6[Glaze] §aVouch for §e" + targetName +
                                            "§a removed successfully.")));
                } else {
                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    String err = json.has("error") ? json.get("error").getAsString() : "Unknown error";
                    Minecraft.getInstance().execute(() ->
                            Minecraft.getInstance().player.sendSystemMessage(
                                    Component.literal("§6[Glaze] §c" + err)));
                }
            } catch (Exception e) {
                LOGGER.error("[Glaze] Error removing vouch: {}", e.getMessage());
            }
        });

        // Return true optimistically since we can't wait for the async result
        return true;
    }

    // Looks up a target's UUID from your given vouches stored on the API
    private UUID resolveUuidFromGivenVouches(String targetName) {
        try {
            String token = Minecraft.getInstance().getUser().getAccessToken();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/vouches/me"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) return null;

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray vouches = json.getAsJsonArray("vouches");

            for (JsonElement element : vouches) {
                JsonObject obj = element.getAsJsonObject();
                if (obj.get("target_name").getAsString()
                        .equalsIgnoreCase(targetName)) {
                    return UUID.fromString(obj.get("target_uuid").getAsString());
                }
            }

        } catch (Exception e) {
            LOGGER.error("[Glaze] Error resolving UUID from given vouches: {}", e.getMessage());
        }
        return null;
    }

    // ── Not needed for API-backed storage ─────────────────────────────────
    @Override
    public long getLastVouchTime(String targetName, String voucher) {
        // The API enforces one vouch per pair at the DB level
        // so we don't need to track this locally anymore
        return -1;
    }

    // ── Helper: resolve player name to UUID from tab list ─────────────────
    private static UUID resolveUuid(String playerName) {
        var connection = Minecraft.getInstance().getConnection();
        if (connection == null) return null;

        return connection.getOnlinePlayers().stream()
                .filter(p -> p.getProfile().name().equalsIgnoreCase(playerName))
                .map(p -> p.getProfile().id())
                .findFirst()
                .orElse(null);
    }

    @Override
    public PlayerVouches getGivenVouches() {
        String token = Minecraft.getInstance().getUser().getAccessToken();
        String myName = Minecraft.getInstance().getUser().getName();

        LOGGER.info("[Glaze] Fetching given vouches, token starts with: {}",
                token.substring(0, Math.min(10, token.length())));

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/vouches/me"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request,
                    HttpResponse.BodyHandlers.ofString());

            LOGGER.info("[Glaze] Given vouches response: {} {}",
                    response.statusCode(), response.body());

            if (response.statusCode() != 200) {
                LOGGER.warn("[Glaze] Failed to get given vouches: {}", response.statusCode());
                return new PlayerVouches(myName);
            }

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            PlayerVouches vouches = new PlayerVouches(myName);
            JsonArray vouchArray = json.getAsJsonArray("vouches");

            for (JsonElement element : vouchArray) {
                JsonObject obj = element.getAsJsonObject();
                vouches.addVouch(new VouchRecord(
                        obj.get("target_name").getAsString(),
                        obj.get("timestamp").getAsLong()
                ));
            }

            return vouches;

        } catch (Exception e) {
            LOGGER.error("[Glaze] Error fetching given vouches: {}", e.getMessage());
            return new PlayerVouches(myName);
        }
    }
}