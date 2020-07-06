package com.prohitman.crittersaroundtheworldmod.entities.goals;

import com.prohitman.crittersaroundtheworldmod.entities.FatSealEntity;

import net.minecraft.entity.ai.goal.RandomWalkingGoal;

public class FatSealWanderGoal extends RandomWalkingGoal {
	private final FatSealEntity seal;

	public FatSealWanderGoal(FatSealEntity seal, double speedIn, int chanceIn) {
		super(seal, speedIn, chanceIn);
		this.seal = seal;
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		return !this.seal.isGoingHome() && !this.seal.isInWater() ? super.shouldExecute() : false;
	}
}
