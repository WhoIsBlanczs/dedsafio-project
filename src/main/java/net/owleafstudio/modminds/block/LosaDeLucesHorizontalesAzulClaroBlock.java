package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LosaDeLucesHorizontalesAzulClaroBlock extends SlabBlock {
   public LosaDeLucesHorizontalesAzulClaroBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(1.0F, 10.0F));
   }
}
