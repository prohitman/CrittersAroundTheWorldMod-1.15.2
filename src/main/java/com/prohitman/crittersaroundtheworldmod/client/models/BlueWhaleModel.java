package com.prohitman.crittersaroundtheworldmod.client.models;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.prohitman.crittersaroundtheworldmod.entities.BlueWhaleEntity;
import com.prohitman.crittersaroundtheworldmod.entities.FatSealEntity;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Whale - Anomalocaris101
 * Created using Tabula 8.0.0
 */
@OnlyIn(Dist.CLIENT)
public class BlueWhaleModel<T extends BlueWhaleEntity> extends AgeableModel<T> {

    public ModelRenderer Body;
    public ModelRenderer Head;
    public ModelRenderer Tail;
    public ModelRenderer LeftFin;
    public ModelRenderer LeftFin_1;
    public ModelRenderer DorsalFin;
    public ModelRenderer Jaw;
    public ModelRenderer Fluke;

    public BlueWhaleModel() {
        this.textureWidth = 256;
        this.textureHeight = 128;
        this.Body = new ModelRenderer(this, 0, 0);
        this.Body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Body.addBox(-12.0F, -10.0F, -13.0F, 24.0F, 20.0F, 26.0F, 0.0F, 0.0F, 0.0F);
        this.Tail = new ModelRenderer(this, 180, 0);
        this.Tail.setRotationPoint(0.0F, 2.0F, 10.0F);
        this.Tail.addBox(-5.0F, -5.0F, 0.0F, 10.0F, 10.0F, 24.0F, 0.0F, 0.0F, 0.0F);
        this.LeftFin_1 = new ModelRenderer(this, 100, 0);
        this.LeftFin_1.setRotationPoint(-8.0F, 2.0F, -3.0F);
        this.LeftFin_1.addBox(-18.0F, -1.0F, -5.0F, 18.0F, 2.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.LeftFin = new ModelRenderer(this, 100, 0);
        this.LeftFin.mirror = true;
        this.LeftFin.setRotationPoint(8.0F, 2.0F, -3.0F);
        this.LeftFin.addBox(0.0F, -1.0F, -5.0F, 18.0F, 2.0F, 10.0F, 0.0F, 0.0F, 0.0F);
        this.Fluke = new ModelRenderer(this, 160, 40);
        this.Fluke.setRotationPoint(0.0F, 2.0F, 20.0F);
        this.Fluke.addBox(-16.0F, -1.0F, 0.0F, 32.0F, 2.0F, 16.0F, 0.0F, 0.0F, 0.0F);
        this.Head = new ModelRenderer(this, 0, 90);
        this.Head.setRotationPoint(0.0F, -3.0F, -13.0F);
        this.Head.addBox(-12.0F, -7.0F, -24.0F, 24.0F, 14.0F, 24.0F, 0.0F, 0.0F, 0.0F);
        this.Jaw = new ModelRenderer(this, 100, 82);
        this.Jaw.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Jaw.addBox(-15.0F, 0.0F, -26.0F, 30.0F, 18.0F, 28.0F, 0.0F, 0.0F, 0.0F);
        this.DorsalFin = new ModelRenderer(this, 0, 0);
        this.DorsalFin.setRotationPoint(0.0F, -9.0F, 8.0F);
        this.DorsalFin.addBox(-0.5F, -4.0F, -3.0F, 1.0F, 4.0F, 6.0F, 0.0F, 0.0F, 0.0F);
       // this.(DorsalFin, -0.3490658503988659F, 0.0F, 0.0F);
        this.Body.addChild(this.Tail);
        this.Body.addChild(this.LeftFin_1);
        this.Body.addChild(this.LeftFin);
        this.Tail.addChild(this.Fluke);
        this.Body.addChild(this.Head);
        this.Head.addChild(this.Jaw);
        this.Body.addChild(this.DorsalFin);
    }

    @Override
    protected Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of(this.Head);
    }

    @Override
    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of(this.Body);
    }

    /**
     * Sets this entity's model rotation angles
     */
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.Body.rotateAngleX = headPitch * ((float)Math.PI / 180F);
        this.Body.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
        if (Entity.horizontalMag(entityIn.getMotion()) > 1.0E-7D) {
            this.Body.rotateAngleX += -0.05F + -0.05F * MathHelper.cos(ageInTicks * 0.3F);
            this.Tail.rotateAngleX = -0.1F * MathHelper.cos(ageInTicks * 0.3F);
            this.LeftFin_1.rotateAngleX = -0.2F * MathHelper.cos(ageInTicks * 0.3F);
        }

    }

}
