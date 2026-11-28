package net.owleafstudio.modminds.boss;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.init.ModmindsModParticleTypes;
import net.owleafstudio.modminds.init.ModmindsModSounds;
import org.joml.Vector3f;

public final class ReviilFx {
   private static final Vector3f VOID = new Vector3f(0.55F, 0.18F, 0.85F);
   private static final Vector3f FURY = new Vector3f(0.95F, 0.22F, 0.16F);

   private ReviilFx() {
   }

   public static void melee(ServerLevel world, ReviilEntity attacker, Entity target) {
      Vec3 at = BossFx.centre(target);
      attacker.setAnimation(world.getGameTime() % 2L == 0L ? "emote.attack_left" : "emote.attack_right");
      world.sendParticles(ParticleTypes.SWEEP_ATTACK, at.x, at.y, at.z, 6, 0.35, 0.35, 0.35, 0.1);
      world.sendParticles(ParticleTypes.SONIC_BOOM, at.x, at.y, at.z, 1, 0.0, 0.0, 0.0, 0.0);
      BossFx.burst(world, at, ParticleTypes.ELECTRIC_SPARK, 14, 0.4, 0.1);
      BossFx.burst(world, at, (ParticleOptions)ModmindsModParticleTypes.BOSS_FLARE.get(), 3, 0.6, 0.05);
      BossFx.burst(world, at, ParticleTypes.GUST_EMITTER_SMALL, 3, 0.5, 0.05);
      BossFx.burst(world, at, (ParticleOptions)ModmindsModParticleTypes.EXPLOSION_LIGHT.get(), 2, 0.45, 0.05);
      BossFx.burst(world, at, (ParticleOptions)ModmindsModParticleTypes.EXPLOSION_FIRE.get(), 3, 0.5, 0.06);
      BossFx.burst(world, at, (ParticleOptions)ModmindsModParticleTypes.BLAST_DUST.get(), 5, 0.7, 0.1);
      BossFx.blastSound(world, at, 0.5);
      BossFx.sound(world, at, SoundEvents.WARDEN_ATTACK_IMPACT, SoundSource.HOSTILE, 1.4F, 0.75);
      BossFx.sound(world, BossFx.centre(attacker), SoundEvents.RAVAGER_ATTACK, SoundSource.HOSTILE, 1.0F, 0.6, 0.15);
      BossFx.shake(world, at, 24.0, 8, 0.9);
   }

   public static void onHurt(ServerLevel world, Entity attacker) {
      BossFx.burst(world, attacker, ParticleTypes.ELECTRIC_SPARK, 8, attacker.getBbWidth() * 0.5, 0.05);
      BossFx.burst(world, attacker, (ParticleOptions)ModmindsModParticleTypes.GUN_SPARK.get(), 4, attacker.getBbWidth() * 0.4, 0.08);
   }

   public static void ambient(ServerLevel world, Entity attacker) {
      Vec3 at = BossFx.centre(attacker);
      BossFx.sound(world, at, SoundEvents.WARDEN_AMBIENT, SoundSource.HOSTILE, 1.4F, 0.55 + world.getRandom().nextDouble() * 0.15, 0.0);
      BossFx.vortex(world, at, attacker.getBbWidth(), 10, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get(), 0.02);
      BossFx.vortex(world, at, attacker.getBbWidth(), 6, ParticleTypes.END_ROD, 0.02);
   }

   public static void death(ServerLevel world, Entity attacker, int deathTick) {
      Vec3 at = BossFx.centre(attacker);
      if (deathTick == 1) {
         BossFx.sound(world, at, SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 3.0F, 0.6);
         BossFx.smash(world, BossFx.ground(attacker), 14.0, 1.6, (ParticleOptions)ModmindsModParticleTypes.SHOCKWAVE_RING.get());
         BossFx.burst(world, at, (ParticleOptions)ModmindsModParticleTypes.BOSS_FLARE.get(), 8, 1.5, 0.1);
      }

      BossFx.vortex(world, at, attacker.getBbWidth() + deathTick * 0.3, 16, ParticleTypes.SOUL_FIRE_FLAME, 0.01);
      if (deathTick % 3 == 0) {
         BossFx.ring(world, at, attacker.getBbWidth() + deathTick * 0.4, 8, (ParticleOptions)ModmindsModParticleTypes.SHOCKWAVE_RING.get(), 1, 0.3);
      }

      if (deathTick % 8 == 0) {
         BossFx.shake(world, at, 40.0, 6, 0.5);
      }

      BossFx.colored(world, at, VOID, 1.4F, 10, attacker.getBbWidth() * 0.6);
      if (deathTick % 5 == 0) {
         BossFx.sound(world, at, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.0F, 0.6);
      }
   }

   public static void explosionCharge(ServerLevel world, Entity attacker) {
      Vec3 at = BossFx.centre(attacker);
      BossFx.alert(world, at, 96.0, "⚠ REVIIL: ANILLOS DEL VACÍO");
      BossFx.sound(world, at, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 1.6F, 0.7);
      BossFx.soundLater(world, at, SoundEvents.BEACON_POWER_SELECT, SoundSource.HOSTILE, 1.0F, 0.6, 6);
      BossFx.soundLater(world, at, SoundEvents.BEACON_POWER_SELECT, SoundSource.HOSTILE, 1.0F, 0.8, 11);
      BossFx.shake(world, at, 40.0, 15, 0.5);

      for (int tick = 0; tick < 15; tick++) {
         int step = tick;
         ModmindsMod.queueServerWork(tick, () -> {
            double radius = attacker.getBbWidth() + 6.0 - step * 0.35;
            BossFx.vortex(world, at, Math.max(1.0, radius), 18, ParticleTypes.SCULK_CHARGE_POP, 0.05);
            BossFx.colored(world, at, VOID, 1.6F, 6, 1.2);
            if (step % 4 == 0) {
               BossFx.ring(world, BossFx.ground(attacker), 3.0 + step * 0.4, 8, BossFx.DUST_PILLAR, 1, 0.5);
            }
         });
      }

      BossFx.burst(world, at, net.owleafstudio.modminds.compat.InvCompat.FLASH, 1, 0.0, 0.0);
   }

