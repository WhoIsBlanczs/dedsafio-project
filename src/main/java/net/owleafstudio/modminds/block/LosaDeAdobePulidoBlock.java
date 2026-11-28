package net.owleafstudio.modminds.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LosaDeAdobePulidoBlock extends SlabBlock {
   public LosaDeAdobePulidoBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.MUD_BRICKS).strength(1.0F, 10.0F));
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }
}
