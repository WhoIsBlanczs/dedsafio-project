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
import net.owleafstudio.modminds.boss.ReviilFx;
import net.owleafstudio.modminds.compat.ArrowCompat;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.entity.ShootReviilEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;
import net.owleafstudio.modminds.init.ModmindsModParticleTypes;
import net.owleafstudio.modminds.network.ModmindsModVariables;
import org.joml.Vector3f;

public class ReviilCroupierProcedure {
   private static final int SPIN_FRAMES = 20;
   private static final int SPIN_TICKS = 4;
   private static final int LAND_TICKS = 88;
   private static final int RESOLVE_TICKS = 110;
   private static final int RECOVER_TICKS = 300;
   private static final int FINISH_POSE_TICKS = 176;
   private static final double WELL_RADIUS = 14.0;
   private static final double RAY_RANGE = 42.0;
   private static final int RAY_SWEEP_TICKS = 60;
   private static final int WALL_WAVES = 6;
   private static final int WALL_DARTS = 14;
   private static final double FORTUNE_RADIUS = 12.0;
   private static final int FORTUNE_TICKS = 120;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel serverLevel) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         reviil.setAnimation("emote.roulette.hability");
         reviil.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
         BossFx.alert(serverLevel, BossFx.centre(reviil), 96.0, "⚠ REVIIL: LA CASA SIEMPRE GANA");
         BossFx.sound(serverLevel, BossFx.centre(reviil), SoundEvents.PLAYER_LEVELUP, SoundSource.HOSTILE, 1.4F, 0.5);
         BossFx.vortex(serverLevel, BossFx.centre(reviil), 8.0, 18, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get(), 0.05);

         for (int frame = 0; frame < 20; frame++) {
            int step = frame;
            ModmindsMod.queueServerWork(frame * 4, () -> {
               if (reviil.isAlive()) {
                  spinFrame(serverLevel, reviil, step);
               }
            });
         }

         int outcome = serverLevel.getRandom().nextInt(4);
         ModmindsMod.queueServerWork(88, () -> {
            if (reviil.isAlive()) {
               BossFx.sound(serverLevel, BossFx.centre(reviil), (SoundEvent)SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE.value(), SoundSource.HOSTILE, 2.0F, 0.6);
               BossFx.impactBurst(serverLevel, BossFx.mouth(reviil, 2.2, 3.4), 3.0, ParticleTypes.TOTEM_OF_UNDYING);
            }
         });
         ModmindsMod.queueServerWork(110, () -> {
            if (reviil.isAlive()) {
               reviil.setAnimation("emote.roulette.shoot.hability");
               resolve(serverLevel, reviil, outcome);
            }
         });
         ModmindsMod.queueServerWork(176, () -> {
            if (reviil.isAlive()) {
               reviil.setAnimation("attack_emote_2");
            }
         });
         ModmindsMod.queueServerWork(300, () -> {
            if (reviil.isAlive()) {
               reviil.releasePose();
            }
         });
      }
   }

   private static void spinFrame(ServerLevel world, ReviilEntity reviil, int step) {
      Vec3 hub = BossFx.mouth(reviil, 2.2, 3.4);
      double decay = 1.0 - step / 20.0;
      double ball = step * (0.22 + 0.95 * decay);
      world.sendParticles(
         ParticleTypes.END_ROD,
         hub.x + Math.cos(ball) * 2.4,
         hub.y + Math.sin(ball) * 0.5,
         hub.z + Math.sin(ball) * 2.4,
         2,
         0.04,
         0.04,
         0.04,
         0.0
      );
      BossFx.burst(world, hub, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get(), 2, 2.2, 0.02);
      BossFx.sound(world, hub, (SoundEvent)SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.HOSTILE, 0.9F, 0.8 + decay * 1.4);
   }

   private static void resolve(ServerLevel world, ReviilEntity reviil, int outcome) {
      switch (outcome) {
         case 0:
            well(world, reviil);
            break;
         case 1:
            rays(world, reviil);
            break;
         case 2:
            wall(world, reviil);
            break;
         default:
            fortune(world, reviil);
      }
   }

   private static void well(ServerLevel world, ReviilEntity reviil) {
      BossFx.alert(world, BossFx.centre(reviil), 96.0, "⚠ CERO: EL POZO DEL VACÍO");
      BossFx.sound(world, BossFx.centre(reviil), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 2.2F, 0.7);
      Vec3 centre = BossFx.centre(reviil);

      for (int pull = 0; pull < 10; pull++) {
         int step = pull;
         ModmindsMod.queueServerWork(
            pull * 4,
            () -> {
               if (reviil.isAlive()) {
                  Vec3 hub = BossFx.centre(reviil);
                  BossFx.vortex(world, hub, 20.0 - step * 1.4, 12, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get(), 0.35);

                  for (Player victim : world.getEntitiesOfClass(
                     Player.class, new AABB(hub, hub).inflate(22.0), e -> e.isAlive() && !reviil.hasPassenger(e)
                  )) {
                     Vec3 to = BossFx.centre(victim).subtract(hub);
                     double len = Math.max(to.length(), 1.0);
                     victim.setDeltaMovement(to.x / len * 0.55, to.y / len * 0.3 + 0.05, to.z / len * 0.55);
                     victim.needsSync = true;
                  }
               }
            }
         );
      }

      ModmindsMod.queueServerWork(
         48,
         () -> {
            if (reviil.isAlive()) {
               Vec3 at = BossFx.ground(reviil);
               BossFx.smash(world, at, 14.0, 1.6, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get());

               for (Player victim : world.getEntitiesOfClass(
                  Player.class, new AABB(at, at).inflate(16.0), e -> e.isAlive() && !reviil.hasPassenger(e)
               )) {
                  double dx = victim.getX() - at.x;
                  double dz = victim.getZ() - at.z;
                  double distance = Math.max(Math.sqrt(dx * dx + dz * dz), 0.4);
                  if (distance <= 14.0 + victim.getBbWidth() * 0.5) {
                     victim.hurt(world.damageSources().sonicBoom(reviil), reviil.phaseDamage(24.0F));
                     reviil.sting(victim);
                     victim.setDeltaMovement(dx / distance * 1.3, 0.95, dz / distance * 1.3);
                     victim.needsSync = true;
                  }
               }
            }
         }
      );
   }

   private static void rays(ServerLevel world, ReviilEntity reviil) {
      BossFx.alert(world, BossFx.centre(reviil), 96.0, "⚠ ROJO: SEIS RAYOS DEL VACÍO");
      BossFx.sound(world, BossFx.centre(reviil), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 1.8F, 1.1);
      BossFx.warningCircle(world, reviil, 23.1, 20);
      Set<UUID> burned = new HashSet<>();

      for (int tick = 0; tick < 60; tick += 2) {
         int step = tick;
         ModmindsMod.queueServerWork(20 + tick, () -> {
            if (reviil.isAlive()) {
               rayFrame(world, reviil, step, burned);
            }
         });
      }
   }

   private static void rayFrame(ServerLevel world, ReviilEntity reviil, int step, Set<UUID> burned) {
      Vec3 origin = BossFx.centre(reviil);
      double sweep = step / 60.0 * Math.PI / 3.0;

      for (int ray = 0; ray < 6; ray++) {
         double angle = ray * Math.PI / 3.0 + sweep;
         Vec3 end = new Vec3(reviil.getX() + Math.cos(angle) * 42.0, origin.y, reviil.getZ() + Math.sin(angle) * 42.0);
         ReviilFx.voidBeamFrame(world, origin, end);

         for (Player victim : world.getEntitiesOfClass(
            Player.class, new AABB(origin, end).inflate(2.5), e -> e.isAlive() && !reviil.hasPassenger(e)
         )) {
            if (burned.add(victim.getUUID()) && BossFx.distanceToSegment(BossFx.centre(victim), origin, end) < victim.getBbWidth() * 0.5 + 1.8) {
               victim.hurt(world.damageSources().sonicBoom(reviil), reviil.phaseDamage(11.0F));
               victim.igniteForSeconds(4.0F);
               reviil.sting(victim);
            }
         }
      }
   }

   private static void wall(ServerLevel world, ReviilEntity reviil) {
      BossFx.alert(world, BossFx.centre(reviil), 96.0, "⚠ NEGRO: MURO DE DARDOS");
      BossFx.sound(world, BossFx.centre(reviil), SoundEvents.DISPENSER_LAUNCH, SoundSource.HOSTILE, 2.4F, 0.5);

      for (int wave = 0; wave < 6; wave++) {
         double spoke = wave * Math.PI * 2.0 / 6.0;
         boolean opening = wave == 0;
         ModmindsMod.queueServerWork(wave * 22, () -> {
            if (reviil.isAlive()) {
               fireSpoke(world, reviil, spoke, opening);
            }
         });
      }
   }

   private static void fireSpoke(ServerLevel world, ReviilEntity reviil, double spoke, boolean opening) {
      Vec3 muzzle = BossFx.mouth(reviil, 2.5, 3.0).add(Math.cos(spoke) * 3.0, 0.0, Math.sin(spoke) * 3.0);
      BossFx.ring(world, muzzle, 2.4, 10, (ParticleOptions)ModmindsModParticleTypes.GUN_SPARK.get(), 1, 0.25);
      BossFx.sound(world, muzzle, SoundEvents.DISPENSER_LAUNCH, SoundSource.HOSTILE, 1.8F, 0.7);
      if (opening) {
         BossFx.shake(world, BossFx.centre(reviil), 44.0, 16, 0.55);
      }

      for (int dart = 0; dart < 14; dart++) {
         double angle = spoke - (Math.PI / 3) + dart * Math.PI * 2.0 / 14.0;
         Vec3 direction = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
         Projectile arrow = dart(world, reviil);
         arrow.setPos(muzzle.x + direction.x * 1.5, muzzle.y, muzzle.z + direction.z * 1.5);
         arrow.shoot(direction.x, 0.0, direction.z, 4.6F, 0.0F);
         world.addFreshEntity(arrow);
      }
   }

   private static void fortune(ServerLevel world, ReviilEntity reviil) {
      BossFx.alert(world, BossFx.centre(reviil), 96.0, "⚠ PRENDA: EL BOTE");
      reviil.heal(reviil.getMaxHealth() * 0.15F);
      BossFx.sound(world, BossFx.centre(reviil), SoundEvents.PLAYER_LEVELUP, SoundSource.HOSTILE, 2.0F, 0.8);
      BossFx.smash(world, BossFx.ground(reviil), 12.0, 1.1, ParticleTypes.TOTEM_OF_UNDYING);
      Set<UUID> paid = new HashSet<>();

      for (int tick = 0; tick < 120; tick += 6) {
         ModmindsMod.queueServerWork(
            tick,
            () -> {
               if (reviil.isAlive()) {
                  Vec3 at = BossFx.centre(reviil);
                  BossFx.ring(world, at, 12.0, 30, ParticleTypes.END_ROD, 1, 0.2);
                  BossFx.ring(world, BossFx.ground(reviil), 12.0, 18, ParticleTypes.TOTEM_OF_UNDYING, 1, 0.3);
                  BossFx.colored(world, at, new Vector3f(1.0F, 0.82F, 0.25F), 1.6F, 6, 3.0);

                  for (Player victim : world.getEntitiesOfClass(
                     Player.class, new AABB(at, at).inflate(13.0), e -> e.isAlive() && !reviil.hasPassenger(e)
                  )) {
                     if (victim.distanceTo(reviil) <= 12.0 && paid.add(victim.getUUID())) {
                        victim.hurt(world.damageSources().magic(), reviil.phaseDamage(9.0F));
                        Vec3 away = victim.position().subtract(reviil.position());
                        double len = Math.max(away.length(), 0.4);
                        victim.setDeltaMovement(away.x / len * 0.9, 0.7, away.z / len * 0.9);
                        victim.needsSync = true;
                     }
                  }
               }
            }
         );
      }
   }

   private static Projectile dart(Level world, ReviilEntity shooter) {
      AbstractArrow arrow = new ShootReviilEntity(ModmindsModEntities.SHOOT_REVIIL.get(), world);
      arrow.setOwner(shooter);
      arrow.setBaseDamage(4.0);
      ArrowCompat.setKnockback(arrow, 1);
      arrow.setSilent(true);
      arrow.setNoGravity(true);
      return arrow;
   }
}
