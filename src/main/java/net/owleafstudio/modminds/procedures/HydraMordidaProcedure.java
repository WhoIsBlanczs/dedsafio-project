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
import net.minecraft.world.level.levelgen.Heightmap;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.entity.HydraEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class HydraMordidaProcedure {
   private static final int SLAMS = 3;
   private static final int SLAM_TICKS = 34;
   private static final int MARK_TICKS = 30;
   private static final int HELD_TICKS = 102;
   private static final double RADIUS = 6.5;
   private static final float DAMAGE = 16.0F;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof HydraEntity hydra && world instanceof ServerLevel serverLevel) {
         ModmindsModVariables.MapVariables.get(world).HydraAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         hydra.setAnimation("area_attack");
         BossFx.alert(serverLevel, BossFx.centre(hydra), 96.0, "⚠ HYDRA: MORDIDA TRIPLE");
         BossPresentation.windup(serverLevel, hydra, BossStyle.HYDRA);

         for (int slam = 0; slam < 3; slam++) {
            boolean opening = slam == 0;
            ModmindsMod.queueServerWork(slam * 34, () -> {
               if (hydra.isAlive()) {
                  markBite(serverLevel, hydra, opening);
               }
            });
         }

         ModmindsMod.queueServerWork(102, () -> {
            if (hydra.isAlive()) {
               hydra.setAnimation("idle");
            }
         });
      }
   }

   private static void markBite(ServerLevel world, HydraEntity hydra, boolean opening) {
      LivingEntity target = hydra.getTarget();
      Vec3 focus = target != null ? target.position() : hydra.position();
      Vec3 at = surface(world, focus);
      BossFx.marker(world, at, 6.5, 30);
      Set<UUID> bitten = new HashSet<>();
      ModmindsMod.queueServerWork(30, () -> {
         if (hydra.isAlive()) {
            bite(world, hydra, at, opening, bitten);
         }
      });
   }

   private static void bite(ServerLevel world, HydraEntity hydra, Vec3 at, boolean opening, Set<UUID> bitten) {
      if (opening) {
         BossFx.smashSmall(world, at, 6.5, 0.55, BossStyle.HYDRA.flare);
      } else {
         BossFx.impactBurst(world, at, 6.5, BossStyle.HYDRA.flare);
      }

      BossFx.ring(world, at.add(0.0, 1.6, 0.0), 6.5, 16, ParticleTypes.BUBBLE_COLUMN_UP, 1, 0.25);
      BossFx.ring(world, at.add(0.0, 0.5, 0.0), 5.8500000000000005, 26, ParticleTypes.SPLASH, 2, 0.35);
      BossFx.ring(world, at.add(0.0, 1.0, 0.0), 3.9, 12, BossStyle.HYDRA.spark(), 1, 0.3);
      BossFx.sound(world, at, SoundEvents.GUARDIAN_ATTACK, SoundSource.HOSTILE, 2.4F, 0.7);
      BossFx.soundLater(world, at, SoundEvents.MACE_SMASH_AIR, SoundSource.HOSTILE, 2.2F, 0.8, 4);

      for (LivingEntity victim : world.getEntitiesOfClass(
         LivingEntity.class, new AABB(at, at).inflate(10.5), e -> e != hydra && e.isAlive() && !(e instanceof HydraEntity)
      )) {
         double reach = 6.5 + victim.getBbWidth() * 0.5;
         if (victim.position().distanceTo(at) <= reach && bitten.add(victim.getUUID())) {
            victim.hurt(world.damageSources().mobAttack(hydra), 16.0F);
            double dx = victim.getX() - at.x;
            double dz = victim.getZ() - at.z;
            double distance = Math.sqrt(dx * dx + dz * dz);
            if (distance > 0.01) {
               victim.setDeltaMovement(dx / distance * 0.9, 0.75, dz / distance * 0.9);
            }
         }
      }
   }

   private static Vec3 surface(ServerLevel world, Vec3 at) {
      int x = (int)Math.floor(at.x);
      int z = (int)Math.floor(at.z);
      return new Vec3(at.x, world.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z), at.z);
   }
}
