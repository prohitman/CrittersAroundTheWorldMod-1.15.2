package com.prohitman.crittersaroundtheworldmod.entities;

import java.util.EnumSet;
import java.util.Random;

import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.Tag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
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

	private static final DataParameter<Byte> HANGING = EntityDataManager.createKey(FireFlyEntity.class,
			DataSerializers.BYTE);
	private static final EntityPredicate ENTITY_HANG_PREDICATE = (new EntityPredicate()).setDistance(4.0D)
			.allowFriendlyFire();
	private float rollAmount;
	private float rollAmountO;
	private int underWaterTicks;
	protected Direction facingDirection = Direction.SOUTH;

	public FireFlyEntity(EntityType<? extends FireFlyEntity> type, World worldIn) {
		super(type, worldIn);
		this.moveController = new FlyingMovementController(this, 20, true);
		this.lookController = new LookController(this);
		this.setPathPriority(PathNodeType.WATER, -1.0F);
		this.setPathPriority(PathNodeType.COCOA, -1.0F);
		this.setPathPriority(PathNodeType.FENCE, -1.0F);
		// this.setIsFlyHanging(true);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(8, new FireFlyEntity.WanderGoal(this));
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

	@Override
	protected void registerData() {
		super.registerData();
		this.dataManager.register(HANGING, (byte) 0);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.dataManager.set(HANGING, compound.getByte("FireFlyFlags"));
	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putByte("FireFlyFlags", this.dataManager.get(HANGING));
	}

	public boolean getIsFlyHanging() {
		return (this.dataManager.get(HANGING) & 1) != 0;
	}

	public void setIsFlyHanging(boolean isHanging) {
		byte b0 = this.dataManager.get(HANGING);
		if (isHanging) {
			this.dataManager.set(HANGING, (byte) (b0 | 1));
		} else {
			this.dataManager.set(HANGING, (byte) (b0 & -2));
		}

	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			if (!this.world.isRemote && this.getIsFlyHanging()) {
				this.setIsFlyHanging(false);
			}

			return super.attackEntityFrom(source, amount);
		}
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

	@SuppressWarnings("deprecation")
	public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
		return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
	}

	public boolean canBePushed() {
		return true;
	}

