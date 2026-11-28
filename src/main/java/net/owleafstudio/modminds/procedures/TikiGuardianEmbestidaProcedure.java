package net.owleafstudio.modminds.procedures;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.entity.TikiGuardianEntity;

public class TikiGuardianEmbestidaProcedure {
   private static final int WINDUP_TICKS = 22;
   private static final int REV_TICKS = 4;
   private static final int CHARGE_TICKS = 46;
   private static final int FRAME_TICKS = 2;
   private static final int FRAMES = 23;
   private static final int SKID_TICKS = 2;
   private static final double BLOCKS_PER_TICK = 1.35;
   private static final double DRAG_OVER_FRAME = 1.91;
   private static final double FRAME_VELOCITY = 1.4136125654450262;
   private static final double CHARGE_SPEED = 1.2;
   private static final double WALK_SPEED = 0.25;
   private static final double TOUCH_RADIUS = 2.6;
   private static final double TRAIL_BEHIND = 1.8;
   private static final double LAUNCH_OUT = 1.1;
   private static final double LAUNCH_UP = 0.8;
   private static final float DAMAGE = 20.0F;

   public static void execute(LevelAccessor world, Entity entity, LivingEntity target) {
      if (entity instanceof TikiGuardianEntity tiki && world instanceof ServerLevel serverLevel) {
         BossFx.alert(serverLevel, BossFx.centre(tiki), 96.0, "⚠ TIKI: EMBESTIDA");
         BossPresentation.windup(serverLevel, tiki, BossStyle.TIKI);
         tiki.setAnimation("animation.TikiGuardian.dash");
         BossFx.sound(serverLevel, BossFx.centre(tiki), SoundEvents.HORSE_GALLOP, SoundSource.HOSTILE, 1.6F, 0.7);

         for (int tick = 0; tick < 22; tick += 4) {
            int step = tick;
            ModmindsMod.queueServerWork(tick, () -> {
               if (tiki.isAlive()) {
                  revFrame(serverLevel, tiki, step);
               }
            });
         }

         ModmindsMod.queueServerWork(22, () -> {
            if (tiki.isAlive()) {
               charge(serverLevel, tiki, target);
            }
         });
      }
   }

   private static void revFrame(ServerLevel world, TikiGuardianEntity tiki, int step) {
      Vec3 feet = BossFx.ground(tiki);
      BossFx.colored(world, feet.add(0.0, 0.6, 0.0), BossStyle.TIKI.aura, 2.2F, 6 + step, 1.0);
      BossFx.ring(world, feet, 4.2 - step * 0.1, 16, BossFx.DUST_PILLAR, 1, 0.4);
      BossFx.burst(world, feet, ParticleTypes.CRIT, 3, 0.9, 0.1);
      BossFx.sound(world, feet, SoundEvents.HORSE_GALLOP, SoundSource.HOSTILE, 0.8F, 0.7 + step * 0.012);
   }

   private static void charge(ServerLevel world, TikiGuardianEntity tiki, LivingEntity target) {
      Vec3 heading = heading(tiki, target);
      if (!tiki.isVehicle()) {
         tiki.setYRot((float)Math.toDegrees(Math.atan2(heading.x, -heading.z)));
      }

      tiki.getNavigation().stop();
      setSpeed(tiki, 1.2);
      Set<UUID> trampled = new HashSet<>();

      for (int frame = 0; frame < 23; frame++) {
         boolean kickingOff = frame == 0;
         ModmindsMod.queueServerWork(frame * 2, () -> {
            if (tiki.isAlive()) {
               chargeFrame(world, tiki, heading, trampled, kickingOff);
            }
         });
      }

      ModmindsMod.queueServerWork(48, () -> recover(world, tiki));
   }

   private static void chargeFrame(ServerLevel world, TikiGuardianEntity tiki, Vec3 heading, Set<UUID> trampled, boolean kickingOff) {
      if (kickingOff) {
         BossFx.shake(world, BossFx.ground(tiki), 30.0, 12, 0.4);
         BossFx.sound(world, BossFx.centre(tiki), SoundEvents.HORSE_GALLOP, SoundSource.HOSTILE, 2.4F, 0.85);
      }

      Vec3 motion = tiki.getDeltaMovement();
      tiki.setDeltaMovement(heading.x * 1.4136125654450262, motion.y, heading.z * 1.4136125654450262);
      Vec3 kicked = BossFx.ground(tiki).subtract(heading.x * 1.8, -0.4, heading.z * 1.8);
      BossFx.burst(world, kicked, BossFx.DUST_PILLAR, 7, 0.45, 0.12);
      BossFx.colored(world, kicked, BossStyle.TIKI.aura, 1.6F, 5, 0.5);
      BossFx.sound(world, kicked, SoundEvents.HORSE_GALLOP, SoundSource.HOSTILE, 0.5F, 1.05, 0.1);
      trample(world, tiki, heading, trampled);
   }

   private static void trample(ServerLevel world, TikiGuardianEntity tiki, Vec3 heading, Set<UUID> trampled) {
      Vec3 at = tiki.position();

      for (LivingEntity victim : world.getEntitiesOfClass(
         LivingEntity.class,
         new AABB(at, at).inflate(4.6),
         e -> e != tiki && e.isAlive() && !(e instanceof TikiGuardianEntity) && !tiki.hasPassenger(e)
      )) {
         double dx = victim.getX() - at.x;
         double dz = victim.getZ() - at.z;
         double distance = Math.sqrt(dx * dx + dz * dz);
         if (distance <= 2.6 + victim.getBbWidth() * 0.5 && trampled.add(victim.getUUID())) {
            victim.hurt(world.damageSources().mobAttack(tiki), 20.0F);
            double nx = distance > 0.01 ? dx / distance : heading.x;
            double nz = distance > 0.01 ? dz / distance : heading.z;
            victim.setDeltaMovement(nx * 1.1, 0.8, nz * 1.1);
            BossFx.burst(world, BossFx.centre(victim), ParticleTypes.CRIT, 8, 0.5, 0.2);
            BossFx.sound(world, BossFx.centre(victim), SoundEvents.WOOD_BREAK, SoundSource.HOSTILE, 1.4F, 0.7);
         }
      }
   }

   private static void recover(ServerLevel world, TikiGuardianEntity tiki) {
      setSpeed(tiki, 0.25);
      BossFx.sound(world, BossFx.ground(tiki), SoundEvents.ANVIL_USE, SoundSource.HOSTILE, 1.8F, 0.75);
      BossFx.impactBurst(world, BossFx.ground(tiki), 5.0, BossStyle.TIKI.flare);
      BossFx.ring(world, BossFx.ground(tiki), 5.0, 20, BossFx.DUST_PILLAR, 1, 0.5);
      ModmindsMod.queueServerWork(6, () -> {
         if (tiki.isAlive()) {
            tiki.setAnimation("animation.TikiGuardian.idle");
         }
      });
   }

   private static Vec3 heading(TikiGuardianEntity tiki, LivingEntity target) {
      Vec3 wanted = !tiki.isVehicle() && target != null ? target.position().subtract(tiki.position()) : tiki.getLookAngle();
      double length = Math.sqrt(wanted.x * wanted.x + wanted.z * wanted.z);
      return length < 1.0E-4 ? tiki.getLookAngle() : new Vec3(wanted.x / length, 0.0, wanted.z / length);
   }

   private static void setSpeed(TikiGuardianEntity tiki, double value) {
      AttributeInstance speed = tiki.getAttribute(Attributes.MOVEMENT_SPEED);
      if (speed != null) {
         speed.setBaseValue(value);
      }
   }
}
