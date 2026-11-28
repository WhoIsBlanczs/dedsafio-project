package net.owleafstudio.modminds.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BloquePulidoBlock extends Block {
   public BloquePulidoBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.COPPER).strength(-1.0F, 3600000.0F));
   }

   public int getLightDampening(BlockState state) {
      return 15;
   }
}
