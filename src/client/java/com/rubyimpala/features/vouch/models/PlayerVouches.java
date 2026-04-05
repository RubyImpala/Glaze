package com.rubyimpala.features.vouch.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerVouches {

    private final String playerName;
    private final List<VouchRecord> vouches;

    public PlayerVouches(String playerName) {
        this.playerName = playerName;
        this.vouches = new ArrayList<>();
    }

    public void addVouch(VouchRecord record) {
        vouches.add(record);
    }

    public List<VouchRecord> getVouches() {
        return Collections.unmodifiableList(vouches);
    }

    public int getCount() {
        return vouches.size();
    }

    public String getPlayerName() {
        return playerName;
    }

    /** Returns true if this player has already been vouched by the given voucher */
    public boolean hasVouchedBy(String voucher) {
        return vouches.stream()
                .anyMatch(v -> v.voucher().equalsIgnoreCase(voucher));
    }

    /** Returns the timestamp of the most recent vouch by this voucher, or -1 if none */
    public long lastVouchTimeBy(String voucher) {
        return vouches.stream()
                .filter(v -> v.voucher().equalsIgnoreCase(voucher))
                .mapToLong(VouchRecord::timestamp)
                .max()
                .orElse(-1);
    }

    /** Removes all vouches given by the specified voucher. Returns true if any were removed. */
    public boolean removeVouchesBy(String voucher) {
        return vouches.removeIf(v -> v.voucher().equalsIgnoreCase(voucher));
    }
}