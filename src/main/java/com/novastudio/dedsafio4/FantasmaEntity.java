package com.novastudio.dedsafio4;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public class FantasmaEntity extends Monster implements GeoEntity {
   static final Map<EntityType<?>, FantasmaEntity.Kind> KINDS = new HashMap<>();
   static final double REACH = 3.2;
   static final int WINDUP = 10;
   private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
   private static final RawAnimation FLY = RawAnimation.begin().thenLoop("walk");
   private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private int rest;
   private int windup;

   public FantasmaEntity(EntityType<? extends Monster> var1, Level var2) {
      super(var1, var2);
      this.moveControl = new FlyingMoveControl(this, 20, true);
      this.setNoGravity(true);
   }

   FantasmaEntity.Kind kind() {
      return KINDS.get(this.getType());
   }

   static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 30.0)
         .add(Attributes.MOVEMENT_SPEED, 0.47)
         .add(Attributes.FLYING_SPEED, 0.47)
         .add(Attributes.FOLLOW_RANGE, 64.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
   }

   protected PathNavigation createNavigation(Level var1) {
      return new FlyingPathNavigation(this, var1);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   protected void customServerAiStep(net.minecraft.server.level.ServerLevel level) {
      super.customServerAiStep(level);
      LivingEntity var1 = this.getTarget();
      if (var1 != null && var1.isAlive()) {
         FantasmaEntity.Kind var2 = this.kind();
         Vec3 var3 = var1.position()
            .add(0.0, var1.getBbHeight() * 0.5, 0.0)
            .subtract(this.position().add(0.0, this.getBbHeight() * 0.5, 0.0));
         double var4 = var3.length();
         if (var4 > 2.4) {
            this.setDeltaMovement(var3.normalize().scale(var2.speed()));
         } else {
            this.setDeltaMovement(Vec3.ZERO);
         }

         this.getLookControl().setLookAt(var1, 30.0F, 30.0F);
         this.setYRot((float)Math.toDegrees(Math.atan2(-var3.x, var3.z)));
         this.yBodyRot = this.getYRot();
         if (this.windup > 0) {
            if (--this.windup == 0) {
               if (var4 <= 4.0) {
                  this.strike(var1, var2);
               }

               this.rest = var2.rest();
            }
         } else if (this.rest > 0) {
            this.rest--;
         } else if (var4 <= 3.2) {
            this.windup = 10;
            this.triggerAnim("main", "attack");
            this.playSound(SoundEvents.VEX_CHARGE, 1.0F, 1.0F);
         }
      } else {
         this.windup = 0;
      }
   }

   private void strike(LivingEntity var1, FantasmaEntity.Kind var2) {
      if (var2.halves()) {
         var1.setHealth(var1.getHealth() / 2.0F);
      }

      var1.hurt(this.damageSources().mobAttack(this), var2.damage());
   }

   public boolean causeFallDamage(double var1, float var2, DamageSource var3) {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.VEX_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.VEX_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.VEX_DEATH;
   }

   public float getVoicePitch() {
      return 0.6F + this.getRandom().nextFloat() * 0.15F;
   }

   public void registerControllers(ControllerRegistrar var1) {
      AnimationController<FantasmaEntity> var2 = new AnimationController<>("main", 4, this::animate);
      var2.triggerableAnim("attack", ATTACK);
      var1.add(var2);
   }

   private PlayState animate(AnimationTest<FantasmaEntity> var1) {
      return var1.setAndContinue(this.getTarget() == null && !var1.isMoving() ? IDLE : FLY);
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public record Kind(String color, double speed, float damage, int rest, boolean halves) {
   }
}
