package net.owleafstudio.modminds.block;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LosaDeCristalDeDilitioMagentaBlock extends SlabBlock {
   public LosaDeCristalDeDilitioMagentaBlock() {
      super(BlockBehaviour.Properties.of().sound(SoundType.GRAVEL).strength(1.0F, 10.0F));
   }

   public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidstate) {
      return true;
   }
}
