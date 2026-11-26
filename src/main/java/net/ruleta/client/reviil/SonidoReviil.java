package net.ruleta.client.reviil;

import java.util.function.Supplier;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

final class SonidoReviil extends AbstractTickableSoundInstance {
   private final Supplier<float[]> estado;

   SonidoReviil(SoundEvent evento, boolean repetir, Supplier<float[]> estado) {
      super(evento, SoundSource.MASTER, RandomSource.create());
      this.estado = estado;
      this.looping = repetir;
      this.relative = true;
      this.attenuation = Attenuation.NONE;
      this.aplicar();
   }

   public void tick() {
      this.aplicar();
   }

   private void aplicar() {
      float[] e = this.estado.get();
      if (e == null) {
         this.stop();
      } else {
         this.volume = Math.max(1.0E-4F, e[0]);
         this.pitch = e[1];
      }
   }

   public boolean canStartSilent() {
      return true;
   }
}
