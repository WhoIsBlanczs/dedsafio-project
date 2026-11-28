package net.owleafstudio.modminds.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BloqueDeRejillasRotasBlock extends Block {
   public BloqueDeRejillasRotasBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(1.0F, 10.0F));
   }

   public int getLightDampening(BlockState state) {
      return 15;
   }
}
