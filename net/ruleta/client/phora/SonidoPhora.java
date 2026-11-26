package net.ruleta.client.phora;

import java.util.function.Supplier;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public final class SonidoPhora extends AbstractTickableSoundInstance {
   private final Supplier<Float> volumen;
   private final Supplier<Vec3> donde;

   public SonidoPhora(SoundEvent evento, boolean repetir, Supplier<Float> volumen, Supplier<Vec3> donde) {
      super(evento, SoundSource.MASTER, RandomSource.create());
      this.volumen = volumen;
      this.donde = donde;
      this.looping = repetir;
      this.delay = 0;
      if (donde == null) {
         this.relative = true;
         this.attenuation = Attenuation.NONE;
      } else {
         this.relative = false;
         this.attenuation = Attenuation.LINEAR;
      }

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
         this.volume = Math.max(1.0E-4F, v);
         this.pitch = 1.0F;
         if (this.donde != null) {
            Vec3 p = this.donde.get();
            if (p == null) {
               this.stop();
               return;
            }

            this.x = p.x;
            this.y = p.y;
            this.z = p.z;
         }
      }
   }

   public boolean canStartSilent() {
      return true;
   }
}
