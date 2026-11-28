package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class PuertaDeVallaTablonesAmarillosAntiguosBlock extends FenceGateBlock {
   public PuertaDeVallaTablonesAmarillosAntiguosBlock() {
      super(WoodType.OAK, BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(-1.0F, 3600000.0F).forceSolidOn());
   }
}
