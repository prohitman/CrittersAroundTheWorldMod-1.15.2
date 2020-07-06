package com.prohitman.crittersaroundtheworldmod.entities.goals.fatseal;

import java.util.List;

import com.prohitman.crittersaroundtheworldmod.entities.FatSealEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class FatSealFollowBoatGoal extends Goal {
	private int updateCountdownTicks;
	private final FatSealEntity mob;
	private LivingEntity passenger;
	private ChaseBoatState state;

	public FatSealFollowBoatGoal(FatSealEntity mob) {
		this.mob = mob;
	}

	/**
	 * Returns whether execution should begin. You can also read and cache any state
	 * necessary for execution in this method as well.
	 */
	public boolean shouldExecute() {
		List<BoatEntity> list = this.mob.world.getEntitiesWithinAABB(BoatEntity.class,
				this.mob.getBoundingBox().grow(5.0D));
		boolean flag = false;

		for (BoatEntity boatentity : list) {
			Entity entity = boatentity.getControllingPassenger();
			if (entity instanceof LivingEntity && (MathHelper.abs(((LivingEntity) entity).moveStrafing) > 0.0F
					|| MathHelper.abs(((LivingEntity) entity).moveForward) > 0.0F)) {
				flag = true;
				break;
			}
		}

		return this.passenger != null && (MathHelper.abs(this.passenger.moveStrafing) > 0.0F
				|| MathHelper.abs(this.passenger.moveForward) > 0.0F) || flag;
	}

	public boolean isPreemptible() {
		return true;
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean shouldContinueExecuting() {
		return this.passenger != null && this.passenger.isPassenger()
				&& (MathHelper.abs(this.passenger.moveStrafing) > 0.0F
						|| MathHelper.abs(this.passenger.moveForward) > 0.0F);
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		for (BoatEntity boatentity : this.mob.world.getEntitiesWithinAABB(BoatEntity.class,
				this.mob.getBoundingBox().grow(5.0D))) {
			if (boatentity.getControllingPassenger() != null
					&& boatentity.getControllingPassenger() instanceof LivingEntity) {
				this.passenger = (LivingEntity) boatentity.getControllingPassenger();
				break;
			}
		}
		
		this.mob.isSwimmingWithPlayer = true;
		this.updateCountdownTicks = 0;
		this.state = ChaseBoatState.GO_TO_BOAT;
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by
	 * another one
	 */
	public void resetTask() {
		this.passenger = null;
		this.mob.isSwimmingWithPlayer = false;
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void tick() {
//		boolean flag = MathHelper.abs(this.passenger.moveStrafing) > 0.0F
//				|| MathHelper.abs(this.passenger.moveForward) > 0.0F;
//		float f = this.state == ChaseBoatState.GO_IN_BOAT_DIRECTION ? (flag ? 0.17999999F : 0.0F) : 0.135F;
//		this.mob.moveRelative(f, new Vec3d(/*(double) this.mob.moveStrafing*/0.0D,
//				(double) this.mob.moveVertical, (double) this.mob.moveForward));
		this.mob.move(MoverType.SELF, this.mob.getMotion());
		if (--this.updateCountdownTicks <= 0) {
			this.updateCountdownTicks = 10;
			if (this.state == ChaseBoatState.GO_TO_BOAT) {
				BlockPos blockpos = (new BlockPos(this.passenger));
						//.offset(this.passenger.getHorizontalFacing().getOpposite());
				//blockpos = blockpos.add(0, -1, 0);
				this.mob.getNavigator().tryMoveToXYZ((double) blockpos.getX(), (double) blockpos.getY(),
						(double) blockpos.getZ(), 1.0D);
				if (this.mob.getDistance(this.passenger) < 4.0F) {
					this.updateCountdownTicks = 0;
					this.state = ChaseBoatState.GO_IN_BOAT_DIRECTION;
				}
			} else if (this.state == ChaseBoatState.GO_IN_BOAT_DIRECTION) {
//				Direction direction = this.passenger.getAdjustedHorizontalFacing();
				BlockPos blockpos1 = (new BlockPos(this.passenger));//.offset(direction, 10);
				this.mob.getNavigator().tryMoveToXYZ((double) blockpos1.getX(),
						(double) (blockpos1.getY() - 1), (double) blockpos1.getZ(), 1.0D);
				if (this.mob.getDistance(this.passenger) > 12.0F) {
					this.updateCountdownTicks = 0;
					this.state = ChaseBoatState.GO_TO_BOAT;
				}
			}

		}
		
		this.mob.isSwimmingWithPlayer = true;
	}

	public enum ChaseBoatState {
		GO_TO_BOAT, GO_IN_BOAT_DIRECTION;
	}
}
