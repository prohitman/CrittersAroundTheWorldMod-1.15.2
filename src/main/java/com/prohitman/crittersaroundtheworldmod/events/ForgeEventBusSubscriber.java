package com.prohitman.crittersaroundtheworldmod.events;

import com.prohitman.crittersaroundtheworldmod.CrittersAroundTheWorld;
import com.prohitman.crittersaroundtheworldmod.entities.FatSealEntity;
import com.prohitman.crittersaroundtheworldmod.entities.FireFlyEntity;
import com.prohitman.crittersaroundtheworldmod.init.ModBlocks;

import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.fish.AbstractGroupFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = CrittersAroundTheWorld.MOD_ID, bus = Bus.FORGE)
public class ForgeEventBusSubscriber {

	@SubscribeEvent
	public static void sealAttacked(LivingSpawnEvent event) {
		if (event.getEntityLiving() instanceof PolarBearEntity) {
			PolarBearEntity polarBear = (PolarBearEntity) event.getEntityLiving();
			polarBear.targetSelector.addGoal(1,
					new NearestAttackableTargetGoal<>(polarBear, FatSealEntity.class, 10, true, false, (entity) -> {
						return Math.abs(entity.getPosY() - polarBear.getPosY()) <= 6.0D;
					}));
			polarBear.getCollisionBox(polarBear);
		}
		if (event.getEntityLiving() instanceof AbstractGroupFishEntity) {
			AbstractGroupFishEntity fish = (AbstractGroupFishEntity) event.getEntityLiving();
			fish.targetSelector.addGoal(1,
					new AvoidEntityGoal<>(fish, FatSealEntity.class, 6.0F, 1.0D, 3.0D));
			fish.getCollisionBox(fish);
		}
	}

	@SubscribeEvent
	public static void jarEntities(EntityInteract event) {

		PlayerEntity player = event.getPlayer();
		Hand hand = event.getHand();
		ItemStack itemstack = player.getHeldItem(hand);

		if (event.getTarget() instanceof SilverfishEntity || event.getTarget() instanceof BeeEntity
				|| event.getTarget() instanceof EndermiteEntity || event.getTarget() instanceof FireFlyEntity) {

			if (itemstack.getItem() == ModBlocks.JAR_CATM.get().asItem() && event.getTarget().isAlive()) {
				
				event.getTarget().playSound(SoundEvents.BLOCK_BEEHIVE_ENTER, 1.0F, 1.0F);
				if (!player.abilities.isCreativeMode) {
					itemstack.shrink(1);
				}
				if (itemstack.isEmpty()) {
					player.setHeldItem(hand, new ItemStack(ModBlocks.JAR_O_FIREFLY.get().asItem()));
				} else if (!player.inventory
						.addItemStackToInventory(new ItemStack(ModBlocks.JAR_O_FIREFLY.get().asItem()))) {
					player.dropItem(new ItemStack(ModBlocks.JAR_O_FIREFLY.get().asItem()), false);
				}
				event.getTarget().remove();
			}
		}
	}
}
