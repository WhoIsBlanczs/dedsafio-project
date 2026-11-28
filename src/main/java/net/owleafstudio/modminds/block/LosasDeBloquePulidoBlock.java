package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LosasDeBloquePulidoBlock extends SlabBlock {
   public LosasDeBloquePulidoBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.COPPER).strength(-1.0F, 3600000.0F));
   }
}
