package com.prohitman.crittersaroundtheworldmod.entities;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.ITag;
import net.minecraft.util.math.*;
import net.minecraft.world.server.ServerWorld;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class FireFlyEntity extends AnimalEntity implements IFlyingAnimal {

	protected static final DataParameter<Optional<BlockPos>> ATTACHED_BLOCK_POS = EntityDataManager.createKey(FireFlyEntity.class, DataSerializers.OPTIONAL_BLOCK_POS);
	protected static final DataParameter<Direction> ATTACHED_FACE = EntityDataManager.createKey(FireFlyEntity.class, DataSerializers.DIRECTION);
	private int attachCooldown = 0;
	private int attachTicks = 0;

	public FireFlyEntity(EntityType<? extends FireFlyEntity> type, World worldIn) {
		super(type, worldIn);
		this.moveController = new FlyingMovementController(this, 20, true);
		this.lookController = new LookController(this);
		this.setPathPriority(PathNodeType.DANGER_FIRE, -1.0F);
		this.setPathPriority(PathNodeType.WATER, -1.0F);
		this.setPathPriority(PathNodeType.WATER_BORDER, 16.0F);
		this.setPathPriority(PathNodeType.COCOA, -1.0F);
		this.setPathPriority(PathNodeType.FENCE, -1.0F);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(8, new WanderGoal());
		this.goalSelector.addGoal(9, new SwimGoal(this));
	}

	public static AttributeModifierMap.MutableAttribute setFireFlyAttributes() {
		return MobEntity.func_233666_p_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 5.0D)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D)
				.createMutableAttribute(Attributes.FLYING_SPEED, 0.6D);
	}

	@Override
	public AgeableEntity func_241840_a(ServerWorld world, AgeableEntity entity) {
		return null;
	}

	@Override
	protected void registerData() {
		super.registerData();
		this.dataManager.register(ATTACHED_FACE, Direction.DOWN);
		this.dataManager.register(ATTACHED_BLOCK_POS, Optional.empty());
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.dataManager.set(ATTACHED_FACE, Direction.byIndex(compound.getByte("AttachFace")));
		this.attachCooldown = compound.getInt("AttachCooldown");
		this.attachTicks = compound.getInt("AttachTicks");
		if (compound.contains("APX")) {
			int i = compound.getInt("APX");
			int j = compound.getInt("APY");
			int k = compound.getInt("APZ");
			this.dataManager.set(ATTACHED_BLOCK_POS, Optional.of(new BlockPos(i, j, k)));
		} else {
			this.dataManager.set(ATTACHED_BLOCK_POS, Optional.empty());
		}
	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putByte("AttachFace", (byte) this.dataManager.get(ATTACHED_FACE).getIndex());
		BlockPos blockpos = this.getAttachmentPos();
		compound.putInt("AttachCooldown", attachCooldown);
		compound.putInt("AttachTicks", attachTicks);
		if (blockpos != null) {
			compound.putInt("APX", blockpos.getX());
			compound.putInt("APY", blockpos.getY());
			compound.putInt("APZ", blockpos.getZ());
		}
	}

	public Direction getAttachmentFacing() {
		return this.dataManager.get(ATTACHED_FACE);
	}

	@Nullable
	public BlockPos getAttachmentPos() {
		BlockPos blockPos = (BlockPos) ((Optional) this.dataManager.get(ATTACHED_BLOCK_POS)).orElse(null);
		return blockPos;
	}

	public void setAttachmentPos(@Nullable BlockPos pos) {
		this.dataManager.set(ATTACHED_BLOCK_POS, Optional.ofNullable(pos));
	}

	@Override
	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.ARTHROPOD;
	}

	protected void handleFluidJump(ITag<Fluid> fluidTag) {
		this.setMotion(this.getMotion().add(0.0D, 0.01D, 0.0D));
	}

	public void applyEntityCollision(Entity entityIn) {
	}

	@OnlyIn(Dist.CLIENT)
	public Vector3d func_241205_ce_() {
		return new Vector3d(0.0D, 0.5F * this.getEyeHeight(), this.getWidth() * 0.2F);
	}

	@Override
	public boolean attackEntityFrom(DamageSource dmg, float i) {
		if (dmg == DamageSource.IN_WALL) {
			return false;
		}
		this.attachTicks = 0;
		attachCooldown = 1000 + rand.nextInt(1500);
		this.dataManager.set(ATTACHED_FACE, Direction.DOWN);
		this.setAttachmentPos(null);
		return super.attackEntityFrom(dmg, i);
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

	@Override
	public void tick() {
		super.tick();
		if (attachCooldown > 0) {
			attachCooldown--;
		}
		boolean flag = true;
		if (this.getAttachmentPos() == null) {
			attachTicks = 0;
			if (this.collidedHorizontally && attachCooldown == 0 && !onGround) {
				attachCooldown = 5;
				Vector3d vec3d = this.getEyePosition(0);
				Vector3d vec3d1 = this.getLook(0);
				Vector3d vec3d2 = vec3d.add(vec3d1.x * 1, vec3d1.y * 1, vec3d1.z * 1);
				BlockRayTraceResult rayTraceblock = this.getEntityWorld().rayTraceBlocks(new RayTraceContext(vec3d, vec3d2, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
				if (rayTraceblock != null && rayTraceblock.getHitVec() != null) {
					BlockPos sidePos = rayTraceblock.getPos();
					if (world.isDirectionSolid(sidePos, this, rayTraceblock.getFace())) {
						this.setAttachmentPos(sidePos);
						this.dataManager.set(ATTACHED_FACE, rayTraceblock.getFace().getOpposite());
						this.setMotion(0, 0, 0);
					}
				}
			}
		} else if (flag) {
			BlockPos pos = this.getAttachmentPos();
			if (world.isDirectionSolid(pos, this, this.getAttachmentFacing())) {
				attachTicks++;
				attachCooldown = 150;
				this.renderYawOffset = 180.0F;
				this.prevRenderYawOffset = 180.0F;
				this.rotationYaw = 180.0F;
				this.prevRotationYaw = 180.0F;//FOR TESTING
				this.rotationYawHead = 180.0F;
				this.prevRotationYawHead = 180.0F;
				this.setMoveForward(0.0F);
				this.setMotion(0, 0, 0);
			} else {
				this.attachTicks = 0;
				this.dataManager.set(ATTACHED_FACE, Direction.DOWN);
				this.setAttachmentPos(null);
			}
		}
		if (attachTicks > 1150 && rand.nextInt(123) == 0 || this.getAttachmentPos() != null && this.getAttackTarget() != null) {
			this.attachTicks = 0;
			attachCooldown = 1000 + rand.nextInt(1500);
			this.dataManager.set(ATTACHED_FACE, Direction.DOWN);
			this.setAttachmentPos(null);
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

	/**
	 * Returns new PathNavigateGround instance
	 */
	protected PathNavigator createNavigator(World worldIn) {
		FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn) {

			public boolean canEntityStandOnPos(BlockPos pos) {
				return !this.world.getBlockState(pos.down()).isAir();
			}

			public void tick() {
				if (FireFlyEntity.this.getAttachmentPos() == null) {
					super.tick();
				}
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
			return FireFlyEntity.this.navigator.noPath() && FireFlyEntity.this.rand.nextInt(10) == 0 && FireFlyEntity.this.getAttachmentPos() == null;
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			return FireFlyEntity.this.navigator.hasPath();
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			Vector3d vector3d = this.getRandomLocation();
			if (vector3d != null) {
				FireFlyEntity.this.navigator.setPath(FireFlyEntity.this.navigator.getPathToPos(new BlockPos(vector3d), 1), 1.0D);
			}
		}

		@Nullable
		private Vector3d getRandomLocation() {
			Vector3d vector3d;
			vector3d = FireFlyEntity.this.getLook(0.0F);
								//4 , 3
			Vector3d vector3d2 = RandomPositionGenerator.findAirTarget(FireFlyEntity.this, 8 , 7, vector3d, (float)Math.PI / 2F, 2, 1);
			return vector3d2 != null ? vector3d2 : RandomPositionGenerator.findGroundTarget(FireFlyEntity.this, 8, 4, -2, vector3d, (float)Math.PI / 2F);
		}// 																															1,1,-1
	}

}
