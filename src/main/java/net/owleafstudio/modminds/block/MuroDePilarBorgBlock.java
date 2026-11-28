package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class MuroDePilarBorgBlock extends WallBlock {
   public MuroDePilarBorgBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(-1.0F, 3600000.0F).forceSolidOn());
   }
}
