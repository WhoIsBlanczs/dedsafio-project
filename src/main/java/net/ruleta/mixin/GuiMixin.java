package net.ruleta.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.ruleta.client.MisOjos;
import net.ruleta.client.compat.Bus;
import net.ruleta.client.compat.RenderGuiEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
   @Shadow
   @Final
   private Minecraft field_2035;

   @Inject(method = "render", at = @At("HEAD"), require = 0)
   private void ruleta$antesDelHud(GuiGraphics grafico, DeltaTracker tiempo, CallbackInfo ci) {
      if (!this.field_2035.options.hideGui) {
         Bus.post(new RenderGuiEvent.Pre(grafico, tiempo.getGameTimeDeltaPartialTick(false)));
      }
   }

   @Inject(method = "renderHearts", at = @At("HEAD"), cancellable = true, require = 0)
   private void ruleta$sinCorazones(CallbackInfo ci) {
      if (MisOjos.ocultarCorazones()) {
         ci.cancel();
      }
   }
}
