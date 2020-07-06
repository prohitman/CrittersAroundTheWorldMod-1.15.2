package com.prohitman.crittersaroundtheworldmod.entities.goals.fatseal;

import com.prohitman.crittersaroundtheworldmod.entities.FatSealEntity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class FatSealGoHomeGoal extends Goal {
	private final FatSealEntity seal;
	private final double speed;
	private boolean noPath;
	private int homeReachingTryTicks;

	public FatSealGoHomeGoal(FatSealEntity seal, double speedIn) {
		this.seal = seal;
		this.speed = speedIn;
	}

	/**
	 * Returns whether execution should begin. You can also read and cache any state
	 * necessary for execution in this method as well.
	 */
	public boolean shouldExecute() {
		if (this.seal.isChild()) {
			return false;
		} else if (this.seal.getRNG().nextInt(700) != 0) {
			return false;
		} else {
			return !this.seal.getHome().withinDistance(this.seal.getPositionVec(), 64.0D)
					&& !this.seal.isSwimmingWithPlayer && !this.seal.isGettingTempted
					&& this.seal.getAttackTarget() == null;
		}
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.seal.setGoingHome(true);
		this.noPath = false;
		this.homeReachingTryTicks = 0;
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by
	 * another one
	 */
	public void resetTask() {
		this.seal.setGoingHome(false);
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean shouldContinueExecuting() {
		return !this.seal.getHome().withinDistance(this.seal.getPositionVec(), 7.0D) && !this.noPath
				&& this.homeReachingTryTicks <= 600;
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void tick() {
		BlockPos blockpos = this.seal.getHome();
		boolean flag = blockpos.withinDistance(this.seal.getPositionVec(), 16.0D);
		if (flag) {
			++this.homeReachingTryTicks;
		}

		if (this.seal.getNavigator().noPath()) {
			Vec3d vec3d = new Vec3d(blockpos);
			Vec3d vec3d1 = RandomPositionGenerator.findRandomTargetTowardsScaled(this.seal, 16, 3, vec3d,
					(double) ((float) Math.PI / 10F));
			if (vec3d1 == null) {
				vec3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(this.seal, 8, 7, vec3d);
			}

			if (vec3d1 != null && !flag
					&& this.seal.world.getBlockState(new BlockPos(vec3d1)).getBlock() != Blocks.WATER) {
				vec3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(this.seal, 16, 5, vec3d);
			}

			if (vec3d1 == null) {
				this.noPath = true;
				return;
			}

			this.seal.getNavigator().tryMoveToXYZ(vec3d1.x, vec3d1.y, vec3d1.z, this.speed);
		}

	}
}
