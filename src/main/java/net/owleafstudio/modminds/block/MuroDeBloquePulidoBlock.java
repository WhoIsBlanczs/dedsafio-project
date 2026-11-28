package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class MuroDeBloquePulidoBlock extends WallBlock {
   public MuroDeBloquePulidoBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.COPPER).strength(-1.0F, 3600000.0F).forceSolidOn());
   }
}
