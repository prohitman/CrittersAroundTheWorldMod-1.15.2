package com.prohitman.crittersaroundtheworldmod.entities.goals.fatseal;

import java.util.EnumSet;

import com.prohitman.crittersaroundtheworldmod.entities.FatSealEntity;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class FatSealPlayerTemptGoal extends Goal {
	private static final EntityPredicate ENTITY_PREDICATE = (new EntityPredicate()).setDistance(10.0D)
			.allowInvulnerable().allowFriendlyFire().setSkipAttackChecks().setLineOfSiteRequired();
	private final FatSealEntity creature;
	private PlayerEntity tempter;
	private final double speed;
	private int cooldown;
	private final Ingredient temptItem;

	public FatSealPlayerTemptGoal(FatSealEntity creature, double speedIn, Ingredient temptItemsIn) {
		this.creature = creature;
		this.speed = speedIn;
		this.temptItem = temptItemsIn;
		this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	/*
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		if (this.cooldown > 0) {
			--this.cooldown;
			return false;
		} else {
			this.tempter = this.creature.world.getClosestPlayer(ENTITY_PREDICATE, this.creature);
			if (this.tempter == null || !this.creature.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty()) {
				return false;
			} else {
				return this.isTemptedBy(this.tempter.getHeldItemMainhand())
						|| this.isTemptedBy(this.tempter.getHeldItemOffhand());
			}
		}
	}
	
	@Override
	public void startExecuting() {
		super.startExecuting();
		this.creature.isGettingTempted = true;
	}

	protected boolean isTemptedBy(ItemStack stack) {
		return this.temptItem.test(stack);
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean shouldContinueExecuting() {
		return this.shouldExecute();
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by
	 * another one
	 */
	public void resetTask() {
		this.tempter = null;
		this.creature.getNavigator().clearPath();
		this.cooldown = 100;
		this.creature.isGettingTempted = false;
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void tick() {
		this.creature.getLookController().setLookPositionWithEntity(this.tempter,
				(float) (this.creature.getHorizontalFaceSpeed() + 20), (float) this.creature.getVerticalFaceSpeed());
		if (this.creature.getDistanceSq(this.tempter) < 6.25D) {
			this.creature.getNavigator().clearPath();
		} else {
			this.creature.getNavigator().tryMoveToEntityLiving(this.tempter, this.speed);
		}
		
		this.creature.isGettingTempted = true;

	}
}
