package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LosaDeCoralPurpuraLuminosoBlock extends SlabBlock {
   public LosaDeCoralPurpuraLuminosoBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.CORAL_BLOCK).strength(-1.0F, 3600000.0F).lightLevel(s -> 15));
   }
}
