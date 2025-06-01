package com.example.examplemod.features.dungeon;

import com.example.examplemod.Utils.GuiHighlightUtils;
import com.example.examplemod.prices.PriceManager;
import com.example.examplemod.Utils.RodUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import com.example.examplemod.prices.HypixelBazaarAPI;

import java.util.ArrayList;
import java.util.Map;

public class DungeonProfitCalc
{

    public static void chestCalc(GuiHighlightUtils guiObj) {
        if (guiObj.container !=null) {

            String chestName = guiObj.container.getLowerChestInventory().getDisplayName().getUnformattedText();
            if (chestName.toLowerCase().contains("the catacombs -")) {

                ArrayList<ItemStack> chestList = new ArrayList<ItemStack>();
                double[] itemCount = {0, 0, 0, 0, 0, 0, 0};
                ArrayList<Slot> slotList = new ArrayList<Slot>();

                Map<String, String> map = HypixelBazaarAPI.createBzMap();

                //Adds each slot object that is a loot chest to an array.
                for (Slot slot : guiObj.container.inventorySlots) {
                    ItemStack stack = slot.getStack();
                    if (stack != null && stack.getDisplayName().toLowerCase().contains("chest") && !stack.getDisplayName().toLowerCase().contains("plate")) {
                        chestList.add(stack);
                        slotList.add(slot);
                    }
                }

                //Loops through each item in the loot chest array
                for (int i = 0; i < chestList.size(); i++)
                {
                    if (chestList.get(i) == null || !chestList.get(i).hasTagCompound()) return;
                    NBTTagCompound tag = chestList.get(i).getTagCompound();
                    if (!tag.hasKey("display")) return;
                    NBTTagCompound display = tag.getCompoundTag("display");
                    if (!display.hasKey("Lore", 9)) return; // 9 = NBTTagList
                    NBTTagList lore = display.getTagList("Lore", 8); // 8 = String type

                    //loops through the lines in the loot chest object. //USE THIS TO FIND THE ITEMS IN EACH
                    for (int j = 0; j < lore.tagCount(); j++)
                    {
                        double sellPrice;
                        String cleanLine = EnumChatFormatting.getTextWithoutFormattingCodes(lore.getStringTagAt(j));
                        sellPrice = getChestDataFromNBT(cleanLine, map);

                        //Adds the value of that "line" to the index (slot) of that chest. IE wood is index 0;
                        itemCount[i] += sellPrice;
                    }

                }
                //Gets the chest that is most profitable
                double largestAmount = itemCount[0];
                int mostProfitableChestIndex = 0;

                for(int i = 0; i < itemCount.length; i++){
                    if(itemCount[i] > largestAmount){
                        largestAmount = itemCount[i];
                        mostProfitableChestIndex = i;
                    }
                }

                if(slotList.size() != 0) {
                    Slot profitItem = null;
                    try {
                        profitItem = slotList.get(mostProfitableChestIndex);
                    } catch (IndexOutOfBoundsException e) {

                    }
                    if (profitItem != null) {
                        String profit;
                        guiObj.drawGuiHighlight(profitItem,0, 200, 0);
                        if (largestAmount > 1000000){
                            profit = String.format("%.1fm", largestAmount/ 1000000); // e.g., "127.3m"
                        } else {
                            profit = String.format("%.1fk", largestAmount / 1000); // e.g., "127.3k"
                        }
                        Minecraft.getMinecraft().fontRendererObj.drawString(
                                profit,
                                0,
                                10, // draw above chest
                                0xFFFF00, // yellow text
                                true // drop shadow
                        );

                        //drawHighlight(chest.guiLeft + profitItem.xDisplayPosition, chest.guiTop + profitItem.yDisplayPosition, 0, 200, 0);
                    }
                }




            }
        }
    }

    private static double getChestDataFromNBT(String cleanLine, Map<String, String> map){

        if (cleanLine == null) return -1;
        String bazaarId;
        double sellPrice = 0;

        //If a book is on the line, it should come out with a clean name EX: Enchanted Book (Infinite Quiver VI) -> Infinite Quiver
        if (cleanLine.contains("Enchanted Book"))
        {
            String bzItem;
            bzItem = cleanLine.replace("(", "")
                    .replace(")", "")
                    .replace("Enchanted Book", "").trim();

            bazaarId = map.get(bzItem);
            sellPrice = HypixelBazaarAPI.getSellPrice(bazaarId);

        } else if (cleanLine.contains("Essence"))
        {
            String essenceType;
            int essenceAmount;

            cleanLine.trim();

            //Splits: Undead Essence x26 to: [Undead, Essence, x26]
            String[] splitLine = cleanLine.split(" ");
            //Turns splitLine into [Undead Essence, Essence, x26]
            essenceType = splitLine[0].concat(" " + splitLine[1]);

            //Essence Amount = the number directly after "x"
            //essenceAmount = Integer.valueOf(splitLine[2].replace("x", ""));
            essenceAmount = Integer.valueOf(splitLine[2].replace("x", ""));

            //WORKS!!
            if (map.containsKey(essenceType))
            {
                bazaarId = map.get(essenceType);

                sellPrice = HypixelBazaarAPI.getSellPrice(bazaarId)*essenceAmount;

                //RodUtils.sendClientMessage(sellPrice);
            }
        } else if(map.containsKey(cleanLine)){
            bazaarId = map.get(cleanLine);
            sellPrice = HypixelBazaarAPI.getSellPrice(bazaarId);
        } else if(cleanLine.contains("Coins")){
            String numToInt;
            numToInt = cleanLine.replace("Coins", "").replace(",", "").trim();
            sellPrice = -(Integer.valueOf(numToInt));

        } else{

            RodUtils.sendClientMessage(PriceManager.getLowestBinPrice(cleanLine));
            sellPrice = PriceManager.getLowestBinPrice(cleanLine);
        }
        return sellPrice;
    }
}

