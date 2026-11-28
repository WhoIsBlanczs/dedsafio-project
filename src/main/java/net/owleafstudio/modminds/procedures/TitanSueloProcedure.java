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
import net.owleafstudio.modminds.entity.LimboTitanEntity;
import net.owleafstudio.modminds.init.ModmindsModSounds;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class TitanSueloProcedure {
   private static final int MARK_TICKS = 30;
   private static final int HOLD_TICKS = 180;
   private static final int RIM_TICKS = 3;
   private static final int PULSE_TICKS = 20;
   private static final double OPEN_RADIUS = 9.0;
   private static final double FULL_RADIUS = 19.0;
   private static final double STEP_HEIGHT = 2.6;
   private static final float DAMAGE = 7.0F;
   private static final int LIFT_TICKS = 18;
   private static final int SLOW_TICKS = 40;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof LimboTitanEntity titan && world instanceof ServerLevel serverLevel) {
         ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         titan.setAnimation("meteorite_attack");
         BossFx.alert(serverLevel, BossFx.centre(titan), 96.0, "⚠ TITAN DEL LIMBO: SUELO DEL LIMBO");
         BossPresentation.windup(serverLevel, titan, BossStyle.LIMBO_TITAN);
         BossFx.sound(serverLevel, BossFx.centre(titan), ModmindsModSounds.ENTRADOALLIMBO.get(), SoundSource.HOSTILE, 1.8F, 0.8);
         BossFx.warningCircle(serverLevel, titan, 9.0, 30);

         for (byte tick = 0; tick < 180; tick += 3) {
            int step = tick;
            ModmindsMod.queueServerWork(30 + tick, () -> {
               if (titan.isAlive()) {
                  limboFrame(serverLevel, titan, step);
               }
            });
         }

         for (int tick = 20; tick <= 180; tick += 20) {
            ModmindsMod.queueServerWork(30 + tick, () -> {
               if (titan.isAlive()) {
                  pulse(serverLevel, titan, new HashSet<>());
               }
            });
         }

         ModmindsMod.queueServerWork(210, () -> {
            if (titan.isAlive()) {
               close(serverLevel, titan);
            }
         });
      }
   }

   private static double radiusAt(int step) {
      double grow = Math.min(1.0, step / 120.0);
      return 9.0 + 10.0 * grow;
   }

   private static void limboFrame(ServerLevel world, LimboTitanEntity titan, int step) {
      Vec3 at = BossFx.ground(titan);
      double radius = radiusAt(step);
      BossFx.ring(world, at.add(0.0, 0.12, 0.0), radius, 34, ParticleTypes.PORTAL, 1, 0.15);
      BossFx.ring(world, at.add(0.0, 0.05, 0.0), radius * 0.82, 22, BossStyle.LIMBO_TITAN.mote, 1, 0.4);
      BossFx.colored(world, at.add(0.0, 0.8, 0.0), BossStyle.LIMBO_TITAN.aura, 1.2F, 8, radius * 0.7);
      if (step % 12 == 0) {
         BossFx.sound(world, at, SoundEvents.SHULKER_AMBIENT, SoundSource.HOSTILE, 1.1F, 0.6 + step % 36 * 0.004, 0.08);
      }
   }

   private static void pulse(ServerLevel world, LimboTitanEntity titan, Set<UUID> sunk) {
      Vec3 at = BossFx.ground(titan);
      double radius = radiusAt(180);
      BossFx.ring(world, at.add(0.0, 0.3, 0.0), radius, 26, ParticleTypes.SOUL_FIRE_FLAME, 1, 0.3);

      for (LivingEntity victim : world.getEntitiesOfClass(
         LivingEntity.class, new AABB(at, at).inflate(21.0), e -> e != titan && e.isAlive() && !titan.hasPassenger(e)
      )) {
         double distance = Math.hypot(victim.getX() - at.x, victim.getZ() - at.z);
         if (!(distance > radius + victim.getBbWidth() * 0.5) && sunk.add(victim.getUUID())) {
            victim.hurt(world.damageSources().magic(), 7.0F);
            victim.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 18, 0, false, false));
            victim.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1, false, false));
            if (victim instanceof ServerPlayer player) {
               player.needsSync = true;
            }

            BossFx.burst(world, BossFx.centre(victim), ParticleTypes.SCULK_SOUL, 6, 0.4, 0.1);
         }
      }
   }

   private static void close(ServerLevel world, LimboTitanEntity titan) {
      Vec3 at = BossFx.ground(titan);
      BossFx.impactBurst(world, at, 19.0, BossStyle.LIMBO_TITAN.mote);
      BossFx.ring(world, at, 13.299999999999999, 24, ParticleTypes.PORTAL, 1, 0.6);
      BossFx.sound(world, at, SoundEvents.BEACON_DEACTIVATE, SoundSource.HOSTILE, 2.0F, 0.55);
      titan.setAnimation("empty");
   }
}
