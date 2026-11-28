package net.owleafstudio.modminds.procedures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import net.minecraft.world.level.levelgen.Heightmap;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.entity.LimboTitanEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class TitanMuroProcedure {
   private static final int MARK_TICKS = 34;
   private static final int LANES = 7;
   private static final int GAPS = 2;
   private static final double LANE_WIDTH = 4.0;
   private static final double WALL_HEIGHT = 3.2;
   private static final int TRAVEL_TICKS = 44;
   private static final double TRAVEL = 36.0;
   private static final double TOUCH = 1.7;
   private static final float DAMAGE = 14.0F;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof LimboTitanEntity titan && world instanceof ServerLevel serverLevel) {
         ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         Vec3 origin = BossFx.ground(titan).add(titan.getLookAngle().x * 4.0, 0.0, titan.getLookAngle().z * 4.0);
         Vec3 forward = flat(titan.getLookAngle());
         Vec3 lateral = new Vec3(-forward.z, 0.0, forward.x);
         List open = gaps(serverLevel);
         titan.setAnimation("fire_ball");
         BossFx.alert(serverLevel, BossFx.centre(titan), 96.0, "⚠ TITAN DEL LIMBO: MURO DE CENIZA");
         BossPresentation.windup(serverLevel, titan, BossStyle.LIMBO_TITAN);
         BossFx.sound(serverLevel, BossFx.centre(titan), SoundEvents.GHAST_WARN, SoundSource.HOSTILE, 2.0F, 0.6);
         BossFx.sound(serverLevel, origin, SoundEvents.BLAZE_AMBIENT, SoundSource.HOSTILE, 1.6F, 0.5);

         for (int paso = 0; paso < 34; paso += 4) {
            final int t = paso;
            ModmindsMod.queueServerWork(t, () -> {
               if (titan.isAlive()) {
                  markFrame(serverLevel, origin, forward, lateral, open, t);
               }
            });
         }

         Set<UUID> burned = new HashSet<>();

         for (int tick = 0; tick < 44; tick++) {
            int step = tick;
            ModmindsMod.queueServerWork(34 + tick, () -> {
               if (titan.isAlive()) {
                  advanceFrame(serverLevel, titan, origin, forward, lateral, open, step, burned);
               }
            });
         }

         ModmindsMod.queueServerWork(
            92,
            () -> {
               if (titan.isAlive()) {
                  titan.setAnimation("empty");
                  BossFx.sound(
                     serverLevel,
                     origin.add(forward.x * 36.0, 0.0, forward.z * 36.0),
                     SoundEvents.FIRE_EXTINGUISH,
                     SoundSource.HOSTILE,
                     2.4F,
                     0.7
                  );
               }
            }
         );
      }
   }

   private static List<Integer> gaps(ServerLevel world) {
      List<Integer> lanes = new ArrayList<>();

      while (lanes.size() < 2) {
         int candidate = world.getRandom().nextInt(7);
         if (!lanes.contains(candidate)) {
            lanes.add(candidate);
         }
      }

      return lanes;
   }

   private static Vec3 flat(Vec3 look) {
      double length = Math.max(Math.hypot(look.x, look.z), 1.0E-4);
      return new Vec3(look.x / length, 0.0, look.z / length);
   }

   private static void markFrame(ServerLevel world, Vec3 origin, Vec3 forward, Vec3 lateral, List<Integer> open, int step) {
      for (int lane = 0; lane < 7; lane++) {
         if (!open.contains(lane)) {
            Vec3 from = lanePoint(origin, lateral, lane);
            Vec3 to = lanePoint(origin, lateral, lane + 1);

            for (int i = 1; i < 6; i++) {
               double t = i / 6.0;
               Vec3 at = new Vec3(
                  from.x + (to.x - from.x) * t, 0.0, from.z + (to.z - from.z) * t
               );
               world.sendParticles(BossFx.ALERT_DOT, at.x, ground(world, at) + 0.08, at.z, 1, 0.24, 0.05, 0.24, 0.0);
            }
         }
      }

      if (step % 8 == 0) {
         BossFx.sound(world, origin, (SoundEvent)SoundEvents.NOTE_BLOCK_HARP.value(), SoundSource.HOSTILE, 0.7F, 0.55);
      }
   }

   private static void advanceFrame(
      ServerLevel world, LimboTitanEntity titan, Vec3 origin, Vec3 forward, Vec3 lateral, List<Integer> open, int step, Set<UUID> burned
   ) {
      double travelled = 36.0 * step / 44.0;
      Vec3 base = origin.add(forward.x * travelled, 0.0, forward.z * travelled);
      if (step == 0) {
         BossFx.shake(world, base, 46.0, 44, 0.3);
      }

      if (step % 6 == 0) {
         BossFx.sound(world, base, SoundEvents.BLAZE_AMBIENT, SoundSource.HOSTILE, 1.2F, 0.8 + step * 0.004, 0.08);
      }

      for (int lane = 0; lane < 7; lane++) {
         if (!open.contains(lane)) {
            Vec3 from = lanePoint(base, lateral, lane);
            Vec3 to = lanePoint(base, lateral, lane + 1);

            for (int i = 0; i <= 4; i++) {
               double t = i / 4.0;
               double x = from.x + (to.x - from.x) * t;
               double z = from.z + (to.z - from.z) * t;
               double floor = ground(world, new Vec3(x, 0.0, z));

               for (double h = 0.0; h <= 3.2; h += 0.7) {
                  world.sendParticles(ParticleTypes.FLAME, x, floor + 0.3 + h, z, 1, 0.28, 0.22, 0.28, 0.0);
               }

               world.sendParticles(ParticleTypes.ASH, x, floor + 3.2 + 0.4, z, 2, 0.5, 0.3, 0.5, 0.01);
               world.sendParticles(ParticleTypes.LARGE_SMOKE, x, floor + 0.4, z, 1, 0.4, 0.2, 0.4, 0.01);
            }

            for (LivingEntity victim : world.getEntitiesOfClass(
               LivingEntity.class, new AABB(from, to).inflate(4.2), e -> e != titan && e.isAlive() && !titan.hasPassenger(e)
            )) {
               Vec3 point = BossFx.centre(victim);
               if (burned.add(victim.getUUID())
                  && BossFx.distanceToSegment(
                        point,
                        new Vec3(from.x, ground(world, from), from.z),
                        new Vec3(to.x, ground(world, to), to.z)
                     )
                     <= 1.7) {
                  victim.hurt(world.damageSources().inFire(), 14.0F);
                  victim.igniteForSeconds(4.0F);
                  victim.setDeltaMovement(forward.x * 0.85, 0.55, forward.z * 0.85);
                  victim.needsSync = true;
                  BossFx.burst(world, point, ParticleTypes.FLAME, 12, 0.5, 0.16);
               }
            }
         }
      }
   }

   private static Vec3 lanePoint(Vec3 base, Vec3 lateral, int lane) {
      double offset = (lane - 3.5) * 4.0;
      return base.add(lateral.x * offset, 0.0, lateral.z * offset);
   }

   private static double ground(ServerLevel world, Vec3 at) {
      return world.getHeight(Heightmap.Types.MOTION_BLOCKING, (int)Math.floor(at.x), (int)Math.floor(at.z));
   }
}
