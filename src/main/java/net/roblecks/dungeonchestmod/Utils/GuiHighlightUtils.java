package net.roblecks.dungeonchestmod.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiScreenEvent;

import java.awt.*;

public class GuiHighlightUtils {
    //Width and height (in pixels) of the gui. Doesn't account for location in window
    private int guiWidth;
    private int guiHeight;

    //Top left starting point in gui (position in window)
    public int guiTop;
    public int guiLeft;


    public ContainerChest container;
    public GuiInventory inventory;


    public GuiHighlightUtils(GuiChest chest){
        this.container = (ContainerChest) chest.inventorySlots;

        this.guiWidth = chest.width;
        this.guiHeight = chest.height;

        //number of chest rows
        int numRows = container.getLowerChestInventory().getSizeInventory() / 9;

        //Size (in pixels) of top chest inventory.
        int ySize = 114 + numRows * 18;
        this.guiLeft = (guiWidth - 176)/2;
        this.guiTop = (guiHeight - ySize)/2;
    }
    public GuiHighlightUtils(GuiInventory inventory){
        this.inventory = inventory;
        this.guiWidth = inventory.width;
        this.guiHeight = inventory.height;

        this.guiTop = (guiHeight - 166)/2;

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if(player.getActivePotionEffects().isEmpty()){
            this.guiLeft = (guiWidth - 176)/2;
        }else{
            this.guiLeft = 160 + (guiWidth - 176 - 200) / 2;
        }
    }
    public GuiHighlightUtils(GuiScreenEvent.DrawScreenEvent.Post event){
        if(event.gui instanceof GuiChest){
            GuiChest chest = (GuiChest) event.gui;
            this.container = (ContainerChest) chest.inventorySlots;
            this.guiWidth = chest.width;
            this.guiHeight = chest.height;

            //number of chest rows
            int numRows = container.getLowerChestInventory().getSizeInventory() / 9;

            //Size (in pixels) of top chest inventory.
            int ySize = 114 + numRows * 18;
            this.guiLeft = (guiWidth - 176)/2;
            this.guiTop = (guiHeight - ySize)/2;

        } else if (event.gui instanceof GuiInventory){
            this.inventory = (GuiInventory) event.gui;
            this.guiWidth = inventory.width;
            this.guiHeight = inventory.height;
            this.guiTop = (guiHeight - 166)/2;

            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if(player.getActivePotionEffects().isEmpty()){
                this.guiLeft = (guiWidth - 176)/2;
            }else{
                this.guiLeft = 160 + (guiWidth - 176 - 200) / 2;
            }
        }

    }

    public void drawGuiHighlight(Slot slot, int r, int g, int b){
        Gui.drawRect(guiLeft+slot.xDisplayPosition, guiTop+slot.yDisplayPosition,
                    guiLeft+slot.xDisplayPosition+16, guiTop+slot.yDisplayPosition+16,
                         new Color(r, g, b, 100).getRGB());
    }



}