   public static int warningTicks(int ringIndex) {
      return ringIndex == 0 ? 60 : 40;
   }

   public static void explosionRingTelegraph(ServerLevel world, Entity attacker, double radius, double depth, int ringIndex) {
      BossFx.sound(world, BossFx.centre(attacker), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.HOSTILE, 1.0F, 0.65 + ringIndex * 0.1);
      BossFx.warningBand(world, attacker, radius, depth, warningTicks(ringIndex));
      ModmindsMod.queueServerWork(warningTicks(ringIndex) - 8, () -> BossFx.ring(world, BossFx.ground(attacker), radius * 0.5, 10, BossFx.DUST_PILLAR, 1, 0.6));
   }

   public static void explosionRingTelegraphAt(ServerLevel world, Vec3 centro, double radius, double depth, int ringIndex) {
      BossFx.sound(world, centro.add(0.0, 1.6, 0.0), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.HOSTILE, 1.0F, 0.65 + ringIndex * 0.1);
      BossFx.warningBandAt(world, centro, radius, depth, warningTicks(ringIndex));
      ModmindsMod.queueServerWork(warningTicks(ringIndex) - 8, () -> BossFx.ring(world, centro, radius * 0.5, 10, BossFx.DUST_PILLAR, 1, 0.6));
   }

   public static void explosionRingBlast(ServerLevel world, Entity attacker, double radius, int ringIndex) {
      explosionRingBlast(world, BossFx.ground(attacker), BossFx.centre(attacker), radius, ringIndex);
   }

   public static void explosionRingBlast(ServerLevel world, Vec3 centro, Vec3 altura, double radius, int ringIndex) {
      if (ringIndex >= 6) {
         BossFx.smash(world, centro, radius * 0.45, 1.3 + (ringIndex - 6) * 0.25, (ParticleOptions)ModmindsModParticleTypes.SHOCKWAVE_RING.get());
      } else {
         BossFx.smashSmall(world, centro, radius * 0.4, 0.8 + ringIndex * 0.12, (ParticleOptions)ModmindsModParticleTypes.SHOCKWAVE_RING.get());
      }

      BossFx.ring(world, centro, radius, 20, (ParticleOptions)ModmindsModParticleTypes.SHOCKWAVE_RING.get(), 1, 0.4);
      BossFx.ring(world, centro, radius, 10, (ParticleOptions)ModmindsModParticleTypes.BOSS_FLARE.get(), 1, 0.5);
      BossFx.burst(world, centro.add(0.0, 0.6, 0.0), (ParticleOptions)ModmindsModParticleTypes.PULSE_EM.get(), 3, radius * 0.25, 0.08);
      BossFx.ring(world, altura, radius, 20, ParticleTypes.SONIC_BOOM, 1, 0.2);
      BossFx.sound(world, altura, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 2.0F, 0.55 + ringIndex * 0.06);
      BossFx.sound(world, altura, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 0.8F, 0.7 + ringIndex * 0.05);
   }

   public static void volleyCharge(ServerLevel world, Entity attacker, int drawTicks) {
      Vec3 at = BossFx.centre(attacker);
      Vec3 rueda = BossFx.mouth(attacker, 3.0, 4.0);
      BossFx.alert(world, at, 96.0, "⚠ REVIIL: RULETA DEL VACÍO");
      BossFx.shake(world, at, 48.0, 34, 0.85);
      BossFx.vortex(world, rueda, 3.2, 12, ParticleTypes.SCULK_CHARGE_POP, 0.1);
      BossFx.sound(world, rueda, ModmindsModSounds.ARMA_CORTA.get(), SoundSource.HOSTILE, 1.0F, 0.62);
      BossFx.burst(world, rueda, (ParticleOptions)ModmindsModParticleTypes.GUN_SPARK.get(), 6, 0.5, 0.1);
      int paso = Math.max(2, drawTicks / 6);

      for (int tick = 0; tick < drawTicks; tick++) {
         int step = tick;
         ModmindsMod.queueServerWork(tick, () -> {
            tambor(world, rueda, attacker, step * 0.22);
            if (step % paso == 0) {
               int camara = step / paso;
               BossFx.sound(world, rueda, ModmindsModSounds.ARMA_CORTA.get(), SoundSource.HOSTILE, 0.9F, 0.7 + camara * 0.09);
               BossFx.ring(world, rueda, 1.5, 8, (ParticleOptions)ModmindsModParticleTypes.GUN_SPARK.get(), 1, 0.25);
            }
         });
      }

      ModmindsMod.queueServerWork(drawTicks - 6, () -> {
         BossFx.burst(world, rueda, net.owleafstudio.modminds.compat.InvCompat.FLASH, 1, 0.0, 0.0);
         BossFx.burst(world, rueda, ParticleTypes.SONIC_BOOM, 1, 0.2, 0.0);
         BossFx.colored(world, rueda, VOID, 1.8F, 8, 0.8);
         BossFx.sound(world, rueda, (SoundEvent)SoundEvents.CROSSBOW_LOADING_END.value(), SoundSource.HOSTILE, 1.6F, 0.7);
         BossFx.shake(world, rueda, 56.0, 10, 0.5);
      });
   }

