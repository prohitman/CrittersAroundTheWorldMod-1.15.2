package com.prohitman.crittersaroundtheworldmod.client.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.prohitman.crittersaroundtheworldmod.CrittersAroundTheWorld;
import com.prohitman.crittersaroundtheworldmod.client.models.FireFlyModel;
import com.prohitman.crittersaroundtheworldmod.client.renderers.layers.FireFlyBumLayerRenderer;
import com.prohitman.crittersaroundtheworldmod.entities.FireFlyEntity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FireFlyRenderer extends MobRenderer<FireFlyEntity, FireFlyModel<FireFlyEntity>>
{
	private static final ResourceLocation resourcelocation = new ResourceLocation(CrittersAroundTheWorld.MOD_ID, "textures/entity/fire_fly.png");

	public FireFlyRenderer(EntityRendererManager renderManagerIn) 
	{
		super(renderManagerIn, new FireFlyModel<>(), 0.1F);
		this.addLayer(new FireFlyBumLayerRenderer<>(this));
	}

	public ResourceLocation getEntityTexture(FireFlyEntity entity) 
	{
		return resourcelocation;
	}
	
	protected void preRenderCallback(FireFlyEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) 
	{
	      matrixStackIn.scale(0.5F, 0.5F, 0.5F);
	}	
}
