package com.prohitman.crittersaroundtheworldmod.init;

import com.prohitman.crittersaroundtheworldmod.CrittersAroundTheWorld;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModSounds {
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CrittersAroundTheWorld.MOD_ID);

	//Fat Seal Sounds
	public static final RegistryObject<SoundEvent> FAT_SEAL_LAND = SOUND_EVENTS.register("entity.fat_seal_land.ambient", () -> new SoundEvent(new ResourceLocation(CrittersAroundTheWorld.MOD_ID, "entity.fat_seal_land.ambient")));
	public static final RegistryObject<SoundEvent> FAT_SEAL_WATER = SOUND_EVENTS.register("entity.fat_seal_water.ambient", () -> new SoundEvent(new ResourceLocation(CrittersAroundTheWorld.MOD_ID, "entity.fat_seal_water.ambient")));
	public static final RegistryObject<SoundEvent> FAT_SEAL_HURT = SOUND_EVENTS.register("entity.fat_seal.hurt", () -> new SoundEvent(new ResourceLocation(CrittersAroundTheWorld.MOD_ID, "entity.fat_seal.hurt")));
	public static final RegistryObject<SoundEvent> FAT_SEAL_DEATH = SOUND_EVENTS.register("entity.fat_seal.death", () -> new SoundEvent(new ResourceLocation(CrittersAroundTheWorld.MOD_ID, "entity.fat_seal.death")));

}
