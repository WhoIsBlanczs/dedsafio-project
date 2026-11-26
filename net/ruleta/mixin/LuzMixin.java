package net.ruleta.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.ruleta.client.eclipse.EfectosEclipse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LightTexture.class})
public abstract class LuzMixin {
   @Shadow
   @Final
   private NativeImage field_4133;
   @Shadow
   @Final
   private Minecraft field_4137;

   @Inject(
      method = {"updateLightTexture"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;upload()V"
      )},
      require = 0
   )
   private void ruleta$oscurecer(float parcial, CallbackInfo ci) {
      ClientLevel nivel = this.field_4137.level;
      if (nivel != null && EfectosEclipse.hayOscuridad()) {
         float oscuroCielo = nivel.getSkyDarken(1.0F) * 0.95F + 0.05F;

         for (int cielo = 0; cielo < 16; cielo++) {
            for (int bloque = 0; bloque < 16; bloque++) {
               float f = EfectosEclipse.factorLuz(nivel, cielo, bloque, oscuroCielo);
               if (!(f >= 1.0F)) {
                  int c = this.field_4133.getPixelRGBA(bloque, cielo);
                  int r = Math.round((c & 0xFF) * f);
                  int g = Math.round((c >> 8 & 0xFF) * f);
                  int b = Math.round((c >> 16 & 0xFF) * f);
                  this.field_4133.setPixelRGBA(bloque, cielo, c & 0xFF000000 | b << 16 | g << 8 | r);
               }
            }
         }
      }
   }
}
