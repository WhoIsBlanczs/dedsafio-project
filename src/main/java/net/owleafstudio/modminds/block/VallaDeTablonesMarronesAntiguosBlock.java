package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class VallaDeTablonesMarronesAntiguosBlock extends FenceBlock {
   public VallaDeTablonesMarronesAntiguosBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(-1.0F, 3600000.0F).forceSolidOn());
   }
}
