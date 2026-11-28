package net.owleafstudio.modminds.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BloqueDeLucesHorizontalesVerdeBlock extends Block {
   public BloqueDeLucesHorizontalesVerdeBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.GRAVEL).strength(1.0F, 10.0F).lightLevel(s -> 10));
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 15;
   }
}
