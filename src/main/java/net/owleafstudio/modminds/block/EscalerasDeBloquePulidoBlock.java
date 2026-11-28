package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class EscalerasDeBloquePulidoBlock extends StairBlock {
   public EscalerasDeBloquePulidoBlock() {
      super(Blocks.AIR.defaultBlockState(), BlockBehaviour.Properties.of().sound(SoundType.COPPER).strength(-1.0F, 3600000.0F));
   }

   public float getExplosionResistance() {
      return 1200.0F;
   }

   public boolean isRandomlyTicking(BlockState state) {
      return false;
   }
}
