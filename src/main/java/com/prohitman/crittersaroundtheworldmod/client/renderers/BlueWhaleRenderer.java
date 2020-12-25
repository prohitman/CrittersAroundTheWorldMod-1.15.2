package com.prohitman.crittersaroundtheworldmod.client.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.prohitman.crittersaroundtheworldmod.CrittersAroundTheWorld;
import com.prohitman.crittersaroundtheworldmod.client.models.BlueWhaleModel;
import com.prohitman.crittersaroundtheworldmod.client.models.FatSealModel;
import com.prohitman.crittersaroundtheworldmod.entities.BlueWhaleEntity;
import com.prohitman.crittersaroundtheworldmod.entities.FireFlyEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlueWhaleRenderer extends MobRenderer<BlueWhaleEntity, BlueWhaleModel<BlueWhaleEntity>> {

    private static final ResourceLocation resourcelocation = new ResourceLocation(CrittersAroundTheWorld.MOD_ID, "textures/entity/blue_whale.png");

    public BlueWhaleRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new BlueWhaleModel<>(), 0.7F);
    }

    protected void preRenderCallback(BlueWhaleEntity entitylivingbaseIn, MatrixStack matrixStackIn,
                                     float partialTickTime) {
        matrixStackIn.scale(4.0F, 4.0F, 4.0F);
    }

    @Override
    public ResourceLocation getEntityTexture(BlueWhaleEntity entity) {
        return resourcelocation;
    }
}
