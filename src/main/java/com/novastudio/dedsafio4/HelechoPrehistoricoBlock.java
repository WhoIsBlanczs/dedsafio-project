package com.novastudio.dedsafio4;

import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelReader;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class HelechoPrehistoricoBlock extends TallGrassBlock {
   public HelechoPrehistoricoBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3, net.minecraft.world.level.block.BonemealSource src$) {
      return false;
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4, net.minecraft.world.level.block.BonemealSource src$) {
      return false;
   }
}
