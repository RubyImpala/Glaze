package com.rubyimpala.features.auction.models;

import java.util.Map;

public record PriceEntry(
        String id,
        int totalPrice,
        int count,
        long timestamp,
        Map<String, Integer> enchants
) {
    public int getUnitPrice(){return totalPrice() / count();}
//    public int getStackPrice() {return price * 64;}
}



