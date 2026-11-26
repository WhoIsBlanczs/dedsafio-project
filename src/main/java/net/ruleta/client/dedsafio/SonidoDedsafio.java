package net.ruleta.client.dedsafio;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

final class SonidoDedsafio extends AbstractTickableSoundInstance {
   private float objetivo;

   SonidoDedsafio(SoundEvent evento, SoundSource fuente, float volumen, boolean repetir) {
      super(evento, fuente, RandomSource.create());
      this.volume = volumen;
      this.objetivo = volumen;
      this.looping = repetir;
      this.relative = true;
      this.attenuation = Attenuation.NONE;
   }

   void volumen(float v) {
      this.objetivo = v;
   }

   void cortar() {
      this.stop();
   }

   public void tick() {
      this.volume = this.volume + (this.objetivo - this.volume) * 0.1F;
   }
}
