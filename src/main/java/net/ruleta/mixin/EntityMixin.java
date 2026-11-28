package net.ruleta.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;
import net.ruleta.compat.Lanzador;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
   @Inject(method = "changeDimension", at = @At("HEAD"), cancellable = true, require = 0)
   private void ruleta$viaje(DimensionTransition destino, CallbackInfoReturnable<Entity> cir) {
      if (Lanzador.viaje((Entity)this, destino)) {
         cir.setReturnValue(null);
      }
   }
}
