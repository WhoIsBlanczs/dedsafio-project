package net.owleafstudio.modminds.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.procedures.WardenMounstrosityAlEstarVivaProcedure;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public class WardenMounstrosityEntity extends Monster implements GeoEntity {
   private boolean swingingMc$;
   public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(WardenMounstrosityEntity.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(WardenMounstrosityEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(WardenMounstrosityEntity.class, EntityDataSerializers.STRING);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean swinging;
   private boolean lastloop;
   private long lastSwing;
   public String animationprocedure = "empty";
   String prevAnim = "empty";
   private int ambientCooldown = 120;

   public WardenMounstrosityEntity(EntityType<WardenMounstrosityEntity> type, Level world) {
      super(type, world);
      this.xpReward = 15;
      this.setNoAi(false);
      this.setPersistenceRequired();
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHOOT, false);
      builder.define(ANIMATION, "undefined");
      builder.define(TEXTURE, "warden_mounstrosity");
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
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true, true));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, ServerPlayer.class, true, true));
      this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, true) {
         protected double getAttackReachSqr(LivingEntity entity) {
            return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
         }
      });
      this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
   }

   public MobCategory getCategory() {
      return MobCategory.MISC;
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return false;
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return BossStyle.WARDEN_MOUNSTROSITY.hurt;
   }

   public SoundEvent getDeathSound() {
      return BossStyle.WARDEN_MOUNSTROSITY.death;
   }

   public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
      if (source.getDirectEntity() instanceof AbstractThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud) {
         return false;
      } else if (source.is(DamageTypes.FALL)) {
         return false;
      } else {
         return source.is(DamageTypes.DROWN) ? false : super.hurtServer(level, source, amount);
      }
   }

   public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput compound) {
      super.addAdditionalSaveData(compound);
      compound.putString("Texture", this.getTexture());
   }

   public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput compound) {
      super.readAdditionalSaveData(compound);
      if (compound.child("Texture").isPresent()) {
         this.setTexture(compound.getStringOr("Texture", ""));
      }
   }

   public void baseTick() {
      super.baseTick();
      WardenMounstrosityAlEstarVivaProcedure.execute(this.level(), this);
      this.refreshDimensions();
   }

   public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity target) {
      if (!this.level().isClientSide() && this.level() instanceof ServerLevel _serverLevel) {
         BossPresentation.melee(_serverLevel, this, target, BossStyle.WARDEN_MOUNSTROSITY);
      }

      return super.doHurtTarget(level, target);
   }

   public void aiStep() {
      super.aiStep();
      if (!this.level().isClientSide() && this.getTarget() != null && --this.ambientCooldown <= 0) {
         if (this.level() instanceof ServerLevel _serverLevel) {
            BossPresentation.ambient(_serverLevel, this, BossStyle.WARDEN_MOUNSTROSITY);
         }

         this.ambientCooldown = 200 + this.random.nextInt(200);
      }
   }

   public static void init() {
   }

   public static AttributeSupplier.Builder createAttributes() {
      AttributeSupplier.Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.2);
      builder = builder.add(Attributes.MAX_HEALTH, 1000.0);
      builder = builder.add(Attributes.ARMOR, 20.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 35.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 32.0);
      builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 50.0);
      return builder.add(Attributes.ATTACK_KNOCKBACK, 5.5);
   }

   private PlayState movementPredicate(AnimationTest<?> event) {
      if (!this.animationprocedure.equals("empty")) {
         return PlayState.STOP;
      } else if ((event.isMoving() || !((event.isMoving() ? 1.0F : 0.0F) > -0.15F) || !((event.isMoving() ? 1.0F : 0.0F) < 0.15F)) && !this.isAggressive()) {
         return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
      } else {
         return this.isAggressive() && event.isMoving()
            ? event.setAndContinue(RawAnimation.begin().thenLoop("run"))
            : event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
      }
   }

   private PlayState attackingPredicate(AnimationTest<?> event) {
      double d1 = this.getX() - this.xOld;
      double d0 = this.getZ() - this.zOld;
      float velocity = (float)Math.sqrt(d1 * d1 + d0 * d0);
      if (this.getSwingAnimation(event.renderState().getPartialTick()) > 0.0F && !this.swingingMc$) {
         this.swingingMc$ = true;
         this.lastSwing = this.level().getGameTime();
      }

      if (this.swingingMc$ && this.lastSwing + 7L <= this.level().getGameTime()) {
         this.swingingMc$ = false;
      }

      if (this.swingingMc$ && event.controller().hasAnimationFinished()) {
         event.controller().reset();
         return event.setAndContinue(RawAnimation.begin().thenPlay("attack"));
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

   protected void tickDeath() {
      this.deathTime++;
      if (this.level() instanceof ServerLevel _serverLevel) {
         BossPresentation.death(_serverLevel, this, this.deathTime, BossStyle.WARDEN_MOUNSTROSITY);
      }

      if (this.deathTime == 20) {
         this.remove(Entity.RemovalReason.KILLED);
      }
   }

   public String getSyncedAnimation() {
      return (String)this.entityData.get(ANIMATION);
   }

   public void setAnimation(String animation) {
      this.entityData.set(ANIMATION, animation);
   }

   public void registerControllers(ControllerRegistrar data) {
      data.add(new AnimationController<>("movement", 4, this::movementPredicate));
      data.add(new AnimationController<>("attacking", 4, this::attackingPredicate));
      data.add(new AnimationController<>("procedure", 4, this::procedurePredicate));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public float maxUpStep() {
      return 1.0F;
   }
}
