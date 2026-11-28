package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LosaDeLadrillosDeAbobeAntiguosBlock extends SlabBlock {
   public LosaDeLadrillosDeAbobeAntiguosBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.MUD_BRICKS).strength(1.0F, 10.0F));
   }
}
