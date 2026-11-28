package net.owleafstudio.modminds.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class MansionBlock extends StairBlock {
   public MansionBlock() {
      super(Blocks.AIR.defaultBlockState(), BlockBehaviour.Properties.of().sound(SoundType.DEEPSLATE_BRICKS).strength(1.0F, 10.0F));
   }

   public float getExplosionResistance() {
      return 10.0F;
   }

   public boolean isRandomlyTicking(BlockState state) {
      return false;
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }
}
