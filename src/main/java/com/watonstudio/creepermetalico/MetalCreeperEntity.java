package com.watonstudio.creepermetalico;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
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

public class MetalCreeperEntity extends Monster implements GeoEntity {
   private static final EntityDataAccessor<Integer> FUSE_TICKS = SynchedEntityData.defineId(MetalCreeperEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DETONATED = SynchedEntityData.defineId(MetalCreeperEntity.class, EntityDataSerializers.BOOLEAN);
   private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
   private int fuseTicks = 0;
   private int lastWalkingSoundTick = 0;
   private boolean detonated = false;
   private static final int FUSE_LENGTH = 60;
   private static final double FUSE_DISTANCE = 4.0;
   private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.metalico.idle");
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.metalico.walk");
   private static final RawAnimation CHARGE = RawAnimation.begin().thenLoop("animation.metalico.charge");
   private static final RawAnimation EXPLODE = RawAnimation.begin().thenPlay("animation.metalico.explode");

   public MetalCreeperEntity(EntityType<? extends MetalCreeperEntity> type, Level world) {
      super(type, world);
      this.xpReward = 5;
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(FUSE_TICKS, 0);
      builder.define(DETONATED, false);
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createMetalCreeperAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.FOLLOW_RANGE, 24.0);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false) {
         protected void checkAndPerformAttack(LivingEntity target) {
         }
      });
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController<>("controller", 5, state -> {
         if ((Boolean)this.entityData.get(DETONATED)) {
            return state.setAndContinue(EXPLODE);
         } else if ((Integer)this.entityData.get(FUSE_TICKS) > 0) {
            return state.setAndContinue(CHARGE);
         } else {
            return state.isMoving() ? state.setAndContinue(WALK) : state.setAndContinue(IDLE);
         }
      }));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.geoCache;
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide() && this.isAlive() && !this.detonated) {
         if (this.onGround() && this.getDeltaMovement().horizontalDistanceSqr() > 0.001 && this.tickCount - this.lastWalkingSoundTick >= 10) {
            this.playServerSound(CreeperMetalicoMod.RESORTE_WALKING, 0.8F, 1.0F);
            this.lastWalkingSoundTick = this.tickCount;
         }

         LivingEntity target = this.getTarget();
         if (target != null && target.isAlive() && !(this.distanceToSqr(target) > 16.0)) {
            this.fuseTicks++;
            if (this.fuseTicks == 1) {
               this.playServerSound(SoundEvents.CREEPER_PRIMED, 1.0F, 1.0F);
            }

            this.entityData.set(FUSE_TICKS, this.fuseTicks);
            if (this.fuseTicks >= 60) {
               this.detonate();
            }
         } else {
            this.fuseTicks = 0;
            this.entityData.set(FUSE_TICKS, 0);
         }
      }
   }

   private void playServerSound(SoundEvent sound, float volume, float pitch) {
      if (this.level() instanceof ServerLevel serverWorld) {
         serverWorld.playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()), sound, SoundSource.HOSTILE, volume, pitch);
      }
   }

   private void detonate() {
      if (!this.detonated && this.level() instanceof ServerLevel serverWorld) {
         this.detonated = true;
         this.entityData.set(DETONATED, true);
         serverWorld.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);

         for (Player player : serverWorld.players()) {
            if (this.distanceTo(player) <= 6.0 && player.isAlive() && !CreeperQteManager.isProtectedFromExplosion(player, this)) {
               player.setDeltaMovement(player.getDeltaMovement().x, 8.0, player.getDeltaMovement().z);
               player.needsSync = true;
            }
         }

         this.playServerSound(CreeperMetalicoMod.RESORTE_EXPLOSION, 2.0F, 1.0F);
         this.discard();
      }
   }
}
