package com.prohitman.crittersaroundtheworldmod;

import com.prohitman.crittersaroundtheworldmod.init.ModSounds;
import com.prohitman.crittersaroundtheworldmod.init.ModEntities;
import com.prohitman.crittersaroundtheworldmod.init.ModBlocks;
import com.prohitman.crittersaroundtheworldmod.init.ModItems;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(value = CrittersAroundTheWorld.MOD_ID)

public class CrittersAroundTheWorld
{
	public static final String MOD_ID = "crittersaroundtheworldmod";

    public CrittersAroundTheWorld() 
    {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        

        MinecraftForge.EVENT_BUS.register(this);
    }
    
    private void setup(final FMLCommonSetupEvent event)
    {
    	ModEntities.registerEntityWorldSpawns();
    	ModEntities.registerSpawnPlacement();
    }

    private void doClientStuff(final FMLClientSetupEvent event) 
    {
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
    	
    }

    private void processIMC(final InterModProcessEvent event)
    {

    }
    
    //To Do : Fix Firefly's Animations, add fireflies hanging AI, Update Flying Path Navigator for fire flies, fix jumping bug, add picking items AI.
    
}
