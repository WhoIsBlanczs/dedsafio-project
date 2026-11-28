package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.BlockParticleOption;
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
import net.owleafstudio.modminds.entity.TikiGuardianEntity;
import net.owleafstudio.modminds.entity.TorretaTikiEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;
import net.owleafstudio.modminds.init.ModmindsModSounds;

public class TikiGuardianGuardianesProcedure {
   private static final int TURRETS = 3;
   private static final int SLAM_TICKS = 16;
   private static final int STAGGER_TICKS = 12;
   private static final int LIFETIME_TICKS = 600;
   private static final double RADIUS = 7.0;
   private static final double SEARCH_RADIUS = 32.0;
   private static final BlockParticleOption SOUL_SOIL_PILLAR = new BlockParticleOption(ParticleTypes.DUST_PILLAR, Blocks.SOUL_SOIL.defaultBlockState());

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof TikiGuardianEntity tiki && world instanceof ServerLevel serverLevel) {
         int missing = 3 - countTurrets(serverLevel, tiki);
         if (missing > 0) {
            BossFx.alert(serverLevel, BossFx.centre(tiki), 96.0, "⚠ TIKI: GUARDIANES");
            tiki.setAnimation("animation.TikiGuardian.torsopunch");
            BossPresentation.windup(serverLevel, tiki, BossStyle.TIKI);
            BossFx.sound(serverLevel, BossFx.centre(tiki), ModmindsModSounds.HAMMER_2.get(), SoundSource.HOSTILE, 1.6F, 0.85);
            LivingEntity target = tiki.getTarget();
            double facing = facing(tiki);

            for (int slot = 0; slot < missing; slot++) {
               double angle = facing + slot * Math.PI * 2.0 / 3.0;
               boolean first = slot == 0;
               ModmindsMod.queueServerWork(16 + slot * 12, () -> {
                  if (tiki.isAlive()) {
                     erupt(serverLevel, tiki, angle, target, first);
                  }
               });
            }
         }
      }
   }

   private static void erupt(ServerLevel world, TikiGuardianEntity tiki, double angle, LivingEntity target, boolean first) {
      Vec3 at = surface(world, tiki.getX() + Math.cos(angle) * 7.0, tiki.getZ() + Math.sin(angle) * 7.0);
      TorretaTikiEntity turret = (TorretaTikiEntity)ModmindsModEntities.TORRETA_TIKI.get().create(world, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
      if (turret != null) {
         turret.setPos(at.x, at.y + 0.1, at.z);
         turret.setYRot((float)Math.toDegrees(Math.atan2(-Math.cos(angle), Math.sin(angle))));
         world.addFreshEntity(turret);
         BossFx.smashSmall(world, at, 4.0, first ? 0.35 : 0.12, BossStyle.TIKI.flare);
         BossFx.blastDamage(world, world.damageSources().mobAttack(tiki), at, 4.0, 10.0F, 3.0F, 1.0, 0.45);
         if (first) {
            BossFx.shake(world, at, 34.0, 14, 0.45);
         }

         BossFx.burst(world, at.add(0.0, 0.9, 0.0), ParticleTypes.COMPOSTER, 14, 0.7, 0.14);
         BossFx.burst(world, at, SOUL_SOIL_PILLAR, 12, 0.6, 0.1);
         BossFx.ring(world, at, 2.4, 10, BossStyle.TIKI.spark(), 1, 0.3);
         BossFx.sound(world, at, SoundEvents.BAMBOO_WOOD_BREAK, SoundSource.HOSTILE, 1.8F, 0.9, 0.15);
         if (target != null) {
            turret.setTarget(target);
         }

         ModmindsMod.queueServerWork(600, () -> rot(world, turret));
      }
   }

   private static void rot(ServerLevel world, TorretaTikiEntity turret) {
      if (turret.isAlive()) {
         Vec3 at = BossFx.ground(turret);
         BossFx.impactBurst(world, at, 3.0, BossStyle.TIKI.flare);
         BossFx.burst(world, at, SOUL_SOIL_PILLAR, 10, 0.5, 0.1);
         BossFx.sound(world, at, SoundEvents.BAMBOO_WOOD_BREAK, SoundSource.HOSTILE, 1.2F, 0.6);
         turret.discard();
      }
   }

   private static int countTurrets(ServerLevel world, TikiGuardianEntity tiki) {
      Vec3 at = tiki.position();
      return world.getEntitiesOfClass(TorretaTikiEntity.class, new AABB(at, at).inflate(32.0), e -> e.isAlive()).size();
   }

   private static double facing(TikiGuardianEntity tiki) {
      Vec3 look = tiki.getLookAngle();
      return Math.atan2(look.z, look.x);
   }

   private static Vec3 surface(ServerLevel world, double x, double z) {
      return new Vec3(x, world.getHeight(Heightmap.Types.MOTION_BLOCKING, (int)Math.floor(x), (int)Math.floor(z)), z);
   }
}
