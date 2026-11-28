package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.init.ModmindsModParticleTypes;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilAvalanchaProcedure {
   private static final double LARGO_MIN = 26.0;
   private static final double LARGO_MAX = 200.0;
   private static final double ANCHO = 11.0;
   private static final double ALTO = 5.5;
   private static final int AVISO_TICKS = 40;
   private static final int VIAJE_TICKS = 76;
   private static final int PASO_TICKS = 2;
   private static final double DAÑO = 26.0;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel level && reviil.isAlive()) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         if (!reviil.isGiant()) {
            BossFx.subtitle(level, BossFx.centre(reviil), 140.0, "ESO ES UN GOLPE DE GIGANTE", 46);
         } else if (reviil.escenaTicks <= 0) {
            Vec3 from = BossFx.ground(reviil);
            Vec3 hacia = BossFx.aim(level, reviil, 200.0);
            double largo = Math.min(200.0, Math.max(26.0, BossFx.distanciaPlana(from, hacia)));
            double angle = BossFx.miraA(reviil, hacia);
            Vec3 dir = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
            reviil.setAnimation("emote.giant.avalancha");
            reviil.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
            BossFx.alert(level, from, 170.0, "⚠ REVIIL SUELTA LA MONTAÑA");
            BossFx.warningBeam(level, null, from, angle, largo, 11.0, 40);
            BossFx.sound(level, from, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 2.4F, 0.45);
            BossFx.shake(level, from, 100.0, 20, 0.14);
            ModmindsMod.queueServerWork(40, () -> {
               if (reviil.isAlive()) {
                  BossFx.blockAvalanche(level, from, largo, 11.0, 5.5, angle, 146);
                  BossFx.sound(level, from, SoundEvents.GRAVEL_FALL, SoundSource.HOSTILE, 2.6F, 0.35);
                  avanza(level, reviil, from, dir, angle, largo, 0);
               }
            });
         }
      }
   }

   private static void avanza(ServerLevel world, ReviilEntity reviil, Vec3 from, Vec3 dir, double angle, double largo, int tick) {
      if (reviil.isAlive()) {
         double anterior = largo * Math.min(1.0, Math.max(0.0, (tick - 2) / 76.0));
         double frente = largo * Math.min(1.0, tick / 76.0);
         golpea(world, reviil, from, dir, angle, anterior, frente);
         Vec3 punta = from.add(dir.x * frente, 0.0, dir.z * frente);
         BossFx.sandWallRow(world, punta, angle, 11.0, (int)Math.round(5.5), 46);
         if (tick % 8 < 2) {
            BossFx.sound(world, punta, SoundEvents.ANVIL_FALL, SoundSource.HOSTILE, 1.8F, 0.4);
            BossFx.shake(world, punta, 60.0, 5, 0.2);
         }

         if (tick >= 76) {
            BossFx.shockwave(world, punta, 17.6, 16);
            BossFx.groundCrack(world, punta, 24.0, 110);
            BossFx.smash(world, punta, 8.0, 0.3, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get());
            BossFx.burst(world, punta.add(0.0, 1.2, 0.0), (ParticleOptions)ModmindsModParticleTypes.METEOR_FLASH.get(), 4, 8.8, 0.06);
            BossFx.burst(world, punta.add(0.0, 0.5, 0.0), (ParticleOptions)ModmindsModParticleTypes.PULSE_EM.get(), 3, 5.5, 0.08);
            BossFx.shake(world, punta, 120.0, 20, 0.6);
            BossFx.sound(world, punta, (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.6F, 0.5);
            ModmindsMod.queueServerWork(28, () -> reviil.releasePose());
         } else {
            ModmindsMod.queueServerWork(2, () -> avanza(world, reviil, from, dir, angle, largo, tick + 2));
         }
      }
   }

   private static void golpea(ServerLevel world, ReviilEntity reviil, Vec3 from, Vec3 dir, double angle, double desde, double hasta) {
      Vec3 normal = new Vec3(-Math.sin(angle), 0.0, Math.cos(angle));
      double medio = 5.5;

      for (ServerPlayer sp : world.getEntitiesOfClass(
         ServerPlayer.class,
         new AABB(from, from.add(dir.x * hasta, 0.0, dir.z * hasta)).inflate(14.0),
         p -> p.isAlive() && !p.isSpectator() && !p.isCreative() && !p.isPassenger()
      )) {
         Vec3 offset = sp.position().subtract(from);
         double largo = offset.dot(dir);
         if (!(largo < desde - 0.8) && !(largo > hasta + 0.8) && !(Math.abs(offset.dot(normal)) > medio + sp.getBbWidth() * 0.5)) {
            sp.hurt(world.damageSources().mobAttack(reviil), reviil.phaseDamage(26.0F));
            reviil.sting(sp);
            sp.setDeltaMovement(dir.x * 0.9, 0.6, dir.z * 0.9);
            sp.needsSync = true;
         }
      }
   }
}
