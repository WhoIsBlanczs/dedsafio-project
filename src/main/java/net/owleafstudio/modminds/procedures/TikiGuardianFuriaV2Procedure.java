package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.entity.TikiGuardianEntity;

public class TikiGuardianFuriaV2Procedure {
   private static final int SLAM_LEAD_TICKS = 34;
   private static final double SLAM_RADIUS = 4.6;
   private static final float SLAM_PEAK = 12.0F;
   private static final float SLAM_EDGE = 3.0F;
   private static final double STOMP_RANGE = 13.0;
   private static final double STOMP_RADIUS = 8.0;
   private static final float STOMP_PEAK = 9.0F;
   private static final float STOMP_EDGE = 2.5F;
   private static final int SPIN_TURNS = 3;
   private static final int SPIN_GAP_TICKS = 22;
   private static final double SPIN_REACH = 6.5;
   private static final float SPIN_DAMAGE = 8.0F;
   private static final double CLOSE_RANGE = 13.0;

   public static void execute(LevelAccessor world, TikiGuardianEntity tiki, double distance) {
      if (world instanceof ServerLevel level && tiki.isAlive()) {
         if (distance <= 13.0) {
            stomp(level, tiki);
         } else if (distance <= 19.5) {
            sweep(level, tiki);
         } else {
            slam(level, tiki);
         }
      }
   }

   private static void slam(ServerLevel level, TikiGuardianEntity tiki) {
      LivingEntity target = tiki.getTarget();
      if (target != null) {
         tiki.setAnimation("animation.TikiGuardian.torsopunch");
         BossFx.sound(level, BossFx.centre(tiki), (SoundEvent)SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE.value(), SoundSource.HOSTILE, 1.8F, 0.5);
         Vec3 marked = BossFx.groundAt(level, target.getX(), target.getZ());
         BossFx.marker(level, marked, 4.6, 34);
         ModmindsMod.queueServerWork(
            34,
            () -> {
               if (tiki.isAlive()) {
                  Vec3 landing = BossFx.groundAt(
                     level, target.isAlive() ? target.getX() : marked.x, target.isAlive() ? target.getZ() : marked.z
                  );
                  BossFx.smash(level, landing, 4.6, 0.5, BossStyle.TIKI.flare);
                  BossFx.groundSwell(level, landing, 7.359999999999999, 26);
                  BossFx.blastDamage(level, level.damageSources().mobAttack(tiki), landing, 4.6, 12.0F, 3.0F, 1.5, 0.85);
                  BossFx.groundCrack(level, landing, 6.4399999999999995, 70);
               }
            }
         );
      }
   }

   private static void stomp(ServerLevel level, TikiGuardianEntity tiki) {
      tiki.setAnimation("animation.TikiGuardian.dash");
      Vec3 centre = BossFx.ground(tiki);
      BossFx.marker(level, centre, 8.0, 18);
      BossFx.sound(level, centre, (SoundEvent)SoundEvents.NOTE_BLOCK_HARP.value(), SoundSource.HOSTILE, 1.6F, 0.4);
      ModmindsMod.queueServerWork(18, () -> {
         if (tiki.isAlive()) {
            Vec3 here = BossFx.ground(tiki);
            BossFx.smashSmall(level, here, 4.0, 0.42, BossStyle.TIKI.flare);
            BossFx.groundSwell(level, here, 8.0, 30);
            BossFx.blastDamage(level, level.damageSources().mobAttack(tiki), here, 8.0, 9.0F, 2.5F, 1.9, 1.05);
            BossFx.groundCrack(level, here, 7.2, 60);
            BossFx.shake(level, here, 28.0, 16, 0.4);
         }
      });
   }

   private static void sweep(ServerLevel level, TikiGuardianEntity tiki) {
      tiki.setAnimation("animation.TikiGuardian.spin");
      BossFx.sound(level, BossFx.centre(tiki), (SoundEvent)SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.HOSTILE, 1.5F, 0.6);

      for (int turn = 0; turn < 3; turn++) {
         int at = 12 + turn * 22;
         double reach = 6.5 * (0.75 + 0.25 * (turn + 1));
         double heading = Math.toRadians(tiki.getYRot() + turn * 30.0);
         boolean opening = turn == 0;
         ModmindsMod.queueServerWork(at, () -> {
            if (tiki.isAlive()) {
               turnSweep(level, tiki, heading, reach, opening);
            }
         });
      }
   }

   private static void turnSweep(ServerLevel level, TikiGuardianEntity tiki, double heading, double reach, boolean opening) {
      Vec3 from = BossFx.centre(tiki);
      Vec3 direction = new Vec3(-Math.sin(heading), 0.0, Math.cos(heading));
      Vec3 to = from.add(direction.x * reach, -0.4, direction.z * reach);
      BossFx.beam(level, from, to, BossStyle.TIKI.flare, 12, 0.25);
      BossFx.burst(level, to, BossFx.CRUSHED_STONE, 12, 0.4, 0.3);
      if (opening) {
         BossFx.shake(level, from, reach + 18.0, 12, 0.3);
      }

      BossFx.sound(level, to, SoundEvents.MACE_SMASH_AIR, SoundSource.HOSTILE, 1.6F, 0.7);

      for (LivingEntity victim : level.getEntitiesOfClass(
         LivingEntity.class, new AABB(from, to).inflate(1.6), v -> v != tiki && v.isAlive() && !v.isSpectator() && !tiki.hasPassenger(v)
      )) {
         if (!(BossFx.distanceToSegment(victim.getEyePosition(), from, to) > 2.2)) {
            victim.hurt(level.damageSources().mobAttack(tiki), 8.0F);
            Vec3 thrown = victim.position().subtract(from);
            double sideways = Math.max(1.0E-4, Math.hypot(thrown.x, thrown.z));
            victim.setDeltaMovement(victim.getDeltaMovement().add(thrown.x / sideways * 1.6, 0.75, thrown.z / sideways * 1.6));
            victim.needsSync = true;
         }
      }
   }
}
