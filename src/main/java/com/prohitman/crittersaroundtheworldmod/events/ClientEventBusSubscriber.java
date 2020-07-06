package com.prohitman.crittersaroundtheworldmod.events;

import com.prohitman.crittersaroundtheworldmod.CrittersAroundTheWorld;
import com.prohitman.crittersaroundtheworldmod.client.renderers.FatSealRenderer;
import com.prohitman.crittersaroundtheworldmod.client.renderers.FireFlyRenderer;
import com.prohitman.crittersaroundtheworldmod.init.ModEntities;
import com.prohitman.crittersaroundtheworldmod.init.ModBlocks;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CrittersAroundTheWorld.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {
	
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
    	RenderTypeLookup.setRenderLayer(ModBlocks.JAR_O_FIREFLY.get(), RenderType.getCutoutMipped());
    	RenderTypeLookup.setRenderLayer(ModBlocks.JAR_CATM.get(), RenderType.getCutoutMipped());
    	
    	//Entity Renderers
    	RenderingRegistry.registerEntityRenderingHandler(ModEntities.FAT_SEAL_ENTITY.get(), FatSealRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(ModEntities.FIRE_FLY_ENTITY.get(), FireFlyRenderer::new);
   }
}
