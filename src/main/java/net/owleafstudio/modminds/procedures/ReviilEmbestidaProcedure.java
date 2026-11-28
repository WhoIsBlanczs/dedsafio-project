package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.init.ModmindsModParticleTypes;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilEmbestidaProcedure {
   private static final int CARGAS = 3;
   private static final int ALINEA_TICKS = 22;
   private static final int AVISO_TICKS = 18;
   private static final int ENTRE_CARGAS_TICKS = 26;
   private static final double RECORRIDO = 30.0;
   private static final double VELOCIDAD = 0.42;
   private static final double DAMN = 26.0;
   private static final double BLOQUEO = 0.35;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel level && reviil.isAlive()) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         if (reviil.isGiant()) {
            BossFx.subtitle(level, BossFx.centre(reviil), 140.0, "REVIIL NO PUEDE CARGAR DE GIGANTE", 48);
         } else if (reviil.escenaTicks <= 0) {
            double ancho = Math.max(3.5, reviil.getBbWidth() * 0.9);
            reviil.setAnimation("emote.carga");
            BossFx.alert(level, BossFx.centre(reviil), 170.0, "⚠ REVIIL CARGA");
            BossFx.groundSwell(level, BossFx.ground(reviil), 12.0, 40);
            BossFx.sound(level, BossFx.centre(reviil), SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 2.0F, 0.62);
            ModmindsMod.queueServerWork(22, () -> alinea(level, reviil, 0, ancho));
         }
      }
   }

   private static void alinea(ServerLevel world, ReviilEntity reviil, int cargo, double ancho) {
      if (reviil.isAlive()) {
         Vec3 from = BossFx.ground(reviil);
         Vec3 hacia = BossFx.aim(world, reviil, 64.0).subtract(from);
         if (hacia.x == 0.0 && hacia.z == 0.0) {
            hacia = new Vec3(0.0, 0.0, 1.0);
         }

         double angle = Math.atan2(hacia.z, hacia.x);
         Vec3 dir = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
         reviil.setYRot((float)Math.toDegrees(Math.atan2(-dir.x, dir.z)));
         reviil.setYHeadRot(reviil.getYRot());
         reviil.setAnimation(cargo == 0 ? "emote.carga" : (cargo == 1 ? "emote.carga.dos" : "emote.carga.tres"));
         BossFx.sound(world, BossFx.centre(reviil), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 2.0F, 0.5 + cargo * 0.12);
         BossFx.warningBeam(world, null, from, angle, 30.0, ancho, 18);
         BossFx.shake(world, BossFx.centre(reviil), 60.0, 18, 0.1 + cargo * 0.05);
         ModmindsMod.queueServerWork(18, () -> corre(world, reviil, from, dir, ancho, cargo, 0));
      }
   }

   private static void corre(ServerLevel world, ReviilEntity reviil, Vec3 origen, Vec3 dir, double ancho, int cargo, int paso) {
      if (reviil.isAlive()) {
         if (paso == 0) {
            reviil.setAnimation("emote.carga.corre");
         }

         double avanzado = reviil.position().subtract(origen).dot(dir);
         if (avanzado >= 30.0) {
            impacto(world, reviil, BossFx.ground(reviil), ancho, cargo);
         } else {
            double velocidad = 0.42 * (1.0 + cargo * 0.34);
            Vec3 antes = reviil.position();
            Vec3 zancada = new Vec3(dir.x * velocidad, 0.0, dir.z * velocidad);
            reviil.move(MoverType.SELF, zancada);
            reviil.setDeltaMovement(zancada.x, reviil.getDeltaMovement().y, zancada.z);
            double recorrido = reviil.position().subtract(antes).dot(dir);
            if (recorrido < velocidad * 0.35) {
               impacto(world, reviil, BossFx.ground(reviil), ancho, cargo);
            } else {
               Vec3 pecho = reviil.position().add(0.0, reviil.getBbHeight() * 0.55, 0.0);
               if (paso % 3 == 0) {
                  BossFx.groundCrack(world, BossFx.ground(reviil), 7.5 + cargo, 55);
               }

               if (paso % 4 == 0) {
                  BossFx.sound(world, pecho, SoundEvents.GRAVEL_FALL, SoundSource.HOSTILE, 1.3F, 0.5 + world.getRandom().nextDouble() * 0.2);
               }

               atropella(world, reviil, antes, reviil.position(), dir, ancho, cargo);
               if (paso % 8 == 0) {
                  BossFx.shake(world, pecho, 48.0, 4, 0.18 + cargo * 0.07);
               }

               ModmindsMod.queueServerWork(1, () -> corre(world, reviil, origen, dir, ancho, cargo, paso + 1));
            }
         }
      }
   }

   private static void atropella(ServerLevel world, ReviilEntity reviil, Vec3 desde, Vec3 hasta, Vec3 dir, double ancho, int cargo) {
      double mitad = ancho * 0.5;
      float daño = reviil.phaseDamage((float)(26.0 * (1.0 + cargo * 0.25)));
      Vec3 normal = new Vec3(-dir.z, 0.0, dir.x);

      for (Player victim : world.getEntitiesOfClass(
         Player.class,
         new AABB(desde, hasta).inflate(mitad + reviil.getBbWidth() + 2.0),
         e -> e.isAlive() && !e.isSpectator() && !e.isPassenger()
      )) {
         if (!(BossFx.distanceToSegment(victim.position(), desde, hasta) > mitad + victim.getBbWidth() * 0.5)) {
            victim.hurt(world.damageSources().mobAttack(reviil), daño);
            reviil.sting(victim);
            double lado = Math.signum(victim.position().subtract(desde).dot(normal));
            if (lado == 0.0) {
               lado = 1.0;
            }

            victim.setDeltaMovement(dir.x * 0.85 + normal.x * lado * 0.42, 0.62, dir.z * 0.85 + normal.z * lado * 0.42);
            victim.needsSync = true;
         }
      }
   }

   private static void impacto(ServerLevel world, ReviilEntity reviil, Vec3 at, double ancho, int cargo) {
      double radio = ancho + 3.5 + cargo * 2.0;
      double daño = reviil.phaseDamage((float)(26.0 * (1.0 + cargo * 0.25)));
      BossFx.shockwave(world, at, radio, 13 + cargo * 2);
      BossFx.smash(world, at, 5.0 + cargo, 0.34, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get());
      BossFx.burst(world, at.add(0.0, 1.3, 0.0), (ParticleOptions)ModmindsModParticleTypes.METEOR_FLASH.get(), 3 + cargo, 2.4, 0.05);
      BossFx.burst(world, at.add(0.0, 0.4, 0.0), (ParticleOptions)ModmindsModParticleTypes.PULSE_EM.get(), 2, 1.6, 0.07);
      BossFx.shake(world, at, 90.0, 20 + cargo * 4, 0.45 + cargo * 0.15);
      BossFx.sound(world, at, (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.2F, 0.72);
      BossFx.sound(world, at, SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.HOSTILE, 1.9F, 0.42);

      for (int i = 0; i < 3; i++) {
         BossFx.groundCrack(world, at.add(i * 1.6 - 1.6, 0.0, i * 1.1 - 1.1), 8.0 + cargo * 2, 80);
      }

      BossFx.blastDamage(world, world.damageSources().mobAttack(reviil), at, radio, (float)daño, (float)(daño * 0.25), 1.0 + cargo * 0.2, 0.95);
      if (cargo + 1 >= 3) {
         BossFx.alert(world, at, 170.0, "⚠ REVIIL TERMINA DE CARGAR");
         reviil.setAnimation("emote.carga.choque");
         ModmindsMod.queueServerWork(30, () -> reviil.releasePose());
      } else {
         ModmindsMod.queueServerWork(26, () -> alinea(world, reviil, cargo + 1, ancho));
      }
   }
}
