package com.example.examplemod.prices;

import com.example.examplemod.Utils.RodUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class HypixelBazaarAPI {

    private static final String BAZAAR_URL = "https://api.hypixel.net/v2/skyblock/bazaar";
    private static final String AH_URL = "https://api.hypixel.net/v2/skyblock/auctions";
    private static final long REFRESH_INTERVAL = 5 * 60 * 1000*2*3; // 5 minutes
    private static long lastFetchTime = 0;
    public static JsonObject bazaarData = null;
    public static JsonObject ahInfo = null;
    public static ArrayList<JsonObject> ahData = null;
    public static List<JsonObject> ahPages;

    //Runs every five minutes
    public static void fetchBazaarData(){

                ahData = new ArrayList<>();
                bazaarData = HypixelBazaarAPI.getJsonData(BAZAAR_URL);

                ahPages = HypixelBazaarAPI.getAllAhPages();

                PriceManager.refreshPrices(bazaarData, ahPages);
                lastFetchTime = System.currentTimeMillis();



    }

    private static JsonObject getJsonData(String link){
        JsonObject json = null;
        try {
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            InputStream in = conn.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            json = new JsonParser().parse(reader).getAsJsonObject();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }


    public static ArrayList<JsonObject> getAhData(){
        return ahData;
    }


    public static double getSellPrice(String bazaarId){
        JsonObject products = bazaarData.getAsJsonObject("products");
        JsonObject itemId = products.getAsJsonObject(bazaarId);
        JsonObject quickStatus = itemId.getAsJsonObject("quick_status");

        return quickStatus.get("buyPrice").getAsDouble();
    }


    public static double getAverageLowestBin(String itemNameToSearch) {

        ArrayList<Long> prices = new ArrayList<>();

        if (ahData == null || ahData.isEmpty()) {
            RodUtils.sendClientMessage("ahData failed to load");
            return -1;
        }
        for (JsonObject page : ahData) {
            JsonArray auctions = page.getAsJsonArray("auctions");

            for (JsonElement auctionElement : auctions) {
                JsonObject auction = auctionElement.getAsJsonObject();

                String itemName = auction.get("item_name").getAsString();
                boolean isBin = auction.has("bin") && auction.get("bin").getAsBoolean();

                if (isBin && itemName.equalsIgnoreCase(itemNameToSearch)) {
                    long price = auction.get("starting_bid").getAsLong();
                    prices.add(price);
                }
            }
        }
        prices.sort(Comparator.naturalOrder());
        int count = Math.min(5, prices.size());
        if(count == 0) return 0;

        long total = 0;
        for (int i = 0; i < count; i++){
            total += prices.get(i);
        }

        return total / (double) count;
    }


    public static Map<String,String> createBzMap(){
        Map<String, String> map = new HashMap<String, String>();

        map.put("Ultimate Jerry I", "ENCHANTMENT_ULTIMATE_JERRY_1");
        map.put("Ultimate Jerry II", "ENCHANTMENT_ULTIMATE_JERRY_2");
        map.put("Ultimate Jerry III", "ENCHANTMENT_ULTIMATE_JERRY_3");
        map.put("Bank I", "ENCHANTMENT_ULTIMATE_BANK_1");
        map.put("Bank II", "ENCHANTMENT_ULTIMATE_BANK_2");
        map.put("Bank III", "ENCHANTMENT_ULTIMATE_BANK_3");
        map.put("Infinite Quiver VI", "ENCHANTMENT_INFINITE_QUIVER_6");
        map.put("Rejuvenate I", "ENCHANTMENT_REJUVENATE_1");
        map.put("Rejuvenate II", "ENCHANTMENT_REJUVENATE_2");
        map.put("Rejuvenate III", "ENCHANTMENT_REJUVENATE_3");
        map.put("Swarm I", "ENCHANTMENT_ULTIMATE_SWARM_1");
        map.put("Ultimate Wise I", "ENCHANTMENT_ULTIMATE_WISE_1");
        map.put("Ultimate Wise II", "ENCHANTMENT_ULTIMATE_WISE_2");
        map.put("Combo I", "ENCHANTMENT_ULTIMATE_COMBO_1");
        map.put("Combo II", "ENCHANTMENT_ULTIMATE_COMBO_2");
        map.put("No Pain No Gain I", "ENCHANTMENT_ULTIMATE_NO_PAIN_NO_GAIN_1");
        map.put("No Pain No Gain II", "ENCHANTMENT_ULTIMATE_NO_PAIN_NO_GAIN_2");
        map.put("Wisdom I", "ENCHANTMENT_ULTIMATE_WISDOM_1");
        map.put("Wisdom II", "ENCHANTMENT_ULTIMATE_WISDOM_2");
        map.put("Feather Falling VI", "ENCHANTMENT_FEATHER_FALLING_6");
        map.put("Last Stand I", "ENCHANTMENT_ULTIMATE_LAST_STAND_1");
        map.put("Last Stand II", "ENCHANTMENT_ULTIMATE_LAST_STAND_2");
        map.put("Overload I", "ENCHANTMENT_OVERLOAD_1");
        map.put("Lethality VI", "ENCHANTMENT_LETHALITY_6");
        map.put("Legion I", "ENCHANTMENT_ULTIMATE_LEGION_1");

        map.put("Giant Tooth", "GIANT_TOOTH");
        map.put("Hot Potato Book", "HOT_POTATO_BOOK");
        map.put("Sadan's Brooch", "SADAN_BROOCH");
        map.put("Recombobulator 3000", "RECOMBOBULATOR_3000");
        map.put("Fuming Potato Book", "FUMING_POTATO_BOOK");
        map.put("Warped Stone", "AOTE_STONE");

        map.put("Wither Essence", "ESSENCE_WITHER");
        map.put("Undead Essence", "ESSENCE_UNDEAD");

        return map;
    }

    private static List<JsonObject> getAllAhPages() {
        List<JsonObject> pages = new ArrayList<>();

        try {
            // Fetch page 0 to determine totalPages
            JsonObject firstPage = getJsonData("https://api.hypixel.net/v2/skyblock/auctions?page=0");
            if (firstPage == null || !firstPage.has("totalPages")) {
                System.err.println("Failed to fetch AH data.");
                return pages;
            }

            int totalPages = firstPage.get("totalPages").getAsInt();
            pages.add(firstPage);

            // Fetch the rest of the pages
            for (int page = 1; page < totalPages; page++) {
                JsonObject pageData = getJsonData("https://api.hypixel.net/v2/skyblock/auctions?page=" + page);
                if (pageData != null) {
                    pages.add(pageData);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pages;
    }


}
