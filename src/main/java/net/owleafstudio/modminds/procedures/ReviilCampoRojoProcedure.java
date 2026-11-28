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

public class ReviilCampoRojoProcedure {
   private static final double RADIO = 30.0;
   private static final int CARGA_TICKS = 66;
   private static final int GOLPE_ADELANTO_TICKS = 6;
   private static final int GOLPE_LARGO_TICKS = 28;
   private static final int IMPACTO_TICKS = 14;
   private static final int CAMPO_TICKS = 240;
   private static final int QUEMA_TICKS = 20;
   private static final double DAÑO_GOLPE = 44.0;
   private static final double DAÑO_CAMPO = 9.0;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel level && reviil.isAlive()) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         if (!reviil.isGiant()) {
            BossFx.subtitle(level, BossFx.centre(reviil), 140.0, "ESO ES UN GOLPE DE GIGANTE", 46);
         } else if (reviil.escenaTicks <= 0) {
            Vec3 desde = BossFx.ground(reviil);
            Vec3 at = BossFx.aim(level, reviil, 200.0);
            BossFx.miraA(reviil, at);
            reviil.setAnimation("emote.giant.dosbrazos");
            reviil.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
            BossFx.alert(level, at, 190.0, "⚠ REVIIL LEVANTA LOS DOS BRAZOS");
            BossFx.warningAt(level, at, 30.0, 66);
            BossFx.sound(level, at, SoundEvents.WARDEN_HEARTBEAT, SoundSource.HOSTILE, 2.4F, 0.45);

            for (int latido = 1; latido <= 5; latido++) {
               double volumen = 1.6 + latido * 0.25;
               double paso = 0.62 - latido * 0.03;
               double temblor = 0.1 + latido * 0.05;
               ModmindsMod.queueServerWork(66 - latido * 10, () -> {
                  if (reviil.isAlive()) {
                     BossFx.sound(level, at, SoundEvents.WARDEN_HEARTBEAT, SoundSource.HOSTILE, (float)volumen, paso);
                     BossFx.shake(level, at, 90.0, 4, temblor);
                  }
               });
            }

            ModmindsMod.queueServerWork(60, () -> {
               if (reviil.isAlive()) {
                  reviil.setAnimation("emote.giant.dosbrazos.golpe");
               }
            });
            ModmindsMod.queueServerWork(66, () -> {
               if (reviil.isAlive()) {
                  BossFx.golpeViaja(level, desde, at, 14, () -> golpe(level, reviil, at));
               }
            });
         }
      }
   }

   private static void golpe(ServerLevel level, ReviilEntity reviil, Vec3 at) {
      if (reviil.isAlive()) {
         double radio = reviil.phaseBlastRadius(30.0);
         BossFx.sound(level, at, (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 3.2F, 0.4);
         BossFx.sound(level, at, SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.HOSTILE, 2.8F, 0.42);
         BossFx.sound(level, at, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 2.2F, 0.5);
         BossFx.shake(level, at, 180.0, 34, 1.0);
         BossFx.smash(level, at, 12.0, 0.5, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get());
         BossFx.burst(level, at.add(0.0, 1.6, 0.0), (ParticleOptions)ModmindsModParticleTypes.METEOR_FLASH.get(), 5, radio * 0.5, 0.05);
         BossFx.burst(level, at.add(0.0, 0.5, 0.0), (ParticleOptions)ModmindsModParticleTypes.PULSE_EM.get(), 4, radio * 0.3, 0.07);
         BossFx.shockwave(level, at, radio * 1.15, 20);
         BossFx.groundCrack(level, at, radio * 0.9, 240);
         BossFx.groundCrack(level, at, radio * 0.7, 240);
         BossFx.groundCrack(level, at, radio * 0.5, 240);
         BossFx.redField(level, at, radio, level.getRandom().nextDouble() * Math.PI * 2.0, 240);
         BossFx.blastDamage(level, level.damageSources().mobAttack(reviil), at, radio, reviil.phaseDamage(44.0F), 4.0F, 1.9, 1.05, reviil::sting);
         ModmindsMod.queueServerWork(28, () -> {
            if (reviil.isAlive()) {
               reviil.setAnimation("emote.giant.dosbrazos.apoya");
            }
         });
         ModmindsMod.queueServerWork(14, () -> quemando(level, reviil, at, radio, 0));
      }
   }

   private static void quemando(ServerLevel world, ReviilEntity reviil, Vec3 at, double radio, int tick) {
      if (tick < 240 && reviil.isAlive()) {
         if (tick % 20 == 0) {
            for (ServerPlayer sp : world.getEntitiesOfClass(
               ServerPlayer.class, new AABB(at, at).inflate(radio + 2.0), p -> p.isAlive() && !p.isSpectator() && !p.isCreative()
            )) {
               if (sp.position().distanceTo(at) <= radio) {
                  sp.hurt(world.damageSources().mobAttack(reviil), reviil.phaseDamage(9.0F));
                  BossFx.marker(world, sp.position(), 1.6, 6);
               }
            }
         }

         ModmindsMod.queueServerWork(1, () -> quemando(world, reviil, at, radio, tick + 1));
      } else {
         reviil.releasePose();
      }
   }
}
