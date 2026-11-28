package com.watonstudio.creepermetalico;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FloralCreeperEntity extends Creeper implements GeoEntity {
   private static final String ACTION_CONTROLLER = "floral_actions";
   private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
   private boolean wasCharging;
   private boolean explosionAnimationTriggered;
   private boolean deathAnimationTriggered;
   private int chargingTicks;
   private static final RawAnimation SPAWN = RawAnimation.begin().thenPlay("animation.floral.spawn");
   private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.floral.idle");
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.floral.walk");
   private static final RawAnimation RUN = RawAnimation.begin().thenLoop("animation.floral.run");
   private static final RawAnimation JUMP = RawAnimation.begin().thenPlay("animation.floral.jump");
   private static final RawAnimation CHARGE = RawAnimation.begin().thenLoop("animation.floral.charge");
   private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("animation.floral.attack");
   private static final RawAnimation HURT = RawAnimation.begin().thenPlay("animation.floral.hurt");
   private static final RawAnimation EXPLODE = RawAnimation.begin().thenPlay("animation.floral.explode");
   private static final RawAnimation DEATH = RawAnimation.begin().thenPlay("animation.floral.death");
   private static final RawAnimation SPECIAL_DANCE = RawAnimation.begin().thenPlay("animation.floral.special_danza_floracion");

   public FloralCreeperEntity(EntityType<? extends FloralCreeperEntity> type, Level world) {
      super(type, world);
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "floral_controller", 3, state -> {
         if (!this.isAlive()) {
            return state.setAndContinue(DEATH);
         } else if (this.tickCount < 52) {
            return state.setAndContinue(SPAWN);
         } else if (this.getSwellDir() > 0) {
            return state.setAndContinue(CHARGE);
         } else if (!this.onGround()) {
            return state.setAndContinue(JUMP);
         } else if (this.isSprinting()) {
            return state.setAndContinue(RUN);
         } else {
            return state.isMoving() ? state.setAndContinue(WALK) : state.setAndContinue(IDLE);
         }
      }));
      controllers.add(
         new AnimationController(this, "floral_actions", 0, state -> PlayState.STOP)
            .triggerableAnim("attack", ATTACK)
            .triggerableAnim("hurt", HURT)
            .triggerableAnim("explode", EXPLODE)
            .triggerableAnim("death", DEATH)
            .triggerableAnim("special_danza_floracion", SPECIAL_DANCE)
      );
   }

   public void tick() {
      if (!this.level().isClientSide) {
         boolean charging = this.getSwellDir() > 0;
         if (charging) {
            if (!this.wasCharging) {
               this.chargingTicks = 0;
               this.explosionAnimationTriggered = false;
               this.triggerAnim("floral_actions", "attack");
            }

            this.chargingTicks++;
            if (this.chargingTicks >= 22 && !this.explosionAnimationTriggered) {
               this.triggerAnim("floral_actions", "explode");
               this.explosionAnimationTriggered = true;
            }
         } else {
            this.chargingTicks = 0;
            this.explosionAnimationTriggered = false;
         }

         this.wasCharging = charging;
      }

      super.tick();
   }

   public void die(DamageSource source) {
      if (!this.level().isClientSide && !this.deathAnimationTriggered) {
         this.deathAnimationTriggered = true;
         this.triggerAnim("floral_actions", "death");
      }

      super.die(source);
   }

   public boolean hurt(DamageSource source, float amount) {
      boolean damaged = super.hurt(source, amount);
      if (damaged && !this.level().isClientSide && this.isAlive()) {
         this.triggerAnim("floral_actions", "hurt");
      }

      return damaged;
   }

   protected InteractionResult mobInteract(Player player, InteractionHand hand) {
      if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
         if (!this.level().isClientSide) {
            this.triggerAnim("floral_actions", "special_danza_floracion");
         }

         return InteractionResult.SUCCESS;
      } else {
         return super.mobInteract(player, hand);
      }
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.geoCache;
   }
}
