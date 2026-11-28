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
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.entity.HydraEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class HydraMareaProcedure {
   private static final int WAVES = 3;
   private static final int LEAD_TICKS = 40;
   private static final int STEPS = 14;
   private static final int STEP_TICKS = 2;
   private static final int STAGGER_TICKS = 44;
   private static final double START = 4.0;
   private static final double REACH = 38.0;
   private static final double BAND = 3.0;
   private static final float DAMAGE = 12.0F;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof HydraEntity hydra && world instanceof ServerLevel serverLevel) {
         ModmindsModVariables.MapVariables.get(world).HydraAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         BossFx.alert(serverLevel, BossFx.centre(hydra), 96.0, "⚠ HYDRA: MAREA ABISAL");
         BossFx.warningCircle(serverLevel, hydra, 38.0, 40);
         BossPresentation.windup(serverLevel, hydra, BossStyle.HYDRA);
         BossFx.sound(serverLevel, BossFx.centre(hydra), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 2.4F, 0.8);

         for (int wave = 0; wave < 3; wave++) {
            int at = 40 + wave * 44;
            boolean opening = wave == 0;
            ModmindsMod.queueServerWork(at, () -> {
               if (hydra.isAlive()) {
                  runCrest(serverLevel, hydra, opening);
               }
            });
         }
      }
   }

   private static void runCrest(ServerLevel world, HydraEntity hydra, boolean opening) {
      Vec3 at = BossFx.ground(hydra);
      if (opening) {
         BossFx.smash(world, at, 9.0, 0.5, BossStyle.HYDRA.flare);
      } else {
         BossFx.impactBurst(world, at, 6.0, BossStyle.HYDRA.flare);
      }

      BossFx.sound(world, at, SoundEvents.GENERIC_SPLASH, SoundSource.HOSTILE, 3.0F, 0.6);
      Set<UUID> swept = new HashSet<>();

      for (int step = 0; step < 14; step++) {
         int frame = step;
         ModmindsMod.queueServerWork(step * 2, () -> crestFrame(world, hydra, frame, swept));
      }
   }

   private static void crestFrame(ServerLevel world, HydraEntity hydra, int frame, Set<UUID> swept) {
      Vec3 at = BossFx.ground(hydra);
      double radius = 4.0 + 34.0 * (frame + 1) / 14.0;
      BossFx.ring(world, at.add(0.0, 1.4, 0.0), radius, 34, ParticleTypes.BUBBLE, 2, 0.35);
      BossFx.ring(world, at.add(0.0, 0.4, 0.0), radius, 22, ParticleTypes.SPLASH, 2, 0.4);
      BossFx.ring(world, at.add(0.0, 0.8, 0.0), radius, 12, BossStyle.HYDRA.spark(), 1, 0.3);
      double inner = radius - 3.0;
      double outer = radius + 2.0999999999999996;

      for (LivingEntity victim : world.getEntitiesOfClass(
         LivingEntity.class, new AABB(at, at).inflate(outer + 2.0), e -> e != hydra && e.isAlive() && !(e instanceof HydraEntity)
      )) {
         double dx = victim.getX() - at.x;
         double dz = victim.getZ() - at.z;
         double distance = Math.sqrt(dx * dx + dz * dz);
         if (distance >= inner && distance <= outer && swept.add(victim.getUUID())) {
            victim.hurt(world.damageSources().mobAttack(hydra), 12.0F);
            if (distance > 0.01) {
               victim.setDeltaMovement(dx / distance * 0.75, 0.6, dz / distance * 0.75);
            }
         }
      }
   }
}
