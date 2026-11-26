package net.ruleta.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.ruleta.client.compat.Bus;
import net.ruleta.client.compat.RenderNameTagEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityRenderer.class})
public abstract class EntidadRenderMixin {
   @Inject(
      method = {"render"},
      at = {@At("HEAD")},
      require = 0
   )
   private void ruleta$nombre(Entity entidad, float guinada, float parcial, PoseStack pila, MultiBufferSource buffer, int luz, CallbackInfo ci) {
      if (Bus.hayOyentes(RenderNameTagEvent.class)) {
         Bus.post(new RenderNameTagEvent(entidad, (EntityRenderer<?>)this, pila, buffer, luz, parcial));
      }
   }
}
