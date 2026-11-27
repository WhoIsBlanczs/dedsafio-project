package net.ruleta.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.TeleportTransition;
import net.ruleta.compat.Lanzador;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
   @Inject(method = "die", at = @At("HEAD"), cancellable = true, require = 0)
   private void ruleta$morir(DamageSource fuente, CallbackInfo ci) {
      if (Lanzador.muerte((ServerPlayer)(Object)this, fuente)) {
         ci.cancel();
      }
   }

   @Inject(method = "changeDimension", at = @At("HEAD"), cancellable = true, require = 0)
   private void ruleta$viaje(TeleportTransition destino, CallbackInfoReturnable<Entity> cir) {
      if (Lanzador.viaje((ServerPlayer)(Object)this, destino)) {
         cir.setReturnValue(null);
      }
   }
}
