package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LosaDeCristalDeTemploNegroBlock extends SlabBlock {
   public LosaDeCristalDeTemploNegroBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.GLASS).strength(-1.0F, 3600000.0F));
   }
}
