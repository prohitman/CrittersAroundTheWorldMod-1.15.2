package com.prohitman.crittersaroundtheworldmod.entities.ai;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.util.math.MathHelper;

public class WaterMobSwimmingAI extends MovementController {
    private final MobEntity dolphin;

    public WaterMobSwimmingAI(MobEntity mob) {
        super(mob);
        this.dolphin = mob;
    }

    public void tick() {
        if (this.dolphin.isInWater()) {
            this.dolphin.setMotion(this.dolphin.getMotion().add(0.0D, 0.005D, 0.0D));
        }

        if (this.action == MovementController.Action.MOVE_TO && !this.dolphin.getNavigator().noPath()) {
            double d0 = this.posX - this.dolphin.getPosX();
            double d1 = this.posY - this.dolphin.getPosY();
            double d2 = this.posZ - this.dolphin.getPosZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            if (d3 < (double)2.5000003E-7F) {
                this.mob.setMoveForward(0.0F);
            } else {
                float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.dolphin.rotationYaw = this.limitAngle(this.dolphin.rotationYaw, f, 10.0F);
                this.dolphin.renderYawOffset = this.dolphin.rotationYaw;
                this.dolphin.rotationYawHead = this.dolphin.rotationYaw;
                float f1 = (float)(this.speed * this.dolphin.getAttributeValue(Attributes.MOVEMENT_SPEED));
                if (this.dolphin.isInWater()) {
                    this.dolphin.setAIMoveSpeed(f1 * 0.02F);
                    float f2 = -((float)(MathHelper.atan2(d1, (double)MathHelper.sqrt(d0 * d0 + d2 * d2)) * (double)(180F / (float)Math.PI)));
                    f2 = MathHelper.clamp(MathHelper.wrapDegrees(f2), -85.0F, 85.0F);
                    this.dolphin.rotationPitch = this.limitAngle(this.dolphin.rotationPitch, f2, 5.0F);
                    float f3 = MathHelper.cos(this.dolphin.rotationPitch * ((float)Math.PI / 180F));
                    float f4 = MathHelper.sin(this.dolphin.rotationPitch * ((float)Math.PI / 180F));
                    this.dolphin.moveForward = f3 * f1;
                    this.dolphin.moveVertical = -f4 * f1;
                } else {
                    this.dolphin.setAIMoveSpeed(f1 * 0.1F);
                }

            }
        } else {
            this.dolphin.setAIMoveSpeed(0.0F);
            this.dolphin.setMoveStrafing(0.0F);
            this.dolphin.setMoveVertical(0.0F);
            this.dolphin.setMoveForward(0.0F);
        }
    }
}
