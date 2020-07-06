package com.prohitman.crittersaroundtheworldmod.events;

import com.prohitman.crittersaroundtheworldmod.CrittersAroundTheWorld;
import com.prohitman.crittersaroundtheworldmod.item.ModSpawnEggItem;

import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
