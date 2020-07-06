package com.prohitman.crittersaroundtheworldmod.init;

import com.prohitman.crittersaroundtheworldmod.CrittersAroundTheWorld;
import com.prohitman.crittersaroundtheworldmod.entities.FatSealEntity;
import com.prohitman.crittersaroundtheworldmod.entities.FireFlyEntity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.gen.Heightmap;
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
			() -> EntityType.Builder.create(FireFlyEntity::new, EntityClassification.AMBIENT).size(0.3F, 0.1F)
					.build(new ResourceLocation(CrittersAroundTheWorld.MOD_ID, "fire_fly_entity").toString()));

	// Entity Spawn Utils
	public static void registerEntityWorldSpawns() {
		for (Biome biome : ForgeRegistries.BIOMES) {
			if (biome != null
					&& (biome == Biomes.FROZEN_OCEAN || biome == Biomes.ICE_SPIKES || biome == Biomes.SNOWY_BEACH)) {
				biome.getSpawns(EntityClassification.CREATURE).add(new SpawnListEntry(FAT_SEAL_ENTITY.get(), 4, 2, 5));
			}

			if ((biome.getCategory() == Biome.Category.TAIGA && biome.getCategory() == Biome.Category.FOREST)
					&& biome != null) {
				biome.getSpawns(EntityClassification.AMBIENT).add(new SpawnListEntry(FIRE_FLY_ENTITY.get(), 25, 1, 6));
			}
		}
	}

	public static void registerSpawnPlacement() {
		EntitySpawnPlacementRegistry.register(FAT_SEAL_ENTITY.get(),
				EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
				FatSealEntity::canSpawn);
		EntitySpawnPlacementRegistry.register(FIRE_FLY_ENTITY.get(),
				EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
				FireFlyEntity::onInitialSpawn);
	}

}
