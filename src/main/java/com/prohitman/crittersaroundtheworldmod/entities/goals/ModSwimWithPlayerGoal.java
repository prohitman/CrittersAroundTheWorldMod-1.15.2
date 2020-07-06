package com.prohitman.crittersaroundtheworldmod.entities.goals;

import java.util.EnumSet;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class ModSwimWithPlayerGoal extends Goal {
	private final CreatureEntity creature;
	private final double speed;
	private PlayerEntity targetPlayer;
	public boolean isSwimmingWithPlayer;
	private static final EntityPredicate ENTITY_PREDICATE = (new EntityPredicate()).setDistance(3.0D)
			.allowFriendlyFire().allowInvulnerable();

	public ModSwimWithPlayerGoal(CreatureEntity creatureIn, double speedIn) {
		this.creature = creatureIn;
		this.speed = speedIn;
		this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		this.targetPlayer = this.creature.world.getClosestPlayer(ENTITY_PREDICATE, this.creature);
		return this.targetPlayer == null ? false : this.targetPlayer.isSwimming() && this.creature.isInWater();
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean shouldContinueExecuting() {
		return this.targetPlayer != null && this.targetPlayer.isSwimming()
				&& this.creature.getDistanceSq(this.targetPlayer) < 256.0D && this.creature.isInWater();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.targetPlayer.addPotionEffect(new EffectInstance(Effects.LUCK, 100));
		this.isSwimmingWithPlayer = true;
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by
	 * another one
	 */
	public void resetTask() {
		this.targetPlayer = null;
		this.creature.getNavigator().clearPath();
		this.isSwimmingWithPlayer = false;
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void tick() {
		this.creature.getLookController().setLookPositionWithEntity(this.targetPlayer,
				(float) (this.creature.getHorizontalFaceSpeed() + 20), (float) this.creature.getVerticalFaceSpeed());
		if (this.creature.getDistanceSq(this.targetPlayer) < 6.25D && this.creature.isInWater()) {
			this.creature.getNavigator().clearPath();
		} else {
			this.creature.getNavigator().tryMoveToEntityLiving(this.targetPlayer, this.speed);
		}

		if (this.targetPlayer.isSwimming() && this.targetPlayer.world.rand.nextInt(6) == 0
				&& this.creature.isInWater()) {
			this.targetPlayer.addPotionEffect(new EffectInstance(Effects.LUCK, 100));
			
		}
		this.isSwimmingWithPlayer = true;
	}
}
