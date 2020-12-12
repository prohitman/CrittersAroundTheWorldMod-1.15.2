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
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = CrittersAroundTheWorld.MOD_ID, bus = Bus.MOD)
public class ModEventBusSubscriber {
	
	// Thanks To Cadiboo
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onPostRegisterEntities(final RegistryEvent.Register<EntityType<?>> event) {
		ModSpawnEggItem.initUnaddedEggs();
	}

	@SubscribeEvent
	private void setupEntityAttributes(final FMLCommonSetupEvent event) {
		DeferredWorkQueue.runLater(() -> {
			GlobalEntityTypeAttributes.put(ModEntities.FAT_SEAL_ENTITY.get(), FatSealEntity.setFatSealAttributes().create());
			GlobalEntityTypeAttributes.put(ModEntities.FIRE_FLY_ENTITY.get(), FireFlyEntity.setFireFlyAttributes().create());
		});
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void addSpawn(BiomeLoadingEvent event) {
		if (event.getName() != null) {
			Biome biome = ForgeRegistries.BIOMES.getValue(event.getName());
			if(biome != null){
				if(biome.getRegistryName() == Biomes.ICE_SPIKES.getRegistryName()){
					event.getSpawns().getSpawner(EntityClassification.CREATURE).add(new MobSpawnInfo.Spawners(ModEntities.FAT_SEAL_ENTITY.get(), 50, 5, 10));

				}
			}
		}
	}
}
