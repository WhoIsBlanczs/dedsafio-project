package com.novastudio.dedsafio4;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;

public final class CreeperGeoEntity extends Creeper implements GeoEntity {
   private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
   private final String animationId;

   public CreeperGeoEntity(EntityType<? extends Creeper> type, Level level, String animationId) {
      super(type, level);
      this.animationId = animationId;
      CreeperAbilities.applyFuse(this, animationId);
   }

   public String animationId() {
      return this.animationId;
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController<>("movement", 0, state -> {
         if (this.getSwellDir() <= 0 && !this.isIgnited()) {
            String anim = state.isMoving() && !"creeper".equals(this.animationId) ? "walk" : "idle";
            return state.setAndContinue(RawAnimation.begin().thenLoop("animation." + this.animationId + "." + anim));
         } else {
            return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation." + this.animationId + ".explode"));
         }
      }));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
