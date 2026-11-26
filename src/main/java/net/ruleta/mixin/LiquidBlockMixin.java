package net.ruleta.mixin;

import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.ruleta.compat.BlockEvent;
import net.ruleta.compat.Bus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlock.class)
public abstract class LiquidBlockMixin {
   @Shadow
   @Final
   protected FlowingFluid field_11279;

   @Inject(method = "shouldSpreadLiquid", at = @At("HEAD"), cancellable = true, require = 0)
   private void ruleta$antesDeConvertir(Level nivel, BlockPos pos, BlockState estado, CallbackInfoReturnable<Boolean> cir) {
      if (this.field_11279.is(FluidTags.LAVA) && Bus.hayOyentes(BlockEvent.FluidPlaceBlockEvent.class)) {
         boolean tierraDeAlmas = nivel.getBlockState(pos.below()).is(Blocks.SOUL_SOIL);
         UnmodifiableIterator var6 = LiquidBlock.POSSIBLE_FLOW_DIRECTIONS.iterator();

         while (var6.hasNext()) {
            Direction dir = (Direction)var6.next();
            BlockPos vecino = pos.relative(dir.getOpposite());
            boolean convierte = nivel.getFluidState(vecino).is(FluidTags.WATER) || tierraDeAlmas && nivel.getBlockState(vecino).is(Blocks.BLUE_ICE);
            if (convierte) {
               if (Bus.post(new BlockEvent.FluidPlaceBlockEvent(nivel, pos, vecino, estado))) {
                  cir.setReturnValue(true);
               }

               return;
            }
         }
      }
   }
}
