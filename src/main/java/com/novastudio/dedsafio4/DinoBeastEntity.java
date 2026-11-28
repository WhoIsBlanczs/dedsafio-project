package com.novastudio.dedsafio4;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.animation.object.LoopType;
import com.geckolib.util.GeckoLibUtil;

public class DinoBeastEntity extends PathfinderMob implements GeoEntity {
   static final Map<EntityType<?>, DinoBeastEntity.Species> SPECIES = new HashMap<>();
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   static final int DIGEST_TICKS = 100;
   private int digest;

   public DinoBeastEntity(EntityType<? extends PathfinderMob> var1, Level var2) {
      super(var1, var2);
      if (this.species().flying()) {
         this.moveControl = new FlyingMoveControl(this, 10, false);
      }
   }

   DinoBeastEntity.Species species() {
      return SPECIES.get(this.getType());
   }

   static AttributeSupplier.Builder createAttributes(DinoBeastEntity.Species var0) {
      AttributeSupplier.Builder var1 = PathfinderMob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, var0.health())
         .add(Attributes.MOVEMENT_SPEED, var0.speed())
         .add(Attributes.ATTACK_DAMAGE, var0.damage())
         .add(Attributes.FOLLOW_RANGE, var0.carnivore() ? 40.0 : 24.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, var0.knockbackResistance());
      if (var0.flying()) {
         var1.add(Attributes.FLYING_SPEED, 0.6);
      }

