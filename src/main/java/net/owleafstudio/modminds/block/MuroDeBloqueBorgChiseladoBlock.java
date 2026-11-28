package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class MuroDeBloqueBorgChiseladoBlock extends WallBlock {
   public MuroDeBloqueBorgChiseladoBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.COPPER).strength(-1.0F, 3600000.0F).forceSolidOn());
   }
}
