package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class MuroDeBloqueIluminanteMagentaBlock extends WallBlock {
   public MuroDeBloqueIluminanteMagentaBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(-1.0F, 3600000.0F).forceSolidOn());
   }
}
