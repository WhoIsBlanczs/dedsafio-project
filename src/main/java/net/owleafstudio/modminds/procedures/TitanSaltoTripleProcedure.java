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
import net.owleafstudio.modminds.init.ModmindsModSounds;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class TitanSaltoTripleProcedure {
   private static final int HOPS = 3;
   private static final int FIRST_MARK_TICKS = 30;
   private static final int FOLLOW_MARK_TICKS = 10;
   private static final int FLIGHT_TICKS = 26;
   private static final int RECOVER_TICKS = 16;
   private static final int TRAIL_TICKS = 2;
   private static final int SETTLE_TICKS = 4;
   private static final double[] HOP_SHRINK = new double[]{1.0, 0.8, 0.6};
   private static final double RADIUS = 9.0;
   private static final double LIFT = 1.05;
   private static final double AIR_DRAG = 0.91;
   private static final double HORIZONTAL_GAIN = (1.0 - Math.pow(0.91, 26.0)) / 0.08999999999999997;
   private static final double MAX_LAUNCH_SPEED = 2.4;
   private static final double MAX_HOP = 2.4 * HORIZONTAL_GAIN;
   private static final double BASE_SPEED = 0.1;
   private static final float DAMAGE = 14.0F;
   private static final float BURN_SECONDS = 4.0F;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof LimboTitanEntity titan && world instanceof ServerLevel serverLevel) {
         ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         BossFx.alert(serverLevel, BossFx.centre(titan), 96.0, "⚠ TITAN DEL LIMBO: METEORO");
         BossPresentation.windup(serverLevel, titan, BossStyle.LIMBO_TITAN);
         titan.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
         int at = 0;

         for (int hop = 0; hop < 3; hop++) {
            boolean opening = hop == 0;
            int mark = opening ? 30 : 10;
            double shrink = HOP_SHRINK[hop];
            ModmindsMod.queueServerWork(at, () -> {
               if (titan.isAlive()) {
                  telegraph(serverLevel, titan, shrink, mark, opening);
               }
            });
            at += mark + 26 + 16;
         }

         ModmindsMod.queueServerWork(at, () -> {
            if (titan.isAlive()) {
               titan.setAnimation("idle");
               titan.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1);
            }
         });
      }
   }

   private static void telegraph(ServerLevel world, LimboTitanEntity titan, double shrink, int mark, boolean opening) {
      Vec3 landing = landingPoint(world, titan, shrink);
      BossFx.marker(world, landing, 9.0, mark);
      if (opening) {
         BossFx.sound(world, BossFx.centre(titan), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 1.6F, 0.65);
      }

      ModmindsMod.queueServerWork(mark, () -> {
         if (titan.isAlive()) {
            launch(world, titan, landing);
         }
      });
   }

   private static void launch(ServerLevel world, LimboTitanEntity titan, Vec3 landing) {
      titan.setAnimation("ultimate_jump");
      Vec3 start = titan.position();
      double dx = landing.x - start.x;
      double dz = landing.z - start.z;
      double distance = Math.sqrt(dx * dx + dz * dz);
      double speed = distance / HORIZONTAL_GAIN;
      if (distance > 0.01) {
         titan.setDeltaMovement(dx / distance * speed, 1.05, dz / distance * speed);
      } else {
         titan.setDeltaMovement(0.0, 1.05, 0.0);
      }

      BossFx.sound(world, BossFx.ground(titan), SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.HOSTILE, 2.2F, 0.7, 0.06);
      BossFx.impactBurst(world, surface(world, start), 3.4, BossFx.CRUSHED_STONE);

      for (int tick = 0; tick < 26; tick += 2) {
         ModmindsMod.queueServerWork(tick, () -> {
            if (titan.isAlive()) {
               flightFrame(world, titan);
            }
         });
      }

      Set<UUID> crushed = new HashSet<>();
      ModmindsMod.queueServerWork(26, () -> {
         if (titan.isAlive()) {
            landed(world, titan, landing, crushed);
         }
      });
   }

   private static void flightFrame(ServerLevel world, LimboTitanEntity titan) {
      Vec3 under = titan.position().add(0.0, -titan.getBbHeight() * 0.45, 0.0);
      BossFx.burst(world, under, ParticleTypes.FLAME, 7, 0.6, 0.05);
      BossFx.burst(world, under, BossStyle.LIMBO_TITAN.mote, 3, 0.9, 0.08);
   }

   private static void landed(ServerLevel world, LimboTitanEntity titan, Vec3 landing, Set<UUID> crushed) {
      titan.setAnimation("meteorite_attack");
      Vec3 at = surface(world, landing);
      BossFx.smashSmall(world, at, 9.0, 0.4, BossStyle.LIMBO_TITAN.flare);
      BossFx.sound(world, at, ModmindsModSounds.HAMMER_1.get(), SoundSource.HOSTILE, 2.6F, 0.85, 0.08);
      crush(world, titan, at, crushed);
      ModmindsMod.queueServerWork(4, () -> crush(world, titan, at, crushed));
   }

   private static void crush(ServerLevel world, LimboTitanEntity titan, Vec3 at, Set<UUID> crushed) {
      for (LivingEntity victim : world.getEntitiesOfClass(LivingEntity.class, new AABB(at, at).inflate(11.0), e -> falls(titan, e))) {
         if (victim.position().distanceTo(at) <= 9.0 + victim.getBbWidth() * 0.5 && crushed.add(victim.getUUID())) {
            victim.hurt(world.damageSources().mobAttack(titan), 14.0F);
            victim.igniteForSeconds(4.0F);
         }
      }
   }

   private static Vec3 landingPoint(ServerLevel world, LimboTitanEntity titan, double shrink) {
      LivingEntity target = titan.getTarget();
      Vec3 focus = target != null ? target.position() : titan.position().add(titan.getLookAngle().scale(10.0));
      double dx = focus.x - titan.getX();
      double dz = focus.z - titan.getZ();
      double distance = Math.min(Math.sqrt(dx * dx + dz * dz) * shrink, MAX_HOP);
      double angle = Math.atan2(dz, dx);
      return surface(
         world, new Vec3(titan.getX() + Math.cos(angle) * distance, titan.getY(), titan.getZ() + Math.sin(angle) * distance)
      );
   }

   private static boolean falls(LimboTitanEntity titan, LivingEntity victim) {
      return victim.isAlive() && victim != titan && !(victim instanceof LimboTitanEntity) && victim.getRootVehicle() != titan;
   }

   private static Vec3 surface(ServerLevel world, Vec3 at) {
      return new Vec3(
         at.x,
         world.getHeight(Heightmap.Types.MOTION_BLOCKING, Mth.floor(at.x), Mth.floor(at.z)),
         at.z
      );
   }
}
