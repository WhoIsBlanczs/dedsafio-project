package com.ghostsafio.mitomod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RopeBlock extends RotatedPillarBlock {
   private static final VoxelShape Y = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
   private static final VoxelShape X = Block.box(0.0, 6.0, 6.0, 16.0, 10.0, 10.0);
   private static final VoxelShape Z = Block.box(6.0, 6.0, 0.0, 10.0, 10.0, 16.0);

   public RopeBlock(Properties properties) {
      super(properties);
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return switch ((Axis)state.getValue(AXIS)) {
         case X -> X;
         case Z -> Z;
         default -> Y;
      };
   }
}
