package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LosaDePilarBorgConLuzVerticalVerdeBlock extends SlabBlock {
   public LosaDePilarBorgConLuzVerticalVerdeBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(-1.0F, 3600000.0F).lightLevel(s -> 10));
   }
}
