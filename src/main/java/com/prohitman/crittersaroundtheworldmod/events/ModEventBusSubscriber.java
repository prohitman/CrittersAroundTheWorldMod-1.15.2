package com.prohitman.crittersaroundtheworldmod.events;

import com.prohitman.crittersaroundtheworldmod.CrittersAroundTheWorld;
import com.prohitman.crittersaroundtheworldmod.entities.FatSealEntity;
import com.prohitman.crittersaroundtheworldmod.entities.FireFlyEntity;
import com.prohitman.crittersaroundtheworldmod.init.ModEntities;
import com.prohitman.crittersaroundtheworldmod.item.ModSpawnEggItem;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;


@Mod.EventBusSubscriber(modid = CrittersAroundTheWorld.MOD_ID, bus = Bus.MOD)
public class ModEventBusSubscriber {
	
	// Thanks To Cadiboo
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onPostRegisterEntities(final RegistryEvent.Register<EntityType<?>> event) {
		ModSpawnEggItem.initUnaddedEggs();
	}
}