      return var1;
   }

   private RawAnimation once(String var1) {
      return RawAnimation.begin().then("animation." + this.species().id() + "." + var1, LoopType.PLAY_ONCE);
   }

   private RawAnimation loop(String var1) {
      return RawAnimation.begin().thenLoop("animation." + this.species().id() + "." + var1);
   }

   protected PathNavigation createNavigation(Level var1) {
      return (PathNavigation)(this.species().flying() ? new FlyingPathNavigation(this, var1) : super.createNavigation(var1));
   }

   protected void registerGoals() {
      DinoBeastEntity.Species var1 = this.species();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, var1.carnivore() ? 1.3 : 1.25, true));
      this.goalSelector.addGoal(5, (Goal)(var1.flying() ? new WaterAvoidingRandomFlyingGoal(this, 1.0) : new WaterAvoidingRandomStrollGoal(this, var1.carnivore() ? 0.8 : 0.6)));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[]{DinoBeastEntity.class}));
      if (var1.carnivore()) {
         this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
         this.targetSelector
            .addGoal(
               3, new NearestAttackableTargetGoal(this, DinoBeastEntity.class, 120, true, false, (var0, nivel$) -> var0 instanceof DinoBeastEntity var1x && !var1x.species().carnivore())
            );
      }
   }

   public void setTarget(LivingEntity var1) {
      boolean var2 = var1 != null && this.getTarget() == null;
      super.setTarget(var1);
      DinoBeastEntity.Species var3 = this.species();
      if (var2 && var3.carnivore() && !this.level().isClientSide()) {
         if (var3.roarAnim() != null) {
            this.triggerAnim("main", "roar");
         }

         this.playSound(var3.roar(), 1.6F, var3.soundPitch());
      }
   }

   public void tick() {
      super.tick();
      if (this.level() instanceof ServerLevel var1 && this.digest > 0 && --this.digest == 0) {
         this.poop(var1);
      }
   }

   protected InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (!this.species().carnivore()
         && this.digest <= 0
         && BuiltInRegistries.ITEM.getKey(var3.getItem()).equals(Identifier.fromNamespaceAndPath("nexo", "fruta_solaria"))) {
         if (this.level() instanceof ServerLevel var4) {
            var3.consume(1, var1);
            this.digest = 100;
            if (this.species().eat() != null) {
               this.triggerAnim("main", "eat");
            }

            this.playSound(SoundEvents.GENERIC_EAT.value(), 1.2F, 0.6F);
            var4.sendParticles(
               ParticleTypes.HEART, this.getX(), this.getY() + this.getBbHeight() * 0.8, this.getZ(), 5, 0.6, 0.4, 0.6, 0.1
            );
         }

         return InteractionResult.SUCCESS;
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   private void poop(ServerLevel var1) {
      double var2 = Math.toRadians(this.yBodyRot);
      double var4 = this.getBbWidth() / 2.0F + 0.6;
      double var6 = Math.sin(var2);
      double var8 = Math.cos(var2);
      BlockPos var10 = BlockPos.containing(
         this.getX() + var6 * var4, this.getY() + this.getBbHeight() * 0.4, this.getZ() - var8 * var4
      );

      for (int var11 = 0; var11 < 3 && !var1.getBlockState(var10).isAir(); var11++) {
         var10 = var10.above();
      }

      if (var1.getBlockState(var10).isAir()) {
         FallingBlockEntity var12 = FallingBlockEntity.fall(var1, var10, DinoContent.CACA_BLOCK.defaultBlockState());
         var12.setDeltaMovement(var6 * 0.25, 0.1, -var8 * 0.25);
         this.playSound(SoundEvents.SLIME_SQUISH, 1.0F, 0.5F);
      }
   }

   public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("NexoDigest", this.digest);
   }

   public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput var1) {
      super.readAdditionalSaveData(var1);
      if (var1.getIntOr("NexoTrap", 0) != 0) {
         this.setNoAi(false);
      }

      this.digest = var1.getIntOr("NexoDigest", 0);
   }

   public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity var1) {
      boolean var2 = super.doHurtTarget(level, var1);
      if (var2) {
         this.triggerAnim("main", "attack" + this.getRandom().nextInt(this.species().attacks().length));
      }

      if (var2 && this.species().id().equals("t_rex") && var1 instanceof ServerPlayer var3) {
         MemeTrap.trapTrex(var3);
      }

      return var2;
   }

   protected void customServerAiStep(net.minecraft.server.level.ServerLevel level) {
      super.customServerAiStep(level);
      DinoBeastEntity.Species var1 = this.species();
      if (this.getTarget() == null && this.onGround() && this.getNavigation().isDone()) {
         if (var1.eat() != null && this.getRandom().nextInt(var1.carnivore() ? 1500 : 500) == 0) {
            this.triggerAnim("main", "eat");
         } else if (var1.idleExtra() != null && this.getRandom().nextInt(600) == 0) {
            this.triggerAnim("main", "extra");
         }
      }
   }

   public void aiStep() {
      super.aiStep();
      if (this.species().flying()) {
         Vec3 var1 = this.getDeltaMovement();
         if (!this.onGround() && var1.y < 0.0) {
            this.setDeltaMovement(var1.multiply(1.0, 0.6, 1.0));
         }
      }
   }

   public boolean causeFallDamage(double var1, float var2, DamageSource var3) {
      return !this.species().flying() && super.causeFallDamage(var1, var2, var3);
   }

   public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource var1, float var2) {
      boolean var3 = super.hurtServer(level, var1, var2);
      if (var3 && !this.level().isClientSide() && this.species().hurtAnim() != null && this.getRandom().nextInt(3) == 0) {
         this.triggerAnim("main", "hurt");
      }

      return var3;
   }

   protected boolean shouldDespawnInPeaceful() {
      return this.species().carnivore();
   }

   public boolean removeWhenFarAway(double var1) {
      return this.species().carnivore();
   }

   protected SoundEvent getAmbientSound() {
      return this.species().ambient();
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return this.species().hurt();
   }

   protected SoundEvent getDeathSound() {
      return this.species().death();
   }

   public float getVoicePitch() {
      return this.species().soundPitch() + (this.getRandom().nextFloat() - 0.5F) * 0.1F;
   }

   public void registerControllers(ControllerRegistrar var1) {
      DinoBeastEntity.Species var2 = this.species();
      AnimationController var3 = new AnimationController<>("main", 5, this::animate);

      for (int var4 = 0; var4 < var2.attacks().length; var4++) {
         var3.triggerableAnim("attack" + var4, this.once(var2.attacks()[var4]));
      }

      if (var2.roarAnim() != null) {
         var3.triggerableAnim("roar", this.once(var2.roarAnim()));
      }

      if (var2.eat() != null) {
         var3.triggerableAnim("eat", this.once(var2.eat()));
      }

      if (var2.idleExtra() != null) {
         var3.triggerableAnim("extra", this.once(var2.idleExtra()));
      }

      if (var2.hurtAnim() != null) {
         var3.triggerableAnim("hurt", this.once(var2.hurtAnim()));
      }

      var1.add(var3);
   }

   private PlayState animate(AnimationTest<DinoBeastEntity> var1) {
      DinoBeastEntity.Species var2 = this.species();
      if (var2.flying() && !this.onGround() && !this.isInWater()) {
         double var3 = this.getY() - this.yo;
         return var1.setAndContinue(this.loop(var3 < -0.06 ? "glide" : "fly"));
      } else {
         return !var1.isMoving()
            ? var1.setAndContinue(this.loop("idle"))
            : var1.setAndContinue(this.loop(!var2.flying() && this.isAggressive() && var2.run() != null ? var2.run() : "walk"));
      }
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public record Species(
      String id,
      boolean carnivore,
      boolean flying,
      double health,
      double speed,
      double damage,
      double knockbackResistance,
      float soundPitch,
      SoundEvent ambient,
      SoundEvent hurt,
      SoundEvent death,
      SoundEvent roar,
      String run,
      String[] attacks,
      String roarAnim,
      String eat,
      String idleExtra,
      String hurtAnim
   ) {
   }
}
