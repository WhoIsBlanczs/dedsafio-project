package com.novastudio.dedsafio4;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityDimensions;
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

public class PterodactiloEntity extends Monster implements GeoEntity, DinoSleepGoal.Sleeper {
   private static final EntityDataAccessor<Boolean> SLEEPING = SynchedEntityData.defineId(PterodactiloEntity.class, EntityDataSerializers.BOOLEAN);
   private static final RawAnimation IDLE = loop("idle");
   private static final RawAnimation WALK = loop("walk");
   private static final RawAnimation FLY = loop("fly");
   private static final RawAnimation FLY_FAST = loop("fly_fast");
   private static final RawAnimation GLIDE = loop("glide");
   private static final RawAnimation HOVER = loop("hover");
   private static final RawAnimation SLEEP = loop("sleep");
   private static final RawAnimation TAKEOFF = RawAnimation.begin().thenPlay("animation.pterodactilo.takeoff").thenLoop("animation.pterodactilo.fly");
   private static final RawAnimation LAND = RawAnimation.begin().thenPlay("animation.pterodactilo.land").thenLoop("animation.pterodactilo.idle");
   private static final String[] ACTIONS = new String[]{"attack_bite", "attack_dive", "screech"};
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int diveCooldown;
   static final int CARRY_TICKS = 200;
   static final int CLIMB_TICKS = 140;
   static final int GRAB_COOLDOWN = 200;
   private static final Map<UUID, Long> GRAB_READY = new HashMap<>();
   private UUID carried;
   int carryTicks;
   private boolean wasOnGround = true;
   private int transitionUntil;
   private RawAnimation transition;

   public PterodactiloEntity(EntityType<? extends Monster> var1, Level var2) {
      super(var1, var2);
      this.moveControl = new FlyingMoveControl(this, 10, false);
   }

