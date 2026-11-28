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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.entity.LimboTitanEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class TitanBolaFuegoProcedure {
   private static final int RELEASE_TICKS = 20;
   private static final int STEP_TICKS = 2;
   private static final int BURN_FRAMES = 6;
   private static final int BURN_GAP_TICKS = 7;
   private static final double ROLL_REACH = 40.0;
   private static final double STEP_DISTANCE = 2.0;
   private static final double FAN_BUDGET = 72.0;
   private static final double GENERATION_SHRINK = 0.5;
   private static final double SPLIT_ANGLE = Math.toRadians(25.0);
   private static final double LANE_WIDTH = 3.0;
   private static final double PATCH_WIDTH = 2.6;
   private static final int MAX_SPLITS = 2;
   private static final float ROLL_DAMAGE = 12.0F;
   private static final float PATCH_DAMAGE = 3.0F;
   private static final float BURN_SECONDS = 6.0F;
   private static final double BASE_SPEED = 0.1;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof LimboTitanEntity titan && world instanceof ServerLevel serverLevel) {
         ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         BossFx.alert(serverLevel, BossFx.centre(titan), 96.0, "⚠ TITAN DEL LIMBO: BOLA DE FUEGO");
         BossPresentation.windup(serverLevel, titan, BossStyle.LIMBO_TITAN);
         BossFx.sound(serverLevel, BossFx.mouth(titan, 3.0, 6.0), SoundEvents.FIRECHARGE_USE, SoundSource.HOSTILE, 2.2F, 0.7);
         titan.setAnimation("fire_ball");
         titan.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
         LivingEntity target = titan.getTarget();
         Vec3 focus = target != null ? target.position() : titan.position();
         double heading = Math.atan2(focus.z - titan.getZ(), focus.x - titan.getX());
         Set<UUID> cut = new HashSet<>();
         Set<UUID> scorched = new HashSet<>();
         ModmindsMod.queueServerWork(20, () -> {
            if (titan.isAlive()) {
               BossFx.sound(serverLevel, BossFx.mouth(titan, 3.0, 6.0), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 2.4F, 0.55);
               rollLane(serverLevel, titan, laneStart(serverLevel, titan, heading), heading, 40.0, 0.0, 0, cut, scorched);
            }
         });
         ModmindsMod.queueServerWork(46, () -> {
            if (titan.isAlive()) {
               titan.setAnimation("idle");
               titan.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1);
            }
         });
      }
   }

   private static void rollLane(
      ServerLevel world, LimboTitanEntity titan, Vec3 start, double heading, double reach, double travelled, int depth, Set<UUID> cut, Set<UUID> scorched
   ) {
      double forward = Math.cos(heading);
      double sideways = Math.sin(heading);
      int steps = (int)Math.ceil(reach / 2.0);

      for (int step = 0; step < steps; step++) {
         int frame = step;
         Vec3 at = surface(world, start.add(forward * frame * 2.0, 0.0, sideways * frame * 2.0));
         ModmindsMod.queueServerWork(frame * 2, () -> {
            if (titan.isAlive()) {
               rollFrame(world, at, titan, frame, cut);
            }
         });
      }

      Vec3 end = surface(world, start.add(forward * reach, 0.0, sideways * reach));
      ModmindsMod.queueServerWork(steps * 2, () -> {
         if (titan.isAlive()) {
            breakUp(world, titan, end, heading, reach, travelled, depth, cut, scorched);
         }
      });
   }

   private static void rollFrame(ServerLevel world, Vec3 at, LimboTitanEntity titan, int frame, Set<UUID> cut) {
      BossFx.burst(world, at, ParticleTypes.FLAME, 9, 0.7, 0.05);
      BossFx.burst(world, at.add(0.0, 0.5, 0.0), ParticleTypes.SMALL_FLAME, 6, 0.5, 0.02);
      BossFx.burst(world, at, ParticleTypes.LAVA, 3, 0.4, 0.0);
      if (frame % 4 == 0) {
         BossFx.burst(world, at.add(0.0, 1.0, 0.0), BossStyle.LIMBO_TITAN.mote, 4, 0.9, 0.1);
      }

      for (LivingEntity victim : world.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(5.0), e -> burns(titan, e))) {
         if (victim.position().distanceTo(at) <= 3.0 + victim.getBbWidth() * 0.5 && cut.add(victim.getUUID())) {
            victim.hurt(world.damageSources().mobAttack(titan), 12.0F);
            victim.igniteForSeconds(6.0F);
         }
      }
   }

   private static void breakUp(
      ServerLevel world, LimboTitanEntity titan, Vec3 at, double heading, double reach, double travelled, int depth, Set<UUID> cut, Set<UUID> scorched
   ) {
      burnPatch(world, at, titan, scorched);
      double used = travelled + reach;
      double fragment = reach * 0.5;
      if (depth < 2 && !(used + fragment > 72.0)) {
         if (depth == 0) {
            BossFx.smash(world, at, 7.0, 0.45, BossStyle.LIMBO_TITAN.flare);
         } else {
            BossFx.impactBurst(world, at, 5.0, BossStyle.LIMBO_TITAN.flare);
         }

         BossFx.sound(world, at, SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.6F, 0.7 + depth * 0.2, 0.2);

         for (int branch = -1; branch <= 1; branch++) {
            double childHeading = heading + branch * SPLIT_ANGLE;
            Vec3 start = surface(world, at.add(Math.cos(childHeading) * 2.0, 0.0, Math.sin(childHeading) * 2.0));
            rollLane(world, titan, start, childHeading, fragment, used, depth + 1, cut, scorched);
         }
      }
   }

   private static void burnPatch(ServerLevel world, Vec3 at, LimboTitanEntity titan, Set<UUID> scorched) {
      for (int frame = 0; frame < 6; frame++) {
         boolean opening = frame == 0;
         ModmindsMod.queueServerWork(frame * 7, () -> {
            BossFx.burst(world, at, ParticleTypes.FLAME, 5, 0.8, 0.04);
            BossFx.burst(world, at.add(0.0, 0.3, 0.0), ParticleTypes.SMALL_FLAME, 4, 0.6, 0.02);
            if (opening) {
               BossFx.sound(world, at, SoundEvents.FIRE_AMBIENT, SoundSource.HOSTILE, 1.0F, 0.8, 0.15);
            }

            for (LivingEntity victim : world.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(3.6), e -> burns(titan, e))) {
               if (victim.position().distanceTo(at) <= 2.6 + victim.getBbWidth() * 0.5 && scorched.add(victim.getUUID())) {
                  victim.hurt(world.damageSources().inFire(), 3.0F);
               }
            }
         });
      }
   }

   private static boolean burns(LimboTitanEntity titan, LivingEntity victim) {
      return victim.isAlive() && victim != titan && !(victim instanceof LimboTitanEntity) && victim.getRootVehicle() != titan;
   }

   private static Vec3 laneStart(ServerLevel world, LimboTitanEntity titan, double heading) {
      return surface(world, BossFx.ground(titan).add(Math.cos(heading) * 4.0, 0.0, Math.sin(heading) * 4.0));
   }

   private static Vec3 surface(ServerLevel world, Vec3 at) {
      return new Vec3(
         at.x,
         world.getHeight(Heightmap.Types.MOTION_BLOCKING, Mth.floor(at.x), Mth.floor(at.z)),
         at.z
      );
   }
}
