package net.owleafstudio.modminds.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BloqueDeLucesHorizontalesBlancoBlock extends Block {
   public BloqueDeLucesHorizontalesBlancoBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.GRAVEL).strength(1.0F, 10.0F));
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 15;
   }
}
