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
import net.owleafstudio.modminds.entity.LimboTitanEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class TitanCorralProcedure {
   private static final int MARK_TICKS = 30;
   private static final int PILLARS = 6;
   private static final double START_RADIUS = 11.0;
   private static final double END_RADIUS = 2.2;
   private static final int SHRINK_STEPS = 6;
   private static final int SHRINK_EVERY = 10;
   private static final double PILLAR_HEIGHT = 4.5;
   private static final double PILLAR_TOUCH = 2.2;
   private static final float PILLAR_DAMAGE = 8.0F;
   private static final double CRUSH_RADIUS = 4.5;
   private static final float CRUSH_DAMAGE = 18.0F;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof LimboTitanEntity titan && world instanceof ServerLevel serverLevel) {
         ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         Vec3 prey = centreOf(serverLevel, titan);
         titan.setAnimation("meteorite_attack");
         BossFx.alert(serverLevel, BossFx.centre(titan), 96.0, "⚠ TITAN DEL LIMBO: CORRAL DE PIEDRA");
         BossPresentation.windup(serverLevel, titan, BossStyle.LIMBO_TITAN);
         BossFx.sound(serverLevel, prey, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.HOSTILE, 2.0F, 0.5);
         BossFx.marker(serverLevel, surface(serverLevel, prey), 11.0, 30);

         for (byte step = 0; step < 30; step += 5) {
            ModmindsMod.queueServerWork(step, () -> {
               BossFx.ring(serverLevel, surface(serverLevel, prey), 11.0, 24, BossFx.CRUSHED_STONE, 1, 0.3);
               BossFx.burst(serverLevel, surface(serverLevel, prey).add(0.0, 0.4, 0.0), ParticleTypes.ASH, 6, 7.699999999999999, 0.06);
            });
         }

         for (int step = 0; step < 6; step++) {
            int tick = step;
            ModmindsMod.queueServerWork(30 + step * 10, () -> {
               if (titan.isAlive()) {
                  ringStep(serverLevel, titan, prey, tick);
               }
            });
         }

         ModmindsMod.queueServerWork(102, () -> {
            if (titan.isAlive()) {
               crush(serverLevel, titan, prey);
            }
         });
         ModmindsMod.queueServerWork(130, () -> {
            if (titan.isAlive()) {
               titan.setAnimation("empty");
            }
         });
      }
   }

   private static Vec3 centreOf(ServerLevel world, LimboTitanEntity titan) {
      Entity target = titan.getTarget();
      return target != null ? target.position() : titan.position().add(titan.getLookAngle().scale(14.0));
   }

   private static void ringStep(ServerLevel world, LimboTitanEntity titan, Vec3 prey, int step) {
      double radius = 11.0 + -8.8 * step / 5.0;
      double spin = step * 0.2;
      Set<UUID> touched = new HashSet<>();

      for (int pillar = 0; pillar < 6; pillar++) {
         double angle = pillar * Math.PI * 2.0 / 6.0 + spin;
         erupt(world, titan, prey.x + Math.cos(angle) * radius, prey.z + Math.sin(angle) * radius, touched, step == 0);
      }

      BossFx.ring(world, surface(world, prey), radius, 20, BossFx.ALERT_DOT, 1, 0.1);
   }

   private static void erupt(ServerLevel world, LimboTitanEntity titan, double x, double z, Set<UUID> touched, boolean opening) {
      Vec3 at = surface(world, new Vec3(x, 0.0, z));

      for (double h = 0.0; h <= 4.5; h += 0.6) {
         world.sendParticles(BossFx.CRUSHED_STONE, x, at.y + h, z, 5, 0.5, 0.3, 0.5, 0.0);
      }

      world.sendParticles(BossFx.DUST_PILLAR, x, at.y + 0.5, z, 4, 0.4, 0.6, 0.4, 0.02);
      BossFx.colored(world, at.add(0.0, 1.2, 0.0), BossStyle.LIMBO_TITAN.aura, 1.8F, 6, 0.6);
      if (opening) {
         BossFx.shake(world, at, 34.0, 12, 0.4);
         BossFx.sound(world, at, SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 1.6F, 0.6);
      } else {
         BossFx.sound(world, at, SoundEvents.ANVIL_HIT, SoundSource.HOSTILE, 1.0F, 0.75 + touched.size() * 0.02, 0.08);
      }

      for (LivingEntity victim : world.getEntitiesOfClass(
         LivingEntity.class,
         new AABB(at, at).inflate(3.2),
         e -> e != titan && e.isAlive() && !(e instanceof LimboTitanEntity) && !titan.hasPassenger(e)
      )) {
         double distance = Math.hypot(victim.getX() - at.x, victim.getZ() - at.z);
         if (distance <= 2.2 + victim.getBbWidth() * 0.5 && touched.add(victim.getUUID())) {
            victim.hurt(world.damageSources().mobAttack(titan), 8.0F);
            victim.setDeltaMovement(0.0, 0.62, 0.0);
            victim.needsSync = true;
            BossFx.burst(world, BossFx.centre(victim), ParticleTypes.CRIT, 8, 0.4, 0.2);
         }
      }
   }

   private static void crush(ServerLevel world, LimboTitanEntity titan, Vec3 prey) {
      Vec3 at = surface(world, prey);
      BossFx.smash(world, at, 4.5, 1.9, BossStyle.LIMBO_TITAN.flare);
      BossFx.ring(world, at, 5.8500000000000005, 22, BossFx.CRUSHED_STONE, 1, 0.5);
      BossFx.sound(world, at, SoundEvents.MACE_SMASH_GROUND_HEAVY, SoundSource.HOSTILE, 2.4F, 0.65);
      BossFx.sound(world, at, SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 2.2F, 0.5);
      BossFx.shake(world, at, 56.0, 26, 0.8);

      for (LivingEntity victim : world.getEntitiesOfClass(
         LivingEntity.class, new AABB(at, at).inflate(6.5), e -> e != titan && e.isAlive() && !titan.hasPassenger(e)
      )) {
         double distance = Math.hypot(victim.getX() - at.x, victim.getZ() - at.z);
         if (distance <= 4.5 + victim.getBbWidth() * 0.5) {
            victim.hurt(world.damageSources().mobAttack(titan), 18.0F);
            double len = Math.max(distance, 0.4);
            victim.setDeltaMovement((victim.getX() - at.x) / len * 0.9, 1.25, (victim.getZ() - at.z) / len * 0.9);
            victim.needsSync = true;
            BossFx.burst(world, BossFx.centre(victim), ParticleTypes.CLOUD, 10, 0.5, 0.2);
         }
      }
   }

   private static Vec3 surface(ServerLevel world, Vec3 at) {
      return new Vec3(
         at.x, world.getHeight(Heightmap.Types.MOTION_BLOCKING, (int)Math.floor(at.x), (int)Math.floor(at.z)), at.z
      );
   }
}
