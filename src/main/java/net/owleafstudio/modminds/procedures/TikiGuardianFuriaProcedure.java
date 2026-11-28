package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.compat.ArrowCompat;
import net.owleafstudio.modminds.entity.DardoTikiProyectilEntity;
import net.owleafstudio.modminds.entity.TikiGuardianEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;

public class TikiGuardianFuriaProcedure {
   private static final int FAN_DARTS = 7;
   private static final int FAN_SPREAD_DEG = 40;
   private static final int RING_DARTS = 12;
   private static final int RING_WAVES = 2;
   private static final float SPEED = 5.5F;
   private static final float DAMAGE = 7.0F;

   public static void execute(LevelAccessor world, Entity entity, boolean aimed) {
      if (entity instanceof TikiGuardianEntity tiki && world instanceof ServerLevel serverLevel) {
         LivingEntity target = tiki.getTarget();
         BossPresentation.windup(serverLevel, tiki, BossStyle.TIKI);
         BossFx.sound(serverLevel, BossFx.centre(tiki), (SoundEvent)SoundEvents.NOTE_BLOCK_HARP.value(), SoundSource.HOSTILE, 1.6F, aimed ? 0.7 : 0.5);
         if (aimed && target != null) {
            tiki.setAnimation("animation.TikiGuardian.emotiza");
            ModmindsMod.queueServerWork(10, () -> {
               if (tiki.isAlive()) {
                  fireFan(serverLevel, tiki, target);
               }
            });
         } else {
            tiki.setAnimation("animation.TikiGuardian.invocation");

            for (int wave = 0; wave < 2; wave++) {
               int at = 12 + wave * 16;
               double phase = wave * Math.PI / 12.0;
               boolean opening = wave == 0;
               ModmindsMod.queueServerWork(at, () -> {
                  if (tiki.isAlive()) {
                     fireRing(serverLevel, tiki, phase, opening);
                  }
               });
            }
         }
      }
   }

   private static void fireFan(ServerLevel world, TikiGuardianEntity tiki, LivingEntity target) {
      Vec3 muzzle = BossFx.mouth(tiki, 2.0, 3.0);
      Vec3 aim = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0).subtract(muzzle);
      double base = Math.atan2(aim.z, aim.x);
      double pitch = Math.atan2(aim.y, Math.sqrt(aim.x * aim.x + aim.z * aim.z));

      for (int i = 0; i < 7; i++) {
         double offset = Math.toRadians(-20.0 + 40 * i / 6.0);
         shoot(world, tiki, muzzle, base + offset, pitch, 5.5F);
      }

      BossFx.sound(world, muzzle, SoundEvents.DISPENSER_LAUNCH, SoundSource.HOSTILE, 1.2F, 1.6);
   }

   private static void fireRing(ServerLevel world, TikiGuardianEntity tiki, double phase, boolean opening) {
      Vec3 muzzle = BossFx.mouth(tiki, 0.0, 3.2);
      if (opening) {
         BossFx.ring(world, muzzle, 3.0, 14, BossStyle.TIKI.flare, 1, 0.3);
         BossFx.shake(world, BossFx.centre(tiki), 26.0, 10, 0.35);
      }

      BossFx.sound(world, muzzle, SoundEvents.DISPENSER_LAUNCH, SoundSource.HOSTILE, 1.4F, 1.4);

      for (int i = 0; i < 12; i++) {
         double angle = phase + i * Math.PI * 2.0 / 12.0;
         shoot(world, tiki, muzzle, angle, -0.06, 4.95F);
      }
   }

   private static void shoot(ServerLevel world, TikiGuardianEntity tiki, Vec3 from, double yaw, double pitch, float speed) {
      double horizontal = Math.cos(pitch);
      Vec3 direction = new Vec3(Math.cos(yaw) * horizontal, Math.sin(pitch), Math.sin(yaw) * horizontal);
      AbstractArrow dart = new DardoTikiProyectilEntity(ModmindsModEntities.DARDO_TIKI_PROYECTIL.get(), world);
      dart.setOwner(tiki);
      dart.setBaseDamage(7.0);
      ArrowCompat.setKnockback(dart, 0);
      dart.setSilent(true);
      dart.setPos(from.x + direction.x, from.y, from.z + direction.z);
      dart.shoot(direction.x, direction.y, direction.z, speed, 0.0F);
      world.addFreshEntity(dart);
      BossFx.burst(world, from.add(direction.x, 0.0, direction.z), ParticleTypes.CRIT, 4, 0.2, 0.1);
   }
}