//	protected void updateFacingWithBoundingBox(Direction facingDirectionIn) {
//		if (this.getIsFlyHanging()) {
//			Validate.notNull(facingDirectionIn);
//			this.facingDirection = facingDirectionIn;
//			if (facingDirectionIn.getAxis().isHorizontal()) {
//				this.rotationPitch = 0.0F;
//				this.rotationYaw = (float) (this.facingDirection.getHorizontalIndex() * 90);
//			} else {
//				this.rotationPitch = (float) (-90 * facingDirectionIn.getAxisDirection().getOffset());
//				this.rotationYaw = 0.0F;
//			}
//
//			this.prevRotationPitch = this.rotationPitch;
//			this.prevRotationYaw = this.rotationYaw;
//		}
//	}

	@Override
	public void applyEntityCollision(Entity entityIn) {
		if (!this.isRidingSameEntity(entityIn)) {
			if (!entityIn.noClip && !this.noClip) {
				double d0 = entityIn.getPosX() - this.getPosX();
				double d1 = entityIn.getPosZ() - this.getPosZ();
				double d2 = MathHelper.absMax(d0, d1);
				if (d2 >= (double) 0.01F) {
					d2 = (double) MathHelper.sqrt(d2);
					d0 = d0 / d2;
					d1 = d1 / d2;
					double d3 = 1.0D / d2;
					if (d3 > 1.0D) {
						d3 = 1.0D;
					}

					d0 = d0 * d3;
					d1 = d1 * d3;
					d0 = d0 * (double) 0.05F;
					d1 = d1 * (double) 0.05F;
					d0 = d0 * (double) (1.0F - this.entityCollisionReduction);
					d1 = d1 * (double) (1.0F - this.entityCollisionReduction);
					if (!this.isBeingRidden()) {
						this.addVelocity(-d0, 0.0D, -d1);
					}

					if (!entityIn.isBeingRidden()) {
						entityIn.addVelocity(d0, 0.0D, d1);
					}
				}

			}
		}
	}

	/**
	 * Return whether this entity should NOT trigger a pressure plate or a tripwire.
	 */
	public boolean doesEntityNotTriggerPressurePlate() {
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
		// super.updateAITasks();
		if (this.isInWaterOrBubbleColumn()) {
			++this.underWaterTicks;
		} else {
			this.underWaterTicks = 0;
		}

		if (this.underWaterTicks > 20) {
			this.attackEntityFrom(DamageSource.DROWN, 1.0F);
		}
		BlockPos blockpos = new BlockPos(this);
		BlockPos blockpos1 = blockpos.north();
		BlockPos blockpos2 = blockpos.south();
		BlockPos blockpos3 = blockpos.east();
		BlockPos blockpos4 = blockpos.west();

		if (this.getIsFlyHanging()) {
			if (this.world.getBlockState(blockpos1).isNormalCube(this.world, blockpos)
					|| this.world.getBlockState(blockpos2).isNormalCube(this.world, blockpos)
					|| this.world.getBlockState(blockpos3).isNormalCube(this.world, blockpos)
					|| this.world.getBlockState(blockpos4).isNormalCube(this.world, blockpos)) {
				if (this.world.getClosestPlayer(ENTITY_HANG_PREDICATE, this) != null) {
					this.setIsFlyHanging(false);
					this.world.playEvent((PlayerEntity) null, 1025, blockpos, 0);
				}
				// this.setPosition((double)MathHelper.floor(this.getPosX()) + 1.175D -
				// (double)this.getWidth(), this.getPosY(), this.getPosZ());
			}

//			else if (!this.collidedHorizontally) {
//				this.setIsFlyHanging(false);
//				this.world.playEvent((PlayerEntity) null, 1025, blockpos, 0);
//			}
//
//			else if (this.getPosX() >= blockpos.getX() + 0.25) {
//				this.setIsFlyHanging(false);
//				this.world.playEvent((PlayerEntity) null, 1025, blockpos, 0);
//			}

			else {
				this.setIsFlyHanging(false);
				this.world.playEvent((PlayerEntity) null, 1025, blockpos, 0);
			}
		}

		else if (/* this.rand.nextInt(100) == 0 && */(this.world.getBlockState(blockpos1).isNormalCube(this.world,
				blockpos1) || this.world.getBlockState(blockpos2).isNormalCube(this.world, blockpos2)
				|| this.world.getBlockState(blockpos3).isNormalCube(this.world, blockpos3)
				|| this.world.getBlockState(blockpos4).isNormalCube(this.world, blockpos4)
						&& this.collidedHorizontally)) {
			this.setIsFlyHanging(true);
		}
	}

	public void tick() {
		super.tick();
		if (!this.getIsFlyHanging()) {
			this.updateBodyPitch();
		}
		BlockPos blockpos = new BlockPos(this);
		BlockPos blockpos1 = blockpos.north();
		BlockPos blockpos2 = blockpos.south();
		BlockPos blockpos3 = blockpos.east();
		BlockPos blockpos4 = blockpos.west();
		if (this.getIsFlyHanging()) {
			Vec3d vec3d = new Vec3d(blockpos1);
			if (this.world.getBlockState(blockpos1).isNormalCube(this.world, blockpos)
					|| this.world.getBlockState(blockpos2).isNormalCube(this.world, blockpos)
					|| this.world.getBlockState(blockpos3).isNormalCube(this.world, blockpos)
					|| this.world.getBlockState(blockpos4).isNormalCube(this.world, blockpos)) {
//				if (this.rand.nextInt(200) == 0) {
//					this.rotationYawHead = (float) this.rand.nextInt(360);
//				}
				if (this.world.getBlockState(blockpos1).isNormalCube(this.world, blockpos)) {
					this.getLookController().setLookPosition(vec3d.x + 0.5, vec3d.y, vec3d.z, blockpos1.getX() + 180f,
							blockpos1.getZ());
					this.setPosition(this.getPosX(), this.getPosY(),
							(double) MathHelper.floor(this.getPosZ()) + 0.64D - (double) this.getWidth());

				}
				if (this.world.getBlockState(blockpos2).isNormalCube(this.world, blockpos)) {
					this.getLookController().setLookPosition(vec3d.x, vec3d.y, vec3d.z + 90, blockpos2.getX(),
							blockpos2.getZ());
					this.setPosition(this.getPosX(), this.getPosY(),
							(double) MathHelper.floor(this.getPosZ()) + 1.175D - (double) this.getWidth());

				}
				if (this.world.getBlockState(blockpos3).isNormalCube(this.world, blockpos)) {
					this.getLookController().setLookPosition(vec3d.x + 390, vec3d.y, vec3d.z, blockpos3.getX() + 180f,
							blockpos3.getZ());
					this.setPosition((double) MathHelper.floor(this.getPosX()) + 1.175D - (double) this.getWidth(),
							this.getPosY(), this.getPosZ());

				}
				if (this.world.getBlockState(blockpos4).isNormalCube(this.world, blockpos)) {
					this.getLookController().setLookPosition(vec3d.x - 390, vec3d.y, vec3d.z, blockpos4.getX() + 180f,
							blockpos4.getZ());
					this.setPosition((double) MathHelper.floor(this.getPosX()) + 0.64D - (double) this.getWidth(),
							this.getPosY(), this.getPosZ());

				}
				// this.setPosition((double)MathHelper.floor(this.getPosX()) + 1.175D -
				// (double)this.getWidth(), this.getPosY(), this.getPosZ());
			}
			this.setMotion(Vec3d.ZERO);

		} else {
			this.setMotion(this.getMotion().mul(1.0D, 0.8D, 1.0D));

		}
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
			@SuppressWarnings("deprecation")
			public boolean canEntityStandOnPos(BlockPos pos) {
				return !this.world.getBlockState(pos.down()).isAir();
			}

			public void tick() {
				if (!FireFlyEntity.this.getIsFlyHanging()) {
					super.tick();
				}
			}
		};
		flyingpathnavigator.setCanOpenDoors(false);
		flyingpathnavigator.setCanSwim(false);
		flyingpathnavigator.setCanEnterDoors(true);
		return flyingpathnavigator;
	}

	static class WanderGoal extends Goal {
		FireFlyEntity firefly;

		WanderGoal(FireFlyEntity firefly) {
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
			this.firefly = firefly;
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state
		 * necessary for execution in this method as well.
		 */
		public boolean shouldExecute() {
			return this.firefly.navigator.noPath() && this.firefly.rand.nextInt(10) == 0
					&& !this.firefly.getIsFlyHanging();
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			return this.firefly.navigator.func_226337_n_();
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			Vec3d vec3d = this.getRandomLocation();
			if (vec3d != null) {
				this.firefly.navigator.setPath(this.firefly.navigator.getPathToPos(new BlockPos(vec3d), 1), 1.0D);
			}
		}

		@Nullable
		private Vec3d getRandomLocation() {
			Vec3d vec3d;
			vec3d = this.firefly.getLook(0.0F);
			Vec3d vec3d2 = RandomPositionGenerator.findAirTarget(this.firefly, 8, 7, vec3d, ((float) Math.PI / 2F), 2,
					1);
			return vec3d2 != null ? vec3d2
					: RandomPositionGenerator.findGroundTarget(this.firefly, 8, 4, -2, vec3d,
							(double) ((float) Math.PI / 2F));
		}
	}

}
