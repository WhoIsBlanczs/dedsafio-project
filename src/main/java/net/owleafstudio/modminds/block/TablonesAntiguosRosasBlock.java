package net.owleafstudio.modminds.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class TablonesAntiguosRosasBlock extends Block {
   public TablonesAntiguosRosasBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(-1.0F, 3600000.0F));
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 15;
   }
}
