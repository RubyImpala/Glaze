package com.rubyimpala.models;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class DonutApiResponse {
    public List<AuctionListing> result = new ArrayList<>();
    public int status;

    public static class AuctionListing {
        public ItemData item = new ItemData();
        public double price; // Matches your 'decimal'
        public SellerData seller = new SellerData();

        @SerializedName("time_left")
        public long timeLeft;
    }

    public static class ItemData {
        @SerializedName("display_name")
        public String displayName = "";

        public String id = "";
        public int count;
    }

    public static class SellerData {
        public String name = "";
    }
}
