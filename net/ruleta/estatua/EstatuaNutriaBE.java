package net.ruleta.estatua;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EstatuaNutriaBE extends BlockEntity {
   public EstatuaNutriaBE(BlockPos pos, BlockState estado) {
      super(EstatuaNutria.BE.get(), pos, estado);
   }
}
