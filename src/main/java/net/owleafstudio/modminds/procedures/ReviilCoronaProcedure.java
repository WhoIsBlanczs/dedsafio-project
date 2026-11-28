package net.owleafstudio.modminds.procedures;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
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
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.init.ModmindsModParticleTypes;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilCoronaProcedure {
   private static final int CROWN_TICKS = 90;
   private static final int CHARGE_TICKS = 20;
   private static final int LAUNCH_TICKS = 110;
   private static final int MINES = 5;
   private static final double ORBIT_RADIUS = 5.5;
   private static final double ORBIT_UP = 3.4;
   private static final double MINE_SPEED = 0.62;
   private static final int MINE_LIFE = 130;
   private static final double LEASH = 30.0;
   private static final double BLAST_RADIUS = 4.8;
   private static final float BLAST_DAMAGE = 20.0F;
   private static final int RECOVER_TICKS = 280;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel serverLevel) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         reviil.setAnimation("attack_emote_2");
         reviil.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
         BossFx.alert(serverLevel, BossFx.centre(reviil), 96.0, "⚠ REVIIL: CORONA DEL VACÍO");
         BossFx.sound(serverLevel, BossFx.centre(reviil), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.8F, 0.7);
         BossFx.vortex(serverLevel, BossFx.centre(reviil).add(0.0, 3.4, 0.0), 11.0, 20, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get(), 0.08);

         for (byte mine = 0; mine < 90; mine += 2) {
            int step = mine;
            ModmindsMod.queueServerWork(mine, () -> {
               if (reviil.isAlive()) {
                  crownFrame(serverLevel, reviil, step);
               }
            });
         }

         Vec3[] mine = new Vec3[5];
         Vec3[] velocity = new Vec3[5];
         UUID[] lock = new UUID[5];
         List<Set<UUID>> caught = new ArrayList<>();

         for (int i = 0; i < 5; i++) {
            caught.add(new HashSet<>());
         }

         ModmindsMod.queueServerWork(90, () -> {
            if (reviil.isAlive()) {
               BossFx.alert(serverLevel, BossFx.centre(reviil), 96.0, "⚠ LA CORONA SE ROMPE");
               BossFx.sound(serverLevel, BossFx.centre(reviil), SoundEvents.BEACON_DEACTIVATE, SoundSource.HOSTILE, 2.0F, 0.6);
               BossFx.shake(serverLevel, BossFx.centre(reviil), 52.0, 24, 0.45);
               List<Player> prey = prey(serverLevel, reviil);

               for (int i = 0; i < 5; i++) {
                  double angle = i * Math.PI * 2.0 / 5.0 + 9.9;
                  mine[i] = orbitPoint(reviil, angle);
                  velocity[i] = new Vec3(Math.cos(angle) * 0.3, 0.1, Math.sin(angle) * 0.3);
                  Player marked = prey.isEmpty() ? null : prey.get(i % prey.size());
                  lock[i] = marked == null ? null : marked.getUUID();
               }
            }
         });

         for (int tick = 0; tick < 20; tick += 2) {
            ModmindsMod.queueServerWork(90 + tick, () -> {
               if (reviil.isAlive()) {
                  for (int i = 0; i < 5; i++) {
                     if (mine[i] != null) {
                        BossFx.burst(serverLevel, mine[i], (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get(), 4, 0.5, 0.16);
                        if (lock[i] != null) {
                           serverLevel.sendParticles(net.minecraft.core.particles.PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1.0F), mine[i].x, mine[i].y, mine[i].z, 1, 0.1, 0.1, 0.1, 0.0);
                        }
                     }
                  }
               }
            });
         }

         ModmindsMod.queueServerWork(110, () -> {
            if (reviil.isAlive()) {
               reviil.setAnimation("emote.roulette.shoot.hability");
            }
         });
         ModmindsMod.queueServerWork(176, () -> {
            if (reviil.isAlive()) {
               reviil.setAnimation("extended_pose.standing.still2");
            }
         });

         for (int step = 0; step < 130; step++) {
            int tick = step;
            ModmindsMod.queueServerWork(110 + step, () -> {
               if (reviil.isAlive()) {
                  for (int i = 0; i < 5; i++) {
                     if (mine[i] != null) {
                        flyFrame(serverLevel, reviil, i, tick, mine, velocity, lock, caught.get(i));
                     }
                  }
               }
            });
         }

         ModmindsMod.queueServerWork(280, () -> {
            if (reviil.isAlive()) {
               reviil.releasePose();
            }
         });
      }
   }

   private static void crownFrame(ServerLevel world, ReviilEntity reviil, int step) {
      double spin = step * 0.11;
      double charge = step / 90.0;

      for (int i = 0; i < 5; i++) {
         Vec3 at = orbitPoint(reviil, i * Math.PI * 2.0 / 5.0 + spin);
         world.sendParticles(
            ModmindsModParticleTypes.VOID_SIGIL.get(), at.x, at.y, at.z, 2 + (int)(charge * 3.0), 0.18, 0.18, 0.18, 0.0
         );
         if (charge > 0.6) {
            world.sendParticles(ParticleTypes.END_ROD, at.x, at.y, at.z, 1, 0.1, 0.1, 0.1, 0.0);
         }
      }

      BossFx.ring(world, orbitPlane(reviil), 5.5, 20, BossFx.ALERT_CORE, 1, 0.12 + charge * 0.2);
      if (step % 10 == 0) {
         BossFx.sound(world, orbitPlane(reviil), (SoundEvent)SoundEvents.NOTE_BLOCK_HARP.value(), SoundSource.HOSTILE, 0.8F, 0.8 + charge * 0.8);
      }
   }

   private static Vec3 orbitPlane(ReviilEntity reviil) {
      return BossFx.centre(reviil).add(0.0, 3.4, 0.0);
   }

   private static Vec3 orbitPoint(ReviilEntity reviil, double angle) {
      Vec3 plane = orbitPlane(reviil);
      return new Vec3(plane.x + Math.cos(angle) * 5.5, plane.y + Math.sin(angle * 2.0) * 0.6, plane.z + Math.sin(angle) * 5.5);
   }

   private static List<Player> prey(ServerLevel world, ReviilEntity reviil) {
      List<Player> found = new ArrayList<>(
         world.getEntitiesOfClass(
            Player.class, new AABB(reviil.position(), reviil.position()).inflate(36.0), e -> e.isAlive() && !reviil.hasPassenger(e)
         )
      );
      found.sort(Comparator.comparingDouble(reviil::distanceToSqr));
      return found;
   }

   private static void flyFrame(
      ServerLevel world, ReviilEntity reviil, int index, int tick, Vec3[] mine, Vec3[] velocity, UUID[] lock, Set<UUID> alreadyCaught
   ) {
      Vec3 at = mine[index];
      Vec3 vel = velocity[index];
      Entity marked = lock[index] == null ? null : world.getEntity(lock[index]);
      if (marked != null && !marked.isAlive()) {
         marked = null;
      }

      if (marked != null && marked.distanceTo(reviil) < 30.0) {
         Vec3 want = BossFx.centre(marked).subtract(at).normalize().scale(0.62);
         vel = vel.add(want.subtract(vel).scale(0.14));
      } else {
         lock[index] = null;
         vel = vel.scale(0.99);
      }

      Vec3 next = at.add(vel);
      BossFx.beam(world, at, next, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get(), 3, 0.1);
      world.sendParticles(ParticleTypes.PORTAL, next.x, next.y, next.z, 2, 0.2, 0.2, 0.2, 0.1);
      mine[index] = next;
      velocity[index] = vel;
      boolean detonate = tick >= 129 || next.distanceToSqr(reviil.position()) > 900.0;
      if (!detonate) {
         for (Player victim : world.getEntitiesOfClass(
            Player.class, new AABB(next, next).inflate(4.8), e -> e.isAlive() && !reviil.hasPassenger(e)
         )) {
            if (victim.position().distanceToSqr(next) <= 4.840000000000001) {
               detonate = true;
               break;
            }
         }
      }

      if (detonate) {
         mine[index] = null;
         blast(world, reviil, next, alreadyCaught);
      }
   }

   private static void blast(ServerLevel world, ReviilEntity reviil, Vec3 at, Set<UUID> alreadyCaught) {
      BossFx.smashSmall(world, at, 4.8, 1.3, (ParticleOptions)ModmindsModParticleTypes.VOID_SIGIL.get());
      BossFx.burst(world, at.add(0.0, 1.1, 0.0), (ParticleOptions)ModmindsModParticleTypes.METEOR_FLASH.get(), 3, 2.4, 0.05);
      BossFx.burst(world, at.add(0.0, 0.4, 0.0), (ParticleOptions)ModmindsModParticleTypes.PULSE_EM.get(), 2, 1.44, 0.07);
      BossFx.impactBurst(world, at, 4.8, ParticleTypes.SOUL_FIRE_FLAME);
      BossFx.sound(world, at, SoundEvents.SHULKER_BULLET_HIT, SoundSource.HOSTILE, 1.6F, 0.6);

      for (Player victim : world.getEntitiesOfClass(Player.class, new AABB(at, at).inflate(5.8), e -> e.isAlive() && !reviil.hasPassenger(e))) {
         double distance = victim.position().distanceTo(at);
         if (distance <= 4.8 + victim.getBbWidth() * 0.5 && alreadyCaught.add(victim.getUUID())) {
            float falloff = (float)Math.max(0.4, 1.0 - distance / 7.199999999999999);
            victim.hurt(world.damageSources().sonicBoom(reviil), reviil.phaseDamage(20.0F) * falloff);
            reviil.sting(victim);
            Vec3 away = victim.position().subtract(at);
            double len = Math.max(away.length(), 0.4);
            victim.setDeltaMovement(away.x / len * 0.8, 0.7 * falloff + 0.25, away.z / len * 0.8);
            victim.needsSync = true;
            victim.igniteForSeconds(3.0F);
         }
      }
   }
}
