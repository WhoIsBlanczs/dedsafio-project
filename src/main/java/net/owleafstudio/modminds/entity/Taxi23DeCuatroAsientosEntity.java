package net.owleafstudio.modminds.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public class Taxi23DeCuatroAsientosEntity extends Monster implements GeoEntity {
   public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(Taxi23DeCuatroAsientosEntity.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(Taxi23DeCuatroAsientosEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(Taxi23DeCuatroAsientosEntity.class, EntityDataSerializers.STRING);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean swinging;
   private boolean lastloop;
   private long lastSwing;
   public String animationprocedure = "empty";
   private String prevAnim = "empty";

   public Taxi23DeCuatroAsientosEntity(EntityType<Taxi23DeCuatroAsientosEntity> type, Level world) {
      super(type, world);
      this.xpReward = 0;
      this.setNoAi(true);
      this.setPersistenceRequired();
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHOOT, false);
      builder.define(ANIMATION, "undefined");
      builder.define(TEXTURE, "taxi4x4");
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

   public String getSyncedAnimation() {
      return this.animationprocedure;
   }

   public void setAnimation(String animation) {
      this.animationprocedure = animation;
      this.entityData.set(ANIMATION, animation);
   }

   protected PathNavigation createNavigation(Level world) {
      return new FlyingPathNavigation(this, world);
   }

   public MobCategory getCategory() {
      return MobCategory.MISC;
   }

   public double getPassengersRidingOffset() {
      return 0.0;
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("entity.generic.hurt"));
   }

   public SoundEvent getDeathSound() {
      return ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("entity.generic.death"));
   }

   public boolean isPickable() {
      return true;
   }

   public boolean canBeCollidedWith(net.minecraft.world.entity.Entity otro$) {
      return true;
   }

   public boolean isPushable() {
      return true;
   }

   public InteractionResult mobInteract(Player player, InteractionHand hand) {
      if (!player.isPassenger() && this.getPassengers().size() < 4) {
         player.startRiding(this, true, true);
         return InteractionResult.SUCCESS;
      } else {
         return super.mobInteract(player, hand);
      }
   }

   public void travel(Vec3 dir) {
      if (this.isVehicle() && !this.getPassengers().isEmpty()) {
         Entity driver = (Entity)this.getPassengers().get(0);
         if (driver instanceof Player player) {
            this.setYRot(player.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(player.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = player.getYRot();
            Vec3 movement = player.getLookAngle().scale(0.5 * player.zza);
            this.setDeltaMovement(movement);
            super.travel(Vec3.ZERO);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.3));
            return;
         }
      }

      super.travel(dir);
   }

   protected void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
      int indexPassenger = this.getPassengers().indexOf(passenger);
      if (indexPassenger == 0) {
         double offsetX = 0.5;
         double offsetZ = 0.6;
         float bodyYRot = this.getYRot() * (float) (Math.PI / 180.0);
         double xRotated = offsetX * Math.cos(bodyYRot) - offsetZ * Math.sin(bodyYRot);
         double zRotated = offsetX * Math.sin(bodyYRot) + offsetZ * Math.cos(bodyYRot);
         moveFunction.accept(passenger, this.getX() + xRotated, this.getY() + this.getPassengersRidingOffset(), this.getZ() + zRotated);
      } else if (indexPassenger == 1) {
         double offsetX = -0.5;
         double offsetZ = 0.6;
         float bodyYRot = this.getYRot() * (float) (Math.PI / 180.0);
         double xRotated = offsetX * Math.cos(bodyYRot) - offsetZ * Math.sin(bodyYRot);
         double zRotated = offsetX * Math.sin(bodyYRot) + offsetZ * Math.cos(bodyYRot);
         moveFunction.accept(passenger, this.getX() + xRotated, this.getY() + this.getPassengersRidingOffset(), this.getZ() + zRotated);
      } else if (indexPassenger == 2) {
         double offsetX = 0.5;
         double offsetZ = -0.7;
         float bodyYRot = this.getYRot() * (float) (Math.PI / 180.0);
         double xRotated = offsetX * Math.cos(bodyYRot) - offsetZ * Math.sin(bodyYRot);
         double zRotated = offsetX * Math.sin(bodyYRot) + offsetZ * Math.cos(bodyYRot);
         moveFunction.accept(passenger, this.getX() + xRotated, this.getY() + this.getPassengersRidingOffset(), this.getZ() + zRotated);
      } else if (indexPassenger == 3) {
         double offsetX = -0.5;
         double offsetZ = -0.7;
         float bodyYRot = this.getYRot() * (float) (Math.PI / 180.0);
         double xRotated = offsetX * Math.cos(bodyYRot) - offsetZ * Math.sin(bodyYRot);
         double zRotated = offsetX * Math.sin(bodyYRot) + offsetZ * Math.cos(bodyYRot);
         moveFunction.accept(passenger, this.getX() + xRotated, this.getY() + this.getPassengersRidingOffset(), this.getZ() + zRotated);
      } else {
         super.positionRider(passenger, moveFunction);
      }
   }

   public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
      if (source.is(DamageTypes.IN_FIRE)) {
         return false;
      } else if (source.getDirectEntity() instanceof AbstractArrow) {
         return false;
      } else if (source.getDirectEntity() instanceof Player) {
         return false;
      } else if (source.getDirectEntity() instanceof AbstractThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud) {
         return false;
      } else if (source.is(DamageTypes.FALL)) {
         return false;
      } else if (source.is(DamageTypes.CACTUS)) {
         return false;
      } else if (source.is(DamageTypes.DROWN)) {
         return false;
      } else if (source.is(DamageTypes.LIGHTNING_BOLT)) {
         return false;
      } else if (source.is(DamageTypes.EXPLOSION)) {
         return false;
      } else if (source.is(DamageTypes.TRIDENT)) {
         return false;
      } else if (source.is(DamageTypes.FALLING_ANVIL)) {
         return false;
      } else if (source.is(DamageTypes.DRAGON_BREATH)) {
         return false;
      } else if (source.is(DamageTypes.WITHER)) {
         return false;
      } else {
         return source.is(DamageTypes.WITHER_SKULL) ? false : super.hurtServer(level, source, amount);
      }
   }

   @SubscribeEvent
   public static void onLivingHurt(LivingHurtEvent event) {
      if (event.getEntity().getVehicle() instanceof Taxi23DeCuatroAsientosEntity) {
         event.setCanceled(true);
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController<>("movement", 4, this::movementPredicate));
      controllers.add(new AnimationController<>("procedure", 4, this::procedurePredicate));
   }

   private PlayState movementPredicate(AnimationTest<?> event) {
      double posY = this.getY();
      double groundLevel = this.level().getMinY() + 1;
      boolean isMounted = this.isVehicle() && !this.getPassengers().isEmpty();
      if (!this.onGround() && !isMounted) {
         return posY <= groundLevel + 0.2
            ? event.setAndContinue(RawAnimation.begin().thenLoop("idle"))
            : event.setAndContinue(RawAnimation.begin().thenLoop("standing"));
      } else if (!isMounted) {
         return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
      } else {
         Player player = (Player)this.getPassengers().get(0);
         return !this.onGround() && player.zza != 0.0F
            ? event.setAndContinue(RawAnimation.begin().thenLoop("fly"))
            : event.setAndContinue(RawAnimation.begin().thenLoop("standing"));
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

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public static void init() {
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MOVEMENT_SPEED, 0.2)
         .add(Attributes.MAX_HEALTH, 1.0)
         .add(Attributes.FOLLOW_RANGE, 16.0)
         .add(Attributes.FLYING_SPEED, 0.2);
   }

   public float maxUpStep() {
      return 0.6F;
   }
}
