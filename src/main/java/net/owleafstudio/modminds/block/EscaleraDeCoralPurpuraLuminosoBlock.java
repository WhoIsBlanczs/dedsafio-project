package net.owleafstudio.modminds.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class EscaleraDeCoralPurpuraLuminosoBlock extends StairBlock {
   public EscaleraDeCoralPurpuraLuminosoBlock() {
      super(
         Blocks.AIR.defaultBlockState(), BlockBehaviour.Properties.of().sound(SoundType.CORAL_BLOCK).strength(-1.0F, 3600000.0F).lightLevel(s -> 15)
      );
   }

   public float getExplosionResistance() {
      return 10.0F;
   }

   public boolean isRandomlyTicking(BlockState state) {
      return false;
   }
}
