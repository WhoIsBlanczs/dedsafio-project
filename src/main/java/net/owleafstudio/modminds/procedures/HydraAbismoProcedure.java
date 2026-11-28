package net.owleafstudio.modminds.procedures;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.entity.HydraEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class HydraAbismoProcedure {
   private static final int TELEGRAPH_TICKS = 45;
   private static final int HELD_TICKS = 140;
   private static final int RIM_TICKS = 4;
   private static final int PULSE_TICKS = 10;
   private static final double RADIUS = 22.0;
   private static final float DAMAGE = 5.0F;
   private static final int SLOW_TICKS = 30;
   private static final int SLOW_AMPLIFIER = 2;
   private static final double PULL = 0.06;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof HydraEntity hydra && world instanceof ServerLevel serverLevel) {
         ModmindsModVariables.MapVariables.get(world).HydraAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         hydra.setAnimation("beam_attack2");
         BossFx.alert(serverLevel, BossFx.centre(hydra), 96.0, "⚠ HYDRA: ABISMO");
         BossFx.warningCircle(serverLevel, hydra, 22.0, 45);
         BossPresentation.windup(serverLevel, hydra, BossStyle.HYDRA);
         BossFx.sound(serverLevel, BossFx.centre(hydra), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 2.4F, 0.7);

         for (int tick = 0; tick < 45; tick += 5) {
            ModmindsMod.queueServerWork(tick, () -> {
               if (hydra.isAlive()) {
                  BossFx.vortex(serverLevel, BossFx.ground(hydra), 22.0, 26, ParticleTypes.NAUTILUS, 0.12);
               }
            });
         }

         for (int tick = 0; tick < 140; tick += 4) {
            ModmindsMod.queueServerWork(45 + tick, () -> {
               if (hydra.isAlive()) {
                  floodRim(serverLevel, hydra);
               }
            });
         }

         for (int tick = 10; tick <= 140; tick += 10) {
            ModmindsMod.queueServerWork(45 + tick, () -> {
               if (hydra.isAlive()) {
                  floodPulse(serverLevel, hydra, new HashSet<>());
               }
            });
         }

         ModmindsMod.queueServerWork(185, () -> {
            if (hydra.isAlive()) {
               drain(serverLevel, hydra);
            }
         });
      }
   }

   private static void floodRim(ServerLevel world, HydraEntity hydra) {
      Vec3 at = BossFx.ground(hydra);
      BossFx.ring(world, at.add(0.0, 1.8, 0.0), 22.0, 40, ParticleTypes.BUBBLE, 2, 0.5);
      BossFx.ring(world, at.add(0.0, 0.4, 0.0), 22.0, 28, ParticleTypes.SPLASH, 2, 0.4);
      BossFx.colored(world, at.add(0.0, 1.2, 0.0), BossStyle.HYDRA.aura, 1.4F, 10, 13.2);
   }

   private static void floodPulse(ServerLevel world, HydraEntity hydra, Set<UUID> dipped) {
      Vec3 at = BossFx.ground(hydra);

      for (LivingEntity victim : world.getEntitiesOfClass(
         LivingEntity.class, new AABB(at, at).inflate(24.0), e -> e != hydra && e.isAlive() && !(e instanceof HydraEntity)
      )) {
         double dx = victim.getX() - at.x;
         double dz = victim.getZ() - at.z;
         double distance = Math.sqrt(dx * dx + dz * dz);
         if (!(distance > 22.0) && dipped.add(victim.getUUID())) {
            victim.hurt(world.damageSources().drown(), 5.0F);
            victim.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 30, 2, false, false));
            if (distance > 0.01) {
               Vec3 motion = victim.getDeltaMovement();
               victim.setDeltaMovement(motion.x - dx / distance * 0.06, motion.y, motion.z - dz / distance * 0.06);
               if (victim instanceof ServerPlayer player) {
                  player.needsSync = true;
               }
            }
         }
      }
   }

   private static void drain(ServerLevel world, HydraEntity hydra) {
      Vec3 at = BossFx.ground(hydra);
      BossFx.impactBurst(world, at, 22.0, BossStyle.HYDRA.flare);
      BossFx.sound(world, at, SoundEvents.GENERIC_SPLASH, SoundSource.HOSTILE, 3.0F, 0.6);
      hydra.setAnimation("idle");
   }
}
