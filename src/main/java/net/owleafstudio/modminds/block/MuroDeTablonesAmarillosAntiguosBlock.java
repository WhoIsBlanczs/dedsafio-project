package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class MuroDeTablonesAmarillosAntiguosBlock extends WallBlock {
   public MuroDeTablonesAmarillosAntiguosBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(-1.0F, 3600000.0F).forceSolidOn());
   }
}
