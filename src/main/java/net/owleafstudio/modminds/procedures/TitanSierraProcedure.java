package net.owleafstudio.modminds.procedures;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.entity.LimboTitanEntity;
import net.owleafstudio.modminds.init.ModmindsModSounds;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class TitanSierraProcedure {
   private static final int REV_TICKS = 40;
   private static final int REV_SOUND_TICKS = 6;
   private static final int TRACK_TICKS = 4;
   private static final int CLICK_TICKS = 2;
   private static final int SWEEP_TICKS = 26;
   private static final double ARC_DEGREES = 160.0;
   private static final double WINDOW_DEGREES = 24.0;
   private static final double ARC_STEP = Math.toRadians(160.0) / 25.0;
   private static final double REACH = 14.0;
   private static final double ARM_OFFSET = 2.0;
   private static final double ARM_LIFT = 0.55;
   private static final float DAMAGE = 22.0F;
   private static final double BASE_SPEED = 0.1;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof LimboTitanEntity titan && world instanceof ServerLevel serverLevel) {
         ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         BossFx.alert(serverLevel, BossFx.centre(titan), 96.0, "⚠ TITAN DEL LIMBO: SIERRA");
         BossPresentation.windup(serverLevel, titan, BossStyle.LIMBO_TITAN);
         titan.setAnimation("meteorite_attack");
         titan.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);

         for (int tick = 0; tick < 40; tick += 2) {
            double progress = tick / 40.0;
            boolean tracked = tick % 4 == 0;
            boolean revved = tick % 6 == 0;
            ModmindsMod.queueServerWork(tick, () -> {
               if (titan.isAlive()) {
                  revFrame(serverLevel, titan, progress, tracked, revved);
               }
            });
         }

         Set<UUID> cut = new HashSet<>();
         ModmindsMod.queueServerWork(40, () -> {
            if (titan.isAlive()) {
               sweep(serverLevel, titan, aimAngle(titan), cut);
            }
         });
         ModmindsMod.queueServerWork(82, () -> {
            if (titan.isAlive()) {
               titan.setAnimation("idle");
               titan.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1);
            }
         });
      }
   }

   private static void revFrame(ServerLevel world, LimboTitanEntity titan, double progress, boolean tracked, boolean revved) {
      double angle = aimAngle(titan);
      Vec3 armRoot = shoulder(titan, angle);
      Vec3 armTip = tip(titan, angle);
      if (tracked) {
         BossFx.beam(world, armRoot, armTip, BossFx.ALERT_DOT, 22, 0.22);
         BossFx.beam(world, armRoot, armTip, BossFx.ALERT_CORE, 11, 0.2);
         BossFx.burst(world, armTip, BossStyle.LIMBO_TITAN.mote, 3, 0.5, 0.08);
      }

      BossFx.sound(world, armRoot, (SoundEvent)SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE.value(), SoundSource.HOSTILE, 0.55F, 1.3 - 0.5 * progress, 0.05);
      if (revved) {
         BossFx.sound(world, armRoot, ModmindsModSounds.ARMA_CORTA.get(), SoundSource.HOSTILE, 2.0F, 1.45 - 0.55 * progress, 0.06);
      }
   }

   private static void sweep(ServerLevel world, LimboTitanEntity titan, double locked, Set<UUID> cut) {
      double startAngle = locked - Math.toRadians(160.0) / 2.0;
      BossFx.shake(world, BossFx.centre(titan), 30.0, 16, 0.55);

      for (int tick = 0; tick < 26; tick++) {
         int step = tick;
         ModmindsMod.queueServerWork(tick, () -> {
            if (titan.isAlive()) {
               cutFrame(world, titan, startAngle, step, cut);
            }
         });
      }
   }

   private static void cutFrame(ServerLevel world, LimboTitanEntity titan, double startAngle, int step, Set<UUID> cut) {
      double angle = startAngle + ARC_STEP * step;
      titan.setYRot(yaw(angle));
      titan.setYBodyRot(yaw(angle));
      Vec3 armRoot = shoulder(titan, angle);
      Vec3 armTip = tip(titan, angle);
      BossFx.beam(world, armRoot, armTip, ParticleTypes.CRIT, 16, 0.3);
      BossFx.burst(world, armTip, ParticleTypes.LARGE_SMOKE, 3, 0.5, 0.06);
      BossFx.colored(world, armTip, BossStyle.LIMBO_TITAN.aura, 1.6F, 5, 0.6);
      if (step % 2 == 0) {
         BossFx.burst(world, armTip, BossStyle.LIMBO_TITAN.spark(), 4, 0.6, 0.12);
      }

      Vec3 anchor = BossFx.ground(titan);
      double perpX = -Math.sin(angle);
      double perpZ = Math.cos(angle);
      boolean bitten = false;

      for (LivingEntity victim : world.getEntitiesOfClass(LivingEntity.class, new AABB(anchor, anchor).inflate(17.0), e -> saws(titan, e))) {
         double dx = victim.getX() - anchor.x;
         double dz = victim.getZ() - anchor.z;
         double distance = Math.sqrt(dx * dx + dz * dz);
         double off = Mth.wrapDegrees((float)Math.toDegrees(Math.atan2(dz, dx) - angle));
         if (distance <= 14.0 + victim.getBbWidth() * 0.5 && Math.abs(off) <= 12.0 && cut.add(victim.getUUID())) {
            victim.hurt(world.damageSources().mobAttack(titan), 22.0F);
            victim.setDeltaMovement(perpX * 0.9, 0.75, perpZ * 0.9);
            bitten = true;
         }
      }

      if (bitten) {
         BossFx.sound(world, armTip, ModmindsModSounds.GHOSTHIT.get(), SoundSource.HOSTILE, 1.8F, 0.8 + step * 0.015, 0.1);
      }

      if (step == 25) {
         BossFx.burst(world, armTip, ParticleTypes.FLAME, 10, 0.9, 0.08);
         BossFx.sound(world, armTip, SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE, 1.4F, 0.5);
      }

      if (step == 0 || step == 25) {
         BossFx.sound(world, armRoot, SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 1.5F, step == 0 ? 0.5 : 0.62);
      }
   }

   private static boolean saws(LimboTitanEntity titan, LivingEntity victim) {
      return victim.isAlive() && victim != titan && !(victim instanceof LimboTitanEntity) && victim.getRootVehicle() != titan;
   }

   private static double aimAngle(LimboTitanEntity titan) {
      LivingEntity target = titan.getTarget();
      Vec3 focus = target != null ? target.position() : titan.position().add(titan.getLookAngle());
      return Math.atan2(focus.z - titan.getZ(), focus.x - titan.getX());
   }

   private static Vec3 shoulder(LimboTitanEntity titan, double angle) {
      return point(titan, angle, 2.0);
   }

   private static Vec3 tip(LimboTitanEntity titan, double angle) {
      return point(titan, angle, 14.0);
   }

   private static Vec3 point(LimboTitanEntity titan, double angle, double out) {
      return new Vec3(
         titan.getX() + Math.cos(angle) * out, titan.getY() + titan.getBbHeight() * 0.55, titan.getZ() + Math.sin(angle) * out
      );
   }

   private static float yaw(double angle) {
      return (float)Math.toDegrees(angle) - 90.0F;
   }
}
