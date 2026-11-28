package com.ghostsafio.mitomod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class PodPhoraEntity extends Monster implements RangedAttackMob, AttackAnimMob {
   public static final int ATTACK_ANIM_TICKS = 10;
   public static final int SHOTS_TO_BREAK_SHIELD = 10;
   public static final float LASER_DAMAGE = 6.0F;
   private static final EntityDataAccessor<Integer> ATTACK_ANIM = SynchedEntityData.defineId(PodPhoraEntity.class, EntityDataSerializers.INT);
   private static final Map<UUID, Integer> SHIELD_SHOTS = new HashMap<>();
   private int idleSoundTicks = 90;
   private static final int HOVER_HEIGHT = 4;

   public PodPhoraEntity(EntityType<? extends PodPhoraEntity> type, Level level) {
      super(type, level);
      this.xpReward = 18;
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
         .add(Attributes.MAX_HEALTH, 30.0)
         .add(Attributes.ATTACK_DAMAGE, 6.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3)
         .add(Attributes.FLYING_SPEED, 0.7)
         .add(Attributes.FOLLOW_RANGE, 48.0);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0, 30, 14.0F));
      this.goalSelector.addGoal(2, new PodPhoraEntity.FlyAroundGoal(this, 12.0));
      this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 24.0F));
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
      return 10;
   }

   public void performRangedAttack(LivingEntity target, float distanceFactor) {
      if (!this.level().isClientSide) {
         this.entityData.set(ATTACK_ANIM, 10);
         PodPhoraLaserEntity laser = new PodPhoraLaserEntity(ModMobs.POD_LASER, this.level());
         Vec3 from = new Vec3(this.getX(), this.getY() + 0.5, this.getZ());
         Vec3 aim = new Vec3(target.getX() - from.x, target.getY(0.4) - from.y, target.getZ() - from.z).normalize();
         from = from.add(aim.scale(0.45));
         laser.setPos(from.x, from.y, from.z);
         laser.setOwner(this);
         laser.shoot(aim.x, aim.y, aim.z, 1.35F, 0.4F);
         this.level().addFreshEntity(laser);
         if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, from.x, from.y, from.z, 8, 0.06, 0.06, 0.06, 0.03);
         }

         this.playSound(ModSounds.POD_LASER, 1.2F, 0.9F + this.random.nextFloat() * 0.2F);
         this.setDeltaMovement(this.getDeltaMovement().add(aim.scale(-0.06)));
      }
   }

   public static void onLaserBlocked(Level level, Player player) {
      level.broadcastEntityEvent(player, (byte)29);
      ItemStack shield = player.getUseItem();
      if (shield.is(Items.SHIELD)) {
         EquipmentSlot slot = LivingEntity.getSlotForHand(player.getUsedItemHand());
         shield.hurtAndBreak(1 + Mth.floor(6.0F), player, slot);
         int hits = SHIELD_SHOTS.merge(player.getUUID(), 1, Integer::sum);
         if (hits >= 10) {
            breakShield(player);
            SHIELD_SHOTS.remove(player.getUUID());
         }
      }
   }

   public static void onLaserHitPlayer(Player player) {
      SHIELD_SHOTS.remove(player.getUUID());
   }

   private static void breakShield(Player player) {
      ItemStack shield = player.getUseItem();
      if (shield.is(Items.SHIELD)) {
         EquipmentSlot slot = LivingEntity.getSlotForHand(player.getUsedItemHand());
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
            this.playSound(ModSounds.POD_IDLE, 0.8F, 0.9F + this.random.nextFloat() * 0.2F);
            this.idleSoundTicks = 180 + this.random.nextInt(140);
         }
      }
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData spawnGroupData) {
      this.playSound(ModSounds.POD_APARECE, 1.0F, 0.95F + this.random.nextFloat() * 0.1F);
      return super.finalizeSpawn(level, difficulty, reason, spawnGroupData);
   }

   public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource source) {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return null;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.SHULKER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SHULKER_DEATH;
   }

   protected float getSoundVolume() {
      return 0.8F;
   }

   private static class FlyAroundGoal extends Goal {
      private final PodPhoraEntity pod;
      private final double range;
      private double tx;
      private double ty;
      private double tz;
      private int wait;

      FlyAroundGoal(PodPhoraEntity pod, double range) {
         this.pod = pod;
         this.range = range;
      }

      public boolean canUse() {
         if (this.pod.getTarget() != null) {
            return false;
         }

         if (this.wait > 0) {
            this.wait--;
            return false;
         }

         Level level = this.pod.level();
         RandomSource rnd = this.pod.getRandom();

         for (int tries = 0; tries < 12; tries++) {
            double x = this.pod.getX() + (rnd.nextDouble() * 2.0 - 1.0) * this.range;
            double z = this.pod.getZ() + (rnd.nextDouble() * 2.0 - 1.0) * this.range;
            double y = this.pod.getY() + (rnd.nextDouble() * 2.0 - 1.0) * 5.0;
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
         this.pod.getNavigation().moveTo(this.tx, this.ty, this.tz, 0.9);
      }

      public boolean canContinueToUse() {
         return this.pod.getTarget() == null && !this.pod.getNavigation().isDone();
      }

      public void stop() {
         this.pod.getNavigation().stop();
         this.wait = 60 + this.pod.getRandom().nextInt(100);
      }
   }
}
