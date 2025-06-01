package net.roblecks.dungeonchestmod;

import net.roblecks.dungeonchestmod.Utils.GuiHighlightUtils;
import net.roblecks.dungeonchestmod.features.dungeon.DungeonProfitCalc;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.roblecks.dungeonchestmod.features.experimentation.ExperimentTable;
import net.roblecks.dungeonchestmod.features.magmarodhelper.RodCombining;
import net.roblecks.dungeonchestmod.prices.HypixelBazaarAPI;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Mod(modid = DungeonChestMod.MODID, version = DungeonChestMod.VERSION)
public class DungeonChestMod
{
    public static final String MODID = "dungeonchestmod";
    public static final String VERSION = "1.0";

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		// some example code
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        System.out.println("DIRT BLOCK >> "+Blocks.dirt.getUnlocalizedName());
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> HypixelBazaarAPI.fetchBazaarData(), 0, 5, TimeUnit.MINUTES);
    }

    @SubscribeEvent
    public void onRenderInventory(GuiScreenEvent.DrawScreenEvent.Post event)
    {
        GuiHighlightUtils e = new GuiHighlightUtils(event);

        RodCombining.findRod(e);
        DungeonProfitCalc.chestCalc(e);
        ExperimentTable.highlightDye(event);
    }

}
