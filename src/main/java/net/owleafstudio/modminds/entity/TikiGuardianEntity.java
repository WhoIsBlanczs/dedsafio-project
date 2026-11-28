package net.owleafstudio.modminds.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.BossPresentation;
import net.owleafstudio.modminds.boss.BossStyle;
import net.owleafstudio.modminds.boss.SceneRunner;
import net.owleafstudio.modminds.procedures.TikiEscenaProcedure;
import net.owleafstudio.modminds.procedures.TikiGuardianEmbestidaProcedure;
import net.owleafstudio.modminds.procedures.TikiGuardianEspinasProcedure;
import net.owleafstudio.modminds.procedures.TikiGuardianFuriaProcedure;
import net.owleafstudio.modminds.procedures.TikiGuardianFuriaV2Procedure;
import net.owleafstudio.modminds.procedures.TikiGuardianGuardianesProcedure;
import org.joml.Vector3f;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public class TikiGuardianEntity extends PathfinderMob implements GeoEntity, SceneRunner.Actor {
   private boolean swingingMc$;
   public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(TikiGuardianEntity.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(TikiGuardianEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(TikiGuardianEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<Boolean> FURIA = SynchedEntityData.defineId(TikiGuardianEntity.class, EntityDataSerializers.BOOLEAN);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean swinging;
   private boolean lastloop;
   private long lastSwing;
   public String animationprocedure = "empty";
   String prevAnim = "empty";
   public int escenaTicks;
   private static final double WALK_SPEED = 0.25;
   private int ambientCooldown = 120;
   private int gestureCooldown = 260;
   private int gestureTicks;
   private static final String[] GESTURE_POSES = new String[]{
      "animation.TikiGuardian.scene.gaze", "animation.TikiGuardian.scene.point.a", "animation.TikiGuardian.scene.point.b", "animation.TikiGuardian.scene.twirl"
   };
   private static final int[] GESTURE_HOLD_TICKS = new int[]{64, 48, 48, 48};
   private String gesturePose = "";
   private int sinceAbility = 999;
   private static final int GESTURE_QUIET_TICKS = 100;
   private int abilityCooldown = 200;
   private int guardianesCooldown = 480;
   private boolean agoniaDicha;
   private static final double OPEN_RANGE = 26.0;
   private static final double GRAPPLE_RANGE = 10.0;
   private static final double DESPERATE_HEALTH = 0.7;
   private static final int GUARDIAN_LIMIT = 3;
   private static final double GUARDIAN_SEARCH = 32.0;
   private static final int ABILITY_GAP_TICKS = 160;
   private static final int FURY_GAP_TICKS = 90;
   private static final double FURIA_HEALTH = 0.5;
   private static final double AGONY_HEALTH = 0.25;
   private static final int ABILITY_JITTER_TICKS = 120;
   private static final int GUARDIANES_GAP_TICKS = 900;
   private static final int GUARDIANES_JITTER_TICKS = 300;

   public TikiGuardianEntity(EntityType<TikiGuardianEntity> type, Level world) {
      super(type, world);
      this.xpReward = 0;
      this.setNoAi(false);
      this.setPersistenceRequired();
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHOOT, false);
      builder.define(ANIMATION, "undefined");
      builder.define(TEXTURE, "tikiguardian");
      builder.define(FURIA, false);
   }

   public boolean isFuria() {
      return (Boolean)this.entityData.get(FURIA);
   }

   public void setFuria(boolean fury) {
      if ((Boolean)this.entityData.get(FURIA) != fury && !this.level().isClientSide()) {
         this.entityData.set(FURIA, fury);
         if (fury && this.level() instanceof ServerLevel level) {
            BossFx.shake(level, BossFx.centre(this), 64.0, 22, 0.45);
            BossFx.ring(level, BossFx.centre(this), 4.2, 26, BossFx.CRUSHED_STONE, 2, 0.35);
            BossFx.colored(level, BossFx.centre(this), new Vector3f(0.85F, 0.2F, 0.1F), 1.6F, 26, 1.4);
            BossFx.sound(level, BossFx.centre(this), SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 2.0F, 0.6F);
            BossFx.alert(level, BossFx.centre(this), 128.0, "⚠ TIKI: SE LE CAEN LAS CADENAS");
         }
      }
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

         public boolean canUse() {
            return TikiGuardianEntity.this.escenaTicks <= 0 && super.canUse();
         }

         public boolean canContinueToUse() {
            return TikiGuardianEntity.this.escenaTicks <= 0 && super.canContinueToUse();
         }
      });
   }

   public MobCategory getCategory() {
      return MobCategory.MISC;
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return false;
   }

   public double getPassengersRidingOffset() {
      return 4.8;
   }

   public SoundEvent getHurtSound(DamageSource ds) {
      return BossStyle.TIKI.hurt;
   }

   public SoundEvent getDeathSound() {
      return BossStyle.TIKI.death;
   }

   public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
      if (this.escenaTicks > 0 && !source.is(DamageTypes.FELL_OUT_OF_WORLD) && !source.is(DamageTypes.GENERIC_KILL)) {
         return false;
      } else if (source.getDirectEntity() instanceof Player rider && rider.isPassenger() && rider.getRootVehicle() == this) {
         return false;
      } else if (source.getDirectEntity() instanceof AbstractThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud) {
         return false;
      } else if (source.is(DamageTypes.FALL)) {
         return false;
      } else {
         return source.is(DamageTypes.CACTUS) ? false : super.hurtServer(level, source, amount);
      }
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

   public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
      if (sourceentity.getVehicle() == this) {
         return InteractionResult.PASS;
      } else {
         ItemStack itemstack = sourceentity.getItemInHand(hand);
         InteractionResult retval = InteractionResult.SUCCESS;
         super.mobInteract(sourceentity, hand);
         sourceentity.startRiding(this);
         return retval;
      }
   }

   public void baseTick() {
      super.baseTick();
      this.refreshDimensions();
   }

   public void travel(Vec3 dir) {
      Entity entity = this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
      if (this.isVehicle()) {
         this.setYRot(entity.getYRot());
         this.yRotO = this.getYRot();
         this.setXRot(entity.getXRot() * 0.5F);
         this.setRot(this.getYRot(), this.getXRot());
         this.yBodyRot = entity.getYRot();
         this.yHeadRot = entity.getYRot();
         if (entity instanceof LivingEntity passenger) {
            this.setSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
            float forward = passenger.zza;
            float strafe = passenger.xxa;
            super.travel(new Vec3(strafe, 0.0, forward));
         }

         double d1 = this.getX() - this.xo;
         double d0 = this.getZ() - this.zo;
         float f1 = (float)Math.sqrt(d1 * d1 + d0 * d0) * 4.0F;
         if (f1 > 1.0F) {
            f1 = 1.0F;
         }

         this.walkAnimation.setSpeed(this.walkAnimation.speed() + (f1 - this.walkAnimation.speed()) * 0.4F);
         this.walkAnimation.position(this.walkAnimation.position() + this.walkAnimation.speed());
         this.calculateEntityAnimation(true);
      } else {
         super.travel(dir);
      }
   }

   public boolean canChangeDimensions() {
      return false;
   }

   public void aiStep() {
      super.aiStep();
      if (!this.level().isClientSide() && this.escenaTicks > 0) {
         this.escenaTicks--;
      }

      if (!this.level().isClientSide() && this.isAlive() && this.escenaTicks <= 0) {
         double share = this.getHealth() / this.getMaxHealth();
         if (share <= 0.5) {
            this.setFuria(true);
         }

         if (share <= 0.25 && !this.agoniaDicha) {
            this.agoniaDicha = true;
            TikiEscenaProcedure.executeAgonia(this.level(), this);
         }
      }

      LivingEntity ridden = this.getTarget();
      if (!this.level().isClientSide() && ridden != null && ridden.getRootVehicle() == this) {
         this.setTarget(null);
      } else if (!this.level().isClientSide() && this.escenaTicks <= 0 && this.getTarget() != null && --this.ambientCooldown <= 0) {
         if (this.level() instanceof ServerLevel _serverLevel) {
            BossPresentation.ambient(_serverLevel, this, BossStyle.TIKI);
         }

         this.ambientCooldown = 200 + this.random.nextInt(200);
      }

      this.tickGesture();
      if (!this.level().isClientSide() && this.level() instanceof ServerLevel world && this.isAlive()) {
         if (this.guardianesCooldown > 0) {
            this.guardianesCooldown--;
         }

         if (this.sinceAbility < 100) {
            this.sinceAbility++;
         }

         if (--this.abilityCooldown <= 0) {
            if (this.escenaTicks <= 0 && (this.isVehicle() || this.getTarget() != null)) {
               this.chooseAbility(world);
               this.sinceAbility = 0;
            }

            this.abilityCooldown = (this.isFuria() ? 90 : 160) + this.random.nextInt(120);
         }
      }
   }

   private void tickGesture() {
      if (!this.level().isClientSide() && this.isAlive()) {
         if (this.gestureTicks > 0) {
            if (!this.gesturePose.equals(this.getSyncedAnimation())) {
               this.gestureTicks = 0;
               this.gesturePose = "";
            } else {
               this.getNavigation().stop();
               Vec3 motion = this.getDeltaMovement();
               this.setDeltaMovement(0.0, motion.y, 0.0);
               if (--this.gestureTicks == 0) {
                  this.gesturePose = "";
                  this.setAnimation("empty");
               }
            }
         } else if (this.escenaTicks <= 0 && this.getTarget() != null && --this.gestureCooldown <= 0 && this.sinceAbility >= 100) {
            int pick = this.random.nextInt(GESTURE_POSES.length);
            this.gesturePose = GESTURE_POSES[pick];
            this.setAnimation(this.gesturePose);
            this.gestureTicks = GESTURE_HOLD_TICKS[pick];
            this.gestureCooldown = 240 + this.random.nextInt(320);
         }
      }
   }

   private void chooseAbility(ServerLevel world) {
      LivingEntity target = this.getTarget();
      double distance = target == null ? 0.0 : this.distanceTo(target);
      if (this.isVehicle()) {
         TikiGuardianEmbestidaProcedure.execute(world, this, target);
      } else if (this.isFuria()) {
         if (this.wantsGuardianes(world)) {
            TikiGuardianGuardianesProcedure.execute(world, this);
            this.guardianesCooldown = 900 + this.random.nextInt(300);
         } else {
            TikiGuardianFuriaV2Procedure.execute(world, this, distance);
         }
      } else if (this.wantsGuardianes(world)) {
         TikiGuardianGuardianesProcedure.execute(world, this);
         this.guardianesCooldown = 900 + this.random.nextInt(300);
      } else if (distance > 26.0) {
         TikiGuardianEspinasProcedure.execute(world, this, target);
      } else if (distance < 10.0 && this.random.nextBoolean()) {
         TikiGuardianEmbestidaProcedure.execute(world, this, target);
      } else {
         TikiGuardianFuriaProcedure.execute(world, this, distance > 10.0);
      }
   }

   private boolean wantsGuardianes(ServerLevel world) {
      return this.guardianesCooldown <= 0 && this.getHealth() < this.getMaxHealth() * 0.7 && this.countGuardianes(world) < 3;
   }

   private int countGuardianes(ServerLevel world) {
      Vec3 at = this.position();
      return world.getEntitiesOfClass(TorretaTikiEntity.class, new AABB(at, at).inflate(32.0), e -> e.isAlive()).size();
   }

   public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity target) {
      if (!this.level().isClientSide() && this.level() instanceof ServerLevel _serverLevel) {
         BossPresentation.melee(_serverLevel, this, target, BossStyle.TIKI);
      }

      return super.doHurtTarget(level, target);
   }

   public static void init() {
   }

   public static AttributeSupplier.Builder createAttributes() {
      AttributeSupplier.Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.25);
      builder = builder.add(Attributes.MAX_HEALTH, 1800.0);
      builder = builder.add(Attributes.ARMOR, 6.0);
      builder = builder.add(Attributes.ARMOR_TOUGHNESS, 2.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 70.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 128.0);
      return builder.add(Attributes.KNOCKBACK_RESISTANCE, 99.0);
   }

   private PlayState movementPredicate(AnimationTest<?> event) {
      if (!this.animationprocedure.equals("empty")) {
         return PlayState.STOP;
      } else {
         return !event.isMoving() && (event.isMoving() ? 1.0F : 0.0F) > -0.15F && (event.isMoving() ? 1.0F : 0.0F) < 0.15F
            ? event.setAndContinue(RawAnimation.begin().thenLoop("animation.TikiGuardian.idle"))
            : event.setAndContinue(RawAnimation.begin().thenLoop("animation.TikiGuardian.walk"));
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

      if (this.swingingMc$ && this.escenaTicks <= 0 && event.controller().hasAnimationFinished()) {
         event.controller().reset();
         return event.setAndContinue(RawAnimation.begin().thenPlay("animation.TikiGuardian.spin"));
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
         BossPresentation.death(_serverLevel, this, this.deathTime, BossStyle.TIKI);
      }

      if (this.deathTime == 20) {
         this.remove(Entity.RemovalReason.KILLED);
      }
   }

   public String getSyncedAnimation() {
      return (String)this.entityData.get(ANIMATION);
   }

   @Override
   public void setAnimation(String animation) {
      if (this.level().isClientSide()) {
         this.entityData.set(ANIMATION, animation);
      } else if (this.escenaTicks <= 0 || animation.equals("empty") || isScenePose(animation)) {
         if (this.getSyncedAnimation().equals(animation)) {
            this.entityData.set(ANIMATION, "undefined");
         }

         this.entityData.set(ANIMATION, animation);
      }
   }

   public static boolean isScenePose(String animation) {
      return animation.startsWith("animation.TikiGuardian.scene.");
   }

   @Override
   public void releasePose() {
      this.setAnimation("empty");
      if (this.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
         this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25);
      }
   }

   @Override
   public Entity sceneEntity() {
      return this;
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
