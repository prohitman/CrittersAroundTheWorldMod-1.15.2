package com.prohitman.crittersaroundtheworldmod.client.models;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Firefly - Anomalocaris101
 * Created using Tabula 7.0.1
 */

@OnlyIn(Dist.CLIENT)
public class FireFlyModel<T extends Entity> extends SegmentedModel<T>
{
    public ModelRenderer Body;
    public ModelRenderer Antenne;
    public ModelRenderer Leg1;
    public ModelRenderer Leg2;
    public ModelRenderer Leg3;
    public ModelRenderer LWingcase;
    public ModelRenderer RWingcase;
    public ModelRenderer LWing;
    public ModelRenderer RWing;
    public ModelRenderer Bum;

    public FireFlyModel() 
    {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.Leg3 = new ModelRenderer(this, 0, 24);
        this.Leg3.setRotationPoint(0.0F, 1.5F, 0.0F);
        this.Leg3.addBox(-2.0F, 0.0F, 0.0F, 4, 3, 1, 0.0F);
        this.setRotateAngle(Leg3, 0.7853981633974483F, 0.0F, 0.0F);
        this.Bum = new ModelRenderer(this, 25, 8);
        this.Bum.setRotationPoint(0.0F, -0.5F, 1.0F);
        this.Bum.addBox(-3.0F, -1.0F, 0.0F, 6, 3, 3, 0.0F);
        this.LWing = new ModelRenderer(this, 0, 19);
        this.LWing.setRotationPoint(1.0F, -1.5F, -1.0F);
        this.LWing.addBox(0.0F, 0.0F, 0.0F, 6, 1, 4, 0.0F);
        this.setRotateAngle(LWing, 0.0F, 0.0F, -0.39269908169872414F);
        this.Antenne = new ModelRenderer(this, 20, 0);
        this.Antenne.setRotationPoint(0.0F, -0.5F, -4.0F);
        this.Antenne.addBox(-3.0F, 0.0F, -5.0F, 6, 1, 5, 0.0F);
        this.setRotateAngle(Antenne, -0.7853981633974483F, 0.0F, 0.0F);
        this.RWing = new ModelRenderer(this, 0, 19);
        this.RWing.mirror = true;
        this.RWing.setRotationPoint(-1.0F, -1.5F, -1.0F);
        this.RWing.addBox(-6.0F, 0.0F, 0.0F, 6, 1, 4, 0.0F);
        this.setRotateAngle(RWing, 0.0F, 0.0F, 0.39269908169872414F);
        this.LWingcase = new ModelRenderer(this, 0, 11);
        this.LWingcase.setRotationPoint(0.0F, -1.5F, -2.0F);
        this.LWingcase.addBox(0.0F, 0.0F, -6.0F, 3, 2, 6, 0.0F);
        this.setRotateAngle(LWingcase, 0.0F, -1.5707963267948966F, -0.7853981633974483F);
        this.RWingcase = new ModelRenderer(this, 0, 11);
        this.RWingcase.mirror = true;
        this.RWingcase.setRotationPoint(0.0F, -1.5F, -2.0F);
        this.RWingcase.addBox(-3.0F, 0.0F, -6.0F, 3, 2, 6, 0.0F);
        this.setRotateAngle(RWingcase, 0.0F, 1.5707963267948966F, 0.8196066167365371F);
        this.Body = new ModelRenderer(this, 3, 3);
        this.Body.setRotationPoint(0.0F, 19.5F, 0.0F);
        this.Body.addBox(-3.0F, -1.5F, -4.0F, 6, 3, 5, 0.0F);
        this.Leg2 = new ModelRenderer(this, 0, 24);
        this.Leg2.setRotationPoint(0.0F, 1.5F, -1.0F);
        this.Leg2.addBox(-2.0F, 0.0F, 0.0F, 4, 3, 1, 0.0F);
        this.setRotateAngle(Leg2, 0.39269908169872414F, 0.0F, 0.0F);
        this.Leg1 = new ModelRenderer(this, 0, 24);
        this.Leg1.setRotationPoint(0.0F, 1.5F, -2.0F);
        this.Leg1.addBox(-2.0F, 0.0F, 0.0F, 4, 3, 1, 0.0F);
        this.setRotateAngle(Leg1, 0.20507618710933373F, 0.0F, 0.0F);
        this.Body.addChild(this.Leg3);
        this.Body.addChild(this.Bum);
        this.Body.addChild(this.LWing);
        this.Body.addChild(this.Antenne);
        this.Body.addChild(this.RWing);
        this.Body.addChild(this.LWingcase);
        this.Body.addChild(this.RWingcase);
        this.Body.addChild(this.Leg2);
        this.Body.addChild(this.Leg1);
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
	public Iterable<ModelRenderer> getParts() 
	{
		return ImmutableList.of(this.Body);
	}

	@Override
	public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) 
	{
        this.RWing.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.LWing.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Body.rotateAngleX = 0.0F; //((float)Math.PI / 4F) + MathHelper.cos(ageInTicks * 0.1F) * 0.15F;
        this.Body.rotateAngleY = 0.0F;
        this.RWing.rotateAngleZ = MathHelper.cos(ageInTicks * 2.0F) * (float)Math.PI * 0.5F; //was Y
        this.LWing.rotateAngleZ = this.RWing.rotateAngleZ; //was Y
	}


}
