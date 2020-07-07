package com.prohitman.crittersaroundtheworldmod.client.renderers.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.prohitman.crittersaroundtheworldmod.CrittersAroundTheWorld;
import com.prohitman.crittersaroundtheworldmod.client.ModRenderTypes;
import com.prohitman.crittersaroundtheworldmod.client.models.FireFlyModel;
import com.prohitman.crittersaroundtheworldmod.entities.FireFlyEntity;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FireFlyBumLayerRenderer<T extends FireFlyEntity> extends LayerRenderer<T, FireFlyModel<T>> {
	private static final RenderType BUM_LAYER = ModRenderTypes
			.getBumLayer(new ResourceLocation(CrittersAroundTheWorld.MOD_ID, "textures/entity/fire_fly_bum.png"));

	public FireFlyBumLayerRenderer(IEntityRenderer<T, FireFlyModel<T>> renderIn) {
		super(renderIn);
	}

	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn,
			float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
			float headPitch) {
		IVertexBuilder ivertexbuilder = bufferIn.getBuffer(BUM_LAYER);
		this.getEntityModel().render(matrixStackIn, ivertexbuilder, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F,
				1.0F, 1.0F);// 15728640
	}
}