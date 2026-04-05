package com.rubyimpala.features.vouch;

import com.rubyimpala.features.vouch.models.PlayerVouches;
import com.rubyimpala.features.vouch.models.VouchRecord;

public interface VouchRepository {
    /** Persist a new vouch for the target player */
    void addVouch(String targetName, VouchRecord record);

    /** Retrieve all vouches for a player. Never returns null — returns empty PlayerVouches if none exist */
    PlayerVouches getVouches(String targetName);

    /** Returns the timestamp of the last time voucher vouched for target, or -1 if never */
    long getLastVouchTime(String targetName, String voucher);

    /** Removes all vouches that voucher has given to target. Returns true if any were removed. */
    boolean removeVouch(String targetName, String voucher);
}