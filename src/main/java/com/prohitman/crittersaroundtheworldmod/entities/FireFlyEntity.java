package com.prohitman.crittersaroundtheworldmod.entities;

import java.util.EnumSet;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.fluid.Fluid;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.Tag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireFlyEntity extends AnimalEntity implements IFlyingAnimal {

	private float rollAmount;
	private float rollAmountO;
	private int underWaterTicks;

	public FireFlyEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
		super(type, worldIn);
		this.moveController = new FlyingMovementController(this, 20, true);
		this.lookController = new LookController(this);
		this.setPathPriority(PathNodeType.WATER, -1.0F);
		this.setPathPriority(PathNodeType.COCOA, -1.0F);
		this.setPathPriority(PathNodeType.FENCE, -1.0F);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(8, new FireFlyEntity.WanderGoal());
		this.goalSelector.addGoal(9, new SwimGoal(this));
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttributes().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
		this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue((double) 0.6F);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double) 0.3F);
	}

	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.ARTHROPOD;
	}

	@OnlyIn(Dist.CLIENT)
	public float getBodyPitch(float p_226455_1_) {
		return MathHelper.lerp(p_226455_1_, this.rollAmountO, this.rollAmount);
	}

	private void updateBodyPitch() {
		this.rollAmountO = this.rollAmount;
		this.rollAmount = Math.max(0.0F, this.rollAmount - 0.24F);

	}

	public static boolean canSpawn(EntityType<FireFlyEntity> entityTypeIn, IWorld world, SpawnReason reason,
			BlockPos blockpos, Random rand) {
		Block block = world.getBlockState(blockpos.down()).getBlock();
		return (block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL
				|| block == Blocks.GRASS_BLOCK || block == Blocks.AIR) && world.getLightSubtracted(blockpos, 0) > 12;
	}

	public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
		return worldIn.getBlockState(pos).isAir(worldIn, pos) ? 10.0F : 0.0F;
	}

	public boolean canBePushed() {
		return true;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return null;
	}

	protected boolean makeFlySound() {
		return true;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_BEE_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_BEE_HURT;
	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		return sizeIn.height * 0.5F;
	}

	protected boolean canTriggerWalking() {
		return false;
	}

	public boolean onLivingFall(float distance, float damageMultiplier) {
		return false;
	}

	protected void handleFluidJump(Tag<Fluid> fluidTag) {
		this.setMotion(this.getMotion().add(0.0D, 0.01D, 0.0D));
	}

	@Override
	protected void updateAITasks() {
		super.updateAITasks();
		if (this.isInWaterOrBubbleColumn()) {
			++this.underWaterTicks;
		} else {
			this.underWaterTicks = 0;
		}

		if (this.underWaterTicks > 20) {
			this.attackEntityFrom(DamageSource.DROWN, 1.0F);
		}
	}

	public void tick() {
		super.tick();
		this.updateBodyPitch();
	}

	@Override
	public AgeableEntity createChild(AgeableEntity ageable) {
		return null;
	}

	/**
	 * Returns new PathNavigateGround instance
	 */
	protected PathNavigator createNavigator(World worldIn) {
		FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn) {
			public boolean canEntityStandOnPos(BlockPos pos) {
				return !this.world.getBlockState(pos.down()).isAir(worldIn, pos);
			}

			public void tick() {
				super.tick();
			}
		};
		flyingpathnavigator.setCanOpenDoors(false);
		flyingpathnavigator.setCanSwim(false);
		flyingpathnavigator.setCanEnterDoors(true);
		return flyingpathnavigator;
	}

	class WanderGoal extends Goal {
		WanderGoal() {
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean shouldExecute() {
			return FireFlyEntity.this.navigator.noPath() && FireFlyEntity.this.rand.nextInt(10) == 0;
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			return FireFlyEntity.this.navigator.func_226337_n_();
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			Vec3d vec3d = this.getRandomLocation();
			if (vec3d != null) {
				FireFlyEntity.this.navigator.setPath(FireFlyEntity.this.navigator.getPathToPos(new BlockPos(vec3d), 1),
						1.0D);
			}

		}

		@Nullable
		private Vec3d getRandomLocation() {
			Vec3d vec3d;
			vec3d = FireFlyEntity.this.getLook(0.0F);
			Vec3d vec3d2 = RandomPositionGenerator.findAirTarget(FireFlyEntity.this, 8, 7, vec3d,
					((float) Math.PI / 2F), 2, 1);
			return vec3d2 != null ? vec3d2
					: RandomPositionGenerator.findGroundTarget(FireFlyEntity.this, 8, 4, -2, vec3d,
							(double) ((float) Math.PI / 2F));
		}
	}

}
