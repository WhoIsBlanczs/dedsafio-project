package net.owleafstudio.modminds.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BloqueDeHierroReforzadoBlock extends Block {
   public BloqueDeHierroReforzadoBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(5.0F, 10.0F));
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 15;
   }
}
