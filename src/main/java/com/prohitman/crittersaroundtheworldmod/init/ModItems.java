package com.prohitman.crittersaroundtheworldmod.init;

import com.prohitman.crittersaroundtheworldmod.CrittersAroundTheWorld;
import com.prohitman.crittersaroundtheworldmod.item.ModSpawnEggItem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
	
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CrittersAroundTheWorld.MOD_ID);
	
	//Entity Spawn eggs
	public static final RegistryObject<Item> FIRE_FLY_ENTITY_EGG = ITEMS.register("fire_fly_entity_egg", () -> new ModSpawnEggItem(ModEntities.FIRE_FLY_ENTITY, 0xdb5800, 0x1b2424, new Item.Properties().group(ItemGroup.MISC)));
	public static final RegistryObject<Item> FAT_SEAL_ENTITY_EGG = ITEMS.register("fat_seal_entity_egg", () -> new ModSpawnEggItem(ModEntities.FAT_SEAL_ENTITY, 0x41413F, 0x353531, new Item.Properties().group(ItemGroup.MISC)));

}
