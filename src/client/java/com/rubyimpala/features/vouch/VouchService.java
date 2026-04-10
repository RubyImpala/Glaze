package com.rubyimpala.features.vouch;

import com.rubyimpala.features.vouch.models.PlayerVouches;
import com.rubyimpala.features.vouch.models.VouchRecord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.util.Collection;

public class VouchService {

    private static final long COOLDOWN_MS = 60 * 1000; // 1 minute

    // The single repository instance — swap this out later for a network implementation
    private static final VouchRepository REPOSITORY = new VouchApiClient();

    public enum VouchResult {
        SUCCESS,
        PLAYER_NOT_ONLINE,   // Target isn't on the server right now
        ON_COOLDOWN,         // Voucher vouched too recently
        SELF_VOUCH           // Player tried to vouch themselves
    }

    /**
     * Attempts to add a vouch from voucher -> target.
     * Returns a VouchResult describing what happened.
     */
    public static VouchResult addVouch(String targetName, String voucherName) {
        if (targetName.equalsIgnoreCase(voucherName)) {
            return VouchResult.SELF_VOUCH;
        }

        if (!isPlayerOnline(targetName)) {
            return VouchResult.PLAYER_NOT_ONLINE;
        }

        // Fire and forget — API handles duplicate prevention
        REPOSITORY.addVouch(targetName, new VouchRecord(voucherName, System.currentTimeMillis()));
        return VouchResult.SUCCESS;
    }

    /** Returns all vouches for a player */
    public static PlayerVouches getVouches(String targetName) {
        return REPOSITORY.getVouches(targetName);
    }

    /** Checks if a player is currently online by looking at the tab list */
    private static boolean isPlayerOnline(String playerName) {
        var connection = Minecraft.getInstance().getConnection();
        if (connection == null) return false;

        Collection<PlayerInfo> players = connection.getOnlinePlayers();
        return players.stream()
                .anyMatch(p -> p.getProfile().name().equalsIgnoreCase(playerName));
    }

    /** Returns how many seconds remain on the cooldown, or 0 if none */
    public static long getCooldownSecondsRemaining(String targetName, String voucherName) {
        long lastVouch = REPOSITORY.getLastVouchTime(targetName, voucherName);
        if (lastVouch == -1) return 0;
        long remaining = COOLDOWN_MS - (System.currentTimeMillis() - lastVouch);
        return remaining > 0 ? remaining / 1000 : 0;
    }

    /** Returns the exact display name of an online player, or null if not found */
    public static String resolveExactName(String playerName) {
        var connection = Minecraft.getInstance().getConnection();
        if (connection == null) return null;

        return connection.getOnlinePlayers().stream()
                .filter(p -> p.getProfile().name().equalsIgnoreCase(playerName))
                .map(p -> p.getProfile().name())
                .findFirst()
                .orElse(null);
    }

    public static boolean removeVouch(String targetName, String voucherName) {
        return REPOSITORY.removeVouch(targetName, voucherName);
    }

    public static PlayerVouches getGivenVouches() {
        return REPOSITORY.getGivenVouches();
    }

}