package com.ghostsafio.mitomod;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class SkiffPhoraEntity extends Monster implements AttackAnimMob {
   public static final int ATTACK_ANIM_TICKS = 14;
   public static final int SHIELD_HITS_TO_BREAK = 4;
   private static final ResourceKey<DamageType> SKIFF_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, Ghostsafio.id("skiff_phora_attack"));
   private static final EntityDataAccessor<Integer> ATTACK_ANIM = SynchedEntityData.defineId(SkiffPhoraEntity.class, EntityDataSerializers.INT);
   private int shieldHits = 0;
   private UUID shieldTarget = null;
   private int idleSoundTicks = 60;
   private static final int HOVER_HEIGHT = 4;

   public SkiffPhoraEntity(EntityType<? extends SkiffPhoraEntity> type, Level level) {
      super(type, level);
      this.xpReward = 12;
      this.moveControl = new FlyingMoveControl(this, 20, true);
      this.navigation = new FlyingPathNavigation(this, level);
      this.setNoGravity(true);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
      this.setPathfindingMalus(PathType.LAVA, -1.0F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(ATTACK_ANIM, 0);
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 24.0)
         .add(Attributes.ATTACK_DAMAGE, 12.0)
         .add(Attributes.MOVEMENT_SPEED, 0.29)
         .add(Attributes.FLYING_SPEED, 0.6)
         .add(Attributes.FOLLOW_RANGE, 40.0)
         .add(Attributes.ATTACK_KNOCKBACK, 0.4);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.35, false));
      this.goalSelector.addGoal(2, new SkiffPhoraEntity.FlyAroundGoal(this, 12.0));
      this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0F));
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   private void hoverHeight() {
      if (this.getTarget() == null && this.tickCount % 10 == 0) {
         BlockPos p = this.blockPosition();
         int ground = Integer.MIN_VALUE;

         for (int i = 0; i <= 12; i++) {
            BlockPos q = p.below(i);
            if (this.level().getBlockState(q).blocksMotion()) {
               ground = q.getY();
               break;
            }
         }

         if (ground != Integer.MIN_VALUE) {
            double want = ground + 1 + 4;
            double dy = this.getY() - want;
            if (dy < -0.5) {
               this.setDeltaMovement(this.getDeltaMovement().x, 0.05, this.getDeltaMovement().z);
            } else if (dy > 1.5) {
               this.setDeltaMovement(this.getDeltaMovement().x, -0.05, this.getDeltaMovement().z);
            }
         }
      }
   }

   @Override
   public int getAttackAnim() {
      return (Integer)this.entityData.get(ATTACK_ANIM);
   }

   @Override
   public int attackAnimTicks() {
      return 14;
   }

   private DamageSource skiffDamage() {
      Holder<DamageType> holder = this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SKIFF_DAMAGE);
      return new DamageSource(holder, this, this);
   }

   public boolean doHurtTarget(Entity target) {
      if (!this.level().isClientSide) {
         this.entityData.set(ATTACK_ANIM, 14);
      }

      DamageSource source = this.skiffDamage();
      float damage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
      float knockback = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
      if (target instanceof Player player && this.playerBlocks(player)) {
         this.hitShield(player, damage);
         return false;
      } else {
         boolean hit = target.hurt(source, damage);
         if (hit) {
            this.resetShieldCounter();
            if (knockback > 0.0F && target instanceof LivingEntity living) {
               living.knockback(knockback * 0.5, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)));
               this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }
         }

         return hit;
      }
   }

   private boolean playerBlocks(Player player) {
      if (player.isBlocking() && player.getUseItem().is(Items.SHIELD)) {
         Vec3 toSkiff = this.position().subtract(player.position());
         Vec3 flat = new Vec3(toSkiff.x, 0.0, toSkiff.z);
         if (flat.lengthSqr() < 1.0E-4) {
            return true;
         }

         Vec3 look = player.calculateViewVector(0.0F, player.getYHeadRot());
         Vec3 lookFlat = new Vec3(look.x, 0.0, look.z).normalize();
         return flat.normalize().dot(lookFlat) > -0.25;
      } else {
         return false;
      }
   }

   private void hitShield(Player player, float damage) {
      this.level().broadcastEntityEvent(player, (byte)29);
      ItemStack shield = player.getUseItem();
      if (!this.level().isClientSide && shield.is(Items.SHIELD) && damage >= 3.0F) {
         EquipmentSlot slot = LivingEntity.getSlotForHand(player.getUsedItemHand());
         shield.hurtAndBreak(1 + Mth.floor(damage), player, slot);
      }

      this.knockback(0.5, this.getX() - player.getX(), this.getZ() - player.getZ());
      this.countShieldHit(player);
   }

   private void resetShieldCounter() {
      this.shieldHits = 0;
      this.shieldTarget = null;
   }

   private void countShieldHit(Player player) {
      if (!player.getUUID().equals(this.shieldTarget)) {
         this.shieldTarget = player.getUUID();
         this.shieldHits = 0;
      }

      this.shieldHits++;
      if (this.shieldHits >= 4) {
         this.breakShield(player);
         this.resetShieldCounter();
      }
   }

   private void breakShield(Player player) {
      ItemStack shield = player.getUseItem();
      if (shield.is(Items.SHIELD)) {
         InteractionHand hand = player.getUsedItemHand();
         EquipmentSlot slot = LivingEntity.getSlotForHand(hand);
         int remaining = Math.max(1, shield.getMaxDamage() - shield.getDamageValue());
         player.stopUsingItem();
         shield.hurtAndBreak(remaining, player, slot);
         player.level()
            .playSound(
               null,
               player.getX(),
               player.getY(),
               player.getZ(),
               SoundEvents.SHIELD_BREAK,
               SoundSource.PLAYERS,
               0.8F,
               0.8F + player.level().random.nextFloat() * 0.4F
            );
      }
   }

   public void tick() {
      super.tick();
      this.hoverHeight();
      if (!this.level().isClientSide) {
         int a = this.getAttackAnim();
         if (a > 0) {
            this.entityData.set(ATTACK_ANIM, a - 1);
         }

         if (this.idleSoundTicks > 0) {
            this.idleSoundTicks--;
         } else if (this.getTarget() == null && this.getDeltaMovement().horizontalDistanceSqr() < 0.0015) {
            this.playSound(ModSounds.SKIFF_IDLE, 0.9F, 0.85F + this.random.nextFloat() * 0.3F);
            this.idleSoundTicks = 180 + this.random.nextInt(140);
         }
      }
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData spawnGroupData) {
      this.playSound(ModSounds.SKIFF_APARECE, 1.0F, 0.95F + this.random.nextFloat() * 0.1F);
      return super.finalizeSpawn(level, difficulty, reason, spawnGroupData);
   }

   public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource source) {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return null;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENDERMITE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENDERMITE_DEATH;
   }

   protected float getSoundVolume() {
      return 0.7F;
   }

   private static class FlyAroundGoal extends Goal {
      private final SkiffPhoraEntity skiff;
      private final double range;
      private double tx;
      private double ty;
      private double tz;
      private int wait;

      FlyAroundGoal(SkiffPhoraEntity skiff, double range) {
         this.skiff = skiff;
         this.range = range;
      }

      public boolean canUse() {
         if (this.skiff.getTarget() != null) {
            return false;
         }

         if (this.wait > 0) {
            this.wait--;
            return false;
         }

         Level level = this.skiff.level();
         RandomSource rnd = this.skiff.getRandom();

         for (int tries = 0; tries < 12; tries++) {
            double x = this.skiff.getX() + (rnd.nextDouble() * 2.0 - 1.0) * this.range;
            double z = this.skiff.getZ() + (rnd.nextDouble() * 2.0 - 1.0) * this.range;
            double y = this.skiff.getY() + (rnd.nextDouble() * 2.0 - 1.0) * 5.0;
            BlockPos p = BlockPos.containing(x, y, z);
            if (level.isEmptyBlock(p) && level.isEmptyBlock(p.above()) && p.getY() >= level.getMinBuildHeight() + 2) {
               this.tx = x;
               this.ty = y;
               this.tz = z;
               return true;
            }
         }

         this.wait = 40;
         return false;
      }

      public void start() {
         this.skiff.getNavigation().moveTo(this.tx, this.ty, this.tz, 0.9);
      }

      public boolean canContinueToUse() {
         return this.skiff.getTarget() == null && !this.skiff.getNavigation().isDone();
      }

      public void stop() {
         this.skiff.getNavigation().stop();
         this.wait = 60 + this.skiff.getRandom().nextInt(100);
      }
   }
}
