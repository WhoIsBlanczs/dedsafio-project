package net.owleafstudio.modminds.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LadrillosNegrosAgrietadosBlock extends Block {
   public LadrillosNegrosAgrietadosBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.NETHER_BRICKS).strength(-1.0F, 3600000.0F));
   }

   public int getLightDampening(BlockState state) {
      return 15;
   }
}
