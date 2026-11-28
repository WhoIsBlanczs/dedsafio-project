package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LosaDeLadrilloDeFortalezaCinceladoBlock extends SlabBlock {
   public LosaDeLadrilloDeFortalezaCinceladoBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.NETHER_BRICKS).strength(-1.0F, 3600000.0F));
   }
}
