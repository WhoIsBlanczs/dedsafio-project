package net.owleafstudio.modminds.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BloqueRuletaGlowingAmarilloBlock extends Block {
   public BloqueRuletaGlowingAmarilloBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.SAND).strength(1.0F, 10.0F).lightLevel(s -> 15));
   }

   public int getLightDampening(BlockState state) {
      return 15;
   }
}
