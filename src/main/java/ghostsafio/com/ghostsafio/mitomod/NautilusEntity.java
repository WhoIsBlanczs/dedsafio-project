package com.ghostsafio.mitomod;

import java.util.EnumSet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;

public class NautilusEntity extends Monster {
   public static final int LEVITATION_MIN_TICKS = 120;
   public static final int LEVITATION_MAX_TICKS = 140;
   public static final float HIT_DAMAGE_ARMORED = 2.0F;
   public static final float HIT_DAMAGE_UNARMORED = 6.0F;
   public static final float HIT_DAMAGE = 1.0F;
   public static final int ATTACK_COOLDOWN_TICKS = 20;
   public static final double CHASE_SPEED = 0.25;

   public NautilusEntity(EntityType<? extends NautilusEntity> type, Level level) {
      super(type, level);
      this.xpReward = 10;
      this.setNoGravity(true);
   }

   public static Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 16.0)
         .add(Attributes.ATTACK_DAMAGE, 1.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.FOLLOW_RANGE, 40.0);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new NautilusEntity.HuntGoal(this));
      this.goalSelector.addGoal(2, new NautilusEntity.WanderGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   public boolean doHurtTarget(Entity target) {
      float dmg = target instanceof LivingEntity victim && victim.getArmorValue() > 0 ? 2.0F : 6.0F;
      boolean hit = target.hurt(this.damageSources().magic(), dmg);
      if (hit) {
         this.playSound(SoundEvents.SQUID_SQUIRT, 0.6F, 1.2F);
      }

      if (hit && target instanceof LivingEntity living) {
         MobEffectInstance current = living.getEffect(MobEffects.LEVITATION);
         if (current == null || current.getDuration() <= 20) {
            int ticks = 120 + this.random.nextInt(21);
            living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, ticks, 0, false, true, true), this);
         }
      }

      return hit;
   }

   public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SQUID_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.SQUID_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SQUID_DEATH;
   }

   private static void faceMovement(NautilusEntity mob, double dx, double dz) {
      if (!(dx * dx + dz * dz < 1.0E-4)) {
         float yaw = (float)Math.toDegrees(Math.atan2(-dx, dz));
         mob.setYRot(Mth.rotLerp(0.25F, mob.getYRot(), yaw));
         mob.yBodyRot = mob.getYRot();
         mob.yHeadRot = mob.getYRot();
      }
   }

   private static class HuntGoal extends Goal {
      private final NautilusEntity mob;
      private int cooldown = 0;

      HuntGoal(NautilusEntity mob) {
         this.mob = mob;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity target = this.mob.getTarget();
         return target != null && target.isAlive();
      }

      public boolean canContinueToUse() {
         return this.canUse();
      }

      public void tick() {
         LivingEntity target = this.mob.getTarget();
         if (target != null) {
            Vec3 from = this.mob.position().add(0.0, this.mob.getBbHeight() * 0.5, 0.0);
            Vec3 to = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0).subtract(from);
            double dist = to.length();
            Vec3 dir = dist > 0.001 ? to.scale(1.0 / dist) : Vec3.ZERO;
            double speed = dist > 1.2 ? 0.25 : 0.05;
            Vec3 current = this.mob.getDeltaMovement();
            Vec3 wanted = dir.scale(speed);
            this.mob.setDeltaMovement(current.add(wanted.subtract(current).scale(0.2)));
            NautilusEntity.faceMovement(this.mob, to.x, to.z);
            if (this.cooldown > 0) {
               this.cooldown--;
            }

            if (this.cooldown <= 0 && this.mob.getBoundingBox().inflate(0.35).intersects(target.getBoundingBox())) {
               this.cooldown = 20;
               this.mob.doHurtTarget(target);
            }
         }
      }
   }

   private static class WanderGoal extends Goal {
      private final NautilusEntity mob;
      private Vec3 dest = null;
      private int timer = 0;

      WanderGoal(NautilusEntity mob) {
         this.mob = mob;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         return this.mob.getTarget() == null;
      }

      public void start() {
         this.pick();
      }

      private void pick() {
         RandomSource r = this.mob.getRandom();
         double angle = r.nextDouble() * Math.PI * 2.0;
         double d = 4.0 + r.nextDouble() * 8.0;
         double x = this.mob.getX() + Math.cos(angle) * d;
         double z = this.mob.getZ() + Math.sin(angle) * d;
         int ground = this.mob.level().getHeight(Types.MOTION_BLOCKING, Mth.floor(x), Mth.floor(z));
         double y = Mth.clamp(this.mob.getY() + (r.nextDouble() - 0.5) * 4.0, ground + 2.0, ground + 7.0);
         this.dest = new Vec3(x, y, z);
         this.timer = 60 + r.nextInt(60);
      }

      public void tick() {
         if (this.dest == null
            || --this.timer <= 0
            || this.mob.horizontalCollision
            || this.mob.verticalCollision
            || this.mob.position().distanceToSqr(this.dest) < 2.0) {
            this.pick();
         }

         Vec3 to = this.dest.subtract(this.mob.position());
         double dist = to.length();
         if (!(dist < 0.001)) {
            Vec3 wanted = to.scale(0.07 / dist);
            Vec3 current = this.mob.getDeltaMovement();
            this.mob.setDeltaMovement(current.add(wanted.subtract(current).scale(0.1)));
            NautilusEntity.faceMovement(this.mob, to.x, to.z);
         }
      }
   }
}
