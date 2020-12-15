package com.prohitman.crittersaroundtheworldmod.client.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.prohitman.crittersaroundtheworldmod.CrittersAroundTheWorld;
import com.prohitman.crittersaroundtheworldmod.client.models.FireFlyModel;
import com.prohitman.crittersaroundtheworldmod.client.renderers.layers.FireFlyBumLayerRenderer;
import com.prohitman.crittersaroundtheworldmod.entities.FireFlyEntity;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FireFlyRenderer extends MobRenderer<FireFlyEntity, FireFlyModel<FireFlyEntity>> {
	private static final ResourceLocation resourcelocation = new ResourceLocation(CrittersAroundTheWorld.MOD_ID,
			"textures/entity/fire_fly.png");

	public FireFlyRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new FireFlyModel<>(), 0.1F);
		this.addLayer(new FireFlyBumLayerRenderer<>(this));
	}

	public ResourceLocation getEntityTexture(FireFlyEntity entity) {
		return resourcelocation;
	}

	protected void preRenderCallback(FireFlyEntity entitylivingbaseIn, MatrixStack matrixStackIn,
									 float partialTickTime) {
		matrixStackIn.scale(0.8F, 0.8F, 0.8F);
	}

	@Override
	protected void applyRotations(FireFlyEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
		super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);

		switch (entityLiving.getAttachmentFacing()) {
			case DOWN:
			default:
				break;
			case EAST:
				matrixStackIn.translate(0.3F, 0.0F, 0.0F);

                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));
				matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90.0F));
				break;
			case WEST:
				matrixStackIn.translate(-0.3F, 0.0F, 0.0F);
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));
                matrixStackIn.rotate(Vector3f.ZN.rotationDegrees(90.0F));

                break;
			case NORTH:
				matrixStackIn.translate(0.0F, 0.0F, -0.3F);
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));
				break;
			case SOUTH:
				matrixStackIn.translate(0.0F, 0.0F, 0.3F);
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));
                matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0F));
				break;
			case UP:
				matrixStackIn.translate(0.0F, 0.0F, 0.0F);
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180.0F));
		}
	}


}
