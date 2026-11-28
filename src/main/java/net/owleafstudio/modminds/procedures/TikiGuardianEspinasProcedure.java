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
import net.minecraft.world.level.levelgen.Heightmap;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.compat.ArrowCompat;
import net.owleafstudio.modminds.entity.DardoTikiProyectilEntity;
import net.owleafstudio.modminds.entity.TikiGuardianEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;

public class TikiGuardianEspinasProcedure {
   private static final int RINGS = 5;
   private static final int RING_SPACING_TICKS = 30;
   private static final int LEAD_TICKS = 26;
   private static final int DARTS_BASE = 10;
   private static final int MARK_POINTS = 24;
   private static final double START_RADIUS = 7.0;
   private static final double RING_SHRINK = 1.2;
   private static final double RISE = 0.85;
   private static final float SPEED = 0.9F;
   private static final double DAMAGE = 6.0;
   private static final float ERUPT_PEAK = 8.0F;
   private static final float ERUPT_EDGE = 2.0F;
   private static final double PITCH_BASE = 0.8;
   private static final double PITCH_STEP = 0.14;

   public static void execute(LevelAccessor world, Entity entity, LivingEntity target) {
      if (entity instanceof TikiGuardianEntity tiki && world instanceof ServerLevel serverLevel && target != null) {
         BossFx.alert(serverLevel, BossFx.centre(tiki), 96.0, "⚠ TIKI: ESPINAS");
         tiki.setAnimation("animation.TikiGuardian.invocation");
         BossPresentation.windup(serverLevel, tiki, BossStyle.TIKI);
         BossFx.sound(serverLevel, BossFx.mouth(tiki, 0.0, 3.2), (SoundEvent)SoundEvents.NOTE_BLOCK_HARP.value(), SoundSource.HOSTILE, 1.5F, 0.8);

         for (int ring = 0; ring < 5; ring++) {
            int step = ring;
            ModmindsMod.queueServerWork(ring * 30, () -> {
               if (tiki.isAlive()) {
                  drawRing(serverLevel, tiki, step);
               }
            });
         }
      }
   }

   private static void drawRing(ServerLevel world, TikiGuardianEntity tiki, int ring) {
      LivingEntity target = tiki.getTarget();
      if (target != null) {
         Vec3 centre = surface(world, target.getX(), target.getZ());
         double radius = 7.0 - 1.2 * ring;
         BossFx.marker(world, centre, radius, 26);
         BossFx.ring(world, centre, radius, 24, BossFx.ALERT_DOT, 1, 0.0);
         BossFx.colored(world, centre, BossFx.ALERT, 1.4F, 6, radius * 0.3);
         BossFx.sound(world, centre, (SoundEvent)SoundEvents.NOTE_BLOCK_HARP.value(), SoundSource.HOSTILE, 1.2F, 0.8 + 0.14 * ring);
         ModmindsMod.queueServerWork(26, () -> {
            if (tiki.isAlive()) {
               sprout(world, tiki, centre, radius, ring);
            }
         });
      }
   }

   private static void sprout(ServerLevel world, TikiGuardianEntity tiki, Vec3 centre, double radius, int ring) {
      int darts = 10 + ring;
      double phase = ring * Math.PI / darts;
      BossFx.impactBurst(world, centre, radius * 0.35, BossStyle.TIKI.flare);
      BossFx.blastDamage(world, world.damageSources().mobAttack(tiki), centre, radius * 0.35, 8.0F, 2.0F, 0.8, 0.35);
      BossFx.sound(world, centre, SoundEvents.DISPENSER_LAUNCH, SoundSource.HOSTILE, 1.3F, 1.2);

      for (int i = 0; i < darts; i++) {
         double angle = phase + i * Math.PI * 2.0 / darts;
         double dx = Math.cos(angle);
         double dz = Math.sin(angle);
         shoot(world, tiki, new Vec3(centre.x + dx * radius, centre.y + 0.1, centre.z + dz * radius), dx, dz);
      }
   }

   private static void shoot(ServerLevel world, TikiGuardianEntity tiki, Vec3 from, double dx, double dz) {
      AbstractArrow dart = new DardoTikiProyectilEntity(ModmindsModEntities.DARDO_TIKI_PROYECTIL.get(), world);
      dart.setOwner(tiki);
      dart.setBaseDamage(6.0);
      ArrowCompat.setKnockback(dart, 0);
      dart.setSilent(true);
      dart.setNoGravity(false);
      dart.setPos(from.x, from.y, from.z);
      dart.shoot(dx, 0.85, dz, 0.9F, 0.0F);
      world.addFreshEntity(dart);
      BossFx.burst(world, from, BossFx.DUST_PILLAR, 5, 0.3, 0.14);
      BossFx.burst(world, from.add(0.0, 0.4, 0.0), ParticleTypes.CRIT, 3, 0.25, 0.15);
   }

   private static Vec3 surface(ServerLevel world, double x, double z) {
      return new Vec3(x, world.getHeight(Heightmap.Types.MOTION_BLOCKING, (int)Math.floor(x), (int)Math.floor(z)), z);
   }
}
