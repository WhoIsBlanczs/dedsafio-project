package net.owleafstudio.modminds.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.registries.ForgeRegistries;
import net.owleafstudio.modminds.procedures.CreeperCakeAlJugadorColisionarConLaEntidadProcedure;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public class CreeperCakeEntity extends Monster implements GeoEntity {
   public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(CreeperCakeEntity.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(CreeperCakeEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(CreeperCakeEntity.class, EntityDataSerializers.STRING);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean swinging;
   private boolean lastloop;
   private long lastSwing;
   public String animationprocedure = "empty";
   String prevAnim = "empty";

   public CreeperCakeEntity(EntityType<CreeperCakeEntity> type, Level world) {
      super(type, world);
      this.xpReward = 0;
      this.setNoAi(false);
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHOOT, false);
      builder.define(ANIMATION, "undefined");
      builder.define(TEXTURE, "creepercake");
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
      this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, false) {
         protected double getAttackReachSqr(LivingEntity entity) {
            return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
         }
      });
      this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(6, new FloatGoal(this));
   }

   public MobCategory getCategory() {
      return MobCategory.MISC;
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("entity.generic.hurt"));
   }

   public SoundEvent getDeathSound() {
      return ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("entity.generic.death"));
   }

   public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput compound) {
      super.addAdditionalSaveData(compound);
      compound.putString("ModmindsTexture", this.getTexture());
   }

   public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput compound) {
      super.readAdditionalSaveData(compound);
      if (compound.child("ModmindsTexture").isPresent()) {
         this.setTexture(compound.getStringOr("ModmindsTexture", ""));
      }
   }

   public void baseTick() {
      super.baseTick();
      this.refreshDimensions();
   }

   public void playerTouch(Player sourceentity) {
      super.playerTouch(sourceentity);
      CreeperCakeAlJugadorColisionarConLaEntidadProcedure.execute(this.level(), this, sourceentity);
   }

   public static void init() {
   }

   public static AttributeSupplier.Builder createAttributes() {
      AttributeSupplier.Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.35);
      builder = builder.add(Attributes.MAX_HEALTH, 10.0);
      builder = builder.add(Attributes.ARMOR, 0.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 0.0);
      return builder.add(Attributes.FOLLOW_RANGE, 64.0);
   }

   private PlayState movementPredicate(AnimationTest<?> event) {
      if (!this.animationprocedure.equals("empty")) {
         return PlayState.STOP;
      } else {
         return !event.isMoving() && (event.isMoving() ? 1.0F : 0.0F) > -0.15F && (event.isMoving() ? 1.0F : 0.0F) < 0.15F
            ? event.setAndContinue(RawAnimation.begin().thenLoop("idle"))
            : event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
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
      data.add(new AnimationController<>("procedure", 4, this::procedurePredicate));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public float maxUpStep() {
      return 0.8F;
   }
}
