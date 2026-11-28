package net.owleafstudio.modminds.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.owleafstudio.modminds.boss.BossFx;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public class SoldadoReviilEntity extends PathfinderMob implements GeoEntity {
   public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(SoldadoReviilEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(SoldadoReviilEntity.class, EntityDataSerializers.STRING);
   public String animationprocedure = "empty";
   String prevAnim = "empty";
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean swinging;
   private long lastSwing;
   public int riseTicks;
   private static final double WALK_SPEED = 0.3;
   private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.SoldadoReviil.idle");
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.SoldadoReviil.walk");
   private static final RawAnimation SLASH = RawAnimation.begin().thenPlay("animation.SoldadoReviil.slash");
   private static final RawAnimation DIE = RawAnimation.begin().thenPlay("animation.SoldadoReviil.die");

   public SoldadoReviilEntity(EntityType<SoldadoReviilEntity> type, Level world) {
      super(type, world);
      this.xpReward = 0;
      this.setPersistenceRequired();
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(ANIMATION, "undefined");
      builder.define(TEXTURE, "soldado_reviil");
   }

   public void setTexture(String texture) {
      this.entityData.set(TEXTURE, texture);
   }

   public String getTexture() {
      return (String)this.entityData.get(TEXTURE);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[]{ReviilEntity.class, SoldadoReviilEntity.class}));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true, true));
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, true) {
         public boolean canUse() {
            return !SoldadoReviilEntity.this.isBusy() && super.canUse();
         }

         protected double getAttackReachSqr(LivingEntity entity) {
            return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth() + 1.6;
         }
      });
      this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0) {
         public boolean canUse() {
            return !SoldadoReviilEntity.this.isBusy() && super.canUse();
         }
      });
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
   }

   public void setTarget(LivingEntity target) {
      super.setTarget(target instanceof Player ? target : null);
   }

   public MobCategory getCategory() {
      return MobCategory.MONSTER;
   }

   public boolean isBusy() {
      return this.riseTicks > 0;
   }

   public void tick() {
      super.tick();
      if (this.riseTicks > 0 && this.level() instanceof ServerLevel) {
         this.riseTicks--;
      }
   }

   public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity target) {
      if (this.level() instanceof ServerLevel serverLevel) {
         BossFx.sound(serverLevel, BossFx.centre(target), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 0.9F, 0.72F);
      }

      return super.doHurtTarget(level, target);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PILLAGER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.PILLAGER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PILLAGER_DEATH;
   }

   protected void playStepSound(BlockPos pos, BlockState state) {
      this.playSound(SoundEvents.GRAVEL_STEP, 0.42F, 0.72F);
   }

   protected void tickDeath() {
      this.deathTime++;
      if (this.deathTime >= 20) {
         this.remove(Entity.RemovalReason.KILLED);
      }
   }

   public static void init() {
   }

   public static AttributeSupplier.Builder createAttributes() {
      AttributeSupplier.Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
      builder = builder.add(Attributes.MAX_HEALTH, 60.0);
      builder = builder.add(Attributes.ARMOR, 4.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 8.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 48.0);
      return builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.4);
   }

   private PlayState movementPredicate(AnimationTest<?> event) {
      if (this.isAlive()) {
         if (!this.animationprocedure.equals("empty")) {
            return PlayState.STOP;
         } else {
            return !event.isMoving() && !(Math.abs((event.isMoving() ? 1.0F : 0.0F)) > 0.15F) ? event.setAndContinue(IDLE) : event.setAndContinue(WALK);
         }
      } else {
         return event.setAndContinue(DIE);
      }
   }

   private PlayState attackingPredicate(AnimationTest<?> event) {
      if (this.getAttackAnim(event.renderState().getPartialTick()) > 0.0F && !this.isSwinging()) {
         this.isSwinging() = true;
         this.lastSwing = this.level().getGameTime();
      }

      if (this.isSwinging() && this.lastSwing + 11L <= this.level().getGameTime()) {
         this.isSwinging() = false;
      }

      if (this.isSwinging() && this.animationprocedure.equals("empty") && event.controller().hasAnimationFinished()) {
         event.controller().reset();
         return event.setAndContinue(SLASH);
      } else {
         return PlayState.CONTINUE;
      }
   }

   private PlayState procedurePredicate(AnimationTest<?> event) {
      if (!this.animationprocedure.equals("empty") && event.controller().hasAnimationFinished()
         || !this.animationprocedure.equals(this.prevAnim) && !this.animationprocedure.equals("empty")) {
         if (!this.animationprocedure.equals(this.prevAnim)) {
            event.controller().reset();
         }

         event.controller().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
         if (event.controller().hasAnimationFinished()) {
            this.animationprocedure = "empty";
            event.controller().reset();
         }
      } else if (this.animationprocedure.equals("empty")) {
         this.prevAnim = "empty";
         return PlayState.STOP;
      }

      this.prevAnim = this.animationprocedure;
      return PlayState.CONTINUE;
   }

   public String getSyncedAnimation() {
      return (String)this.entityData.get(ANIMATION);
   }

   public void setAnimation(String animation) {
      if (this.level().isClientSide()) {
         this.entityData.set(ANIMATION, animation);
      } else {
         if (this.getSyncedAnimation().equals(animation)) {
            this.entityData.set(ANIMATION, "undefined");
         }

         this.entityData.set(ANIMATION, animation);
      }
   }

   public void releasePose() {
      this.riseTicks = 0;
      this.setAnimation("empty");
      if (this.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
         this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3);
      }
   }

   public void registerControllers(ControllerRegistrar data) {
      data.add(new AnimationController<>("movement", 4, this::movementPredicate));
      data.add(new AnimationController<>("attacking", 6, this::attackingPredicate));
      data.add(new AnimationController<>("procedure", 4, this::procedurePredicate));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public float maxUpStep() {
      return 1.0F;
   }
}
