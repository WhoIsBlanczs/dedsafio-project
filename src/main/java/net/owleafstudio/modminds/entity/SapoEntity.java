package net.owleafstudio.modminds.entity;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.procedures.SapoComportamientoProcedure;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public class SapoEntity extends Monster implements GeoEntity {
   private boolean swingingMc$;
   public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(SapoEntity.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(SapoEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(SapoEntity.class, EntityDataSerializers.STRING);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean swinging;
   private boolean lastloop;
   private long lastSwing;
   public String animationprocedure = "empty";
   String prevAnim = "empty";
   private int ambientCooldown = 120;

   public SapoEntity(EntityType<SapoEntity> type, Level world) {
      super(type, world);
      this.xpReward = 0;
      this.setNoAi(false);
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHOOT, false);
      builder.define(ANIMATION, "undefined");
      builder.define(TEXTURE, "frog_big");
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
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, false, false));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, ServerPlayer.class, false, false));
      this.goalSelector.addGoal(6, new SapoEntity.SapoWanderGoal(this, 0.8, 80));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(8, new FloatGoal(this));
   }

   public MobCategory getCategory() {
      return MobCategory.MISC;
   }

   public double getPassengersRidingOffset() {
      return 0.1;
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return BossStyle.SAPO.hurt;
   }

   public SoundEvent getDeathSound() {
      return BossStyle.SAPO.death;
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
      SapoComportamientoProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
      this.refreshDimensions();
   }

   public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity target) {
      if (!this.level().isClientSide() && this.level() instanceof ServerLevel _serverLevel) {
         BossPresentation.melee(_serverLevel, this, target, BossStyle.SAPO);
      }

      return super.doHurtTarget(level, target);
   }

   public void aiStep() {
      super.aiStep();
      if (!this.level().isClientSide() && this.getTarget() != null && --this.ambientCooldown <= 0) {
         if (this.level() instanceof ServerLevel _serverLevel) {
            BossPresentation.ambient(_serverLevel, this, BossStyle.SAPO);
         }

         this.ambientCooldown = 200 + this.random.nextInt(200);
      }
   }

   public static void init() {
   }

   public static AttributeSupplier.Builder createAttributes() {
      AttributeSupplier.Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
      builder = builder.add(Attributes.MAX_HEALTH, 70.0);
      builder = builder.add(Attributes.ARMOR, 0.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 3.0);
      return builder.add(Attributes.FOLLOW_RANGE, 32.0);
   }

   private PlayState movementPredicate(AnimationTest<?> event) {
      if (!this.animationprocedure.equals("empty")) {
         return PlayState.STOP;
      } else {
         boolean hasPassenger = !this.getPassengers().isEmpty();
         return !event.isMoving() && !(Math.abs((event.isMoving() ? 1.0F : 0.0F)) > 0.15F)
            ? event.setAndContinue(RawAnimation.begin().thenLoop(hasPassenger ? "entityeufonia.frog_big.idle_player" : "entityeufonia.frog_big.idle"))
            : event.setAndContinue(RawAnimation.begin().thenLoop(hasPassenger ? "entityeufonia.frog_big.walk_player" : "entityeufonia.frog_big.walk"));
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
         return event.setAndContinue(RawAnimation.begin().thenPlay("entityeufonia.frog_big.attack_1"));
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
         BossPresentation.death(_serverLevel, this, this.deathTime, BossStyle.SAPO);
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

   public Vec3 getRandomGroundPos() {
      Random random = new Random();

      for (int i = 0; i < 10; i++) {
         double dx = (random.nextDouble() * 2.0 - 1.0) * 16.0;
         double dz = (random.nextDouble() * 2.0 - 1.0) * 16.0;
         double rx = this.getX() + dx;
         double rz = this.getZ() + dz;
         BlockPos pos = new BlockPos((int)rx, (int)this.getY(), (int)rz);
         int y = this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos).getY();
         BlockPos groundPos = new BlockPos((int)rx, y - 1, (int)rz);
         if (this.level().getBlockState(groundPos).isSolidRender()) {
            return new Vec3(rx, y, rz);
         }
      }

      return this.position();
   }

   public float maxUpStep() {
      return 0.6F;
   }

   public static class SapoWanderGoal extends Goal {
      private final SapoEntity sapo;
      private final double speed;
      private final int chance;
      private Path path;
      private final PathNavigation navigation;

      public SapoWanderGoal(SapoEntity sapo, double speed, int chance) {
         this.sapo = sapo;
         this.speed = speed;
         this.chance = chance;
         this.navigation = sapo.getNavigation();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         if (this.sapo.getTarget() != null) {
            return false;
         } else if (this.sapo.random.nextInt(this.chance) != 0) {
            return false;
         } else {
            Vec3 vec3 = this.sapo.getRandomGroundPos();
            if (vec3 == null) {
               return false;
            } else {
               this.path = this.navigation.createPath(vec3.x, vec3.y, vec3.z, 0);
               return this.path != null;
            }
         }
      }

      public boolean canContinueToUse() {
         return !this.navigation.isDone();
      }

      public void start() {
         if (this.path != null) {
            this.navigation.moveTo(this.path, this.speed);
         } else {
            Vec3 randomPos = this.sapo.getRandomGroundPos();
            this.sapo.getMoveControl().setWantedPosition(randomPos.x, randomPos.y, randomPos.z, this.speed);
         }
      }
   }
}
