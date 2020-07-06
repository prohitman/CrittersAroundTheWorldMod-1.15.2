package com.prohitman.crittersaroundtheworldmod.entities;

import java.util.Random;


import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.AmbientEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class FireFlyEntity extends AmbientEntity {
	private static final DataParameter<Byte> WALL_HANGING = EntityDataManager.createKey(BatEntity.class,
			DataSerializers.BYTE);
	private static final EntityPredicate ENTITY_PREDICATE = (new EntityPredicate()).setDistance(4.0D)
			.allowFriendlyFire();
	private BlockPos blockpos = new BlockPos(this);
	private BlockPos blockposSouth = blockpos.south();
	private BlockPos blockposNorth = blockpos.north();
	private BlockPos blockposEast = blockpos.east();
	private BlockPos blockposWest = blockpos.west();
	private BlockPos blockposUp = blockpos.up();
	private BlockPos blockposDown = blockpos.down();
	private BlockPos spawnPosition;

	public FireFlyEntity(EntityType<? extends AmbientEntity> type, World worldIn) {
		super(type, worldIn);
		this.setIsFireFlyHanging(true);
	}

	protected void registerData() {
		super.registerData();
		this.dataManager.register(WALL_HANGING, (byte) 0);
	}

	@SuppressWarnings("deprecation")
	public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
		return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
	}

	public static boolean canSpawn(EntityType<FireFlyEntity> entityTypeIn, IWorld world, SpawnReason reason,
			BlockPos blockpos, Random rand) {
		return blockpos.getY() < world.getSeaLevel() + 8 && world.getLight(blockpos) < 8;
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

	protected void collideWithEntity(Entity entityIn) {

	}

	protected void collideWithNearbyEntities() {

	}

	protected boolean canTriggerWalking() {
		return false;
	}

	public boolean onLivingFall(float distance, float damageMultiplier) {
		return false;
	}

	protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
	}

	/**
	 * Return whether this entity should NOT trigger a pressure plate or a tripwire.
	 */
	public boolean doesEntityNotTriggerPressurePlate() {
		return true;
	}

	public boolean getIsFireFlyHanging() {
		return (this.dataManager.get(WALL_HANGING) & 1) != 0;
	}

	public void setIsFireFlyHanging(boolean isHanging) {
		byte b0 = this.dataManager.get(WALL_HANGING);
		if (isHanging) {
			this.dataManager.set(WALL_HANGING, (byte) (b0 | 1));
		} else {
			this.dataManager.set(WALL_HANGING, (byte) (b0 & -2));
		}

	}

	public void tick() {
		super.tick();
		if (this.getIsFireFlyHanging()) {
			this.setMotion(Vec3d.ZERO);
			this.setRawPosition(this.getPosX(),
					(double) MathHelper.floor(this.getPosY()) + 0.5D - (double) this.getHeight(), this.getPosZ());
		} else {
			this.setMotion(this.getMotion().mul(1.0D, 0.6D, 1.0D)); // was 1.0 0.6 1.0
		}
	}

	protected void updateAITasks() {
		super.updateAITasks();

		if (this.getIsFireFlyHanging()) {
			if (!this.world.getBlockState(blockposDown).isNormalCube(this.world, blockpos)
					&& !this.world.getBlockState(blockposUp).isNormalCube(this.world, blockpos)
					&& (this.world.getBlockState(blockposNorth).isNormalCube(this.world, blockpos)
							|| this.world.getBlockState(blockposSouth).isNormalCube(this.world, blockpos)
							|| this.world.getBlockState(blockposEast).isNormalCube(this.world, blockpos)
							|| this.world.getBlockState(blockposWest).isNormalCube(this.world, blockpos))) {
				if (this.rand.nextInt(200) == 0) {
					this.rotationYawHead = (float) this.rand.nextInt(360);
				}

				if (this.world.getClosestPlayer(ENTITY_PREDICATE, this) != null) {
					this.setIsFireFlyHanging(false);
					this.world.playEvent((PlayerEntity) null, 1025, blockpos, 0);
				}
			} else {
				this.setIsFireFlyHanging(false);
				this.world.playEvent((PlayerEntity) null, 1025, blockpos, 0);
			}
		} else {
			if (this.spawnPosition != null
					&& (!this.world.isAirBlock(this.spawnPosition) || this.spawnPosition.getY() < 1)) {
				this.spawnPosition = null;
			}

			if (this.spawnPosition == null || this.rand.nextInt(30) == 0
					|| this.spawnPosition.withinDistance(this.getPositionVec(), 2.0D)) {
				this.spawnPosition = new BlockPos(
						this.getPosX() + (double) this.rand.nextInt(7) - (double) this.rand.nextInt(7),
						this.getPosY() + (double) this.rand.nextInt(6) - 2.0D,
						this.getPosZ() + (double) this.rand.nextInt(7) - (double) this.rand.nextInt(7));
			}

			double d0 = (double) this.spawnPosition.getX() + 0.5D - this.getPosX();
			double d1 = (double) this.spawnPosition.getY() + 0.1D - this.getPosY();
			double d2 = (double) this.spawnPosition.getZ() + 0.5D - this.getPosZ();
			Vec3d vec3d = this.getMotion();
			Vec3d vec3d1 = vec3d.add((Math.signum(d0) * 0.5D - vec3d.x) * (double) 0.1F,
					(Math.signum(d1) * (double) 0.7F - vec3d.y) * (double) 0.1F,
					(Math.signum(d2) * 0.5D - vec3d.z) * (double) 0.1F);
			this.setMotion(vec3d1);
			float f = (float) (MathHelper.atan2(vec3d1.z, vec3d1.x) * (double) (180F / (float) Math.PI)) - 90.0F;
			float f1 = MathHelper.wrapDegrees(f - this.rotationYaw);
			this.moveForward = 0.5F;
			this.rotationYaw += f1;
			if (this.rand.nextInt(100) == 0
					&& !this.world.getBlockState(blockposDown).isNormalCube(this.world, blockposDown)
					&& !this.world.getBlockState(blockposUp).isNormalCube(this.world, blockposUp)
					&& (this.world.getBlockState(blockposNorth).isNormalCube(this.world, blockposNorth)
							|| this.world.getBlockState(blockposSouth).isNormalCube(this.world, blockposSouth)
							|| this.world.getBlockState(blockposEast).isNormalCube(this.world, blockposEast)
							|| this.world.getBlockState(blockposWest).isNormalCube(this.world, blockposWest))) {
				this.setIsFireFlyHanging(true);
			}
		}
	}

	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		return sizeIn.height / 2.0F;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			if (!this.world.isRemote && this.getIsFireFlyHanging()) {
				this.setIsFireFlyHanging(false);
			}

			return super.attackEntityFrom(source, amount);
		}
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.dataManager.set(WALL_HANGING, compound.getByte("FireFlyFlags"));
	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putByte("FireFlyFlags", this.dataManager.get(WALL_HANGING));
	}
}
