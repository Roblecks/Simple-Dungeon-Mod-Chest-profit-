package com.example.examplemod.prices;

import com.google.gson.*;
import java.util.*;

public class PriceManager {

    private static final Map<String, Double> lowestBinPrices = new HashMap<>();
    private static final Map<String, Double> bazaarPrices = new HashMap<>();

    public static void refreshPrices(JsonObject bazaarData, List<JsonObject> auctionPages) {
        updateBazaarPrices(bazaarData);
        updateLowestBinPrices(auctionPages);
    }

    private static void updateBazaarPrices(JsonObject bazaarData) {
        if (bazaarData == null || !bazaarData.has("products")) return;

        JsonObject products = bazaarData.getAsJsonObject("products");
        for (Map.Entry<String, JsonElement> entry : products.entrySet()) {
            String itemId = entry.getKey();
            JsonObject productInfo = entry.getValue().getAsJsonObject();

            if (productInfo.has("quick_status")) {
                JsonObject status = productInfo.getAsJsonObject("quick_status");
                if (status.has("sellPrice")) {
                    double price = status.get("sellPrice").getAsDouble();
                    bazaarPrices.put(itemId, price);
                }
            }
        }
    }

    private static void updateLowestBinPrices(List<JsonObject> auctionPages) {
        if (auctionPages == null) return;

        lowestBinPrices.clear();
        for (JsonObject page : auctionPages) {
            if (!page.has("auctions")) continue;

            JsonArray auctions = page.getAsJsonArray("auctions");

            for (JsonElement auctionElement : auctions) {
                JsonObject auction = auctionElement.getAsJsonObject();

                if (!auction.has("bin") || !auction.get("bin").getAsBoolean()) continue;
                if (!auction.has("item_name") || !auction.has("starting_bid")) continue;

                String itemName = auction.get("item_name").getAsString();
                double price = auction.get("starting_bid").getAsDouble();

                // Store the lowest BIN price seen for this item
                lowestBinPrices.merge(itemName, price, Math::min);
            }
        }
    }

    // Public getter methods

    public static double getLowestBinPrice(String itemName) {
        return lowestBinPrices.getOrDefault(itemName, -1.0);
    }

    public static double getBazaarPrice(String itemId) {
        return bazaarPrices.getOrDefault(itemId, -1.0);
    }

    // Optional: for debugging
    public static void printPriceSummary() {
        System.out.println("Bazaar prices: " + bazaarPrices.size());
        System.out.println("Lowest BINs: " + lowestBinPrices.size());
    }
}