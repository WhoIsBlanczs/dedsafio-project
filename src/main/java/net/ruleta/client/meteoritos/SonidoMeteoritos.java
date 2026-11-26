package net.ruleta.client.meteoritos;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public final class SonidoMeteoritos extends AbstractTickableSoundInstance {
   private final Supplier<Float> volumen;
   private final Supplier<Vec3> donde;

   public SonidoMeteoritos(SoundEvent evento, boolean repetir, Supplier<Float> volumen, Supplier<Vec3> donde) {
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
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
               double d = mc.player.getEyePosition().distanceTo(p);
               this.pitch = Mth.clamp(0.72F + 0.5F * (float)Math.exp(-d / 140.0), 0.7F, 1.25F);
            }
         }
      }
   }

   public boolean canStartSilent() {
      return true;
   }
}
