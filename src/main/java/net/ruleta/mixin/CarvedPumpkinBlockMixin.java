package net.ruleta.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.ruleta.Ruleta;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CarvedPumpkinBlock.class)
public abstract class CarvedPumpkinBlockMixin {
   @Shadow
   protected abstract BlockPattern method_9729();

   @Shadow
   protected abstract BlockPattern method_9730();

   @Inject(method = "trySpawnGolem", at = @At("HEAD"), cancellable = true, require = 0)
   private void ruleta$sinGolems(Level nivel, BlockPos pos, CallbackInfo ci) {
      if (!nivel.isClientSide() && Ruleta.cambioActivo("sin-golems")) {
         if (this.method_9729().find(nivel, pos) != null || this.method_9730().find(nivel, pos) != null) {
            if (nivel.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 8.0, false) instanceof ServerPlayer jugador) {
               jugador.sendOverlayMessage(Component.literal("Reviil lanzó una maldición: las calabazas ya no dan vida a los golems."));
            }

            ci.cancel();
         }
      }
   }
}
