package net.ruleta.mixin;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.ruleta.client.dedsafio.OcultarCliente;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityRenderDispatcher.class})
public abstract class JugadorOcultoMixin {
   @Inject(
      method = {"shouldRender"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void ruleta$oculto(Entity entidad, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
      if (OcultarCliente.ocultar(entidad)) {
         cir.setReturnValue(false);
      }
   }
}
