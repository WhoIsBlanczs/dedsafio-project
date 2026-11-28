package net.owleafstudio.modminds.boss;

import net.minecraft.world.entity.Entity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public final class BossPresentation {
   private BossPresentation() {
   }

   public static void melee(ServerLevel world, Entity attacker, Entity target, BossStyle style) {
      Vec3 at = BossFx.centre(target);
      world.sendParticles(ParticleTypes.SWEEP_ATTACK, at.x, at.y, at.z, 6, 0.35, 0.35, 0.35, 0.1);
      world.sendParticles(ParticleTypes.SONIC_BOOM, at.x, at.y, at.z, 1, 0.0, 0.0, 0.0, 0.0);
      BossFx.burst(world, at, style.flare, 12, 0.45, 0.12);
      BossFx.burst(world, at, style.spark(), 5, 0.5, 0.1);
      BossFx.burst(world, at, ParticleTypes.POOF, 4, 0.4, 0.12);
      BossFx.colored(world, at, style.aura, 1.5F, 8, 0.5);
      BossFx.sound(world, at, style.impact, SoundSource.HOSTILE, 1.3F, 0.8, 0.2);
      BossFx.sound(world, at, SoundEvents.MACE_SMASH_AIR, SoundSource.HOSTILE, 1.1F, 1.0, 0.15);
      BossFx.sound(world, BossFx.centre(attacker), style.charge, SoundSource.HOSTILE, 0.7F, 1.3, 0.2);
      BossFx.shake(world, at, 20.0, 6, 0.6);
   }

   public static void ambient(ServerLevel world, Entity entity, BossStyle style) {
      Vec3 at = BossFx.centre(entity);
      BossFx.sound(world, at, style.ambient, SoundSource.HOSTILE, 1.0F, 0.75 + world.getRandom().nextDouble() * 0.25);
      BossFx.vortex(world, at, entity.getBbWidth() + 1.0, 10, style.flare, 0.02);
   }

   public static void death(ServerLevel world, Entity entity, int deathTick, BossStyle style) {
      Vec3 at = BossFx.centre(entity);
      if (deathTick == 1) {
         BossFx.sound(world, at, style.death, SoundSource.HOSTILE, 2.0F, 0.85);
         BossFx.burst(world, at, net.owleafstudio.modminds.compat.InvCompat.FLASH, 1, 0.0, 0.0);
         BossFx.burst(world, at, style.spark(), 8, entity.getBbWidth() * 0.6, 0.12);
      }

      BossFx.vortex(world, at, entity.getBbWidth() + deathTick * 0.3, 14, style.mote, 0.01);
      if (deathTick % 4 == 0) {
         BossFx.ring(world, at, entity.getBbWidth() + deathTick * 0.5, 10, style.spark(), 1, 0.3);
      }

      BossFx.colored(world, at, style.aura, 1.6F, 10, entity.getBbWidth() * 0.6);
      if (deathTick % 5 == 0) {
         BossFx.sound(world, at, style.charge, SoundSource.HOSTILE, 1.0F, 0.6);
      }
   }

   public static void windup(ServerLevel world, Entity boss, BossStyle style) {
      Vec3 at = BossFx.centre(boss);
      BossFx.vortex(world, at, boss.getBbWidth() + 4.0, 16, style.flare, 0.06);
      BossFx.vortex(world, at, boss.getBbWidth() + 4.0, 8, style.spark(), 0.06);
      BossFx.colored(world, at, style.aura, 2.0F, 10, 1.0);
      BossFx.ring(world, BossFx.ground(boss), boss.getBbWidth() * 0.8 + 2.0, 10, BossFx.DUST_PILLAR, 1, 0.5);
      BossFx.sound(world, at, style.charge, SoundSource.HOSTILE, 1.6F, 0.85, 0.1);
      BossFx.shake(world, at, boss.getBbWidth() + 26.0, 14, 0.35);
   }

   public static void telegraph(ServerLevel world, Entity boss, double radius, int leadTicks, BossStyle style) {
      announce(world, boss, style);
      BossFx.warningCircle(world, boss, radius, leadTicks);
   }

   public static void announce(ServerLevel world, Entity boss, BossStyle style) {
      BossFx.alert(world, BossFx.centre(boss), 96.0, "⚠ " + style.label() + ": GOLPE EN ÁREA");
   }

   public static void sweep(ServerLevel world, Entity boss, Vec3 to, BossStyle style) {
      BossFx.beam(world, BossFx.centre(boss), to, style.flare, 14, 0.3);
      impact(world, to, style);
   }

   private static void impact(ServerLevel world, Vec3 at, BossStyle style) {
      BossFx.impactBurst(world, at, 2.4, style.flare);
      BossFx.burst(world, at, style.spark(), 8, 0.9, 0.14);
      BossFx.colored(world, at, style.aura, 1.8F, 10, 0.8);
      BossFx.sound(world, at, style.impact, SoundSource.HOSTILE, 1.8F, 0.7, 0.12);
   }
}
