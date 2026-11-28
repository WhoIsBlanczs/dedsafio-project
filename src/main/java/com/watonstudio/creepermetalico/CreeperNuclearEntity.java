package com.watonstudio.creepermetalico;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public class CreeperNuclearEntity extends Monster implements GeoEntity {
   private static final EntityDataAccessor<Boolean> DETONATED = SynchedEntityData.defineId(CreeperNuclearEntity.class, EntityDataSerializers.BOOLEAN);
   private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
   private int fuseTicks = 0;
   private int explosionAnimationTicks = 0;
   private boolean explosionExecuted = false;
   private static final int EXPLOSION_ANIMATION_LENGTH = 35;
   private static final double FUSE_DISTANCE = 4.0;
   private static final double RADIATION_RADIUS = 24.0;
   private static final float MAX_RADIATION_DAMAGE = 80.0F;
   private static final int CRATER_RADIUS = 12;
   private static final int CRATER_DEPTH = 8;
   private static final int MAX_BLOCKS_TO_REMOVE = 2500;
   private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.creeper_nuclear.idle");
   private static final RawAnimation EXPLODE = RawAnimation.begin().thenPlay("animation.creeper_nuclear.explode");

   public CreeperNuclearEntity(EntityType<? extends CreeperNuclearEntity> type, Level world) {
      super(type, world);
      this.xpReward = 10;
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DETONATED, false);
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createCreeperNuclearAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 80.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.ATTACK_DAMAGE, 8.0)
         .add(Attributes.FOLLOW_RANGE, 32.0);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false) {
         protected void checkAndPerformAttack(LivingEntity target) {
         }
      });
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController<>("nuclear_controller", 0, state -> this.entityData.get(DETONATED) ? state.setAndContinue(EXPLODE) : state.setAndContinue(IDLE)
         )
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.geoCache;
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide() && this.isAlive()) {
         if ((Boolean)this.entityData.get(DETONATED)) {
            this.explosionAnimationTicks++;
            if (this.explosionAnimationTicks >= 35) {
               this.detonate();
            }
         } else {
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive()) {
               if (this.distanceToSqr(target) > 16.0) {
                  this.fuseTicks = 0;
                  this.getNavigation().moveTo(target, 1.0);
               } else {
                  this.getNavigation().stop();
                  if (this.fuseTicks == 0) {
                     this.playSound(SoundEvents.CREEPER_PRIMED, 1.5F, 0.5F);
                     this.entityData.set(DETONATED, true);
                     this.explosionAnimationTicks = 0;
                  }

                  this.fuseTicks++;
               }
            } else {
               this.fuseTicks = 0;
            }
         }
      }
   }

   private void detonate() {
      if (this.level() instanceof ServerLevel serverWorld) {
         if (!this.explosionExecuted) {
            this.explosionExecuted = true;
            double centerX = this.getX();
            double centerY = this.getY();
            double centerZ = this.getZ();
            BlockPos centerPos = BlockPos.containing(centerX, centerY, centerZ);

            for (Player player : serverWorld.players()) {
               double dx = player.getX() - centerX;
               double dy = player.getY() - centerY;
               double dz = player.getZ() - centerZ;
               double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
               if (distance <= 24.0 && player.isAlive()) {
                  float damage = (float)(80.0 * (1.0 - distance / 24.0));
                  if (damage > 0.0F) {
                     player.hurt(this.damageSources().explosion(this, this), damage);
                  }
               }
            }

            serverWorld.sendParticles(ParticleTypes.EXPLOSION_EMITTER, centerX, centerY, centerZ, 1, 0.0, 0.0, 0.0, 0.0);
            serverWorld.playSound(null, centerPos, (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 4.0F, 0.5F);
            MutableBlockPos pos = new MutableBlockPos();
            int removed = 0;

            for (int x = -12; x <= 12; x++) {
               for (int z = -12; z <= 12; z++) {
                  double normalizedDistance = (x * x + z * z) / 144.0;
                  if (!(normalizedDistance > 1.0)) {
                     int localDepth = Math.max(2, (int)(8.0 * (1.0 - normalizedDistance)) + 2);

                     for (int depth = 0; depth < localDepth && removed < 2500; depth++) {
                        int y = centerPos.getY() - depth;
                        if (y <= serverWorld.getMinY()) {
                           break;
                        }

                        pos.set(centerPos.getX() + x, y, centerPos.getZ() + z);
                        BlockState blockState = serverWorld.getBlockState(pos);
                        if (!blockState.isAir()
                           && !blockState.is(Blocks.BEDROCK)
                           && !blockState.is(Blocks.END_PORTAL)
                           && !blockState.is(Blocks.END_PORTAL_FRAME)) {
                           serverWorld.destroyBlock(pos, false, this);
                           removed++;
                        }
                     }

                     if (removed >= 2500) {
                        break;
                     }
                  }
               }

               if (removed >= 2500) {
                  break;
               }
            }

            this.discard();
         }
      }
   }
}
