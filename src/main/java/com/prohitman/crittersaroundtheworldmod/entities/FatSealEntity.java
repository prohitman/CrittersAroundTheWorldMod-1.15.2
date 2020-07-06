package com.prohitman.crittersaroundtheworldmod.entities;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.prohitman.crittersaroundtheworldmod.init.ModSounds;
import com.prohitman.crittersaroundtheworldmod.entities.goals.FatSealWanderGoal;
import com.prohitman.crittersaroundtheworldmod.entities.goals.ModGoToWaterGoal;
import com.prohitman.crittersaroundtheworldmod.entities.goals.fatseal.FatSealFollowBoatGoal;
import com.prohitman.crittersaroundtheworldmod.entities.goals.fatseal.FatSealGoHomeGoal;
import com.prohitman.crittersaroundtheworldmod.entities.goals.fatseal.FatSealPlayerTemptGoal;
import com.prohitman.crittersaroundtheworldmod.init.ModEntities;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.fish.AbstractGroupFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.pathfinding.WalkAndSwimNodeProcessor;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FatSealEntity extends AnimalEntity {
	private static final DataParameter<BlockPos> HOME_POS = EntityDataManager.createKey(FatSealEntity.class,
			DataSerializers.BLOCK_POS);
	private static final DataParameter<Boolean> GOING_HOME = EntityDataManager.createKey(FatSealEntity.class,
			DataSerializers.BOOLEAN);
	private static final Ingredient TEMPTATION_ITEMS = Ingredient.fromItems(Items.COD, Items.SALMON,
			Items.TROPICAL_FISH);
	private static final DataParameter<Boolean> TRAVELLING = EntityDataManager.createKey(FatSealEntity.class,
			DataSerializers.BOOLEAN);
	private static final DataParameter<BlockPos> TRAVEL_POS = EntityDataManager.createKey(FatSealEntity.class,
			DataSerializers.BLOCK_POS);
	private static final DataParameter<Integer> EATING_TICKS = EntityDataManager.createKey(FatSealEntity.class,
			DataSerializers.VARINT);
	private static final Predicate<ItemEntity> ITEM_SELECTOR = (itementity) -> {
		Item item = itementity.getItem().getItem();
		return (item == Items.COD || item == Items.SALMON || item == Items.TROPICAL_FISH) && !itementity.cannotPickup()
				&& itementity.isAlive();
	};

	private int jumpTicks;
	private int jumpDuration;
	private boolean wasOnGround;
	public boolean isSwimmingWithPlayer;
	private int ticksUntilJump;
	public boolean isGettingTempted;
	public int ticksInWater = 0;
	// private long lastOnPlayed;
	private boolean isPaniced;

	public FatSealEntity(EntityType<? extends AnimalEntity> type, World worldIn) {
		super(type, worldIn);
		this.jumpController = new FatSealJumpHelperController(this);
		this.moveController = new FatSealEntity.MoveHelperController(this);
		this.setPathPriority(PathNodeType.WATER, 0.0F);
		this.setMovementSpeed(0.0D);
		this.stepHeight = 1.0F;
		if (!this.isChild()) {
			this.setCanPickUpLoot(true);
		}
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new FatSealPanicGoal(this, 1.2D));
		this.goalSelector.addGoal(1, new BreedGoal(this, 1.2D));
		this.goalSelector.addGoal(2, new FatSealEntity.SwimWithPlayerGoal(this, 2.0D));
		this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.1D));
		// this.goalSelector.addGoal(3, new FatSealEntity.GetOutOfWaterGoal(this,
		// 1.0D));
		this.goalSelector.addGoal(4, new FatSealGoHomeGoal(this, 1.0D));
		this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(4, new FatSealPlayerTemptGoal(this, 1.0D, TEMPTATION_ITEMS));
		this.goalSelector.addGoal(6, new ModGoToWaterGoal(this, 1.0D, 16));
		this.goalSelector.addGoal(7, new FatSealEntity.TravelGoal(this, 1.0D)); // NEEDS TEST
		this.goalSelector.addGoal(8, new FatSealFollowBoatGoal(this));
		this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(9, new FatSealEntity.PickUpFoodGoal(this));
		this.goalSelector.addGoal(9, new FatSealWanderGoal(this, 1.0D, 100));
		this.goalSelector.addGoal(9, new AvoidEntityGoal<>(this, PolarBearEntity.class, 8.0F, 1.0D, 2.0D));
		this.targetSelector.addGoal(2,
				new NearestAttackableTargetGoal<>(this, AbstractGroupFishEntity.class, 2, true, true, (entity) -> {
					return this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty();
				}));
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16.0D);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
		this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
	}

	public void setHome(BlockPos position) {
		this.dataManager.set(HOME_POS, position);
	}

	public BlockPos getHome() {
		return this.dataManager.get(HOME_POS);
	}

	public boolean isGoingHome() {
		return this.dataManager.get(GOING_HOME);
	}

	public void setGoingHome(boolean isGoingHome) {
		this.dataManager.set(GOING_HOME, isGoingHome);
	}

	public boolean isEating() {
		return (Integer) this.dataManager.get(EATING_TICKS) > 0;
	}

	public void setEating(boolean eating) {
		this.dataManager.set(EATING_TICKS, eating ? 1 : 0);
	}

	private int getEatingTicks() {
		return (Integer) this.dataManager.get(EATING_TICKS);
	}

	private void setEatingTicks(int eatingTicks) {
		this.dataManager.set(EATING_TICKS, eatingTicks);
	}

	private boolean isTravelling() {
		return this.dataManager.get(TRAVELLING);
	}

	private void setTravelling(boolean isTravelling) {
		this.dataManager.set(TRAVELLING, isTravelling);
	}

	private void setTravelPos(BlockPos position) {
		this.dataManager.set(TRAVEL_POS, position);
	}

	private BlockPos getTravelPos() {
		return this.dataManager.get(TRAVEL_POS);
	}

	protected void registerData() {
		super.registerData();
		this.dataManager.register(HOME_POS, BlockPos.ZERO);
		this.dataManager.register(GOING_HOME, false);
		this.dataManager.register(TRAVEL_POS, BlockPos.ZERO);
		this.dataManager.register(TRAVELLING, false);
		this.dataManager.register(EATING_TICKS, 0);
	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putInt("HomePosX", this.getHome().getX());
		compound.putInt("HomePosY", this.getHome().getY());
		compound.putInt("HomePosZ", this.getHome().getZ());
		compound.putInt("TravelPosX", this.getTravelPos().getX());
		compound.putInt("TravelPosY", this.getTravelPos().getY());
		compound.putInt("TravelPosZ", this.getTravelPos().getZ());
	}

	public void readAdditional(CompoundNBT compound) {
		int i = compound.getInt("HomePosX");
		int j = compound.getInt("HomePosY");
		int k = compound.getInt("HomePosZ");
		this.setHome(new BlockPos(i, j, k));
		super.readAdditional(compound);
		int l = compound.getInt("TravelPosX");
		int i1 = compound.getInt("TravelPosY");
		int j1 = compound.getInt("TravelPosZ");
		this.setTravelPos(new BlockPos(l, i1, j1));
	}

	public static boolean canSpawn(EntityType<FatSealEntity> entityTypeIn, IWorld world, SpawnReason reason,
			BlockPos blockpos, Random rand) {
		return blockpos.getY() < world.getSeaLevel() + 4 && (world.getBlockState(blockpos.down()).getBlock().getTags() == BlockTags.ICE || world.getBlockState(blockpos.down()).getBlock() == Blocks.SNOW) && world.getLightSubtracted(blockpos, 0) > 8;
	}

	@Nullable
	public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
			@Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		this.setHome(new BlockPos(this));
		this.setTravelPos(BlockPos.ZERO);
		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source == DamageSource.FALL && this.isPaniced) {
			this.getNavigator().clearPath();
		}
		return this.isInvulnerableTo(source) ? false : super.attackEntityFrom(source, amount);
	}

	public void travel(Vec3d positionIn) {
		if (this.isServerWorld() && this.isInWater()) {
			this.moveRelative(0.1F, positionIn);
			this.move(MoverType.SELF, this.getMotion());
			this.setMotion(this.getMotion().scale(0.9D));
			if (this.getAttackTarget() == null
					&& (!this.isGoingHome() || !this.getHome().withinDistance(this.getPositionVec(), 20.0D))) {
				this.setMotion(this.getMotion().add(0.0D, -0.005D, 0.0D));
			}
		} else {
			super.travel(positionIn);
		}
	}

	protected float getJumpUpwardsMotion() {
		if (!this.collidedHorizontally
				&& (!this.moveController.isUpdating() || !(this.moveController.getY() > this.getPosY() + 0.5D))) {
			Path path = this.navigator.getPath();
			if (path != null && path.getCurrentPathIndex() < path.getCurrentPathLength()) {
				Vec3d vec3d = path.getPosition(this);
				if (vec3d.y > this.getPosY() + 0.5F) {
					return 0.2F;// was 0.5
				}
			}

			return this.moveController.getSpeed() <= 0.6D ? 0.1F : 0.2F;
		} else {
			return 0.2F;// was 0.5
		}
	}

	@Override
	protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
		super.updateFallState(y, onGroundIn, state, pos);
	}

	/**
	 * Causes this entity to do an upwards motion (jumping).
	 */
	protected void jump() {
		super.jump();
		double d0 = this.moveController.getSpeed();
		if (d0 > 0.0D) {
			double d1 = horizontalMag(this.getMotion());
			if (d1 < 0.01D) {
				this.moveRelative(0.1F, new Vec3d(0.0D, 0.0D, 1.0D));
			}
		}

		if (!this.world.isRemote) {
			this.world.setEntityState(this, (byte) 1);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public float getJumpCompletion(float delta) {
		return this.jumpDuration == 0 ? 0.0F : ((float) this.jumpTicks + delta) / (float) this.jumpDuration;
	}

	public void setMovementSpeed(double newSpeed) {
		this.getNavigator().setSpeed(newSpeed);
		this.moveController.setMoveTo(this.moveController.getX(), this.moveController.getY(),
				this.moveController.getZ(), newSpeed);
	}

	public void setJumping(boolean jumping) {
		super.setJumping(jumping);
		if (jumping) {
			this.playSound(this.getJumpSound(), this.getSoundVolume(),
					((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
		}
	}

	public void startTravelling() {
		this.setTravelling(true);
	}

	public void startJumping() {
		this.setJumping(true);
		this.jumpDuration = 10;
		this.jumpTicks = 0;
	}

	private void lookTowards(double x, double z) {
		this.rotationYaw = (float) (MathHelper.atan2(z - this.getPosZ(), x - this.getPosX())
				* (double) (180F / (float) Math.PI)) - 90.0F;
	}

	private void enableJumpControl() {
		((FatSealJumpHelperController) this.jumpController).setCanJump(true);
	}

	private void disableJumpControl() {
		((FatSealJumpHelperController) this.jumpController).setCanJump(false);
	}

	private void doScheduleJump() {
		if (this.moveController.getSpeed() < 1.2D) {
			this.ticksUntilJump = 10;
		} else {
			this.ticksUntilJump = 5;
		}
	}

	private void checkLandingDelay() {
		this.doScheduleJump();
		this.disableJumpControl();
	}

	public void livingTick() {
		super.livingTick();
		if (this.collidedHorizontally && this.isInWater() && !this.world.isRemote && this.isAlive()) {
			this.getNavigator().clearPath();
		}
		if (this.isInWater() && !this.world.isRemote && this.isAlive()) {
			++this.ticksInWater;
		}

		else if (!this.isInWater() && !this.world.isRemote && this.isAlive() && this.ticksInWater != 0) {
			this.ticksInWater = 0;
		}

		if (this.jumpTicks != this.jumpDuration) {
			++this.jumpTicks;
		} else if (this.jumpDuration != 0) {
			this.jumpTicks = 0;
			this.jumpDuration = 0;
			this.setJumping(false);
		}

	}

	private boolean canEat(ItemStack stack) {
		return this.isBreedingItem(stack);
	}

	@Override
	public boolean canPickUpItem(ItemStack itemstackIn) {
		EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(itemstackIn);
		if (!this.getItemStackFromSlot(equipmentslottype).isEmpty()) {
			return false;
		} else {
			return equipmentslottype == EquipmentSlotType.MAINHAND && super.canPickUpItem(itemstackIn);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id == 1) {
			this.createRunningParticles();
			this.jumpDuration = 10;
			this.jumpTicks = 0;
		} else {
			super.handleStatusUpdate(id);
		}
	}

	private void updateEatingAnimation() {
		if (!this.isEating() && !this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty()
				&& this.rand.nextInt(80) == 1) {
			this.setEating(true);
		} else if (this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty()) {
			this.setEating(false);
		}

		if (this.isEating()) {
			this.playEatingAnimation();
			if (!this.world.isRemote && this.getEatingTicks() > 80 && this.rand.nextInt(20) == 1) {
				if (this.getEatingTicks() > 100 && this.canEat(this.getItemStackFromSlot(EquipmentSlotType.MAINHAND))) {
					if (!this.world.isRemote) {
						this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
					}

				}

				this.setEating(false);
				return;
			}

			this.setEatingTicks(this.getEatingTicks() + 1);
		}

	}

	private void playEatingAnimation() {
		// long l = this.world.getGameTime();
		if (this.getEatingTicks() % 5 == 0) {
			this.playSound(SoundEvents.ENTITY_PANDA_EAT, 0.5F + 0.5F * (float) this.rand.nextInt(2),
					(this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			for (int i = 0; i < 6; ++i) {
				Vec3d vec3d = new Vec3d(((double) this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D,
						((double) this.rand.nextFloat() - 0.5D) * 0.1D);
				vec3d = vec3d.rotatePitch(-this.rotationPitch * ((float) Math.PI / 180F));
				vec3d = vec3d.rotateYaw(-this.rotationYaw * ((float) Math.PI / 180F));
				double d0 = (double) (-this.rand.nextFloat()) * 0.6D - 0.3D;
				Vec3d vec3d1 = new Vec3d((((double) this.rand.nextFloat() - 0.5D) * 0.5D), d0 + 0.35D,
						1.0D + ((double) this.rand.nextFloat() - 0.5D) * 0.4D - 0.465D);
				vec3d1 = vec3d1.rotateYaw(-this.renderYawOffset * ((float) Math.PI / 180F));
				vec3d1 = vec3d1.add(this.getPosX(), this.getPosYEye(), this.getPosZ());
				this.world.addParticle(
						new ItemParticleData(ParticleTypes.ITEM, this.getItemStackFromSlot(EquipmentSlotType.MAINHAND)),
						vec3d1.x, vec3d1.y, vec3d1.z, 0.0D, 0.0D, 0.0D);
			}

		}

	}

	@Override
	public void tick() {
		super.tick();
		this.updateEatingAnimation();
	}

	/**
	 * Tests if this entity should pickup a weapon or an armor. Entity drops current
	 * weapon or armor if the new one is better.
	 */
	protected void updateEquipmentIfNeeded(ItemEntity itemEntity) {
		if (this.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty() && ITEM_SELECTOR.test(itemEntity)) {
			ItemStack itemstack = itemEntity.getItem();
			this.setItemStackToSlot(EquipmentSlotType.MAINHAND, itemstack);
			this.inventoryHandsDropChances[EquipmentSlotType.MAINHAND.getIndex()] = 2.0F;
			this.onItemPickup(itemEntity, itemstack.getCount());
			itemEntity.remove();
		}

	}

	public void updateAITasks() {
		if (this.ticksUntilJump > 0) {
			--this.ticksUntilJump;
		}

		if (this.onGround) {
			if (!this.wasOnGround) {
				this.setJumping(false);
				this.checkLandingDelay();
			}

			if (this.ticksUntilJump == 0) {
				LivingEntity livingentity = this.getAttackTarget();
				if (livingentity != null && this.getDistanceSq(livingentity) < 16.0D) {
					this.lookTowards(livingentity.getPosX(), livingentity.getPosZ());
					this.moveController.setMoveTo(livingentity.getPosX(), livingentity.getPosY(),
							livingentity.getPosZ(), this.moveController.getSpeed());
					this.startJumping();
					this.wasOnGround = true;
				}
			}

			FatSealJumpHelperController fatsealentity$jumphelpercontroller = (FatSealJumpHelperController) this.jumpController;
			if (!fatsealentity$jumphelpercontroller.getIsJumping()) {
				if (this.moveController.isUpdating() && this.ticksUntilJump == 0) {
					Path path = this.navigator.getPath();
					Vec3d vec3d = new Vec3d(this.moveController.getX(), this.moveController.getY(),
							this.moveController.getZ());
					if (path != null && path.getCurrentPathIndex() < path.getCurrentPathLength()) {
						vec3d = path.getPosition(this);
					}

					this.lookTowards(vec3d.x, vec3d.z);
					this.startJumping();
				}
			} else if (!fatsealentity$jumphelpercontroller.canJump()) {
				this.enableJumpControl();
			}
		}

		this.wasOnGround = this.onGround;
	}

	public boolean processInteract(PlayerEntity player, Hand hand) {
		ItemStack itemstack = player.getHeldItem(hand);
		if (itemstack.getItem() instanceof SpawnEggItem) {
			return super.processInteract(player, hand);
		} else if (this.isBreedingItem(itemstack)) {

			if (this.isChild()) {
				this.consumeItemFromStack(player, itemstack);
				this.ageUp((int) ((float) (-this.getGrowingAge() / 20) * 0.1F), true);
			} else if (!this.world.isRemote && this.getGrowingAge() == 0 && this.canBreed()) {
				this.consumeItemFromStack(player, itemstack);
				this.setInLove(player);
			} else {
				if (this.world.isRemote) { // was this.isInWater()
					return false;
				}
				this.setEating(true);
				ItemStack itemstack1 = this.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
				if (!itemstack1.isEmpty() && !player.abilities.isCreativeMode) {
					this.entityDropItem(itemstack1);
				}

				this.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(itemstack.getItem(), 1));
				this.consumeItemFromStack(player, itemstack);
			}

			player.swing(hand, true);
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
		if (!this.isGoingHome() && worldIn.getFluidState(pos).isTagged(FluidTags.WATER)) {
			return 10.0F;
		}

		else {
			return worldIn.getBlockState(pos.down()).getBlock() == Blocks.SNOW ? 10.0F
					: worldIn.getBrightness(pos) - 0.5F;
		}
	}

	public boolean isPushedByWater() {
		return false;
	}

	public boolean canBreatheUnderwater() {
		return true;
	}

	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.WATER;
	}

	public SoundEvent getJumpSound() {
		return SoundEvents.ENTITY_RABBIT_JUMP;
	}

	public SoundCategory getSoundCategory() {
		return SoundCategory.AMBIENT;
	}

	@Override
	public int getTalkInterval() {
		return 200;
	}

	@Override
	@Nullable
	public SoundEvent getAmbientSound() {
		return this.isInWater() ? ModSounds.FAT_SEAL_WATER.get() : ModSounds.FAT_SEAL_LAND.get();
	}

	@Override
	@Nullable
	public void playSwimSound(float volume) {
		super.playSwimSound(volume * 2.0F);
	}

	@Override
	@Nullable
	public SoundEvent getSwimSound() {
		return SoundEvents.ENTITY_TURTLE_SWIM;
	}

	@Override
	@Nullable
	public SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return ModSounds.FAT_SEAL_HURT.get();
	}

	@Override
	@Nullable
	public SoundEvent getDeathSound() {
		return ModSounds.FAT_SEAL_DEATH.get();
	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	public boolean isBreedingItem(ItemStack stack) {
		return TEMPTATION_ITEMS.test(stack);
	}

	public boolean attackEntityAsMob(Entity entityIn) {
		this.playSound(SoundEvents.ENTITY_DOLPHIN_ATTACK, 1.0F,
				(this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
		return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 6.0F);
	}

	static class MoveHelperController extends MovementController {
		private final FatSealEntity seal;
		private double nextJumpSpeed;

		public MoveHelperController(FatSealEntity seal) {
			super(seal);
			this.seal = seal;
		}

		private void updateSpeed() {
			if (this.seal.isInWater()) {
				this.seal.setMotion(this.seal.getMotion().add(0.0D, 0.005D, 0.0D));
				if (!this.seal.getHome().withinDistance(this.seal.getPositionVec(), 16.0D)) {
					this.seal.setAIMoveSpeed(Math.max(this.seal.getAIMoveSpeed() / 2.0F, 0.08F));
				}
				this.seal.setJumping(false);

			}
			if (this.seal.onGround && !this.seal.isJumping
					&& !((FatSealJumpHelperController) this.seal.jumpController).getIsJumping()
					&& !this.seal.isInWater()) {
				this.seal.setMovementSpeed(0.0D);
			}

			else if (this.isUpdating() && !this.seal.isInWater()) {
				this.seal.setMovementSpeed(this.nextJumpSpeed);
			}
		}

		public void tick() {
			this.updateSpeed();
			if (this.action == MovementController.Action.MOVE_TO && !this.seal.getNavigator().noPath()
					&& this.seal.isInWater()) {
				double d0 = this.posX - this.seal.getPosX();
				double d1 = this.posY - this.seal.getPosY();
				double d2 = this.posZ - this.seal.getPosZ();
				double d3 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
				d1 = d1 / d3;
				float f = (float) (MathHelper.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
				this.seal.rotationYaw = this.limitAngle(this.seal.rotationYaw, f, 90.0F);
				this.seal.renderYawOffset = this.seal.rotationYaw;
				float f1 = (float) (this.speed
						* this.seal.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
				this.seal.setAIMoveSpeed(MathHelper.lerp(0.125F, this.seal.getAIMoveSpeed(), f1));
				this.seal.setMotion(
						this.seal.getMotion().add(0.0D, (double) this.seal.getAIMoveSpeed() * d1 * 0.1D, 0.0D));
			} else if (!this.seal.isInWater()) {
				super.tick();
			}

			else {
				this.seal.setAIMoveSpeed(0.0F);
			}

		}

		/**
		 * Sets the speed and location to move to
		 */
		public void setMoveTo(double x, double y, double z, double speedIn) {
			super.setMoveTo(x, y, z, speedIn);
			if (speedIn > 0.0D && !this.seal.isInWater()) {
				this.nextJumpSpeed = speedIn;
			}
		}
	}

	public class FatSealJumpHelperController extends JumpController {
		private final FatSealEntity seal;
		private boolean canJump;

		public FatSealJumpHelperController(FatSealEntity seal) {
			super(seal);
			this.seal = seal;
		}

		public boolean getIsJumping() {
			return this.isJumping;
		}

		public boolean canJump() {
			return this.canJump;
		}

		public void setCanJump(boolean canJumpIn) {
			this.canJump = canJumpIn;
		}

		/**
		 * Called to actually make the entity jump if isJumping is true.
		 */
		public void tick() {
			if (this.isJumping) {
				this.seal.startJumping();
				this.isJumping = false;
			}
		}
	}

	@Override
	public AgeableEntity createChild(AgeableEntity ageable) {
		return ModEntities.FAT_SEAL_ENTITY.get().create(this.world);
	}

	static class TravelGoal extends Goal {
		private final FatSealEntity seal;
		private final double speed;
		private boolean noPath;

		TravelGoal(FatSealEntity seal, double speedIn) {
			this.seal = seal;
			this.speed = speedIn;
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			return !this.seal.isGoingHome() && this.seal.isInWater() && !this.seal.isSwimmingWithPlayer
					&& !this.seal.isGettingTempted && this.seal.getAttackTarget() == null; // NEEDS
			// TESTING
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			Random random = this.seal.rand;
			int k = random.nextInt(1025) - 512;
			int l = random.nextInt(9) - 4;
			int i1 = random.nextInt(1025) - 512;
			if ((double) l + this.seal.getPosY() > (double) (this.seal.world.getSeaLevel() - 1)) {
				l = 0;
			}

			BlockPos blockpos = new BlockPos((double) k + this.seal.getPosX(), (double) l + this.seal.getPosY(),
					(double) i1 + this.seal.getPosZ());
			this.seal.setTravelPos(blockpos);
			this.seal.setTravelling(true);
			this.noPath = false;
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		@SuppressWarnings("deprecation")
		public void tick() {
			if (this.seal.getNavigator().noPath()) {
				Vec3d vec3d = new Vec3d(this.seal.getTravelPos());
				Vec3d vec3d1 = RandomPositionGenerator.findRandomTargetTowardsScaled(this.seal, 16, 3, vec3d,
						(double) ((float) Math.PI / 10F));
				if (vec3d1 == null) {
					vec3d1 = RandomPositionGenerator.findRandomTargetBlockTowards(this.seal, 8, 7, vec3d);
				}

				if (vec3d1 != null) {
					int i = MathHelper.floor(vec3d1.x);
					int j = MathHelper.floor(vec3d1.z);
					if (!this.seal.world.isAreaLoaded(i - 34, 0, j - 34, i + 34, 0, j + 34)) {
						vec3d1 = null;
					}
				}

				if (vec3d1 == null) {
					this.noPath = true;
					return;
				}

				this.seal.getNavigator().tryMoveToXYZ(vec3d1.x, vec3d1.y, vec3d1.z, this.speed);
			}

		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			return !this.seal.isGoingHome() && !this.seal.getNavigator().noPath() && !this.noPath;
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by
		 * another one
		 */
		public void resetTask() {
			this.seal.setTravelling(false);
			super.resetTask();
		}
	}

	protected PathNavigator createNavigator(World worldIn) {
		return new FatSealEntity.Navigator(this, worldIn);
	}

	static class Navigator extends SwimmerPathNavigator {
		Navigator(FatSealEntity seal, World worldIn) {
			super(seal, worldIn);
		}

		/**
		 * If on ground or swimming and can swim
		 */
		protected boolean canNavigate() {
			return true;
		}

		protected PathFinder getPathFinder(int range) {
			this.nodeProcessor = new WalkAndSwimNodeProcessor();
			return new PathFinder(this.nodeProcessor, range);
		};

		@SuppressWarnings("deprecation")
		public boolean canEntityStandOnPos(BlockPos pos) {
			if (this.entity instanceof FatSealEntity) {
				FatSealEntity sealentity = (FatSealEntity) this.entity;
				if (sealentity.isTravelling()) {
					return this.world.getBlockState(pos).getBlock() == Blocks.WATER;
				}
			}

			return !this.world.getBlockState(pos.down()).isAir(/* this.entity.world, pos */);// MAYBE SOURCE
		}
	}

	static class GetOutOfWaterGoal extends MoveToBlockGoal {
		private final FatSealEntity seal;

		public GetOutOfWaterGoal(FatSealEntity sealIn, double speedIn) {
			super(sealIn, speedIn, 8, 12);
			this.seal = sealIn;
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			return this.seal.isInWater() && this.seal.ticksInWater >= 100;
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			return super.shouldContinueExecuting();
		}

		/**
		 * Return true to set given position as destination
		 */
		protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
			BlockPos blockpos = pos.up();
			return worldIn.isAirBlock(blockpos) && worldIn.isAirBlock(blockpos.up())
					? worldIn.getBlockState(pos).isTopSolid(worldIn, pos, this.seal)
					: false;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			super.startExecuting();
			this.seal.isSwimmingWithPlayer = true;
		}

		public void tick() {
			super.tick();
			this.seal.isSwimmingWithPlayer = true;
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by
		 * another one
		 */
		public void resetTask() {
			super.resetTask();
			this.creature.getNavigator().clearPath();
			this.seal.isSwimmingWithPlayer = false;
		}
	}

	static class PickUpFoodGoal extends Goal {
		private int startAge;
		private final FatSealEntity seal;

		public PickUpFoodGoal(FatSealEntity seal) {
			this.seal = seal;
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		public boolean shouldExecute() {
			if (this.startAge <= this.seal.ticksExisted && !this.seal.isChild() && !this.seal.isEating()) {
				List<ItemEntity> list = this.seal.world.getEntitiesWithinAABB(ItemEntity.class,
						this.seal.getBoundingBox().expand(8.0D, 8.0D, 8.0D), FatSealEntity.ITEM_SELECTOR);
				return !list.isEmpty() || !this.seal.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty();
			} else {
				return false;
			}
		}

		public boolean shouldContinueExecuting() {
			if (this.seal.rand.nextInt(600) != 1) {

				return this.seal.rand.nextInt(2000) != 1;
			} else {
				return false;
			}
		}

		public void startExecuting() {
			List<ItemEntity> list = this.seal.world.getEntitiesWithinAABB(ItemEntity.class,
					this.seal.getBoundingBox().expand(6.0D, 6.0D, 6.0D), FatSealEntity.ITEM_SELECTOR);
			if (!list.isEmpty() && this.seal.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty()) {
				this.seal.getNavigator().tryMoveToEntityLiving(list.get(0), 1.2D);
				// this.seal.isGettingTempted = true;
			}

			this.startAge = 0;
		}

		public void resetTask() {
			this.seal.getNavigator().clearPath();
			ItemStack itemStack = this.seal.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
			if (!itemStack.isEmpty()) {
				// this.seal.entityDropItem(itemStack);
				// this.seal.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
				int i = this.seal.rand.nextInt(150) + 10;
				this.startAge = this.seal.ticksExisted + i * 20;
				// this.seal.isGettingTempted = false;
			}

		}
	}

	static class SwimWithPlayerGoal extends Goal {
		private final FatSealEntity creature;
		private final double speed;
		private PlayerEntity targetPlayer;
		private static final EntityPredicate ENTITY_PREDICATE = (new EntityPredicate()).setDistance(3.0D)
				.allowFriendlyFire().allowInvulnerable();

		public SwimWithPlayerGoal(FatSealEntity creatureIn, double speedIn) {
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
			this.creature.isSwimmingWithPlayer = true;
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by
		 * another one
		 */
		public void resetTask() {
			this.targetPlayer = null;
			this.creature.getNavigator().clearPath();
			this.creature.isSwimmingWithPlayer = false;
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			this.creature.getLookController().setLookPositionWithEntity(this.targetPlayer,
					(float) (this.creature.getHorizontalFaceSpeed() + 20),
					(float) this.creature.getVerticalFaceSpeed());
			if (this.creature.getDistanceSq(this.targetPlayer) < 6.25D && this.creature.isInWater()) {
				this.creature.getNavigator().clearPath();
			} else {
				this.creature.getNavigator().tryMoveToEntityLiving(this.targetPlayer, this.speed);
			}

			if (this.targetPlayer.isSwimming() && this.targetPlayer.world.rand.nextInt(6) == 0
					&& this.creature.isInWater()) {
				this.targetPlayer.addPotionEffect(new EffectInstance(Effects.LUCK, 100));

			}
			this.creature.isSwimmingWithPlayer = true;
		}
	}

	static class FatSealPanicGoal extends PanicGoal {
		private final FatSealEntity seal;

		public FatSealPanicGoal(FatSealEntity seal, double speedIn) {
			super(seal, speedIn);
			this.seal = seal;
		}

		@Override
		public void startExecuting() {
			this.seal.isPaniced = true;
			super.startExecuting();
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void tick() {
			super.tick();
			this.seal.setMovementSpeed(this.speed);
			this.seal.isPaniced = true;
			if (this.seal.collidedHorizontally && this.seal.isJumping) {
				this.seal.getNavigator().clearPath();
			}
		}

		@Override
		public void resetTask() {
			this.seal.isPaniced = false;
			super.resetTask();
		}
	}
}
