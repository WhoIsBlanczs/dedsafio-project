package net.ruleta.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.ruleta.burbuja.Burbuja;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class CamaraMixin {
   @Inject(method = "isDetached", at = @At("HEAD"), cancellable = true, require = 0)
   private void ruleta$verseEnLaBurbuja(CallbackInfoReturnable<Boolean> cir) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null && mc.player.getVehicle() instanceof Burbuja) {
         cir.setReturnValue(true);
      }
   }
}
