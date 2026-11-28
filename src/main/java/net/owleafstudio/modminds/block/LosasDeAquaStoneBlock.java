package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LosasDeAquaStoneBlock extends SlabBlock {
   public LosasDeAquaStoneBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.NETHERRACK).strength(1.0F, 10.0F));
   }
}
