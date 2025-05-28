package com.example.examplemod.features.magmarodhelper;

import com.example.examplemod.Utils.GuiHighlightUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;

import static com.example.examplemod.Utils.RodUtils.hasAttribute;

public class RodCombining {

    public static void findRod(GuiHighlightUtils guiObj){

        if (guiObj.container != null){

            //Loops through chest to look for magma rod
            for(Slot slot : guiObj.container.inventorySlots)
            {
                ItemStack stack = slot.getStack();
                checkRods(stack,slot, guiObj);
            }
        } else if (guiObj.inventory != null)
        {
            for (Slot slot : guiObj.inventory.inventorySlots.inventorySlots)
            {
                ItemStack stack = slot.getStack();
                checkRods(stack,slot, guiObj);


            }
        }
    }
    private static void checkRods(ItemStack stack, Slot slot, GuiHighlightUtils inventory){

            if(stack != null && stack.getDisplayName().toLowerCase().contains("magma rod")) {


                if (!hasAttribute(stack, "fishing speed i") && !hasAttribute(stack, "double hook i")) {
                    //draws red if rod has neither double hook or fishing speed
                    inventory.drawGuiHighlight(slot, 200, 0, 0);

                } else if (hasAttribute(stack, "fishing speed i") && hasAttribute(stack, "double hook i")) {
                    //draws green if rod has both double hook & fishing speed
                    inventory.drawGuiHighlight(slot, 0, 200, 0);

                } else if(hasAttribute(stack, "fishing speed i") && hasAttribute(stack, "trophy hunter i")){
                    //draws yellow if both fishing speed and trophy hunter
                    inventory.drawGuiHighlight(slot, 235, 216, 52);
                } else if (hasAttribute(stack, "fishing speed i") && !hasAttribute(stack, "double hook i")) {
                    //draws teal if only fishing speed
                    inventory.drawGuiHighlight(slot, 6, 234, 234);

                } else if (!hasAttribute(stack, "fishing speed i") && hasAttribute(stack, "double hook i")) {
                    //draws purple if only double hook
                    inventory.drawGuiHighlight(slot, 122, 52, 235);
                }
            }
        }
    }


