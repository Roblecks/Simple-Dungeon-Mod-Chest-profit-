package com.example.examplemod.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class RodUtils
{

    public int romanToInt(String roman)
    {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("I", 1);
        map.put("II", 2);
        map.put("III", 3);
        map.put("IV", 4);
        map.put("V", 5);
        map.put("VI", 6);
        map.put("VII", 7);
        map.put("VIII", 8);
        map.put("IX", 9);
        map.put("X", 10);

        int result = 0;
        int prev = 0;
        for (int i = roman.length() -1; i >= 0; i--)
        {
            int current = map.getOrDefault(roman.charAt(i),0);
            if (current < prev)
            {
                result -= current;
            }
            else
            {
                result += current;
            }
            prev = current;
        }
        return result;
    }
    public static void sendClientMessage(String message)
    {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("[DEBUG] " + message));
    }
    public static void sendClientMessage(double message)
    {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("[DEBUG] " + message));
    }


    public static boolean hasAttribute(ItemStack stack, String type)
    {

        if (stack ==null||!stack.hasTagCompound()) return false;
        NBTTagCompound tag = stack.getTagCompound();

        if(!tag.hasKey("display")) return false;

        NBTTagCompound display = tag.getCompoundTag("display");

        if (!display.hasKey("Lore", 9)) return false; // 9 = NBTTagList

        NBTTagList lore = display.getTagList("Lore", 8); // 8 = String type


        for (int i = 0; i < lore.tagCount(); i++)
        {
            //String line = lore.getStringTagAt(i).toLowerCase();
            String cleanLine = EnumChatFormatting.getTextWithoutFormattingCodes(lore.getStringTagAt(i).toLowerCase());

            if (cleanLine.equalsIgnoreCase(type) && !cleanLine.contains(":"))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isTargetItem(ItemStack stack, String name)
    {
        return stack.getDisplayName().contains(name); // or use NBT tags
    }
    public static void chestHighlight(GuiScreenEvent.DrawScreenEvent.Post event){



    }
}
