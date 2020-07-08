package com.prohitman.crittersaroundtheworldmod.client.renderers;

import javax.annotation.Nonnull;

import com.prohitman.crittersaroundtheworldmod.CrittersAroundTheWorld;
import com.prohitman.crittersaroundtheworldmod.client.models.AdvancedFatSealModel;
import com.prohitman.crittersaroundtheworldmod.client.renderers.layers.FatSealHeldItemLayer;
import com.prohitman.crittersaroundtheworldmod.entities.FatSealEntity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FatSealRenderer extends MobRenderer<FatSealEntity, AdvancedFatSealModel<FatSealEntity>>
{
	private static final ResourceLocation resourcelocation = new ResourceLocation(CrittersAroundTheWorld.MOD_ID, "textures/entity/fat_seal.png");

	public FatSealRenderer(EntityRendererManager renderManagerIn) 
	{
		super(renderManagerIn, new AdvancedFatSealModel<>(), 0.7F);
		this.addLayer(new FatSealHeldItemLayer(this));
	}
	
	@Nonnull
	public ResourceLocation getEntityTexture(FatSealEntity entity) 
	{
		return resourcelocation;
	}
}
