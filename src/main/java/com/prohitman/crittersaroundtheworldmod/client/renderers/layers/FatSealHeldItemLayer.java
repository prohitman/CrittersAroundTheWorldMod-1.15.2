package com.prohitman.crittersaroundtheworldmod.client.renderers.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.prohitman.crittersaroundtheworldmod.client.models.FatSealModel;
import com.prohitman.crittersaroundtheworldmod.entities.FatSealEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FatSealHeldItemLayer extends LayerRenderer<FatSealEntity, FatSealModel<FatSealEntity>> {
	public FatSealHeldItemLayer(IEntityRenderer<FatSealEntity, FatSealModel<FatSealEntity>> layerRendererContext) {
		super(layerRendererContext);
	}

	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn,
			FatSealEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
			float ageInTicks, float netHeadYaw, float headPitch) {

		matrixStackIn.push();
		matrixStackIn.translate((double) ((this.getEntityModel()).Body.rotationPointX / 16.0F),
				(double) ((this.getEntityModel()).Body.rotationPointY / 16.0F),
				(double) ((this.getEntityModel()).Body.rotationPointZ / 16.0F));
		matrixStackIn.translate((double) -0.05F, (double) 0.1F, -0.72D);

		matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));
		matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(-45.0F));

		ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
		Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entitylivingbaseIn, itemstack,
				ItemCameraTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
		matrixStackIn.pop();
	}
}
