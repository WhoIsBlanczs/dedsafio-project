package com.ghostsafio.mitomod;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class CerebroEntity extends Monster {
   public static final int BITE_INTERVAL_TICKS = 15;
   public static final float BITE_DAMAGE_ARMORED = 2.0F;
   public static final float BITE_DAMAGE_UNARMORED = 6.0F;
   private static final EntityDataAccessor<Integer> HOST_ID = SynchedEntityData.defineId(CerebroEntity.class, EntityDataSerializers.INT);
   private int biteTimer = 15;

   public CerebroEntity(EntityType<? extends CerebroEntity> type, Level level) {
      super(type, level);
      this.xpReward = 8;
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(HOST_ID, -1);
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 10.0)
         .add(Attributes.ATTACK_DAMAGE, 1.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3)
         .add(Attributes.FOLLOW_RANGE, 24.0);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, false));
      this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8));
      this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   public int getHostId() {
      return (Integer)this.entityData.get(HOST_ID);
   }

   public Entity getHost() {
      int id = this.getHostId();
      return id < 0 ? null : this.level().getEntity(id);
   }

   public boolean isAttached() {
      return this.getHostId() >= 0;
   }

   public boolean doHurtTarget(Entity target) {
      if (!this.isAttached() && target instanceof LivingEntity living && living.isAlive()) {
         boolean taken = false;

         for (CerebroEntity other : this.level().getEntitiesOfClass(CerebroEntity.class, living.getBoundingBox().inflate(4.0))) {
            if (other != this && other.getHostId() == living.getId()) {
               taken = true;
               break;
            }
         }

         if (taken) {
            this.bite(living);
            return true;
         } else {
            this.entityData.set(HOST_ID, living.getId());
            this.biteTimer = 15;
            this.getNavigation().stop();
            this.playSound(SoundEvents.SLIME_ATTACK, 0.8F, 0.7F);
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean hurt(DamageSource source, float amount) {
      Entity host = this.getHost();
      return host != null && source.getEntity() != null && source.getEntity() == host ? false : super.hurt(source, amount);
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return super.isInvulnerableTo(source) || this.isAttached() && source.is(DamageTypes.IN_WALL);
   }

   public boolean isPushable() {
      return !this.isAttached() && super.isPushable();
   }

   public void tick() {
      super.tick();
      int id = this.getHostId();
      if (id < 0) {
         this.noPhysics = false;
         this.setNoGravity(false);
         this.biteTimer = 15;
      } else if (this.level().getEntity(id) instanceof LivingEntity host && host.isAlive() && !host.isSpectator() && !host.isRemoved()) {
         this.noPhysics = true;
         this.setNoGravity(true);
         this.fallDistance = 0.0F;
         this.setDeltaMovement(0.0, 0.0, 0.0);
         this.setPos(host.getX(), host.getY() + host.getBbHeight() - 0.1, host.getZ());
         this.setYRot(host.yBodyRot);
         this.yBodyRot = host.yBodyRot;
         this.yHeadRot = host.yBodyRot;
         if (!this.level().isClientSide) {
            this.getNavigation().stop();
            this.setTarget(host);
            if (--this.biteTimer <= 0) {
               this.biteTimer = 15;
               this.bite(host);
            }
         }
      } else {
         if (!this.level().isClientSide) {
            this.entityData.set(HOST_ID, -1);
         }
      }
   }

   private void bite(LivingEntity host) {
      float amount = host.getArmorValue() > 0 ? 2.0F : 6.0F;
      if (host.hurt(this.damageSources().magic(), amount)) {
         this.playSound(SoundEvents.SLIME_ATTACK, 0.6F, 1.5F);
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENDERMITE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENDERMITE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENDERMITE_DEATH;
   }
}
