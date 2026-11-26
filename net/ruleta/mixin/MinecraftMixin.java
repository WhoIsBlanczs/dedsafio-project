package net.ruleta.mixin;

import net.minecraft.client.Minecraft;
import net.ruleta.client.linterna.LinternaCliente;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public abstract class MinecraftMixin {
   @Inject(
      method = {"startUseItem"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private void ruleta$sinRepetirClic(CallbackInfo ci) {
      if (LinternaCliente.ignorarUso()) {
         ci.cancel();
      }
   }
}
