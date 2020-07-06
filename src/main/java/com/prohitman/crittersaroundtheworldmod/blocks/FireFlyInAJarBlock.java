package com.prohitman.crittersaroundtheworldmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BreakableBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireFlyInAJarBlock extends BreakableBlock
{
	protected static final VoxelShape SHAPE = VoxelShapes.or(Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 10.0D, 12.0D), Block.makeCuboidShape(5.0D, 10.0D, 5.0D, 11.0D, 11.0D, 11.0));
	
	public FireFlyInAJarBlock(Block.Properties properties) 
	{
		super(properties);
	}
	
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) 
	{
	      return SHAPE;
	}
	
	@OnlyIn(Dist.CLIENT)
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) 
	{
	      return 1.0F;
	}

	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) 
	{
	      return true;
	}

	public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) 
	{
		return false;
	}

	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) 
	{
	      return false;
	}

	public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type) 
	{
	      return false;
	}
	
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) 
	{
	      return hasEnoughSolidSide(worldIn, pos.down(), Direction.UP);
	}
}
