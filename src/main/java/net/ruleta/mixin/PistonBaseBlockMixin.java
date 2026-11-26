package net.ruleta.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.ruleta.compat.Bus;
import net.ruleta.compat.PistonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin {
   @Inject(method = "moveBlocks", at = @At("HEAD"), cancellable = true, require = 0)
   private void ruleta$antesDeMover(Level nivel, BlockPos pos, Direction direccion, boolean extiende, CallbackInfoReturnable<Boolean> cir) {
      if (!nivel.isClientSide && Bus.hayOyentes(PistonEvent.Pre.class) && Bus.post(new PistonEvent.Pre(nivel, pos, direccion, extiende))) {
         cir.setReturnValue(false);
      }
   }
}
