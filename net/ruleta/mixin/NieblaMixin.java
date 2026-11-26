package net.ruleta.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.ruleta.client.compat.Bus;
import net.ruleta.client.compat.ViewportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({FogRenderer.class})
public abstract class NieblaMixin {
   @Shadow
   private static float field_4034;
   @Shadow
   private static float field_4033;
   @Shadow
   private static float field_4032;

   @Inject(
      method = {"setupColor"},
      at = {@At("TAIL")},
      require = 0
   )
   private static void ruleta$colorNiebla(Camera camara, float parcial, ClientLevel nivel, int distancia, float oscuridad, CallbackInfo ci) {
      if (Bus.hayOyentes(ViewportEvent.ComputeFogColor.class)) {
         ViewportEvent.ComputeFogColor e = new ViewportEvent.ComputeFogColor(camara, parcial, field_4034, field_4033, field_4032);
         Bus.post(e);
         field_4034 = e.getRed();
         field_4033 = e.getGreen();
         field_4032 = e.getBlue();
         RenderSystem.clearColor(field_4034, field_4033, field_4032, 0.0F);
      }
   }

   @Inject(
      method = {"setupFog"},
      at = {@At("TAIL")},
      require = 0
   )
   private static void ruleta$niebla(Camera camara, FogMode modo, float lejosVista, boolean espesa, float parcial, CallbackInfo ci) {
      if (Bus.hayOyentes(ViewportEvent.RenderFog.class)) {
         ViewportEvent.RenderFog e = new ViewportEvent.RenderFog(
            modo,
            camara.getFluidInCamera(),
            camara,
            parcial,
            RenderSystem.getShaderFogStart(),
            RenderSystem.getShaderFogEnd(),
            RenderSystem.getShaderFogShape()
         );
         if (Bus.post(e)) {
            RenderSystem.setShaderFogStart(e.getNearPlaneDistance());
            RenderSystem.setShaderFogEnd(e.getFarPlaneDistance());
            RenderSystem.setShaderFogShape(e.getFogShape());
         }
      }
   }
}
