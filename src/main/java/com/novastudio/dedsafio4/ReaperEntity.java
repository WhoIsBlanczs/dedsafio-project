package com.novastudio.dedsafio4;

import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.server.MinecraftServer;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public class ReaperEntity extends Monster implements GeoEntity {
   static final int REAP_TICKS = 8;
   private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
   private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private ServerPlayer victim;
   private int reap;
   public static Consumer<ServerPlayer> ON_REAP = ReaperEntity::sendToSpawn;

   private static void sendToSpawn(ServerPlayer player) {
      MinecraftServer server = player.level().getServer();
      ServerLevel level = server.getLevel(com.novastudio.dedsafio4.compat.Compat.respawnDim(player));
      BlockPos pos = com.novastudio.dedsafio4.compat.Compat.respawnPos(player);
      if (level == null || pos == null) {
         level = server.overworld();
         pos = com.novastudio.dedsafio4.compat.Compat.spawnMundo(level);
      }

      player.fallDistance = 0.0F;
      com.novastudio.dedsafio4.compat.Compat.teleport(player, level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getYRot(), player.getXRot());
   }

   public ReaperEntity(EntityType<? extends Monster> var1, Level var2) {
      super(var1, var2);
   }

   static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MOVEMENT_SPEED, 0.3)
         .add(Attributes.MAX_HEALTH, 60.0)
         .add(Attributes.ATTACK_DAMAGE, 16.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, false));
      this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 16.0F));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, false));
   }

   public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity var1) {
      if (var1 instanceof ServerPlayer var2 && !var2.isCreative() && !var2.isSpectator()) {
         if (this.victim == null) {
            this.victim = var2;
            this.reap = 8;
            this.triggerAnim("main", "attack");
            this.playSound(SoundEvents.WARDEN_SONIC_BOOM, 0.8F, 1.4F);
         }

         return true;
      } else {
         return super.doHurtTarget(level, var1);
      }
   }

   protected void customServerAiStep(net.minecraft.server.level.ServerLevel level) {
      super.customServerAiStep(level);
      if (this.victim != null && --this.reap <= 0) {
         ServerPlayer var1 = this.victim;
         this.victim = null;
         if (!var1.isRemoved() && var1.isAlive() && !(this.distanceTo(var1) > 4.5F)) {
            ON_REAP.accept(var1);
            if (this.level() instanceof ServerLevel var2) {
               var2.sendParticles(
                  ParticleTypes.LARGE_SMOKE,
                  this.getX(),
                  this.getY() + this.getBbHeight() / 2.0F,
                  this.getZ(),
                  40,
                  0.5,
                  this.getBbHeight() / 3.0F,
                  0.5,
                  0.02
               );
               var2.sendParticles(
                  ParticleTypes.SOUL,
                  this.getX(),
                  this.getY() + this.getBbHeight() / 2.0F,
                  this.getZ(),
                  20,
                  0.6,
                  this.getBbHeight() / 3.0F,
                  0.6,
                  0.05
               );
            }

            this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.5F, 0.5F);
            this.discard();
         }
      }
   }

   public void registerControllers(ControllerRegistrar var1) {
      AnimationController<ReaperEntity> var2 = new AnimationController<>("main", 4, this::animate);
      var2.triggerableAnim("attack", ATTACK);
      var1.add(var2);
   }

   private PlayState animate(AnimationTest<ReaperEntity> var1) {
      return var1.setAndContinue(var1.isMoving() ? WALK : IDLE);
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
