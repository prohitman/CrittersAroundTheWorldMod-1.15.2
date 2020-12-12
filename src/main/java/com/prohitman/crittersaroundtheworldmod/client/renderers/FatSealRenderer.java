package com.prohitman.crittersaroundtheworldmod.client.renderers;

import javax.annotation.Nonnull;

import com.prohitman.crittersaroundtheworldmod.CrittersAroundTheWorld;
import com.prohitman.crittersaroundtheworldmod.client.models.FatSealModel;
import com.prohitman.crittersaroundtheworldmod.client.renderers.layers.FatSealHeldItemLayer;
import com.prohitman.crittersaroundtheworldmod.entities.FatSealEntity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FatSealRenderer extends MobRenderer<FatSealEntity, FatSealModel<FatSealEntity>>
{
	private static final ResourceLocation resourcelocation = new ResourceLocation(CrittersAroundTheWorld.MOD_ID, "textures/entity/fat_seal.png");

	public FatSealRenderer(EntityRendererManager renderManagerIn) 
	{
		super(renderManagerIn, new FatSealModel<>(), 0.7F);
		this.addLayer(new FatSealHeldItemLayer(this));
	}
	
	@Nonnull
	public ResourceLocation getEntityTexture(FatSealEntity entity) 
	{
		return resourcelocation;
	}
}
