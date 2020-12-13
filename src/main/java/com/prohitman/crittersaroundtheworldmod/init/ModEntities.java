package com.prohitman.crittersaroundtheworldmod.init;

import com.prohitman.crittersaroundtheworldmod.CrittersAroundTheWorld;
import com.prohitman.crittersaroundtheworldmod.entities.FatSealEntity;
import com.prohitman.crittersaroundtheworldmod.entities.FireFlyEntity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities {

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES,
			CrittersAroundTheWorld.MOD_ID);

	// Entities
	public static final RegistryObject<EntityType<FatSealEntity>> FAT_SEAL_ENTITY = ENTITY_TYPES.register(
			"fat_seal_entity",
			() -> EntityType.Builder.create(FatSealEntity::new, EntityClassification.CREATURE).size(0.9F, 0.5F)
					.build(new ResourceLocation(CrittersAroundTheWorld.MOD_ID, "fat_seal_entity").toString()));

	public static final RegistryObject<EntityType<FireFlyEntity>> FIRE_FLY_ENTITY = ENTITY_TYPES.register(
			"fire_fly_entity",
			() -> EntityType.Builder.create(FireFlyEntity::new, EntityClassification.CREATURE).size(0.4F, 0.4F)
					.build(new ResourceLocation(CrittersAroundTheWorld.MOD_ID, "fire_fly_entity").toString()));

	// Entity Spawn Utils
/*	public static void registerEntityWorldSpawns() {
		for (Biome biome : ForgeRegistries.BIOMES) {
			if (biome != null
					&& (biome = Biomes.ICE_SPIKES || biome = Biomes.SNOWY_BEACH)) {
				biome.getSpawns(EntityClassification.CREATURE).add(new SpawnListEntry(FAT_SEAL_ENTITY.get(), 30, 5, 10));
			}

			if ((biome.getCategory() == Biome.Category.TAIGA || biome.getCategory() == Biome.Category.FOREST)
					&& biome != null) {
				biome.getSpawns(EntityClassification.AMBIENT).add(new SpawnListEntry(FIRE_FLY_ENTITY.get(), 10, 3, 8));// do 3 / 10
			}
		}*/


	public static void registerSpawnPlacement() {
		EntitySpawnPlacementRegistry.register(FAT_SEAL_ENTITY.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FatSealEntity::canSpawn);
		EntitySpawnPlacementRegistry.register(FIRE_FLY_ENTITY.get(), EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FireFlyEntity::canSpawn);
	}

	public static void initEntityAttributes(){
		GlobalEntityTypeAttributes.put(ModEntities.FAT_SEAL_ENTITY.get(), FatSealEntity.setFatSealAttributes().create());
		GlobalEntityTypeAttributes.put(ModEntities.FIRE_FLY_ENTITY.get(), FireFlyEntity.setFireFlyAttributes().create());
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
