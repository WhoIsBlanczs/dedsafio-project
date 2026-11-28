package com.novastudio.dedsafio4;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public class RaptorEntity extends Monster implements GeoEntity, DinoSleepGoal.Sleeper {
   private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(RaptorEntity.class, EntityDataSerializers.BOOLEAN);
   private static final RawAnimation IDLE = loop("idle");
   private static final RawAnimation IDLE_ALERT = loop("idle_alert");
   private static final RawAnimation WALK = loop("walk");
   private static final RawAnimation RUN = loop("run");
   private static final RawAnimation RUN_FAST = loop("run_fast");
   private static final RawAnimation SLEEP = loop("sleep");
   private static final RawAnimation JUMP = RawAnimation.begin().thenPlayAndHold("animation.raptor.jump");
   private static final String[] ACTIONS = new String[]{"attack_bite", "attack_slash", "attack_pounce", "roar", "eat"};
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public RaptorEntity(EntityType<? extends Monster> var1, Level var2) {
      super(var1, var2);
   }

   private static RawAnimation loop(String var0) {
      return RawAnimation.begin().thenLoop("animation.raptor." + var0);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 30.0)
         .add(Attributes.MOVEMENT_SPEED, 0.33)
         .add(Attributes.ATTACK_DAMAGE, 6.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.2);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(SLEEPING, false);
   }

   @Override
   public void setDinoSleeping(boolean var1) {
      this.entityData.set(SLEEPING, var1);
   }

   public boolean isDinoSleeping() {
      return (Boolean)this.entityData.get(SLEEPING);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new LeapAtTargetGoal(this, 0.45F) {
         public void start() {
            super.start();
            RaptorEntity.this.triggerAnim("main", "attack_pounce");
         }
      });
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.5, true));
      this.goalSelector.addGoal(3, new DinoSleepGoal(this, false));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.9));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 12.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, (var1, nivel$) -> !this.isDinoSleeping() || this.distanceToSqr(var1) < 36.0));
   }

   public void setTarget(LivingEntity var1) {
      boolean var2 = var1 != null && this.getTarget() == null;
      super.setTarget(var1);
      if (var2 && !this.level().isClientSide()) {
         this.setDinoSleeping(false);
         this.triggerAnim("main", "roar");
         this.playSound(SoundEvents.RAVAGER_ROAR, 1.2F, 1.5F);
      }
   }

   public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity var1) {
      boolean var2 = super.doHurtTarget(level, var1);
      if (var2) {
         this.triggerAnim("main", this.getRandom().nextBoolean() ? "attack_bite" : "attack_slash");
      }

      return var2;
   }

   protected void customServerAiStep(net.minecraft.server.level.ServerLevel level) {
      super.customServerAiStep(level);
      if (this.getTarget() == null
         && !this.isDinoSleeping()
         && this.onGround()
         && this.getNavigation().isDone()
         && this.getRandom().nextInt(600) == 0) {
         this.triggerAnim("main", "eat");
      }
   }

   public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource var1, float var2) {
      if (!this.level().isClientSide()) {
         this.setDinoSleeping(false);
      }

      return super.hurtServer(level, var1, var2);
   }

   protected SoundEvent getAmbientSound() {
      return this.isDinoSleeping() ? null : SoundEvents.RAVAGER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.RAVAGER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.RAVAGER_DEATH;
   }

   public float getVoicePitch() {
      return super.getVoicePitch() * 1.4F;
   }

   public void registerControllers(ControllerRegistrar var1) {
      AnimationController var2 = new AnimationController<>("main", 4, this::animate);

      for (String var6 : ACTIONS) {
         var2.triggerableAnim(var6, RawAnimation.begin().thenPlay("animation.raptor." + var6));
      }

      var1.add(var2);
   }

   private PlayState animate(AnimationTest<RaptorEntity> var1) {
      if (this.isDinoSleeping()) {
         return var1.setAndContinue(SLEEP);
      } else if (!this.onGround() && !this.isInWater()) {
         return var1.setAndContinue(JUMP);
      } else {
         boolean var2 = this.isAggressive();
         if (var1.isMoving()) {
            double var3 = this.getX() - this.xo;
            double var5 = this.getZ() - this.zo;
            return var1.setAndContinue(var2 ? RUN_FAST : (var3 * var3 + var5 * var5 > 0.02 ? RUN : WALK));
         } else {
            return var1.setAndContinue(var2 ? IDLE_ALERT : IDLE);
         }
      }
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
