package net.owleafstudio.modminds.procedures;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.compat.ArrowCompat;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.entity.ShootReviilEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;
import net.owleafstudio.modminds.init.ModmindsModParticleTypes;
import net.owleafstudio.modminds.init.ModmindsModSounds;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilTormentaProcedure {
   private static final int WINDUP_TICKS = 34;
   private static final int DASH_TICKS = 40;
   private static final double MAX_RUN = 26.0;
   private static final double END_RADIUS = 10.0;
   private static final int DART_CYCLE = 4;
   private static final int DARTS_PER_CYCLE = 8;
   private static final float DART_SPEED = 5.0F;
   private static final float CONTACT_DAMAGE = 12.0F;
   private static final float END_DAMAGE = 28.0F;
   private static final int RECOVER_TICKS = 154;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel serverLevel) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         Vec3 from = reviil.position();
         Vec3 aim = lane(reviil);
         double run = Math.min(26.0, Math.hypot(aim.x - from.x, aim.z - from.z));
         double dx = aim.x - from.x;
         double dz = aim.z - from.z;
         double flat = Math.max(Math.hypot(dx, dz), 0.4);
         Vec3 dir = new Vec3(dx / flat, 0.0, dz / flat);
         Vec3 landing = new Vec3(from.x + dir.x * run, from.y, from.z + dir.z * run);
         double perTick = run / 40.0;
         reviil.setAnimation("emote.helicopter");
         reviil.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
         BossFx.alert(serverLevel, BossFx.centre(reviil), 96.0, "⚠ REVIIL: TORMENTA DE ACERO");
         BossFx.sound(serverLevel, BossFx.centre(reviil), SoundEvents.GOAT_PREPARE_RAM, SoundSource.HOSTILE, 2.0F, 0.6);
         BossFx.ring(serverLevel, BossFx.ground(reviil), 5.0, 16, (ParticleOptions)ModmindsModParticleTypes.GUN_SPARK.get(), 1, 0.4);
         BossFx.marker(serverLevel, landing, 10.0, 34);
         BossFx.shake(serverLevel, BossFx.centre(reviil), 48.0, 74, 0.35);

         for (byte shredded = 0; shredded < 34; shredded += 3) {
            ModmindsMod.queueServerWork(
               shredded,
               () -> {
                  if (reviil.isAlive()) {
                     Vec3 feet = BossFx.ground(reviil);

                     for (int i = 1; i <= 20; i++) {
                        double d = run * i / 20.0;
                        serverLevel.sendParticles(
                           BossFx.ALERT_DOT,
                           feet.x + dir.x * d,
                           feet.y,
                           feet.z + dir.z * d,
                           1,
                           0.1,
                           0.05,
                           0.1,
                           0.0
                        );
                     }
                  }
               }
            );
         }

         Set<UUID> shredded = new HashSet<>();

         for (int step = 0; step < 40; step++) {
            int tick = step;
            ModmindsMod.queueServerWork(34 + step, () -> {
               if (reviil.isAlive()) {
                  dashTick(serverLevel, reviil, dir, perTick, tick, shredded);
               }
            });
         }

         ModmindsMod.queueServerWork(74, () -> {
            if (reviil.isAlive()) {
               land(serverLevel, reviil, dir);
            }
         });
         ModmindsMod.queueServerWork(154, () -> {
            if (reviil.isAlive()) {
               reviil.releasePose();
            }
         });
      }
   }

   private static Vec3 lane(ReviilEntity reviil) {
      Entity target = reviil.getTarget();
      return target != null ? target.position() : reviil.getLookAngle().scale(18.0).add(reviil.position());
   }

   private static void dashTick(ServerLevel world, ReviilEntity reviil, Vec3 dir, double perTick, int step, Set<UUID> shredded) {
      Vec3 at = reviil.position().add(dir.x * perTick, 0.0, dir.z * perTick);
      reviil.setDeltaMovement(0.0, reviil.getDeltaMovement().y, 0.0);
      reviil.setPos(at.x, reviil.getY(), at.z);
      reviil.fallDistance = 0.0F;
      Vec3 hub = BossFx.centre(reviil);
      world.sendParticles(ParticleTypes.END_ROD, hub.x, hub.y, hub.z, 3, 0.7, 0.9, 0.7, 0.02);
      BossFx.burst(world, BossFx.ground(reviil), (ParticleOptions)ModmindsModParticleTypes.BLAST_DUST.get(), 4, 0.9, 0.1);
      if (step % 4 == 0) {
         shed(world, reviil, step);
      }

      for (Player victim : world.getEntitiesOfClass(Player.class, new AABB(hub, hub).inflate(3.2), e -> e.isAlive() && !reviil.hasPassenger(e))) {
         if (shredded.add(victim.getUUID())) {
            victim.hurt(world.damageSources().mobAttack(reviil), reviil.phaseDamage(12.0F));
            victim.setDeltaMovement(dir.x * 0.9, 0.75, dir.z * 0.9);
            victim.needsSync = true;
            reviil.sting(victim);
            world.sendParticles(ParticleTypes.CRIT, victim.getX(), victim.getY() + 1.0, victim.getZ(), 12, 0.4, 0.5, 0.4, 0.3);
         }
      }
   }

   private static void shed(ServerLevel world, ReviilEntity reviil, int step) {
      Vec3 hub = BossFx.centre(reviil);
      BossFx.ring(world, hub, 2.2, 8, (ParticleOptions)ModmindsModParticleTypes.GUN_SPARK.get(), 1, 0.3);
      BossFx.sound(world, hub, ModmindsModSounds.ARMA_CORTA.get(), SoundSource.HOSTILE, 1.1F, 0.8 + step % 12 * 0.02, 0.0);
      double phase = step * 0.55;

      for (int dart = 0; dart < 8; dart++) {
         double angle = phase + dart * Math.PI * 2.0 / 8.0;
         Vec3 direction = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
         Projectile arrow = dart(world, reviil);
         arrow.setPos(hub.x + direction.x * 1.4, hub.y, hub.z + direction.z * 1.4);
         arrow.shoot(direction.x, -0.12, direction.z, 5.0F, 0.0F);
         world.addFreshEntity(arrow);
      }
   }

   private static void land(ServerLevel world, ReviilEntity reviil, Vec3 dir) {
      Vec3 at = BossFx.ground(reviil);
      BossFx.smash(world, at, 10.0, 1.9, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get());
      BossFx.sound(world, at, (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 2.6F, 0.45);
      BossFx.ring(world, at, 12.0, 26, ParticleTypes.CRIT, 1, 0.5);

      for (Player victim : world.getEntitiesOfClass(Player.class, new AABB(at, at).inflate(12.0), e -> e.isAlive() && !reviil.hasPassenger(e))) {
         double dx = victim.getX() - at.x;
         double dz = victim.getZ() - at.z;
         double distance = Math.max(Math.hypot(dx, dz), 0.4);
         if (distance <= 10.0 + victim.getBbWidth() * 0.5) {
            float falloff = (float)Math.max(0.35, 1.0 - distance / 16.0);
            victim.hurt(world.damageSources().sonicBoom(reviil), reviil.phaseDamage(28.0F) * falloff);
            reviil.sting(victim);
            double forward = Math.max(0.0, (dx * dir.x + dz * dir.z) / distance);
            victim.setDeltaMovement(dir.x * (0.7 + 0.7 * forward), 0.9 * falloff + 0.3, dir.z * (0.7 + 0.7 * forward));
            victim.needsSync = true;
         }
      }
   }

   private static Projectile dart(Level world, ReviilEntity shooter) {
      AbstractArrow arrow = new ShootReviilEntity(ModmindsModEntities.SHOOT_REVIIL.get(), world);
      arrow.setOwner(shooter);
      arrow.setBaseDamage(3.0);
      ArrowCompat.setKnockback(arrow, 0);
      arrow.setSilent(true);
      arrow.setNoGravity(true);
      return arrow;
   }
}
