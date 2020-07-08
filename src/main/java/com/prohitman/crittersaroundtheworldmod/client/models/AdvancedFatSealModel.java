package com.prohitman.crittersaroundtheworldmod.client.models;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.prohitman.crittersaroundtheworldmod.entities.FatSealEntity;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancedFatSealModel<T extends FatSealEntity> extends AdvancedEntityModel<T> {
	public AdvancedModelBox Body;
	public AdvancedModelBox Nose;
	public AdvancedModelBox LFoot;
	public AdvancedModelBox RFoot;
	public AdvancedModelBox LArm;
	public AdvancedModelBox RArm;
	public AdvancedModelBox Whiskers;
	// private ModelAnimator animator;

	public AdvancedFatSealModel() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.Nose = new AdvancedModelBox(this, 0, 26);
		this.Nose.setRotationPoint(0.0F, 1.0F, -8.0F);
		this.Nose.addBox(-2.0F, -2.0F, -3.0F, 4, 3, 3, 0.0F);
		this.RArm = new AdvancedModelBox(this, 42, 0);
		this.RArm.mirror = true;
		this.RArm.setRotationPoint(-6.0F, 3.5F, -3.0F);
		this.RArm.addBox(-6.0F, -0.5F, -2.0F, 6, 1, 4, 0.0F);
		this.LFoot = new AdvancedModelBox(this, 42, 6);
		this.LFoot.setRotationPoint(3.0F, 3.5F, 8.0F);
		this.LFoot.addBox(-2.0F, -0.5F, 0.0F, 4, 1, 6, 0.0F);
		this.LArm = new AdvancedModelBox(this, 42, 0);
		this.LArm.setRotationPoint(6.0F, 3.5F, -3.0F);
		this.LArm.addBox(0.0F, -0.5F, -2.0F, 6, 1, 4, 0.0F);
		this.Whiskers = new AdvancedModelBox(this, 23, 28);
		this.Whiskers.setRotationPoint(0.0F, -2.0F, -2.0F);
		this.Whiskers.addBox(-4.0F, 0.0F, 0.0F, 8, 4, 0, 0.0F);
		this.RFoot = new AdvancedModelBox(this, 42, 6);
		this.RFoot.mirror = true;
		this.RFoot.setRotationPoint(-3.0F, 3.5F, 8.0F);
		this.RFoot.addBox(-2.0F, -0.5F, 0.0F, 4, 1, 6, 0.0F);
		this.Body = new AdvancedModelBox(this, 0, 0);
		this.Body.setRotationPoint(0.0F, 19.0F, 0.0F);
		this.Body.addBox(-6.0F, -5.0F, -8.0F, 12, 10, 16, 0.0F);
		this.Body.addChild(this.Nose);
		this.Body.addChild(this.RArm);
		this.Body.addChild(this.LFoot);
		this.Body.addChild(this.LArm);
		this.Nose.addChild(this.Whiskers);
		this.Body.addChild(this.RFoot);
		// this.animator = ModelAnimator.create();
		this.updateDefaultPose();
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(AdvancedModelBox modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha) {
		if (this.isChild) {
			matrixStackIn.push();
			float f1 = 0.5f;
			matrixStackIn.scale(f1, f1, f1);
			matrixStackIn.translate(0.0D, (double) (24.0f / 16.0F), 0.0D);
			this.getParts().forEach((modelrenderer) -> {
				modelrenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			});
			matrixStackIn.pop();
		} else {
			this.getParts().forEach((modelrenderer) -> {
				modelrenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			});
		}
	}

	@Override
	public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
		super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
	}

	@Override
	public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {
		this.resetToDefaultPose();
	}

	@Override
	public Iterable<AdvancedModelBox> getAllParts() {
		return ImmutableList.of(Body);
	}

	@Override
	public Iterable<ModelRenderer> getParts() {
		return ImmutableList.of(this.Body);
	}

}
