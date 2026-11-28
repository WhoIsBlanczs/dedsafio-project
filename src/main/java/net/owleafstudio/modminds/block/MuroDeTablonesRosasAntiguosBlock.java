package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class MuroDeTablonesRosasAntiguosBlock extends WallBlock {
   public MuroDeTablonesRosasAntiguosBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(-1.0F, 3600000.0F).forceSolidOn());
   }
}
