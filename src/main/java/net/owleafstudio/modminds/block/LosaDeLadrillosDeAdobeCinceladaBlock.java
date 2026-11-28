package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LosaDeLadrillosDeAdobeCinceladaBlock extends SlabBlock {
   public LosaDeLadrillosDeAdobeCinceladaBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.MUD_BRICKS).strength(-1.0F, 3600000.0F));
   }
}
