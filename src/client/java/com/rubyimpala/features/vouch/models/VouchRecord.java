package com.rubyimpala.features.vouch.models;

public record VouchRecord(
        String voucher,   // Who gave the vouch
        long timestamp    // When it was given
) {}