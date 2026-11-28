package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.init.ModmindsModParticleTypes;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilArrastrarManoProcedure {
   private static final double LENGTH_MIN = 22.0;
   private static final double LENGTH_MAX = 200.0;
   private static final double WIDTH = 7.5;
   private static final int TELEGRAPH_TICKS = 36;
   private static final int TRAVEL_TICKS = 58;
   private static final int STEP_TICKS = 3;
   private static final double DAMAGE = 30.0;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel level && reviil.isAlive()) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         if (!reviil.isGiant()) {
            BossFx.subtitle(level, BossFx.centre(reviil), 140.0, "ESO ES UN GOLPE DE GIGANTE", 46);
         } else if (reviil.escenaTicks <= 0) {
            Vec3 desde = BossFx.ground(reviil);
            Vec3 hacia = BossFx.aim(level, reviil, 200.0);
            double length = Math.min(200.0, Math.max(22.0, BossFx.distanciaPlana(desde, hacia)));
            double angle = BossFx.miraA(reviil, hacia);
            Vec3 dir = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
            reviil.setAnimation("emote.giant.rastrillo");
            reviil.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
            BossFx.warningBeam(level, null, desde, angle, length, 7.5, 36);
            BossFx.alert(level, BossFx.centre(reviil), 160.0, "⚠ REVIIL ARRASTRA LA MANO");
            BossFx.sound(level, BossFx.centre(reviil), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 2.2F, 0.55);
            BossFx.shake(level, BossFx.centre(reviil), 70.0, 12, 0.25);
            ModmindsMod.queueServerWork(36, () -> drag(level, reviil, desde, angle, dir, length, 0));
         }
      }
   }

   private static void drag(ServerLevel world, ReviilEntity reviil, Vec3 from, double angle, Vec3 dir, double length, int tick) {
      if (reviil.isAlive()) {
         if (tick == 0) {
            reviil.setAnimation("emote.giant.rastrillo.pase");
         }

         double previous = length * Math.min(1.0, Math.max(0.0, (tick - 3) / 58.0));
         double along = length * Math.min(1.0, tick / 58.0);
         Vec3 front = from.add(dir.x * along, 0.0, dir.z * along);
         hit(world, reviil, from, dir, angle, previous, along);
         BossFx.sound(world, front, SoundEvents.GRAVEL_FALL, SoundSource.HOSTILE, 1.5F, 0.45 + world.getRandom().nextDouble() * 0.15);
         if (tick % 12 < 3) {
            BossFx.groundCrack(world, front, 9.0, 70);
            BossFx.shake(world, front, 55.0, 5, 0.2);
         }

         if (tick >= 58) {
            BossFx.shockwave(world, front, 14.0, 14);
            BossFx.smash(world, front, 7.0, 0.3, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get());
            BossFx.burst(world, front.add(0.0, 1.4, 0.0), (ParticleOptions)ModmindsModParticleTypes.METEOR_FLASH.get(), 4, 3.2, 0.05);
            BossFx.burst(world, front.add(0.0, 0.4, 0.0), (ParticleOptions)ModmindsModParticleTypes.PULSE_EM.get(), 3, 2.0, 0.07);
            BossFx.shake(world, front, 90.0, 16, 0.5);
            BossFx.sound(world, front, (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.4F, 0.35);
            ModmindsMod.queueServerWork(26, () -> reviil.releasePose());
         } else {
            ModmindsMod.queueServerWork(3, () -> drag(world, reviil, from, angle, dir, length, tick + 3));
         }
      }
   }

   private static void hit(ServerLevel world, ReviilEntity reviil, Vec3 from, Vec3 dir, double angle, double from0, double to) {
      double half = 3.75;
      Vec3 axis = new Vec3(-Math.sin(angle), 0.0, Math.cos(angle));

      for (Player victim : world.getEntitiesOfClass(
         Player.class,
         new AABB(from, from.add(dir.x * to, 0.0, dir.z * to)).inflate(10.5),
         e -> e.isAlive() && !e.isSpectator() && !e.isPassenger()
      )) {
         Vec3 offset = victim.position().subtract(from);
         double along = offset.dot(dir);
         if (!(along < from0 - 0.6) && !(along > to + 0.6) && !(Math.abs(offset.dot(axis)) > half + victim.getBbWidth() * 0.5)) {
            victim.hurt(world.damageSources().mobAttack(reviil), reviil.phaseDamage(30.0F));
            reviil.sting(victim);
            double sign = offset.dot(axis) >= 0.0 ? 1.0 : -1.0;
            victim.setDeltaMovement(victim.getDeltaMovement().add(axis.x * sign * 0.9, 0.55, axis.z * sign * 0.9));
            victim.needsSync = true;
         }
      }
   }
}
