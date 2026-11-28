package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LosaDePilarBorgConLuzVercicalRosaBlock extends SlabBlock {
   public LosaDePilarBorgConLuzVercicalRosaBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(-1.0F, 3600000.0F).lightLevel(s -> 10));
   }
}
