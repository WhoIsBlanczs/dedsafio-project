package net.ruleta.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.ruleta.candado.Candados;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HopperBlockEntity.class})
public abstract class HopperBlockEntityMixin {
   @Inject(
      method = {"pushItemsTick"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private static void ruleta$conCandado(Level nivel, BlockPos pos, BlockState estado, HopperBlockEntity tolva, CallbackInfo ci) {
      if (!nivel.isClientSide) {
         if (Candados.estaBloqueado(nivel, pos.above()) || Candados.estaBloqueado(nivel, pos.relative((Direction)estado.getValue(HopperBlock.FACING)))) {
            ci.cancel();
         }
      }
   }
}
