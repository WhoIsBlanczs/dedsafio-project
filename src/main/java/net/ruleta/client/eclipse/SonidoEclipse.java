package net.ruleta.client.eclipse;

import java.util.function.Supplier;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

final class SonidoEclipse extends AbstractTickableSoundInstance {
   private final Supplier<Float> volumen;

   SonidoEclipse(SoundEvent evento, boolean repetir, Supplier<Float> volumen) {
      super(evento, SoundSource.MASTER, RandomSource.create());
      this.volumen = volumen;
      this.looping = repetir;
      this.relative = true;
      this.attenuation = Attenuation.NONE;
      this.aplicar();
   }

   public void tick() {
      this.aplicar();
   }

   private void aplicar() {
      float v = this.volumen.get();
      if (v < 0.0F) {
         this.stop();
      } else {
         this.volume = Math.max(1.0E-4F, Math.min(1.0F, v));
         this.pitch = 1.0F;
      }
   }

   public boolean canStartSilent() {
      return true;
   }
}
