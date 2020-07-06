package com.prohitman.crittersaroundtheworldmod.init;

import com.prohitman.crittersaroundtheworldmod.CrittersAroundTheWorld;
import com.prohitman.crittersaroundtheworldmod.blocks.JarBlock;
import com.prohitman.crittersaroundtheworldmod.blocks.FireFlyInAJarBlock;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = CrittersAroundTheWorld.MOD_ID, bus = Bus.MOD)
public class ModBlocks {

	// Blocks
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CrittersAroundTheWorld.MOD_ID);

	public static final RegistryObject<Block> JAR_CATM = BLOCKS.register("jar_catm", () -> new JarBlock(Block.Properties.create(Material.GLASS).hardnessAndResistance(0.0F).notSolid().sound(SoundType.GLASS)));
	
	public static final RegistryObject<Block> JAR_O_FIREFLY = BLOCKS.register("jar_o_firefly",() -> new FireFlyInAJarBlock(Block.Properties.create(Material.GLASS).lightValue(10).hardnessAndResistance(0.0F).sound(SoundType.GLASS)));
	
	@SubscribeEvent
	public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();

		ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
			final Item.Properties properties = new Item.Properties().group(ItemGroup.MISC);
			final BlockItem blockItem = new BlockItem(block, properties);
			blockItem.setRegistryName(block.getRegistryName());
			registry.register(blockItem);
		});
	}
}
