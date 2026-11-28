package net.owleafstudio.modminds.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LosaDePilarDeWarpedBlock extends SlabBlock {
   public LosaDePilarDeWarpedBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(1.0F, 10.0F));
   }

   public int getLightDampening(BlockState state) {
      return 0;
   }
}
