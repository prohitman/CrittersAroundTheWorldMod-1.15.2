package com.prohitman.crittersaroundtheworldmod.client.models;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.prohitman.crittersaroundtheworldmod.entities.FatSealEntity;

import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FatSealModel<T extends FatSealEntity> extends AgeableModel<T>
{
    public ModelRenderer Body;
    public ModelRenderer Nose;
    public ModelRenderer LFoot;
    public ModelRenderer RFoot;
    public ModelRenderer LArm;
    public ModelRenderer RArm;
    public ModelRenderer Whiskers;


    public FatSealModel() 
    {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.Nose = new ModelRenderer(this, 0, 26);
        this.Nose.setRotationPoint(0.0F, 1.0F, -8.0F);
        this.Nose.addBox(-2.0F, -2.0F, -3.0F, 4, 3, 3, 0.0F);
        this.RArm = new ModelRenderer(this, 42, 0);
        this.RArm.mirror = true;
        this.RArm.setRotationPoint(-6.0F, 3.5F, -3.0F);
        this.RArm.addBox(-6.0F, -0.5F, -2.0F, 6, 1, 4, 0.0F);
        this.LFoot = new ModelRenderer(this, 42, 6);
        this.LFoot.setRotationPoint(3.0F, 3.5F, 8.0F);
        this.LFoot.addBox(-2.0F, -0.5F, 0.0F, 4, 1, 6, 0.0F);
        this.LArm = new ModelRenderer(this, 42, 0);
        this.LArm.setRotationPoint(6.0F, 3.5F, -3.0F);
        this.LArm.addBox(0.0F, -0.5F, -2.0F, 6, 1, 4, 0.0F);
        this.Whiskers = new ModelRenderer(this, 23, 28);
        this.Whiskers.setRotationPoint(0.0F, -2.0F, -2.0F);
        this.Whiskers.addBox(-4.0F, 0.0F, 0.0F, 8, 4, 0, 0.0F);
        this.RFoot = new ModelRenderer(this, 42, 6);
        this.RFoot.mirror = true;
        this.RFoot.setRotationPoint(-3.0F, 3.5F, 8.0F);
        this.RFoot.addBox(-2.0F, -0.5F, 0.0F, 4, 1, 6, 0.0F);
        this.Body = new ModelRenderer(this, 0, 0);
        this.Body.setRotationPoint(0.0F, 19.0F, 0.0F);
        this.Body.addBox(-6.0F, -5.0F, -8.0F, 12, 10, 16, 0.0F);
        this.Body.addChild(this.Nose);
        this.Body.addChild(this.RArm);
        this.Body.addChild(this.LFoot);
        this.Body.addChild(this.LArm);
        this.Nose.addChild(this.Whiskers);
        this.Body.addChild(this.RFoot);
    }
    
	@Override
    @Nonnull
    protected Iterable<ModelRenderer> getHeadParts() 
    {
        return ImmutableList.of();
     }  
	
	@Override
    @Nonnull
    protected Iterable<ModelRenderer> getBodyParts() 
    {
        return ImmutableList.of(this.Body);
     }
    

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
	
	@Override
	public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) 
	{
	      this.LFoot.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F * 0.6F) * 0.5F * limbSwingAmount;
	      this.RFoot.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F * 0.6F + (float)Math.PI) * 0.5F * limbSwingAmount;
	      this.LFoot.rotateAngleZ = MathHelper.cos(limbSwing * 0.6662F * 0.6F + (float)Math.PI) * 0.5F * limbSwingAmount;
	      this.RFoot.rotateAngleZ = MathHelper.cos(limbSwing * 0.6662F * 0.6F) * 0.5F * limbSwingAmount;
	      this.LArm.rotateAngleX = 0.0F;
	      this.RArm.rotateAngleX = 0.0F;
	      this.LArm.rotateAngleY = 0.0F;
	      this.RArm.rotateAngleY = 0.0F;
	      this.RFoot.rotateAngleY = 0.0F;
	      this.LFoot.rotateAngleY = 0.0F;
	      if (!entityIn.isInWater()) 
	      {
	         float f = 1.0F;
	         float f1 = 1.0F;
	         this.RArm.rotateAngleY = MathHelper.cos(f * limbSwing * 2.5F + (float)Math.PI) * 2.3F * limbSwingAmount * f1;
	         this.RArm.rotateAngleZ = 0.0F;
	         this.LArm.rotateAngleY = MathHelper.cos(f * limbSwing * 2.5F) * 2.3F * limbSwingAmount * f1;
	         this.LArm.rotateAngleZ = 0.0F;
	         this.RFoot.rotateAngleY = MathHelper.cos(limbSwing * 2.5F + (float)Math.PI) * 0.85F * limbSwingAmount;
	         this.RFoot.rotateAngleX = 0.0F;
	         this.LFoot.rotateAngleY = MathHelper.cos(limbSwing * 2.5F) * 0.85F * limbSwingAmount;
	         this.LFoot.rotateAngleX = 0.0F;
	         if (this.isChild)
	         {
	        	 this.RArm.rotateAngleY = MathHelper.cos(f * limbSwing * 2.5F + (float)Math.PI) * 1.16F * limbSwingAmount * f1;
		         this.RArm.rotateAngleZ = 0.0F;
		         this.LArm.rotateAngleY = MathHelper.cos(f * limbSwing * 2.5F) * 1.16F * limbSwingAmount * f1;
		         this.LArm.rotateAngleZ = 0.0F;
		         this.RFoot.rotateAngleY = MathHelper.cos(limbSwing * 2.5F + (float)Math.PI) * 0.425F * limbSwingAmount;
		         this.RFoot.rotateAngleX = 0.0F;
		         this.LFoot.rotateAngleY = MathHelper.cos(limbSwing * 2.5F) * 0.425F * limbSwingAmount;
		         this.LFoot.rotateAngleX = 0.0F;
	         }
	      }	
	}

}