   private static RawAnimation loop(String var0) {
      return RawAnimation.begin().thenLoop("animation.pterodactilo." + var0);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 24.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.FLYING_SPEED, 0.7)
         .add(Attributes.ATTACK_DAMAGE, 5.0)
         .add(Attributes.FOLLOW_RANGE, 40.0);
   }

   protected PathNavigation createNavigation(Level var1) {
      return new FlyingPathNavigation(this, var1);
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
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.4, true));
      this.goalSelector.addGoal(2, new DinoSleepGoal(this, true));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0) {
         public boolean canUse() {
            return !PterodactiloEntity.this.level().isDarkOutside() && super.canUse();
         }
      });
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, (var1, nivel$) -> !this.isDinoSleeping() || this.distanceToSqr(var1) < 36.0));
   }

   public void setTarget(LivingEntity var1) {
      boolean var2 = var1 != null && this.getTarget() == null;
      super.setTarget(var1);
      if (var2 && !this.level().isClientSide()) {
         this.setDinoSleeping(false);
         this.triggerAnim("main", "screech");
         this.playSound(SoundEvents.PARROT_AMBIENT, 1.5F, 0.55F);
      }
   }

   public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity var1) {
      if (var1 instanceof ServerPlayer var2 && this.canGrab(var2)) {
         this.grab(var2);
         return true;
      } else {
         boolean var3 = super.doHurtTarget(level, var1);
         if (var3) {
            this.triggerAnim("main", "attack_bite");
         }

         return var3;
      }
   }

   private boolean canGrab(ServerPlayer var1) {
      return this.carried == null
         && !this.isVehicle()
         && !var1.isPassenger()
         && !var1.isCreative()
         && !var1.isSpectator()
         && this.level().getGameTime() >= GRAB_READY.getOrDefault(var1.getUUID(), 0L);
   }

   private void grab(ServerPlayer var1) {
      if (var1.startRiding(this, true, true)) {
         this.carried = var1.getUUID();
         this.carryTicks = 0;
         this.setTarget(null);
         this.triggerAnim("main", "screech");
         this.playSound(SoundEvents.PARROT_AMBIENT, 1.5F, 0.55F);
      }
   }

   void release(ServerPlayer var1, boolean var2) {
      GRAB_READY.put(var1.getUUID(), this.level().getGameTime() + 200L);
      this.carried = null;
      if (var1.getVehicle() == this) {
         var1.stopRiding();
      }

      if (var2) {
         var1.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 160));
      }
   }

   private void carry() {
      if (!(this.getFirstPassenger() instanceof ServerPlayer var1 && var1.getUUID().equals(this.carried))) {
         GRAB_READY.put(this.carried, this.level().getGameTime() + 200L);
         this.carried = null;
      } else if (++this.carryTicks >= 200) {
         this.release(var1, false);
      } else {
         var1.setShiftKeyDown(false);
         this.getNavigation().stop();
         double var4 = Math.toRadians(this.getYRot());
         this.setDeltaMovement(-Math.sin(var4) * 0.12, this.carryTicks < 140 ? 0.3 : 0.08, Math.cos(var4) * 0.12);
      }
   }

   protected Vec3 getPassengerAttachmentPoint(Entity var1, EntityDimensions var2, float var3) {
      return new Vec3(0.0, -var1.getBbHeight() + 0.2, 0.0);
   }

   protected void customServerAiStep(net.minecraft.server.level.ServerLevel level) {
      super.customServerAiStep(level);
      if (this.carried != null) {
         this.carry();
      }

      if (this.diveCooldown > 0) {
         this.diveCooldown--;
      }

      LivingEntity var1 = this.getTarget();
      if (var1 != null && this.diveCooldown == 0 && !this.onGround() && this.getY() > var1.getY() + 2.0 && this.distanceToSqr(var1) < 100.0) {
         this.diveCooldown = 80;
         this.triggerAnim("main", "attack_dive");
         this.playSound(SoundEvents.PHANTOM_SWOOP, 1.2F, 1.1F);
      }
   }

   public void aiStep() {
      super.aiStep();
      Vec3 var1 = this.getDeltaMovement();
      if (!this.onGround() && var1.y < 0.0) {
         this.setDeltaMovement(var1.multiply(1.0, 0.6, 1.0));
      }
   }

   public boolean causeFallDamage(double var1, float var2, DamageSource var3) {
      return false;
   }

   public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource var1, float var2) {
      if (this.carried != null && var1.getEntity() instanceof ServerPlayer var3 && var3.getUUID().equals(this.carried)) {
         return false;
      } else {
         if (!this.level().isClientSide()) {
            this.setDinoSleeping(false);
         }

         return super.hurtServer(level, var1, var2);
      }
   }

   protected SoundEvent getAmbientSound() {
      return this.isDinoSleeping() ? null : SoundEvents.PHANTOM_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.PHANTOM_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PHANTOM_DEATH;
   }

   public void registerControllers(ControllerRegistrar var1) {
      AnimationController var2 = new AnimationController<>("main", 4, this::animate);

      for (String var6 : ACTIONS) {
         var2.triggerableAnim(var6, RawAnimation.begin().thenPlay("animation.pterodactilo." + var6));
      }

      var1.add(var2);
   }

   private PlayState animate(AnimationTest<PterodactiloEntity> var1) {
      if (this.isDinoSleeping()) {
         return var1.setAndContinue(SLEEP);
      } else {
         boolean var2 = this.onGround() || this.isInWater();
         if (var2 != this.wasOnGround) {
            this.wasOnGround = var2;
            this.transition = var2 ? LAND : TAKEOFF;
            this.transitionUntil = this.tickCount + (var2 ? 24 : 28);
         }

         if (this.tickCount < this.transitionUntil) {
            return var1.setAndContinue(this.transition);
         } else if (var2) {
            return var1.setAndContinue(var1.isMoving() ? WALK : IDLE);
         } else {
            double var3 = this.getX() - this.xo;
            double var5 = this.getY() - this.yo;
            double var7 = this.getZ() - this.zo;
            double var9 = var3 * var3 + var7 * var7;
            if (var9 < 0.003) {
               return var1.setAndContinue(HOVER);
            } else {
               return var5 < -0.08 ? var1.setAndContinue(GLIDE) : var1.setAndContinue(!(var9 > 0.12) && !this.isAggressive() ? FLY : FLY_FAST);
            }
         }
      }
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
