package net.ruleta.mixin;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.ruleta.client.compat.Bus;
import net.ruleta.client.compat.PlaySoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SoundEngine.class})
public abstract class SonidoMixin {
   @Inject(
      method = {"play"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private void ruleta$alSonar(SoundInstance sonido, CallbackInfo ci) {
      if (Bus.hayOyentes(PlaySoundEvent.class)) {
         PlaySoundEvent e = new PlaySoundEvent(sonido);
         Bus.post(e);
         if (e.getSound() == null) {
            ci.cancel();
         }
      }
   }
}
