package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class MuroDeAquaStoneBlock extends WallBlock {
   public MuroDeAquaStoneBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.NETHERRACK).strength(1.0F, 10.0F).forceSolidOn());
   }
}
