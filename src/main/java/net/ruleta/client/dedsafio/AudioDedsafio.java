package net.ruleta.client.dedsafio;

import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

final class AudioDedsafio {
   private static final String NS = "ruletadedsafio";
   private static final SoundEvent CANTO_I = SoundEvent.createVariableRangeEvent(Vtx.rl("ruletadedsafio", "canto_i"));
   private static final SoundEvent CANTO_D = SoundEvent.createVariableRangeEvent(Vtx.rl("ruletadedsafio", "canto_d"));
   private static final SoundEvent CANCION_I = SoundEvent.createVariableRangeEvent(Vtx.rl("ruletadedsafio", "cancion_i"));
   private static final SoundEvent CANCION_D = SoundEvent.createVariableRangeEvent(Vtx.rl("ruletadedsafio", "cancion_d"));
   private static final SoundEvent OLA_LEJOS = SoundEvent.createVariableRangeEvent(Vtx.rl("ruletadedsafio", "ola_lejos"));
   private static final SoundEvent OLA_DENTRO = SoundEvent.createVariableRangeEvent(Vtx.rl("ruletadedsafio", "ola_dentro"));
   private static final SoundEvent OLA_GOLPE = SoundEvent.createVariableRangeEvent(Vtx.rl("ruletadedsafio", "ola_golpe"));
   private static final double DISTANCIA = 6.0;
   private static final double APERTURA = 24.0;
   private static SonidoPosicional cantoI;
   private static SonidoPosicional cantoD;
   private static SonidoPosicional cancionI;
   private static SonidoPosicional cancionD;
   private static SonidoPosicional olaLejos;
   private static SonidoDedsafio dentro;
   private static SonidoDedsafio golpe;

   private AudioDedsafio() {
   }

   private static Function<Vec3, Vec3> haciaElSol(int lado) {
      return ojo -> {
         float[] s = DedsafioCielo.direccionSol();
         double fx = s[0];
         double fz = s[2];
         double n = Math.sqrt(fx * fx + fz * fz);
         if (n < 1.0E-6) {
            fx = 0.0;
            fz = 1.0;
         } else {
            fx /= n;
            fz /= n;
         }

         double a = Math.toRadians(24.0);
         double dx = fx * Math.cos(a) + lado * -fz * Math.sin(a);
         double dz = fz * Math.cos(a) + lado * fx * Math.sin(a);
         return ojo.add(dx * 6.0, 0.0, dz * 6.0);
      };
   }

   private static void reproducir(SonidoPosicional s) {
      Minecraft.getInstance().getSoundManager().play(s);
   }

   static void iniciarCanto() {
      if (cantoI == null) {
         cantoI = new SonidoPosicional(CANTO_I, 0.9F, true, haciaElSol(-1));
         cantoD = new SonidoPosicional(CANTO_D, 0.9F, true, haciaElSol(1));
         reproducir(cantoI);
         reproducir(cantoD);
      }
   }

   static void pararCanto() {
      if (cantoI != null) {
         cantoI.cortar();
         cantoD.cortar();
         cantoI = null;
         cantoD = null;
      }
   }

   static void iniciarCancion(float volumen) {
      pararCancion();
      cancionI = new SonidoPosicional(CANCION_I, volumen, false, haciaElSol(-1));
      cancionD = new SonidoPosicional(CANCION_D, volumen, false, haciaElSol(1));
      reproducir(cancionI);
      reproducir(cancionD);
   }

   static void pararCancion() {
      if (cancionI != null) {
         cancionI.cortar();
         cancionD.cortar();
         cancionI = null;
         cancionD = null;
      }
   }

   static void ola(float volumen, Function<Vec3, Vec3> donde) {
      if (olaLejos != null && !olaLejos.isStopped()) {
         olaLejos.volumen(volumen);
      } else {
         olaLejos = new SonidoPosicional(OLA_LEJOS, volumen, true, donde);
         reproducir(olaLejos);
      }
   }

   static void pararOla() {
      if (olaLejos != null) {
         olaLejos.cortar();
         olaLejos = null;
      }
   }

   static void tragado() {
      pararOla();
      pararDentro();
      golpe = new SonidoDedsafio(OLA_GOLPE, SoundSource.MASTER, 1.0F, false);
      dentro = new SonidoDedsafio(OLA_DENTRO, SoundSource.MASTER, 0.0F, true);
      dentro.volumen(0.9F);
      Minecraft.getInstance().getSoundManager().play(golpe);
      Minecraft.getInstance().getSoundManager().play(dentro);
   }

   static void pararDentro() {
      if (dentro != null) {
         dentro.cortar();
         dentro = null;
      }

      if (golpe != null) {
         golpe.cortar();
         golpe = null;
      }
   }

   static void pararTodo() {
      pararCanto();
      pararCancion();
      pararOla();
      pararDentro();
   }
}