   private static void tambor(ServerLevel world, Vec3 centro, Entity attacker, double angulo) {
      Vec3 mira = attacker.getLookAngle();
      double plano = Math.hypot(mira.x, mira.z);
      double ax = plano > 0.001 ? -mira.z / plano : 1.0;
      double az = plano > 0.001 ? mira.x / plano : 0.0;
      double radio = 1.15;

      for (int camara = 0; camara < 6; camara++) {
         double a = angulo + camara * Math.PI * 2.0 / 6.0;
         double alto = Math.sin(a) * radio;
         double lado = Math.cos(a) * radio;
         world.sendParticles(
            ModmindsModParticleTypes.VOID_SIGIL.get(),
            centro.x + ax * lado,
            centro.y + alto,
            centro.z + az * lado,
            1,
            0.05,
            0.05,
            0.05,
            0.0
         );
         if (alto > radio * 0.85) {
            BossFx.burst(
               world,
               new Vec3(centro.x + ax * lado, centro.y + alto, centro.z + az * lado),
               ParticleTypes.END_ROD,
               2,
               0.12,
               0.02
            );
         }
      }
   }

   public static void volleyMuzzle(ServerLevel world, Entity attacker, Vec3 muzzle) {
      BossFx.beam(world, BossFx.centre(attacker), muzzle, ParticleTypes.END_ROD, 8, 0.15);
      BossFx.burst(world, muzzle, (ParticleOptions)ModmindsModParticleTypes.GUN_SPARK.get(), 8, 0.3, 0.15);
      BossFx.burst(world, muzzle, ParticleTypes.SWEEP_ATTACK, 1, 0.3, 0.0);
      BossFx.burst(world, muzzle, (ParticleOptions)ModmindsModParticleTypes.EXPLOSION_LIGHT.get(), 1, 0.2, 0.04);
      BossFx.burst(world, muzzle.add(0.0, -0.4, 0.0), ParticleTypes.LARGE_SMOKE, 1, 0.15, 0.02);
      if (world.getGameTime() % 8L == 0L) {
         BossFx.sound(world, muzzle, ModmindsModSounds.NAVEDISPARO.get(), SoundSource.HOSTILE, 1.2F, 1.0 + world.getRandom().nextDouble() * 0.3, 0.0);
      }
   }

