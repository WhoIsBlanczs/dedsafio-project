package com.novastudio.dedsafio4;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class CacaDinoBlock extends FallingBlock {
   private static final MapCodec<CacaDinoBlock> CODEC = MapCodec.unit(CacaDinoBlock::new);
   private static final VoxelShape SHAPE = box(1.0, 0.0, 2.0, 15.0, 12.0, 14.0);

   public CacaDinoBlock() {
      super(BlockBehaviour.Properties.of().strength(0.6F).sound(SoundType.MUD).noOcclusion());
   }

   protected MapCodec<CacaDinoBlock> codec() {
      return CODEC;
   }

   public int getDustColor(BlockState var1, BlockGetter var2, BlockPos var3) {
      return -10730719;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }
}
