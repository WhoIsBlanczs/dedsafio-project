package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LosasDeBlueStoneBlock extends SlabBlock {
   public LosasDeBlueStoneBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.NETHERRACK).strength(1.0F, 10.0F));
   }
}
