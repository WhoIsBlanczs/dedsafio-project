package net.ruleta.client.compat;

import net.minecraft.client.resources.sounds.SoundInstance;

public final class PlaySoundEvent extends Evento {
   private SoundInstance sonido;

   public PlaySoundEvent(SoundInstance sonido) {
      this.sonido = sonido;
   }

   public SoundInstance getSound() {
      return this.sonido;
   }

   public void setSound(SoundInstance sonido) {
      this.sonido = sonido;
   }
}
