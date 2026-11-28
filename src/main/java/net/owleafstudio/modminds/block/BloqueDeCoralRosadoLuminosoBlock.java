package net.owleafstudio.modminds.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BloqueDeCoralRosadoLuminosoBlock extends Block {
   public BloqueDeCoralRosadoLuminosoBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.CORAL_BLOCK).strength(-1.0F, 3600000.0F).lightLevel(s -> 15));
   }

   public int getLightDampening(BlockState state) {
      return 15;
   }
}
