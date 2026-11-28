package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilZarpazoProcedure {
   private static final int CARGA_TICKS = 22;
   private static final int[] PASADA_TICKS = new int[]{4, 12, 24};
   private static final float[] PASADA_DANIO = new float[]{7.0F, 9.0F, 13.0F};
   private static final double[] PASADA_ELEVACION = new double[]{0.45, 0.6, 1.5};
   private static final int FIN_CLIP = 44;
   private static final double LARGO = 12.0;
   private static final double ANCHO = 2.8;
   private static final double ABANICO = 0.3;
   private static final double INICIO = 2.0;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel level && reviil.isAlive()) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         if (reviil.isGiant()) {
            BossFx.subtitle(level, BossFx.centre(reviil), 140.0, "REVIIL NO ZARPA DE GIGANTE", 48);
         } else if (reviil.escenaTicks <= 0) {
            Vec3 desde = BossFx.ground(reviil);
            Vec3 hacia = BossFx.aim(level, reviil, 20.0);
            double angulo = BossFx.miraA(reviil, hacia);
            double tramo = Math.max(4.0, Math.min(12.0, BossFx.distanciaPlana(desde, hacia)) - 2.0);
            double[] pasadas = new double[]{angulo, angulo - 0.3, angulo + 0.3};
            reviil.setAnimation("emote.zarpazo.carga");
            reviil.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);

            for (int i = 0; i < pasadas.length; i++) {
               Vec3 boca = desde.add(new Vec3(Math.cos(pasadas[i]) * 2.0, 0.0, Math.sin(pasadas[i]) * 2.0));
               BossFx.warningBeam(level, null, boca, pasadas[i], tramo, 2.8, 22);
            }

            BossFx.alert(level, BossFx.centre(reviil), 150.0, "⚠ REVIIL ZARPA");
            BossFx.sound(level, BossFx.centre(reviil), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 1.6F, 0.9);
            BossFx.shake(level, BossFx.centre(reviil), 30.0, 8, 0.12);
            ModmindsMod.queueServerWork(22, () -> {
               if (!reviil.isAlive()) {
                  reviil.releasePose();
               } else {
                  reviil.setAnimation("emote.zarpazo.golpe");

                  for (int ix = 0; ix < PASADA_TICKS.length; ix++) {
                     int espera = PASADA_TICKS[ix];
                     double giro = pasadas[ix];
                     float dana = PASADA_DANIO[ix];
                     double elevacion = PASADA_ELEVACION[ix];
                     boolean remata = ix == PASADA_TICKS.length - 1;
                     ModmindsMod.queueServerWork(espera, () -> corta(level, reviil, desde, giro, tramo, dana, elevacion, remata));
                  }

                  ModmindsMod.queueServerWork(44, () -> {
                     if (reviil.isAlive()) {
                        reviil.releasePose();
                     }
                  });
               }
            });
         }
      }
   }

   private static void corta(ServerLevel world, ReviilEntity reviil, Vec3 desde, double angle, double tramo, float dana, double elevacion, boolean remata) {
      if (reviil.isAlive()) {
         Vec3 dir = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
         Vec3 boca = desde.add(dir.x * 2.0, 0.0, dir.z * 2.0);
         Vec3 punta = boca.add(dir.x * tramo, 0.0, dir.z * tramo);
         BossFx.clawFurrow(world, null, boca, tramo, 2.8, angle, remata ? 7 : 5, remata ? 150 : 110);
         BossFx.burst(world, boca.add(0.0, 0.6, 0.0), BossFx.CRUSHED_STONE, 6, 1.2, 0.2);
         BossFx.sound(world, punta, SoundEvents.GRAVEL_FALL, SoundSource.HOSTILE, 1.6F, remata ? 0.95 : 1.3);
         BossFx.shake(world, boca, 26.0, remata ? 10 : 6, remata ? 0.34 : 0.18);
         if (remata) {
            BossFx.sound(world, punta, (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.8F, 0.6);
         }

         hit(world, reviil, boca, dir, angle, tramo, dana, elevacion);
      }
   }

   private static void hit(ServerLevel world, ReviilEntity reviil, Vec3 boca, Vec3 dir, double angle, double tramo, float dana, double elevacion) {
      double half = 1.4;
      Vec3 axis = new Vec3(-Math.sin(angle), 0.0, Math.cos(angle));

      for (Player victim : world.getEntitiesOfClass(
         Player.class,
         new AABB(boca, boca.add(dir.x * tramo, 1.0, dir.z * tramo)).inflate(5.8),
         e -> e.isAlive() && !e.isSpectator() && !e.isPassenger()
      )) {
         Vec3 offset = victim.position().subtract(boca);
         double along = offset.dot(dir);
         if (!(along < -0.6) && !(along > tramo + 0.6)) {
            double lado = offset.dot(axis);
            if (!(Math.abs(lado) > half + victim.getBbWidth() * 0.5)) {
               victim.hurt(world.damageSources().mobAttack(reviil), reviil.phaseDamage(dana));
               reviil.sting(victim);
               double sign = lado >= 0.0 ? 1.0 : -1.0;
               victim.setDeltaMovement(victim.getDeltaMovement().add(axis.x * sign * 0.85, elevacion, axis.z * sign * 0.85));
               victim.needsSync = true;
            }
         }
      }
   }
}
