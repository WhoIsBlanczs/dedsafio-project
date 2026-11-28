package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.compat.ArrowCompat;
import net.owleafstudio.modminds.entity.HydraEntity;
import net.owleafstudio.modminds.entity.WaterChargeEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class HydraChorroProcedure {
   private static final int TELEGRAPH_TICKS = 20;
   private static final int FRAMES = 40;
   private static final int FRAME_TICKS = 3;
   private static final double STEP_ANGLE = Math.toRadians(27.0);
   private static final double ARM_ANGLE = Math.toRadians(135.0);
   private static final int ARMS = 2;
   private static final double WARNING_RADIUS = 26.0;
   private static final double MOUTH_FORWARDS = 3.0;
   private static final double MOUTH_UP = 14.0;
   private static final float CHARGE_DAMAGE = 3.0F;
   private static final float CHARGE_SPEED = 1.6F;
   private static final float CHARGE_INACCURACY = 1.0F;
   private static final double NOSE_DOWN = -0.05;
   private static final double CHARGE_GRAVITY = 0.05;
   private static final int FLIGHT_TICKS = 60;
   private static final int BEAM_SAMPLES = 14;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof HydraEntity hydra && world instanceof ServerLevel serverLevel) {
         ModmindsModVariables.MapVariables.get(world).HydraAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         hydra.setAnimation("beam_attack3");
         BossFx.alert(serverLevel, BossFx.centre(hydra), 96.0, "⚠ HYDRA: CHORRO ESPIRAL");
         BossFx.warningCircle(serverLevel, hydra, 26.0, 20);
         BossPresentation.windup(serverLevel, hydra, BossStyle.HYDRA);
         LivingEntity target = hydra.getTarget();
         double base = target != null
            ? Math.atan2(target.getZ() - hydra.getZ(), target.getX() - hydra.getX())
            : (Math.PI / 2) + Math.toRadians(hydra.getYRot());

         for (int frame = 0; frame < 40; frame++) {
            int step = frame;
            ModmindsMod.queueServerWork(20 + frame * 3, () -> {
               if (hydra.isAlive()) {
                  sweepFrame(serverLevel, hydra, base + step * STEP_ANGLE, step);
               }
            });
         }

         ModmindsMod.queueServerWork(140, () -> {
            if (hydra.isAlive()) {
               hydra.setAnimation("idle");
            }
         });
      }
   }

   private static void sweepFrame(ServerLevel world, HydraEntity hydra, double angle, int frame) {
      Vec3 muzzle = BossFx.mouth(hydra, 3.0, 14.0);

      for (int arm = 0; arm < 2; arm++) {
         double jet = angle + arm * ARM_ANGLE;
         double dx = Math.cos(jet);
         double dz = Math.sin(jet);
         fire(world, hydra, muzzle, dx, dz);
         BossFx.beam(world, muzzle, landingPoint(world, muzzle, dx, dz), ParticleTypes.DOLPHIN, 14, 0.2);
      }

      if (frame % 4 == 0) {
         BossFx.sound(world, muzzle, SoundEvents.GENERIC_SPLASH, SoundSource.HOSTILE, 2.2F, 1.0, 0.2);
      }
   }

   private static void fire(ServerLevel world, HydraEntity hydra, Vec3 from, double dx, double dz) {
      Projectile charge = (new Object() {
         public Projectile getArrow(Level level, Entity shooter, float damage, int knockback) {
            AbstractArrow entityToSpawn = new WaterChargeEntity(ModmindsModEntities.WATER_CHARGE.get(), level);
            entityToSpawn.setOwner(shooter);
            entityToSpawn.setBaseDamage(damage);
            ArrowCompat.setKnockback(entityToSpawn, knockback);
            entityToSpawn.setSilent(true);
            return entityToSpawn;
         }
      }).getArrow(world, hydra, 3.0F, 0);
      charge.setPos(from.x, from.y, from.z);
      charge.setNoGravity(false);
      charge.shoot(dx, -0.05, dz, 1.6F, 1.0F);
      world.addFreshEntity(charge);
   }

   private static Vec3 landingPoint(ServerLevel world, Vec3 from, double dx, double dz) {
      double x = from.x;
      double y = from.y;
      double z = from.z;
      double vy = -0.0800000011920929;

      for (int tick = 0; tick < 60; tick++) {
         x += dx * 1.6F;
         z += dz * 1.6F;
         vy -= 0.05;
         y += vy;
         double floor = world.getHeight(Heightmap.Types.MOTION_BLOCKING, (int)Math.floor(x), (int)Math.floor(z));
         if (y <= floor) {
            return new Vec3(x, floor, z);
         }
      }

      return new Vec3(x, y, z);
   }
}
