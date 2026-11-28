package com.watonstudio.creepermetalico;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public class NautilusOseoEntity extends Monster implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.waton_oseo.idle");
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.waton_oseo.walk");

   public NautilusOseoEntity(EntityType<? extends Monster> type, Level world) {
      super(type, world);
   }

   public static Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 60.0)
         .add(Attributes.ATTACK_DAMAGE, 12.0)
         .add(Attributes.FOLLOW_RANGE, 36.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   public boolean tryAttack(ServerLevel world, Entity target) {
      boolean hit = super.doHurtTarget(world, target);
      if (hit && target instanceof LivingEntity living) {
         living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 180, 0), this);
      }

      return hit;
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide() && this.tickCount % 120 == 0 && this.getTarget() instanceof Player target && this.distanceToSqr(target) <= 1296.0) {
         double dx = target.getX() - this.getX();
         double dy = target.getY() - this.getY();
         double dz = target.getZ() - this.getZ();
         double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
         if (length > 0.001) {
            this.setDeltaMovement(dx / length * 0.8, dy / length * 0.8 + 0.2, dz / length * 0.8);
            this.needsSync = true;
         }
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController<>("controller", 5, state -> state.setAndContinue(state.isMoving() ? WALK : IDLE)));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
