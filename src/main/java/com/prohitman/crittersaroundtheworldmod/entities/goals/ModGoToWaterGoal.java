package com.prohitman.crittersaroundtheworldmod.entities.goals;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class ModGoToWaterGoal extends MoveToBlockGoal {
	private final CreatureEntity creature;

	public ModGoToWaterGoal(CreatureEntity creatureIn, double speedIn, int length) {
		super(creatureIn, speedIn, length);
		this.creature = creatureIn;
		this.field_203112_e = -2;
	}

	public boolean shouldMove() {
		return this.timeoutCounter % 160 == 0;
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		return !this.creature.isInWater() ? super.shouldExecute() : false;
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean shouldContinueExecuting() {
		return !this.creature.isInWater() && this.timeoutCounter <= 1200
				&& this.shouldMoveTo(this.creature.world, this.destinationBlock);
	}

	/**
	 * Return true to set given position as destination
	 */
	protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
		Block block = worldIn.getBlockState(pos).getBlock();
		return block == Blocks.WATER;
	}
}
