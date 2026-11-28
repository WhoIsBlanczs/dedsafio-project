package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LosaDeLucesHorizontalesVerdeBlock extends SlabBlock {
   public LosaDeLucesHorizontalesVerdeBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.GRAVEL).strength(1.0F, 10.0F).lightLevel(s -> 10));
   }
}
