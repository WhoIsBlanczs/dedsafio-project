package net.owleafstudio.modminds.entity;

import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.owleafstudio.modminds.boss.BossFx;
import net.owleafstudio.modminds.boss.ReviilFx;
import net.owleafstudio.modminds.boss.SceneRunner;
import net.owleafstudio.modminds.init.ModmindsModSounds;
import net.owleafstudio.modminds.network.ModmindsModVariables;
import net.owleafstudio.modminds.procedures.ReviilAlEstarVivaProcedure;
import net.owleafstudio.modminds.procedures.ReviilAlHacerleClickDerechoProcedure;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public class ReviilEntity extends PathfinderMob implements GeoEntity, SceneRunner.Actor {
   public static final double WALK_SPEED = 0.23;
   public static final double FURY_WALK_SPEED = 0.3;
   public static final double MELEE_DAMAGE = 130.0;
   public static final double MAX_LIFE = 12000.0;
   public static final float BASE_SCALE = 1.4F;
   public static final float GIANT_SCALE = 3.4F;
   public static final float FURY_SCALE = 1.65F;
   public static final float FURY_HEAL = 3000.0F;
   private static final int[] GIANTE_BARRAGE = new int[]{1, 3, 4, 5, 6, 13, 17, 19, 20, 21, 22, 23, 24, 25, 26};
   private static final int[] FURIA_BARRAGE = new int[]{1, 2, 4, 5, 9, 11, 13, 27, 29};
   private static final int[] CALMA_BARRAGE = new int[]{2, 13, 1, 29};
   public static final int[] REVIIL_BURLA_TICKS = new int[]{105, 169, 137, 155, 182, 98, 105};
   private static final String[] BURLA_TEXTO = new String[]{
      "¿Eso era un ataque? Jaja.",
      "Ustedes confunden terquedad con poder.",
      "No me hagan reír. Sin Eón no son nada. JAJA.",
      "Ustedes pelean como escriben sus leyendas.",
      "Satanás los usa y ustedes le dan las gracias. JAJA.",
      "Ustedes olvidan rápido. Yo no tengo ese lujo.",
      "¿Comida? ¿En medio de una batalla? Qué patético."
   };
   private static final int[] BURLA_FASE = new int[]{0, 0, 1, 0, 1, 0, 2};
   private static final int BURLA_GAP_TICKS = 500;
   private static final int BURLA_GAP_SPREAD = 350;
   public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(ReviilEntity.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(ReviilEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(ReviilEntity.class, EntityDataSerializers.STRING);
   public static final EntityDataAccessor<Integer> GRAB_BEAT = SynchedEntityData.defineId(ReviilEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Integer> GRAB_PROGRESS = SynchedEntityData.defineId(ReviilEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(ReviilEntity.class, EntityDataSerializers.INT);
   public static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(ReviilEntity.class, EntityDataSerializers.FLOAT);
   public static final EntityDataAccessor<Integer> ESFUERZO = SynchedEntityData.defineId(ReviilEntity.class, EntityDataSerializers.INT);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean swinging;
   private boolean lastloop;
   private long lastSwing;
   public String animationprocedure = "empty";
   String prevAnim = "empty";
   private int ambientCooldown = 120;
   public int escenaTicks;
   private int hablaCorte = 0;
   public long ruletaHasta = 0L;
   private int phaseBeforeGiant = 0;
   public int escenaStage;
   private int phaseAuraCooldown;
   private int barrageCooldown = 90;
   private int barrageLastId;
   private int burlaTicks;
   private int burlaCooldown = 400;
   private int burlaLastId = -1;
   private int furyWalkHold;
   private static final String[] IDLE_POSES = new String[]{
      "extended_pose.standing.still",
      "emote.idle.brazos_atras",
      "emote.idle.cadera",
      "emote.idle.mira",
      "emote.idle.cruzados",
      "extended_pose.standing.still",
      "emote.idle.respira",
      "emote.idle.cuello_lado",
      "emote.idle.hombros",
      "emote.idle.dobla",
      "extended_pose.standing.still",
      "emote.idle.bosteza",
      "emote.idle.punio_estudia",
      "emote.idle.unas",
      "emote.idle.sacude",
      "extended_pose.standing.still",
      "emote.idle.encogimiento",
      "emote.idle.peso_atras",
      "emote.idle.giro",
      "emote.idle.silba",
      "emote.idle.ruleta",
      "emote.idle.amenaza"
   };
   private static final String[] FURY_POSES = new String[]{
      "emote.fury.stance",
      "emote.fury.stance.venga",
      "emote.fury.stance.dientes",
      "emote.fury.stance.pecho",
      "emote.fury.stance.inclinado",
      "emote.fury.stance.desafio",
      "emote.fury.stance.asoma"
   };
   private static final String[] GIANT_POSES = new String[]{
      "emote.giant.stance",
      "emote.giant.stance.atras",
      "emote.giant.stance.cadera",
      "emote.giant.stance.cruzados",
      "emote.giant.stance.repaso",
      "emote.giant.stance.palmas"
   };
   private String[] posePool;
   private String poseClip;
   private int posePick;
   private long poseSince;

   public ReviilEntity(EntityType<ReviilEntity> type, Level world) {
      super(type, world);
      this.xpReward = 0;
      this.setNoAi(false);
      this.setPersistenceRequired();
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SHOOT, false);
      builder.define(ANIMATION, "undefined");
      builder.define(TEXTURE, "reviil_boss");
      builder.define(GRAB_BEAT, 0);
      builder.define(GRAB_PROGRESS, 100);
      builder.define(PHASE, 0);
      builder.define(SIZE, 1.4F);
      builder.define(ESFUERZO, 0);
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
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
         public boolean canUse() {
            return ReviilEntity.this.escenaTicks <= 0 && !ReviilEntity.this.isGiant() && super.canUse();
         }

         public boolean canContinueToUse() {
            return ReviilEntity.this.escenaTicks <= 0 && !ReviilEntity.this.isGiant() && super.canContinueToUse();
         }

         protected double getAttackReachSqr(LivingEntity entity) {
            return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
         }
      });
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, false, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, ServerPlayer.class, false, false));
   }

   public void setTarget(@Nullable LivingEntity target) {
      super.setTarget(target instanceof Player ? target : null);
   }

   public MobCategory getCategory() {
      return MobCategory.MISC;
   }

   public boolean removeWhenFarAway(double distanceToClosestPlayer) {
      return false;
   }

   public double getPassengersRidingOffset() {
      return 1.6;
   }

   @Nullable
   public SoundEvent getHurtSound(DamageSource ds) {
      return SoundEvents.WARDEN_HURT;
   }

   @Nullable
   public SoundEvent getDeathSound() {
      return null;
   }

   public float getSoundVolume() {
      return 0.5F;
   }

   public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity target) {
      if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
         ReviilFx.melee(serverLevel, this, target);
      }

      return super.doHurtTarget(level, target);
   }

   public void tickDeath() {
      super.tickDeath();
      if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
         ReviilFx.death(serverLevel, this, this.deathTime);
      }
   }

   public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
      if (this.escenaTicks > 0 && !source.is(DamageTypes.FELL_OUT_OF_WORLD) && !source.is(DamageTypes.GENERIC_KILL)) {
         return false;
      } else if (source.getDirectEntity() instanceof Player rider && rider.isPassenger() && rider.getRootVehicle() == this) {
         return false;
      } else if (source.is(DamageTypes.IN_FIRE)) {
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
      } else if (source.is(DamageTypes.FALLING_ANVIL)) {
         return false;
      } else if (source.is(DamageTypes.DRAGON_BREATH)) {
         return false;
      } else if (source.is(DamageTypes.WITHER)) {
         return false;
      } else if (source.is(DamageTypes.WITHER_SKULL)) {
         return false;
      } else if (!super.hurtServer(level, source, amount)) {
         return false;
      } else {
         if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            ReviilFx.onHurt(serverLevel, this);
         }

         return true;
      }
   }

   public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput compound) {
      super.addAdditionalSaveData(compound);
      compound.putString("ModmindsTexture", this.getTexture());
      compound.putInt("ReviilPhase", this.phase());
      compound.putFloat("ReviilSize", (Float)this.entityData.get(SIZE));
   }

   public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput compound) {
      super.readAdditionalSaveData(compound);
      if (compound.getString("ModmindsTexture").isPresent()) {
         this.setTexture(compound.getStringOr("ModmindsTexture", ""));
      }

      int phase = compound.getIntOr("ReviilPhase", 0);
      if (phase > 0) {
         this.entityData.set(PHASE, phase);
         this.entityData.set(SIZE, compound.getFloatOr("ReviilSize", 3.4F));
         this.applyPhaseStats();
      }
   }

   public int phase() {
      return (Integer)this.entityData.get(PHASE);
   }

   public boolean isFury() {
      return this.phase() >= 1;
   }

   public boolean isGiant() {
      return this.phase() == 2;
   }

   public boolean isPushable() {
      return !this.isGiant() && super.isPushable();
   }

   protected void pushEntities() {
   }

   protected EntityDimensions getDefaultDimensions(Pose pose) {
      boolean sized = this.entityData != null;
      return sized && this.isGiant()
         ? super.getDefaultDimensions(pose).scale(1.7F, 1.1F)
         : (sized && this.isFury() ? super.getDefaultDimensions(pose).scale(1.3F, 1.05F) : super.getDefaultDimensions(pose).scale(1.25F, 1.05F));
   }

   @Override
   public float getAgeScale() {
      return this.entityData == null ? 1.4F : (Float)this.entityData.get(SIZE);
   }

   public void setSize(float scale) {
      this.entityData.set(SIZE, Mth.clamp(scale, 1.4F, 3.4F));
      this.refreshDimensions();
   }

   public float phaseScale() {
      return this.isGiant() ? 3.4F : (this.isFury() ? 1.65F : 1.4F);
   }

   public boolean enterFury() {
      if (this.phase() >= 1) {
         return false;
      } else {
         this.entityData.set(PHASE, 1);
         this.setSize(1.65F);
         this.heal(3000.0F);
         this.applyPhaseStats();
         return true;
      }
   }

   public boolean enterGiant() {
      if (this.isGiant()) {
         return false;
      } else {
         this.phaseBeforeGiant = this.phase();
         this.entityData.set(PHASE, 2);
         this.applyPhaseStats();
         return true;
      }
   }

   public boolean exitGiant() {
      if (!this.isGiant()) {
         return false;
      } else {
         this.entityData.set(PHASE, this.phaseBeforeGiant);
         this.applyPhaseStats();
         return true;
      }
   }

   private double phaseWalkSpeed() {
      return this.isGiant() ? 0.0 : (this.isFury() ? 0.3 : 0.23);
   }

   private void applyPhaseStats() {
      if (this.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(130.0 * this.phaseDamageMultiplier());
      }

      if (this.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
         this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.phaseWalkSpeed());
      }
   }

   public double phaseDamageMultiplier() {
      return this.isGiant() ? 2.6 : (this.isFury() ? 1.6 : 1.0);
   }

   public float phaseDamage(float base) {
      return (float)(base * this.phaseDamageMultiplier());
   }

   public double phaseBlastRadius(double base) {
      return this.isGiant() ? base * 1.45 : (this.isFury() ? base * 1.15 : base);
   }

   public void sting(LivingEntity victim) {
      if (this.phase() != 0) {
         victim.addEffect(new MobEffectInstance(MobEffects.WITHER, (this.isGiant() ? 8 : 4) * 20, 0, false, true));
      }
   }

   public InteractionResult mobInteract(Player sourceentity, InteractionHand hand) {
      if (sourceentity.getVehicle() == this) {
         return InteractionResult.PASS;
      } else {
         ItemStack itemstack = sourceentity.getItemInHand(hand);
         if (itemstack.isEmpty() && !sourceentity.isSecondaryUseActive()) {
            InteractionResult retval = InteractionResult.SUCCESS;
            super.mobInteract(sourceentity, hand);
            sourceentity.startRiding(this);
            if (!this.level().isClientSide()) {
               ModmindsModVariables.MapVariables.get(this.level()).RevilPunchCooldown = true;
               ModmindsModVariables.MapVariables.get(this.level()).syncData(this.level());
            }

            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();
            Level world = this.level();
            ReviilAlHacerleClickDerechoProcedure.execute(world, this, sourceentity);
            return retval;
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   public void baseTick() {
      super.baseTick();
      ReviilAlEstarVivaProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
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

   public void aiStep() {
      super.aiStep();
      if (!this.level().isClientSide() && this.escenaTicks > 0) {
         this.escenaTicks--;
      }

      if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel && this.isAggressive() && this.ambientCooldown-- <= 0) {
         this.ambientCooldown = this.isFury() ? 120 + serverLevel.getRandom().nextInt(90) : 200 + serverLevel.getRandom().nextInt(200);
         ReviilFx.ambient(serverLevel, this);
      }

      if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel && this.isFury() && this.phaseAuraCooldown-- <= 0) {
         this.phaseAuraCooldown = this.isGiant() ? 8 : 16;
         ReviilFx.phaseAura(serverLevel, this);
      }

      if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel && this.isAggressive()) {
         if (this.isGiant()) {
            this.giantBarrage(serverLevel);
         } else if (this.isFury()) {
            this.furyBarrage(serverLevel);
         } else {
            this.calmaBarrage(serverLevel);
         }
      }

      if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevelx) {
         this.tickBurla(serverLevelx);
      }
   }

   private void giantBarrage(ServerLevel serverLevel) {
      this.sueltaAlAzar(serverLevel, GIANTE_BARRAGE, 200.0, 100, 70);
   }

   private void furyBarrage(ServerLevel serverLevel) {
      this.sueltaAlAzar(serverLevel, FURIA_BARRAGE, 64.0, 130, 90);
   }

   private void calmaBarrage(ServerLevel serverLevel) {
      this.sueltaAlAzar(serverLevel, CALMA_BARRAGE, 60.0, 220, 120);
   }

   private void sueltaAlAzar(ServerLevel serverLevel, int[] arsenal, double ventana, int baseTicks, int suerteTicks) {
      if (this.escenaTicks <= 0 && BossFx.hayObjetivo(serverLevel, this, ventana)) {
         if (this.ruletaHasta <= serverLevel.getGameTime()) {
            String clip = this.getSyncedAnimation();
            if (clip.equals("empty") || clip.equals("undefined")) {
               if (ModmindsModVariables.MapVariables.get(this.level()).ReviilAtaques == 0.0) {
                  if (this.barrageCooldown-- <= 0) {
                     this.barrageCooldown = baseTicks + serverLevel.getRandom().nextInt(suerteTicks);

                     int attack;
                     do {
                        attack = arsenal[serverLevel.getRandom().nextInt(arsenal.length)];
                     } while (attack == this.barrageLastId);

                     this.barrageLastId = attack;
                     ModmindsModVariables.MapVariables.get(this.level()).ReviilAtaques = attack;
                     ModmindsModVariables.MapVariables.get(this.level()).syncData(this.level());
                  }
               }
            }
         }
      }
   }

   private void tickBurla(ServerLevel serverLevel) {
      if (this.burlaTicks > 0) {
         if (--this.burlaTicks == 0) {
            this.burlaCooldown = 500 + serverLevel.getRandom().nextInt(350);
         }
      } else {
         double ventana = this.isGiant() ? 200.0 : 60.0;
         if (this.escenaTicks <= 0 && BossFx.hayObjetivo(serverLevel, this, ventana) && (Integer)this.entityData.get(GRAB_BEAT) <= 0) {
            String clip = this.getSyncedAnimation();
            if (clip.equals("empty") || clip.equals("undefined")) {
               if (--this.burlaCooldown <= 0) {
                  int burla = this.pickBurla(serverLevel);
                  if (burla < 0) {
                     this.burlaCooldown = 500;
                  } else {
                     this.burlaLastId = burla;
                     this.burlaTicks = REVIIL_BURLA_TICKS[burla];
                     BossFx.sound(serverLevel, BossFx.centre(this), burlaSound(burla), SoundSource.RECORDS, 4.0F, 1.0);
                     SceneRunner.speak(serverLevel, this, BURLA_TEXTO[burla], REVIIL_BURLA_TICKS[burla]);
                  }
               }
            }
         }
      }
   }

   private int pickBurla(ServerLevel serverLevel) {
      for (int attempt = 0; attempt < REVIIL_BURLA_TICKS.length * 3; attempt++) {
         int id = serverLevel.getRandom().nextInt(REVIIL_BURLA_TICKS.length);
         if (id != this.burlaLastId && BURLA_FASE[id] <= (Integer)this.entityData.get(PHASE)) {
            return id;
         }
      }

      return -1;
   }

   private static SoundEvent burlaSound(int id) {
      return switch (id) {
         case 0 -> (SoundEvent)ModmindsModSounds.REVIIL_BURLA_1.get();
         case 1 -> (SoundEvent)ModmindsModSounds.REVIIL_BURLA_2.get();
         case 2 -> (SoundEvent)ModmindsModSounds.REVIIL_BURLA_3.get();
         case 3 -> (SoundEvent)ModmindsModSounds.REVIIL_BURLA_4.get();
         case 4 -> (SoundEvent)ModmindsModSounds.REVIIL_BURLA_5.get();
         case 5 -> (SoundEvent)ModmindsModSounds.REVIIL_BURLA_6.get();
         default -> (SoundEvent)ModmindsModSounds.REVIIL_BURLA_7.get();
      };
   }

   public static void init() {
   }

   public static AttributeSupplier.Builder createAttributes() {
      AttributeSupplier.Builder builder = Mob.createMobAttributes();
      builder = builder.add(Attributes.MOVEMENT_SPEED, 0.23);
      builder = builder.add(Attributes.MAX_HEALTH, 12000.0);
      builder = builder.add(Attributes.ARMOR, 4.0);
      builder = builder.add(Attributes.ARMOR_TOUGHNESS, 6.0);
      builder = builder.add(Attributes.ATTACK_DAMAGE, 130.0);
      builder = builder.add(Attributes.FOLLOW_RANGE, 200.0);
      return builder.add(Attributes.KNOCKBACK_RESISTANCE, 999.0);
   }

   private PlayState movementPredicate(AnimationTest<?> event) {
      if (!this.animationprocedure.equals("empty")) {
         return PlayState.STOP;
      } else if (this.isGiant()) {
         return event.setAndContinue(RawAnimation.begin().thenLoop(this.poseClip(GIANT_POSES, 300L)));
      } else if (this.isFury()) {
         return event.setAndContinue(RawAnimation.begin().thenLoop(this.isFuryWalking(event) ? "emote.fury.walk" : this.poseClip(FURY_POSES, 240L)));
      } else if ((event.isMoving() || !((event.isMoving() ? 1.0F : 0.0F) > -0.15F) || !((event.isMoving() ? 1.0F : 0.0F) < 0.15F)) && !this.isAggressive()) {
         return event.setAndContinue(RawAnimation.begin().thenLoop("extended_pose.standing.moving"));
      } else {
         return this.isAggressive() && event.isMoving()
            ? event.setAndContinue(RawAnimation.begin().thenLoop("extended_pose.standing.sprint"))
            : event.setAndContinue(RawAnimation.begin().thenLoop(this.poseClip(IDLE_POSES, 320L)));
      }
   }

   private String poseClip(String[] pool, long holdTicks) {
      long now = this.level().getGameTime();
      if (pool != this.posePool) {
         this.posePool = pool;
         this.posePick = this.random.nextInt(pool.length);
         this.poseClip = pool[this.posePick];
         this.poseSince = now;
      } else if (now - this.poseSince >= holdTicks) {
         String next;
         do {
            this.posePick = (this.posePick + 1 + this.random.nextInt(pool.length - 1)) % pool.length;
            next = pool[this.posePick];
         } while (next.equals(this.poseClip));

         this.poseClip = next;
         this.poseSince = now;
      }

      return this.poseClip;
   }

   private boolean isFuryWalking(AnimationTest<?> event) {
      if (event.isMoving()) {
         this.furyWalkHold = 8;
      } else if (this.furyWalkHold > 0) {
         this.furyWalkHold--;
      }

      return this.furyWalkHold > 0;
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

   public int hablaCorte() {
      return this.hablaCorte;
   }

   @Override
   public void setAnimation(String animation) {
      if (this.level().isClientSide()) {
         this.entityData.set(ANIMATION, animation);
      } else if (this.escenaTicks <= 0 || animation.equals("empty") || isScenePose(animation)) {
         if (!animation.equals("empty") && !isScenePose(animation)) {
            this.hablaCorte++;
         }

         if (this.getSyncedAnimation().equals(animation)) {
            this.entityData.set(ANIMATION, "undefined");
         }

         this.entityData.set(ANIMATION, animation);
      }
   }

   public static boolean isScenePose(String animation) {
      return animation.startsWith("emote.talk") || animation.startsWith("emote.scene") || animation.startsWith("emote.esc") || animation.equals("emote.shout");
   }

   @Override
   public void releasePose() {
      this.setAnimation("empty");
      this.setGrabHold(0, 100);
      this.setEscapeEffort(0);
      if (this.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
         this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.phaseWalkSpeed());
      }
   }

   public void addEscapeEffort(int amount) {
      this.setEscapeEffort((Integer)this.entityData.get(ESFUERZO) + amount);
   }

   public int escapeEffort() {
      return (Integer)this.entityData.get(ESFUERZO);
   }

   public void setEscapeEffort(int value) {
      this.entityData.set(ESFUERZO, Mth.clamp(value, 0, 100));
   }

   public boolean isGiantHeld(Entity victim) {
      int beat = (Integer)this.entityData.get(GRAB_BEAT);
      return (beat == 5 || beat == 6) && victim.getVehicle() == this;
   }

   public int grabBeat() {
      return (Integer)this.entityData.get(GRAB_BEAT);
   }

   public void setGrabHold(int beat, int progress) {
      this.entityData.set(GRAB_BEAT, beat);
      this.entityData.set(GRAB_PROGRESS, Mth.clamp(progress, 0, 100));
   }

   public Vec3 grabFistPosition() {
      return this.position().add(this.grabFistOffset());
   }

   public Vec3 grabFistOffset() {
      int beat = (Integer)this.entityData.get(GRAB_BEAT);
      double rise = Mth.clamp((Integer)this.entityData.get(GRAB_PROGRESS), 0, 100) / 100.0;
      double height = this.getBbHeight();
      double up;
      double forward;
      if (beat <= 1) {
         up = height * (0.26 + 0.46 * rise);
         forward = 1.5 + 0.45 * rise;
      } else if (beat == 2) {
         up = height * 0.72 + Math.sin(this.tickCount * 0.32) * 0.08;
         forward = 1.95;
      } else if (beat == 3) {
         up = height * (0.74 + 0.34 * rise);
         forward = 1.95 - 1.05 * rise;
      } else if (beat == 5) {
         up = height * (0.2 + 0.52 * rise) + Math.sin(this.tickCount * 0.55) * 0.05;
         forward = 1.55 + 0.37 * rise;
      } else if (beat == 6) {
         up = height * (0.06 + 0.66 * rise) + (rise > 0.02 ? Math.sin(this.tickCount * 1.4) * 0.04 : 0.0);
         forward = 5.0 - 3.08 * rise;
      } else {
         up = height * 1.1;
         forward = 0.85;
      }

      double yaw = Math.toRadians(this.getYRot());
      return new Vec3(-Mth.sin((float)yaw) * forward, up, Mth.cos((float)yaw) * forward);
   }

   protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float partialTick) {
      return this.entityData.get(GRAB_BEAT) <= 0 ? super.getPassengerAttachmentPoint(passenger, dimensions, partialTick) : this.grabFistOffset();
   }

   @Override
   public Entity sceneEntity() {
      return this;
   }

   public void registerControllers(ControllerRegistrar data) {
      data.add(new AnimationController<>("movement", 10, this::movementPredicate));
      data.add(new AnimationController<>("procedure", 6, this::procedurePredicate));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public float maxUpStep() {
      return 1.0F;
   }
}
