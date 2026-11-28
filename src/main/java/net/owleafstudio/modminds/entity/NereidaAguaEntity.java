package net.owleafstudio.modminds.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
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
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;
import net.owleafstudio.modminds.procedures.NereidaAguaAlEstarVivaProcedure;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public class NereidaAguaEntity extends Monster implements GeoEntity {
   private boolean swingingMc$;
   public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(NereidaAguaEntity.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(NereidaAguaEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(NereidaAguaEntity.class, EntityDataSerializers.STRING);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean swinging;
   private boolean lastloop;
   private long lastSwing;
   public String animationprocedure = "empty";
   String prevAnim = "empty";

   public NereidaAguaEntity(EntityType<NereidaAguaEntity> type, Level world) {
      super(type, world);
      this.xpReward = 0;
      this.setNoAi(false);
      this.setPersistenceRequired();
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.moveControl = new MoveControl(this) {
         public void tick() {
            if (NereidaAguaEntity.this.isInWater()) {
               NereidaAguaEntity.this.setDeltaMovement(NereidaAguaEntity.this.getDeltaMovement().add(0.0, 0.005, 0.0));
            }

            if (this.operation == MoveControl.Operation.MOVE_TO && !NereidaAguaEntity.this.getNavigation().isDone()) {
               double dx = this.wantedX - NereidaAguaEntity.this.getX();
               double dy = this.wantedY - NereidaAguaEntity.this.getY();
               double dz = this.wantedZ - NereidaAguaEntity.this.getZ();
               float f = (float)(Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
               float f1 = (float)(this.speedModifier * NereidaAguaEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
               NereidaAguaEntity.this.setYRot(this.rotlerp(NereidaAguaEntity.this.getYRot(), f, 10.0F));
               NereidaAguaEntity.this.yBodyRot = NereidaAguaEntity.this.getYRot();
               NereidaAguaEntity.this.yHeadRot = NereidaAguaEntity.this.getYRot();
               if (NereidaAguaEntity.this.isInWater()) {
                  NereidaAguaEntity.this.setSpeed((float)NereidaAguaEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getValue());
                  float f2 = -((float)(Mth.atan2(dy, (float)Math.sqrt(dx * dx + dz * dz)) * (180.0 / Math.PI)));
                  f2 = Mth.clamp(Mth.wrapDegrees(f2), -85.0F, 85.0F);
                  NereidaAguaEntity.this.setXRot(this.rotlerp(NereidaAguaEntity.this.getXRot(), f2, 5.0F));
                  float f3 = Mth.cos(NereidaAguaEntity.this.getXRot() * (float) (Math.PI / 180.0));
                  NereidaAguaEntity.this.setZza(f3 * f1);
                  NereidaAguaEntity.this.setYya((float)(f1 * dy));
               } else {
                  NereidaAguaEntity.this.setSpeed(f1 * 0.05F);
               }
            } else {
               NereidaAguaEntity.this.setSpeed(0.0F);
               NereidaAguaEntity.this.setYya(0.0F);
               NereidaAguaEntity.this.setZza(0.0F);
            }
         }
      };
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHOOT, false);
      builder.define(ANIMATION, "undefined");
      builder.define(TEXTURE, "nereida");
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

   protected PathNavigation createNavigation(Level world) {
      return new WaterBoundPathNavigation(this, world);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, false, false));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, ServerPlayer.class, false, false));
      this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, true) {
         protected double getAttackReachSqr(LivingEntity entity) {
            return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
         }
      });
   }

   public MobCategory getCategory() {
      return MobCategory.MISC;
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return false;
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("entity.generic.hurt"));
   }

   public SoundEvent getDeathSound() {
      return ForgeRegistries.SOUND_EVENTS.getValue(Identifier.parse("entity.generic.death"));
   }

   public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
      return source.is(DamageTypes.DROWN) ? false : super.hurtServer(level, source, amount);
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
      NereidaAguaAlEstarVivaProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
      this.refreshDimensions();
   }

   protected int decreaseAirSupply(int air) {
      return air;
   }

   public boolean checkSpawnObstruction(LevelReader world) {
      return world.isUnobstructed(this);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public static void init() {
   }

   public static AttributeSupplier.Builder createAttributes() {
      AttributeSupplier.Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.SCALE, 0.7);
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
      builder = builder.add(Attributes.MAX_HEALTH, 20.0);
      builder = builder.add(Attributes.ARMOR, 0.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 3.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 64.0);
      return builder.add(ForgeMod.SWIM_SPEED.get(), 0.3);
   }

   private PlayState movementPredicate(AnimationTest<?> event) {
      if (!this.animationprocedure.equals("empty")) {
         return PlayState.STOP;
      } else if ((event.isMoving() || !((event.isMoving() ? 1.0F : 0.0F) > -0.15F) || !((event.isMoving() ? 1.0F : 0.0F) < 0.15F)) && !this.isAggressive()) {
         return event.setAndContinue(RawAnimation.begin().thenLoop("walkwater"));
      } else {
         return this.isAggressive() && event.isMoving()
            ? event.setAndContinue(RawAnimation.begin().thenLoop("runwater"))
            : event.setAndContinue(RawAnimation.begin().thenLoop("idlewater"));
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
         return event.setAndContinue(RawAnimation.begin().thenPlay("attackwater"));
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
      return 0.6F;
   }
}
