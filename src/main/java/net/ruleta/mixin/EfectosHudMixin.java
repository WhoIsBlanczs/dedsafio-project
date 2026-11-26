package net.ruleta.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.ruleta.client.EfectosHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class EfectosHudMixin {
   @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true, require = 0)
   private void ruleta$efectosAbajoIzquierda(GuiGraphicsExtractor grafico, DeltaTracker tiempo, CallbackInfo ci) {
      EfectosHud.dibujar(grafico);
      ci.cancel();
   }
}
