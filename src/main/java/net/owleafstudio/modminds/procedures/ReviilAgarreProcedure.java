package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.ReviilFx;
import net.owleafstudio.modminds.entity.ReviilEntity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilAgarreProcedure {
   private static final double GRAB_REACH = 11.0;
   private static final int WINDUP_TICKS = 26;
   private static final int LIFT_TICKS = 28;
   private static final int STARE_TICKS = 40;
   private static final int TWIST_TICKS = 22;
   private static final int RISE_TICKS = 36;
   private static final double LAUNCH_SPEED = 1.9;
   private static final double GRAB_DAMAGE = 24.0;
   private static final double SLAM_DAMAGE = 32.0;
   private static final double SLAM_RADIUS = 7.0;

   public static void execute(LevelAccessor world, Entity entity) {
      if (entity instanceof ReviilEntity reviil && world instanceof ServerLevel level && reviil.isAlive()) {
         ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 0.0;
         ModmindsModVariables.MapVariables.get(world).syncData(world);
         LivingEntity victim = reviil.getTarget();
         reviil.setAnimation("emote.attack_left");
         reviil.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
         if (victim != null && victim.isAlive() && !victim.isSpectator()) {
            ReviilFx.grabWindup(level, reviil, victim.position());
            if (reviil.distanceTo(victim) > 11.0) {
               Vec3 toward = victim.position().subtract(reviil.position()).normalize();
               ModmindsMod.queueServerWork(13, () -> {
                  if (reviil.isAlive()) {
                     reviil.setDeltaMovement(toward.x * 1.4, 0.25, toward.z * 1.4);
                  }
               });
            }

            ModmindsMod.queueServerWork(26, () -> {
               if (reviil.isAlive()) {
                  if (!victim.isRemoved() && victim.isAlive() && !victim.isSpectator() && !(reviil.distanceTo(victim) > 11.0)) {
                     hold(level, reviil, victim);
                  } else {
                     BossFx.alert(level, victim.position(), 64.0, "REVIIL FALLA EL AGARRE");
                     BossFx.burst(level, BossFx.mouth(reviil, 2.0, reviil.getBbHeight() * 0.8), ParticleTypes.POOF, 8, 0.4, 0.1);
                     reviil.releasePose();
                  }
               }
            });
         } else {
            BossFx.alert(level, BossFx.centre(reviil), 96.0, "REVIIL BUSCA A ALGUIEN Y NO ENCUENTRA");
            ModmindsMod.queueServerWork(26, reviil::releasePose);
         }
      }
   }

   private static void hold(ServerLevel level, ReviilEntity reviil, LivingEntity victim) {
      victim.hurt(level.damageSources().mobAttack(reviil), reviil.phaseDamage(24.0F));
      reviil.sting(victim);
      victim.setDeltaMovement(Vec3.ZERO);
      victim.startRiding(reviil, true, true);
      reviil.setAnimation("emote.grab.lift");

      for (int step = 0; step <= 5; step++) {
         int at = step;
         ModmindsMod.queueServerWork(step * 5, () -> {
            if (isHeld(reviil, victim)) {
               victim.fallDistance = 0.0F;
               reviil.setGrabHold(1, at * 20);
               ReviilFx.grabLift(level, reviil, victim, at);
            }
         });
      }

      ModmindsMod.queueServerWork(28, () -> stare(level, reviil, victim));
   }

   private static void stare(ServerLevel level, ReviilEntity reviil, LivingEntity victim) {
      if (stillHeld(level, reviil, victim)) {
         reviil.setAnimation("emote.grab.stare");
         reviil.setGrabHold(2, 100);
         ReviilFx.grabStare(level, reviil, victim);
         ModmindsMod.queueServerWork(40, () -> twist(level, reviil, victim));
      }
   }

   private static void twist(ServerLevel level, ReviilEntity reviil, LivingEntity victim) {
      if (!isHeld(reviil, victim)) {
         nobodyInHisHand(level, reviil, victim);
      } else {
         reviil.setAnimation("emote.grab.twist");
         reviil.setGrabHold(3, 0);
         ReviilFx.grabTwist(level, reviil, victim);

         for (int step = 1; step <= 4; step++) {
            int at = step;
            ModmindsMod.queueServerWork(step * 5, () -> {
               if (isHeld(reviil, victim)) {
                  reviil.setGrabHold(3, at * 25);
               }
            });
         }

         ModmindsMod.queueServerWork(22, () -> throwUp(level, reviil, victim));
      }
   }

   private static void throwUp(ServerLevel level, ReviilEntity reviil, LivingEntity victim) {
      if (!isHeld(reviil, victim)) {
         nobodyInHisHand(level, reviil, victim);
      } else {
         victim.stopRiding();
         reviil.setGrabHold(4, 100);
         reviil.setAnimation("emote.grab.throw");
         victim.setDeltaMovement(0.0, 1.9, 0.0);
         victim.fallDistance = 0.0F;
         ReviilFx.grabLaunch(level, reviil, victim.position());
         ModmindsMod.queueServerWork(36, () -> drop(level, reviil, victim));
      }
   }

   private static boolean isHeld(ReviilEntity reviil, LivingEntity victim) {
      return victim.isPassenger() && !victim.isRemoved() && victim.isAlive() && reviil.isAlive();
   }

   private static boolean stillHeld(ServerLevel level, ReviilEntity reviil, LivingEntity victim) {
      if (isHeld(reviil, victim)) {
         return true;
      } else {
         nobodyInHisHand(level, reviil, victim);
         return false;
      }
   }

   private static void nobodyInHisHand(ServerLevel level, ReviilEntity reviil, LivingEntity victim) {
      if (reviil.isAlive()) {
         BossFx.alert(level, victim.position(), 64.0, "REVIIL: NADIE EN SU PUÑO");
         reviil.releasePose();
      }
   }

   private static void drop(ServerLevel level, ReviilEntity reviil, LivingEntity victim) {
      if (!victim.isRemoved() && !victim.isSpectator() && reviil.isAlive()) {
         Vec3 from = victim.position();
         Vec3 floor = BossFx.groundAt(level, from.x, from.z);
         victim.setDeltaMovement(0.0, -3.4, 0.0);
         BossFx.beam(level, from, floor, ParticleTypes.CRIT, 14, 0.2);
         boolean[] landed = new boolean[1];

         for (int tick = 2; tick <= 70; tick += 2) {
            ModmindsMod.queueServerWork(tick, () -> {
               if (!landed[0] && !victim.isRemoved()) {
                  victim.fallDistance = 0.0F;
                  victim.setDeltaMovement(victim.getDeltaMovement().x, -3.4, victim.getDeltaMovement().z);
                  if (victim.getY() <= floor.y + 0.4) {
                     landed[0] = true;
                     slam(level, reviil, victim, floor);
                  }
               }
            });
         }

         ModmindsMod.queueServerWork(74, () -> {
            if (!landed[0]) {
               landed[0] = true;
               slam(level, reviil, victim, floor);
            }
         });
      } else {
         reviil.releasePose();
      }
   }

   private static void slam(ServerLevel level, ReviilEntity reviil, LivingEntity victim, Vec3 landing) {
      ReviilFx.waterDrop(level, landing, 7.0);
      if (victim.isAlive()) {
         victim.hurt(level.damageSources().mobAttack(reviil), reviil.phaseDamage(32.0F));
         reviil.sting(victim);
         victim.setDeltaMovement(victim.getDeltaMovement().multiply(0.2, 0.35, 0.2));
      }

      for (Player bystander : level.getEntitiesOfClass(
         Player.class, new AABB(landing, landing).inflate(7.0), e -> e != victim && e.isAlive() && !e.isSpectator()
      )) {
         double distance = bystander.position().distanceTo(landing);
         if (distance <= 7.0) {
            bystander.hurt(level.damageSources().mobAttack(reviil), reviil.phaseDamage((float)(10.0 * (1.0 - distance / 7.0) + 2.0)));
            reviil.sting(bystander);
         }
      }

      ModmindsMod.queueServerWork(30, () -> {
         if (reviil.isAlive()) {
            reviil.releasePose();
         }
      });
   }
}
