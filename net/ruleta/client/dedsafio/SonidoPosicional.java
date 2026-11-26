package net.ruleta.client.dedsafio;

import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

final class SonidoPosicional extends AbstractTickableSoundInstance {
   private final Function<Vec3, Vec3> posicion;
   private float objetivo;

   SonidoPosicional(SoundEvent evento, float volumen, boolean repetir, Function<Vec3, Vec3> posicion) {
      super(evento, SoundSource.MASTER, RandomSource.create());
      this.volume = volumen;
      this.objetivo = volumen;
      this.looping = repetir;
      this.relative = false;
      this.attenuation = Attenuation.LINEAR;
      this.posicion = posicion;
      this.mover();
   }

   void volumen(float v) {
      this.objetivo = v;
   }

   void cortar() {
      this.stop();
   }

   private void mover() {
      Entity e = Minecraft.getInstance().getCameraEntity();
      if (e != null) {
         Vec3 p = this.posicion.apply(e.getEyePosition());
         this.x = p.x;
         this.y = p.y;
         this.z = p.z;
      }
   }

   public void tick() {
      this.mover();
      this.volume = this.volume + (this.objetivo - this.volume) * 0.1F;
   }
}