   public static void arrowImpact(ServerLevel world, Vec3 at) {
      BossFx.burst(world, at, (ParticleOptions)ModmindsModParticleTypes.GUN_SPARK.get(), 10, 0.25, 0.12);
      BossFx.burst(world, at, ParticleTypes.SONIC_BOOM, 1, 0.2, 0.0);
      BossFx.colored(world, at, VOID, 1.2F, 6, 0.2);
      BossFx.sound(world, at, (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 0.45F, 1.6, 0.15);
      BossFx.sound(world, at, SoundEvents.TRIDENT_HIT, SoundSource.HOSTILE, 0.8F, 0.55, 0.1);
   }

   public static void orbitWindup(ServerLevel world, Entity attacker, double radius, int leadTicks) {
      Vec3 at = BossFx.centre(attacker);
      BossFx.alert(world, at, 96.0, "⚠ REVIIL: ÓRBITA DEL VACÍO");
      BossFx.sound(world, at, SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 2.5F, 0.65);
      BossFx.warningCircle(world, attacker, radius, leadTicks);
      ModmindsMod.queueServerWork(leadTicks - 30, () -> BossFx.shake(world, at, 40.0, 20, 0.5));
      BossFx.soundLater(world, at, SoundEvents.BEACON_DEACTIVATE, SoundSource.HOSTILE, 1.6F, 0.7, leadTicks - 10);
      BossFx.soundLater(world, at, SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.2F, 0.8, leadTicks);
   }

   public static void orbitBlast(ServerLevel world, Entity attacker, double radius) {
      Vec3 at = BossFx.centre(attacker);
      Vec3 ground = BossFx.ground(attacker);
      BossFx.shockwave(world, ground, radius, 18);
      BossFx.smash(world, ground, radius * 0.6, 1.6, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get());
      BossFx.ring(world, at, radius, 24, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get(), 1, 0.5);
      BossFx.sound(world, at, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 2.2F, 0.6);
      BossFx.flashbang(world, ground.add(0.0, 2.0, 0.0), radius * 1.1, 9, 50);
   }

   public static void groundPoundCharge(ServerLevel world, Entity attacker, double radius, int leadTicks) {
      Vec3 at = BossFx.centre(attacker);
      BossFx.alert(world, at, 96.0, "⚠ REVIIL: GOLPE SUELO");
      BossFx.sound(world, at, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 1.9F, 0.62);
      BossFx.warningCircle(world, attacker, radius, leadTicks);
      BossFx.groundSwell(world, BossFx.ground(attacker), radius, leadTicks);
      BossFx.burst(world, BossFx.ground(attacker).add(0.0, 1.6, 0.0), (ParticleOptions)ModmindsModParticleTypes.SIGIL_DESERT.get(), 3, radius * 0.5, 0.02);
      BossFx.shake(world, at, radius * 2.0, leadTicks - 8, 0.16);
      ModmindsMod.queueServerWork(leadTicks - 14, () -> BossFx.sound(world, at, SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 2.4F, 0.55));
   }

   public static void groundPoundChargeAt(ServerLevel world, Entity attacker, Vec3 centro, double radius, int leadTicks) {
      Vec3 at = BossFx.centre(attacker);
      BossFx.alert(world, at, 96.0, "⚠ REVIIL: GOLPE SUELO");
      BossFx.sound(world, at, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 1.9F, 0.62);
      BossFx.warningAt(world, centro, radius, leadTicks);
      BossFx.groundSwell(world, centro, radius, leadTicks);
      BossFx.burst(world, centro.add(0.0, 1.6, 0.0), (ParticleOptions)ModmindsModParticleTypes.SIGIL_DESERT.get(), 3, radius * 0.5, 0.02);
      BossFx.shake(world, at, radius * 2.0, leadTicks - 8, 0.16);
      ModmindsMod.queueServerWork(leadTicks - 14, () -> BossFx.sound(world, at, SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 2.4F, 0.55));
   }

   public static void groundPoundImpact(ServerLevel world, Entity attacker, double radius) {
      golpeEnElSuelo(world, attacker, BossFx.ground(attacker), radius);
   }

   public static void groundPoundImpactAt(ServerLevel world, Entity attacker, Vec3 centro, double radius) {
      golpeEnElSuelo(world, attacker, centro, radius);
   }

   private static void golpeEnElSuelo(ServerLevel world, Entity attacker, Vec3 centro, double radius) {
      Vec3 at = BossFx.centre(attacker);
      BossFx.shockwave(world, centro, radius, 22);
      BossFx.burst(world, centro.add(0.0, 1.3, 0.0), (ParticleOptions)ModmindsModParticleTypes.METEOR_FLASH.get(), 4, radius * 0.25, 0.05);
      BossFx.burst(world, centro.add(0.0, 0.7, 0.0), (ParticleOptions)ModmindsModParticleTypes.PULSE_EM.get(), 3, radius * 0.2, 0.06);
      BossFx.smash(world, centro, radius * 0.5, 2.6, (ParticleOptions)ModmindsModParticleTypes.SHOCKWAVE_RING.get());
      BossFx.groundSwell(world, centro, radius * 1.15, 14);

      for (int i = 0; i < 5; i++) {
         int step = i;
         ModmindsMod.queueServerWork(
            step * 2,
            () -> {
               double angle = step * Math.PI * 2.0 / 5.0 + world.getRandom().nextDouble() * 0.6;
               Vec3 fissure = BossFx.groundAt(
                  world, centro.x + Math.cos(angle) * radius * 0.55, centro.z + Math.sin(angle) * radius * 0.55
               );
               BossFx.groundCrack(world, fissure, radius * 0.7, 120);
            }
         );
      }

      BossFx.ring(world, at, radius * 0.7, 20, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get(), 1, 0.45);
      BossFx.colored(world, at, VOID, 2.2F, 10, 1.6);
      BossFx.burst(world, centro.add(0.0, 1.2, 0.0), ParticleTypes.TOTEM_OF_UNDYING, 24, radius * 0.4, 0.1);
      BossFx.blastSound(world, at, 1.2);
      BossFx.sound(world, at, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.4F, 0.55);
      BossFx.flashbang(world, centro.add(0.0, 1.4, 0.0), radius * 1.5, 10, 55);
      if (!centro.equals(BossFx.ground(attacker))) {
         BossFx.blastSound(world, centro, 0.9);
         BossFx.shake(world, centro, 60.0, 16, 0.5);
      }

      arenaArriba(world, centro, radius);
   }

   private static void arenaArriba(ServerLevel world, Vec3 centro, double radius) {
      for (int i = 0; i < 8; i++) {
         int step = i;
         double angle = i * Math.PI * 2.0 / 8.0 + world.getRandom().nextDouble() * 0.5;
         Vec3 base = BossFx.groundAt(world, centro.x + Math.cos(angle) * radius * 0.62, centro.z + Math.sin(angle) * radius * 0.62);
         ModmindsMod.queueServerWork(4 + i, () -> {
            if (world.getEntitiesOfClass(Entity.class, new AABB(base, base).inflate(0.9, 1.6, 0.9), e -> e.isAlive() && !e.isSpectator()).isEmpty()) {
               BossFx.sandColumn(world, base, 2 + step % 2, 2, 70);
            }
         });
      }
   }

   public static void voidBeamCharge(ServerLevel world, Entity attacker, double angle, double length, double arc, int leadTicks) {
      Vec3 at = BossFx.centre(attacker);
      Vec3 origin = BossFx.ground(attacker);
      BossFx.alert(world, at, 96.0, "⚠ REVIIL: RAYO DE VACÍO");
      BossFx.sound(world, at, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 1.8F, 0.75);
      BossFx.shake(world, at, 48.0, leadTicks - 2, 0.7);
      int strips = 11;
      double width = length * arc / strips * 1.45;

      for (int i = 0; i < strips; i++) {
         BossFx.warningBeam(world, attacker, origin, angle + ((double)i / (strips - 1) - 0.5) * arc, length, width, leadTicks);
      }
   }

   public static void voidBeamRelease(ServerLevel world, Entity attacker) {
      BossFx.smash(world, BossFx.ground(attacker), 9.0, 1.5, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get());
      BossFx.sound(world, BossFx.centre(attacker), SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.HOSTILE, 2.4F, 0.7);
   }

   public static void voidBeamFrame(ServerLevel world, Vec3 from, Vec3 to) {
      BossFx.beam(world, from, to, ParticleTypes.END_ROD, 22, 0.08);
      BossFx.beam(world, from, to, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get(), 10, 0.22);
      BossFx.beam(world, from, to, BossFx.ALERT_CORE, 6, 0.4);
      BossFx.burst(world, to, ParticleTypes.GUST_EMITTER_LARGE, 3, 0.5, 0.05);
      BossFx.burst(world, to, ParticleTypes.SWEEP_ATTACK, 2, 0.6, 0.05);
      if (world.getGameTime() % 4L == 0L) {
         BossFx.burst(world, to, ParticleTypes.SONIC_BOOM, 1, 0.3, 0.0);
         BossFx.burst(world, to, BossFx.DUST_PILLAR, 2, 0.8, 0.02);
      }
   }

   public static void howlCharge(ServerLevel world, Entity attacker, double radius, int leadTicks) {
      Vec3 at = BossFx.centre(attacker);
      BossFx.alert(world, at, 96.0, "⚠ REVIIL: AULLIDO DEL VACÍO");
      BossFx.sound(world, at, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 2.0F, 0.6);
      BossFx.warningCircle(world, attacker, radius, leadTicks);
      BossFx.shake(world, at, 44.0, leadTicks, 0.3);

      for (int tick = 0; tick < leadTicks; tick += 6) {
         int step = tick;
         ModmindsMod.queueServerWork(tick, () -> {
            double pull = radius * (1.0 - (double)step / leadTicks) * 0.5 + 2.0;
            BossFx.vortex(world, at, pull, 10, ParticleTypes.SCULK_CHARGE_POP, 0.08);
            BossFx.ring(world, BossFx.ground(attacker), pull, 8, BossFx.DUST_PILLAR, 1, 0.5);
         });
      }
   }

   public static void howlBlast(ServerLevel world, Entity attacker, double radius) {
      Vec3 at = BossFx.centre(attacker);
      Vec3 ground = BossFx.ground(attacker);
      BossFx.sound(world, at, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 2.6F, 0.7);
      BossFx.shake(world, at, 90.0, 20, 1.1);
      BossFx.smash(world, ground, radius * 0.5, 1.8, (ParticleOptions)ModmindsModParticleTypes.SHOCKWAVE_RING.get());
      BossFx.colored(world, ground.add(0.0, 0.6, 0.0), VOID, 2.4F, 14, radius * 0.45);
      BossFx.groundSwell(world, ground, radius * 0.8, 16);

      for (int i = 0; i < 4; i++) {
         int paso = i;
         ModmindsMod.queueServerWork(
            paso * 3,
            () -> {
               double angulo = paso * Math.PI / 2.0 + 0.35;
               BossFx.groundCrack(
                  world,
                  BossFx.groundAt(world, attacker.getX() + Math.cos(angulo) * radius * 0.5, attacker.getZ() + Math.sin(angulo) * radius * 0.5),
                  radius * 0.9,
                  160
               );
            }
         );
      }

      for (int tick = 0; tick < 12; tick++) {
         int step = tick;
         ModmindsMod.queueServerWork(tick, () -> {
            double r = radius * (step + 1) / 12.0;
            BossFx.ring(world, ground, r, 24, (ParticleOptions)ModmindsModParticleTypes.SHOCKWAVE_RING.get(), 1, 0.3);
            BossFx.ring(world, at, r, 12, ParticleTypes.END_ROD, 1, 0.2);
            BossFx.ring(world, ground, r, 8, ParticleTypes.CRIT, 1, 0.4);
            if (step % 3 == 0) {
               BossFx.ring(world, ground, r, 10, BossFx.DUST_PILLAR, 1, 0.5);
            }
         });
      }
   }

   public static void rainCharge(ServerLevel world, Entity attacker) {
      Vec3 at = BossFx.centre(attacker);
      BossFx.alert(world, at, 96.0, "⚠ REVIIL: LLUVIA DEL VACÍO");
      BossFx.sound(world, at, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.8F, 0.6);
      BossFx.colored(world, at, VOID, 2.0F, 12, 1.5);
      BossFx.burst(world, at.add(0.0, attacker.getBbHeight() * 0.5, 0.0), net.owleafstudio.modminds.compat.InvCompat.FLASH, 1, 0.0, 0.0);
      BossFx.shake(world, at, 52.0, 20, 0.6);
   }

   public static void meteorTelegraph(ServerLevel world, Vec3 landing, int leadTicks) {
      BossFx.marker(world, landing, 3.6, leadTicks);
      BossFx.burst(world, landing.add(0.0, 1.2, 0.0), (ParticleOptions)ModmindsModParticleTypes.SIGIL_DESERT.get(), 2, 1.2, 0.01);
      ModmindsMod.queueServerWork(leadTicks / 2, () -> {
         BossFx.burst(world, landing.add(0.0, 1.5, 0.0), (ParticleOptions)ModmindsModParticleTypes.SIGIL_DESERT.get(), 2, 1.8, 0.01);
         BossFx.burst(world, landing.add(0.0, 0.4, 0.0), ParticleTypes.LAVA, 3, 2.6, 0.02);
      });
   }

   public static void meteorImpact(ServerLevel world, Vec3 landing, double radius) {
      BossFx.beam(world, landing.add(0.0, 28.0, 0.0), landing, ParticleTypes.END_ROD, 18, 0.2);
      BossFx.burst(world, landing.add(0.0, 1.1, 0.0), (ParticleOptions)ModmindsModParticleTypes.METEOR_FLASH.get(), 3, 0.7, 0.02);
      BossFx.burst(world, landing.add(0.0, 0.6, 0.0), (ParticleOptions)ModmindsModParticleTypes.PULSE_EM.get(), 2, 0.4, 0.05);
      BossFx.burst(world, landing.add(0.0, 0.3, 0.0), (ParticleOptions)ModmindsModParticleTypes.METEOR_TRAIL.get(), 10, radius * 0.7, 0.25);
      BossFx.smashSmall(world, landing, radius, 0.75, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get());
      BossFx.ring(world, landing.add(0.0, 0.1, 0.0), radius, 16, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get(), 1, 0.3);
      BossFx.colored(world, landing, VOID, 1.6F, 8, 1.0);
      BossFx.colored(world, landing.add(0.0, 0.8, 0.0), FURY, 2.0F, 10, radius * 0.5);
      BossFx.burst(world, landing, ParticleTypes.LAVA, 6, radius * 0.4, 0.35);
      BossFx.sound(world, landing, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.HOSTILE, 1.5F, 0.6);
   }

   public static void meteorFall(ServerLevel world, Vec3 landing, int fallTicks) {
      double alto = 30.0;

      for (int tick = 0; tick < fallTicks; tick++) {
         int step = tick;
         ModmindsMod.queueServerWork(tick, () -> {
            double altura = alto * (1.0 - (double)(step + 1) / fallTicks);
            Vec3 roca = landing.add(0.0, altura, 0.0);
            BossFx.burst(world, roca, (ParticleOptions)ModmindsModParticleTypes.METEOR_FLASH.get(), 1, 0.1, 0.0);
            BossFx.burst(world, roca.add(0.0, 1.0, 0.0), (ParticleOptions)ModmindsModParticleTypes.METEOR_TRAIL.get(), 4, 0.35, 0.06);
            BossFx.colored(world, roca, FURY, 2.6F, 5, 0.3);
            BossFx.burst(world, roca, ParticleTypes.LAVA, 2, 0.15, 0.05);
            BossFx.burst(world, roca.add(0.0, 1.2, 0.0), ParticleTypes.LARGE_SMOKE, 3, 0.3, 0.02);
         });
      }
   }

   public static void grabWindup(ServerLevel world, Entity attacker, Vec3 target) {
      BossFx.alert(world, target, 96.0, "⚠ REVIIL: AGARRE DEL VACÍO");
      BossFx.sound(world, BossFx.centre(attacker), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 1.5F, 0.85);
      BossFx.marker(world, target, 3.2, 24);
   }

   public static void grabLift(ServerLevel world, ReviilEntity attacker, Entity victim, int step) {
      Vec3 hand = attacker.grabFistPosition();
      BossFx.burst(world, hand, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get(), 3, 0.3, 0.02);
      BossFx.colored(world, BossFx.centre(victim), VOID, 1.1F, 3, 0.25);
      if (step == 0) {
         BossFx.sound(world, hand, SoundEvents.GOAT_PREPARE_RAM, SoundSource.HOSTILE, 1.7F, 0.45);
         BossFx.sound(world, hand, SoundEvents.CHAIN_BREAK, SoundSource.HOSTILE, 1.4F, 0.7);
         BossFx.shake(world, BossFx.centre(attacker), 20.0, 8, 0.35);
      } else if (step % 2 == 0) {
         BossFx.sound(world, hand, SoundEvents.CHAIN_BREAK, SoundSource.HOSTILE, 1.0F, 0.8);
      }
   }

   public static void grabStare(ServerLevel world, ReviilEntity attacker, Entity victim) {
      Vec3 hand = attacker.grabFistPosition();
      BossFx.alert(world, hand, 96.0, "⚠ REVIIL TE TIENE EN LA MANO");
      BossFx.burst(world, hand, ParticleTypes.CRIT, 12, 0.4, 0.12);
      BossFx.burst(world, hand.add(0.0, 0.6, 0.0), (ParticleOptions)ModmindsModParticleTypes.SIGIL_DESERT.get(), 1, 0.5, 0.02);
      BossFx.ring(world, hand, 1.8, 14, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get(), 1, 0.3);
      BossFx.colored(world, BossFx.centre(victim), VOID, 1.4F, 4, 0.2);
      BossFx.shake(world, BossFx.centre(attacker), 18.0, 26, 0.16);
      BossFx.sound(world, hand, SoundEvents.ELDER_GUARDIAN_HURT, SoundSource.HOSTILE, 1.5F, 0.5);
      BossFx.soundLater(world, BossFx.centre(attacker), SoundEvents.BEACON_AMBIENT, SoundSource.HOSTILE, 1.8F, 0.35, 12);
   }

   public static void grabTwist(ServerLevel world, ReviilEntity attacker, Entity victim) {
      Vec3 hand = attacker.grabFistPosition();
      BossFx.beam(world, BossFx.ground(attacker), hand, ParticleTypes.END_ROD, 10, 0.3);
      BossFx.colored(world, hand, VOID, 2.0F, 8, 0.5);
      BossFx.ring(world, BossFx.ground(attacker), 7.0, 14, (ParticleOptions)ModmindsModParticleTypes.SHOCKWAVE_RING.get(), 1, 0.4);
      BossFx.shake(world, BossFx.centre(attacker), 22.0, 14, 0.45);
      BossFx.sound(world, hand, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 1.8F, 0.5);
      BossFx.sound(world, BossFx.centre(attacker), SoundEvents.RAVAGER_ROAR, SoundSource.HOSTILE, 1.5F, 0.55);
   }

   public static void grabLaunch(ServerLevel world, ReviilEntity attacker, Vec3 from) {
      Vec3 hand = attacker.grabFistPosition();
      BossFx.beam(world, BossFx.ground(attacker), hand.add(0.0, 12.0, 0.0), ParticleTypes.END_ROD, 18, 0.25);
      BossFx.burst(world, hand, (ParticleOptions)ModmindsModParticleTypes.BOSS_FLARE.get(), 10, 0.7, 0.25);
      BossFx.colored(world, from, VOID, 2.2F, 12, 0.9);
      BossFx.ring(world, BossFx.ground(attacker), 11.0, 12, (ParticleOptions)ModmindsModParticleTypes.SHOCKWAVE_RING.get(), 1, 0.55);
      BossFx.sound(world, hand, SoundEvents.ENDER_DRAGON_SHOOT, SoundSource.HOSTILE, 1.8F, 0.9);
      BossFx.sound(world, from, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 1.2F, 1.4);
      BossFx.shake(world, BossFx.centre(attacker), 34.0, 12, 0.8);
   }

   public static void grabPlant(ServerLevel world, ReviilEntity attacker, Vec3 at, double radius, int wallHeight) {
      BossFx.smash(world, at, 5.5, 0.55, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get());
      BossFx.groundCrack(world, at, radius * 3.1, 120);
      BossFx.groundCrack(world, at, radius * 1.9, 120);
      BossFx.marker(world, at, radius + 0.6, 12);
      double pies = Math.max(2.6, attacker.getBbWidth() * 0.34);

      for (int i = 0; i < 10; i++) {
         int paso = i;
         double angulo = i * Math.PI * 2.0 / 10.0 + world.getRandom().nextDouble() * 0.3;
         Vec3 base = BossFx.groundAt(world, at.x + Math.cos(angulo) * radius, at.z + Math.sin(angulo) * radius);
         if (!(Math.hypot(base.x - attacker.getX(), base.z - attacker.getZ()) < pies)) {
            ModmindsMod.queueServerWork(
               2 + i,
               () -> {
                  if (world.getEntitiesOfClass(Entity.class, new AABB(base, base).inflate(0.9, 1.6, 0.9), e -> e.isAlive() && !e.isSpectator())
                     .isEmpty()) {
                     BossFx.sandColumn(world, base, wallHeight - paso % 2, 2, 130);
                  }
               }
            );
         }
      }
   }

   public static void grabBreak(ServerLevel world, ReviilEntity attacker, Vec3 at) {
      BossFx.blast(world, at.add(0.0, 0.7, 0.0), 4.2);
      BossFx.ring(world, at, 5.2, 16, (ParticleOptions)ModmindsModParticleTypes.BLAST_DUST.get(), 1, 0.4);
      BossFx.colored(world, at, VOID, 1.8F, 12, 0.8);
      BossFx.burst(world, at.add(0.0, 1.4, 0.0), (ParticleOptions)ModmindsModParticleTypes.BOSS_FLARE.get(), 8, 0.8, 0.3);
      BossFx.sound(world, at, SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.HOSTILE, 1.6F, 0.85);
      BossFx.shake(world, at, 80.0, 14, 0.55);
   }

   public static void grabPress(ServerLevel world, ReviilEntity attacker, Vec3 at, int effort) {
      double cerca = effort / 100.0;
      BossFx.ring(world, at, 1.4 + cerca * 2.6, 4 + (int)(cerca * 10.0), (ParticleOptions)ModmindsModParticleTypes.BLAST_DUST.get(), 1, 0.3);
      BossFx.burst(world, at.add(0.0, 0.4, 0.0), (ParticleOptions)ModmindsModParticleTypes.GUN_SPARK.get(), 2 + (int)(cerca * 5.0), 0.5, 0.2);
      if (cerca > 0.7) {
         BossFx.ring(world, at, 3.4, 8, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get(), 1, 0.3);
      }
   }

   public static void waterDrop(ServerLevel world, Vec3 landing, double radius) {
      BossFx.smash(world, landing, radius, 2.1, (ParticleOptions)ModmindsModParticleTypes.SHOCKWAVE_RING.get());
      BossFx.burst(world, landing.add(0.0, 1.2, 0.0), (ParticleOptions)ModmindsModParticleTypes.METEOR_FLASH.get(), 3, radius * 0.35, 0.05);
      BossFx.burst(world, landing.add(0.0, 0.7, 0.0), (ParticleOptions)ModmindsModParticleTypes.PULSE_EM.get(), 2, radius * 0.25, 0.06);
      BossFx.burst(world, landing.add(0.0, 1.4, 0.0), ParticleTypes.TOTEM_OF_UNDYING, 40, 1.2, 0.12);
      BossFx.ring(world, landing.add(0.0, 0.6, 0.0), radius, 14, ParticleTypes.TOTEM_OF_UNDYING, 1, 0.5);
      BossFx.sound(world, landing, SoundEvents.TOTEM_USE, SoundSource.HOSTILE, 2.0F, 0.7);
      BossFx.sound(world, landing, (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.2F, 0.4);
      BossFx.shake(world, landing, 120.0, 12, 0.75);
      BossFx.groundSwell(world, landing, radius * 1.3, 18);
      BossFx.groundCrack(world, landing, radius * 1.4, 90);
      BossFx.flashbang(world, landing.add(0.0, 1.2, 0.0), radius * 2.4, 12, 70);
   }

   public static void furyEnter(ServerLevel world, ReviilEntity attacker) {
      Vec3 at = BossFx.centre(attacker);
      Vec3 ground = BossFx.ground(attacker);
      BossFx.alert(world, at, 140.0, "⚠ REVIIL ENTRA EN FURIA");
      BossFx.sound(world, at, SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 3.2F, 0.5);
      BossFx.sound(world, at, (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.4F, 0.35);
      BossFx.soundLater(world, at, SoundEvents.CHAIN_BREAK, SoundSource.HOSTILE, 2.2F, 0.55, 4);
      BossFx.soundLater(world, at, SoundEvents.CHAIN_BREAK, SoundSource.HOSTILE, 2.2F, 0.45, 9);
      BossFx.shake(world, at, 140.0, 34, 1.1);
      BossFx.burst(world, at, net.owleafstudio.modminds.compat.InvCompat.FLASH, 1, 0.0, 0.0);
      BossFx.burst(world, at, (ParticleOptions)ModmindsModParticleTypes.METEOR_FLASH.get(), 4, 1.2, 0.05);
      BossFx.burst(world, ground.add(0.0, 0.8, 0.0), (ParticleOptions)ModmindsModParticleTypes.PULSE_EM.get(), 3, 1.0, 0.06);
      BossFx.burst(world, ground.add(0.0, 1.6, 0.0), (ParticleOptions)ModmindsModParticleTypes.SIGIL_DESERT.get(), 4, 6.0, 0.02);
      BossFx.shockwave(world, ground, 22.0, 20);
      BossFx.ring(world, ground, 12.0, 24, (ParticleOptions)ModmindsModParticleTypes.SHOCKWAVE_RING.get(), 1, 0.4);
      BossFx.ring(world, at, attacker.getBbWidth(), 20, BossFx.DUST_PILLAR, 1, 0.5);
      BossFx.groundSwell(world, ground, 14.0, 16);
      BossFx.colored(world, at, FURY, 2.4F, 18, attacker.getBbWidth() * 0.8);

      for (int i = 0; i < 4; i++) {
         int step = i;
         ModmindsMod.queueServerWork(step * 3, () -> {
            double angle = step * Math.PI / 2.0 + 0.4;
            Vec3 fissure = BossFx.groundAt(world, attacker.getX() + Math.cos(angle) * 9.0, attacker.getZ() + Math.sin(angle) * 9.0);
            BossFx.groundCrack(world, fissure, 16.0, 140);
         });
      }
   }

   public static void furyStomp(ServerLevel world, ReviilEntity attacker) {
      Vec3 ground = BossFx.ground(attacker);
      BossFx.smashSmall(world, ground, 8.0, 1.4, (ParticleOptions)ModmindsModParticleTypes.SHOCKWAVE_RING.get());
      BossFx.ring(world, ground, 10.0, 18, BossFx.DUST_PILLAR, 1, 0.4);
      BossFx.groundSwell(world, ground, 9.0, 12);
      BossFx.groundCrack(world, ground, 7.0, 60);
      BossFx.shake(world, BossFx.centre(attacker), 130.0, 16, 0.9);
      BossFx.sound(world, ground, (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.8F, 0.35);
   }

   public static void phaseAura(ServerLevel world, ReviilEntity attacker) {
      double height = attacker.getBbHeight();
      double width = attacker.getBbWidth();
      Vec3 chest = BossFx.centre(attacker);
      Vec3 ground = BossFx.ground(attacker);
      if (attacker.isGiant()) {
         BossFx.vortex(world, chest, width * 1.4, 16, ParticleTypes.SCULK_CHARGE_POP, 0.03);
         BossFx.colored(world, ground.add(0.0, 0.4, 0.0), FURY, 2.0F, 8, width * 0.7);
         BossFx.ring(world, ground, width * 0.9, 12, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get(), 1, 0.25);
         BossFx.shake(world, chest, width * 6.0, 6, 0.1);
      } else {
         BossFx.colored(world, chest.add(0.0, height * 0.22, 0.0), FURY, 1.5F, 5, width * 0.5);
         BossFx.burst(world, chest, ParticleTypes.LAVA, 2, width * 0.4, 0.02);
         BossFx.ring(world, ground, width * 0.7, 10, BossFx.DUST_PILLAR, 1, 0.3);
      }
   }

   public static void giantEnter(ServerLevel world, ReviilEntity attacker, int step, int totalSteps) {
      Vec3 at = BossFx.centre(attacker);
      if (step == 0) {
         BossFx.alert(world, at, 160.0, "⚠ REVIIL SE HACE GIGANTE");
         BossFx.sound(world, at, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 2.6F, 0.4);
         BossFx.sound(world, at, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 2.0F, 0.45);
      }

      double reach = 8.0 + (totalSteps - step) * 2.5;
      BossFx.vortex(world, at, reach, 18, ParticleTypes.END_ROD, 0.06);
      BossFx.colored(world, at, FURY, 2.2F, 8, attacker.getBbWidth() * 0.7);
      BossFx.shake(world, at, 160.0, 4, 0.35 + step * 0.05);
   }

   public static void giantLand(ServerLevel world, ReviilEntity attacker) {
      Vec3 at = BossFx.centre(attacker);
      Vec3 ground = BossFx.ground(attacker);
      double width = attacker.getBbWidth();
      BossFx.sound(world, at, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 3.0F, 0.55);
      BossFx.sound(world, at, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 1.6F, 0.5);
      BossFx.blastSound(world, ground, 1.1);
      BossFx.shake(world, at, 180.0, 46, 1.6);
      BossFx.burst(world, at, net.owleafstudio.modminds.compat.InvCompat.FLASH, 1, 0.0, 0.0);
      BossFx.burst(world, ground.add(0.0, 1.4, 0.0), (ParticleOptions)ModmindsModParticleTypes.METEOR_FLASH.get(), 5, width, 0.08);
      BossFx.burst(world, ground.add(0.0, 0.9, 0.0), (ParticleOptions)ModmindsModParticleTypes.PULSE_EM.get(), 4, width * 0.6, 0.08);
      BossFx.shockwave(world, ground, width * 4.0, 26);
      BossFx.smash(world, ground, width * 1.6, 3.0, (ParticleOptions)ModmindsModParticleTypes.SHOCKWAVE_RING.get());
      BossFx.groundSwell(world, ground, width * 2.6, 20);
      BossFx.colored(world, at, FURY, 3.2F, 26, width);
      BossFx.ring(world, at, width * 1.3, 26, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get(), 1, 0.5);

      for (int i = 0; i < 6; i++) {
         int step = i;
         ModmindsMod.queueServerWork(
            step * 2,
            () -> {
               double angle = step * Math.PI / 3.0 + 0.25;
               Vec3 fissure = BossFx.groundAt(
                  world, attacker.getX() + Math.cos(angle) * width * 1.6, attacker.getZ() + Math.sin(angle) * width * 1.6
               );
               BossFx.groundCrack(world, fissure, width * 2.4, 200);
               BossFx.burst(world, fissure, BossFx.DUST_PILLAR, 8, 1.2, 0.1);
            }
         );
      }
   }

   public static void giantLeave(ServerLevel world, ReviilEntity attacker, int step, int totalSteps) {
      Vec3 at = BossFx.centre(attacker);
      Vec3 ground = BossFx.ground(attacker);
      if (step == 0) {
         BossFx.alert(world, at, 160.0, "⚠ REVIIL SUELTA EL TAMAÑO");
         BossFx.sound(world, at, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 2.0F, 1.25);
         BossFx.sound(world, at, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 2.2F, 1.1);
      }

      BossFx.vortex(world, ground, attacker.getBbWidth() * 1.5, 14, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get(), 0.2);
      BossFx.burst(world, at, ParticleTypes.END_ROD, 4, attacker.getBbWidth() * 0.8, 0.14);
      BossFx.colored(world, at, FURY, 2.0F, 6, attacker.getBbWidth() * (1.3 - (double)step / totalSteps));
      BossFx.shake(world, at, 150.0, 3, 0.18);
   }

   public static void giantLeaveLand(ServerLevel world, ReviilEntity attacker) {
      Vec3 at = BossFx.centre(attacker);
      Vec3 ground = BossFx.ground(attacker);
      double width = attacker.getBbWidth();
      BossFx.sound(world, at, SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE, 2.4F, 1.4);
      BossFx.sound(world, ground, (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 1.6F, 1.5);
      BossFx.shake(world, at, 150.0, 12, 0.5);
      BossFx.shockwave(world, ground, width * 3.0, 20, 11824127);
      BossFx.groundSwell(world, ground, width * 1.6, 12);
      BossFx.ring(world, ground.add(0.0, 0.1, 0.0), width * 0.9, 22, ParticleTypes.END_ROD, 1, 0.4);
   }
}
