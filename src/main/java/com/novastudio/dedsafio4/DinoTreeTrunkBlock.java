package com.novastudio.dedsafio4;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class DinoTreeTrunkBlock extends Block {
   public static final IntegerProperty FRUIT_KIND = IntegerProperty.create("fruit_kind", 0, 3);
   public static final IntegerProperty FRUIT_COUNT = IntegerProperty.create("fruit_count", 0, 4);

   public DinoTreeTrunkBlock() {
      super(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD));
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(FRUIT_KIND, 0)).setValue(FRUIT_COUNT, 0));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      super.createBlockStateDefinition(var1);
      var1.add(new Property[]{FRUIT_KIND, FRUIT_COUNT});
   }

   public BlockState fruitBearingState(BlockState var1, int var2, int var3) {
      return (BlockState)((BlockState)var1.setValue(FRUIT_KIND, var2)).setValue(FRUIT_COUNT, var3);
   }
}
