package net.ruleta.hudcontrol.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ruleta.cajero.client.CajeroHud;
import net.ruleta.hudcontrol.client.HudDeditasToggleClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = CajeroHud.class, remap = false)
public abstract class CajeroHudMixin {
   @Inject(method = "dibujar", at = @At("HEAD"), cancellable = true, remap = false)
   private static void ruletafanproject$skipHiddenHud(CallbackInfo callbackInfo) {
      if (!HudDeditasToggleClient.isVisible()) {
         callbackInfo.cancel();
      }
   }
}
