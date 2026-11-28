package com.novastudio.dedsafio4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
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
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public class MemeCreeperEntity extends Monster implements GeoEntity {
   private static final EntityDataAccessor<Boolean> TRAPPING = SynchedEntityData.defineId(MemeCreeperEntity.class, EntityDataSerializers.BOOLEAN);
   static final int TRAP_TICKS = 200;
   static final int EXPLODE_DELAY = 25;
   static final int COOLDOWN = 200;
   private static final Identifier FREEZE = Identifier.fromNamespaceAndPath("nexo", "meme_creeper_trap");
   private static final Map<UUID, Long> READY = new HashMap<>();
   static final Map<UUID, MemeCreeperEntity> HOLDING = new HashMap<>();
   private static final RawAnimation IDLE = loop("idle");
   private static final RawAnimation WALK = loop("walk");
   private static final RawAnimation RUN = loop("run");
   private static final RawAnimation CHARGE = loop("charge");
   private static final String[] ACTIONS = new String[]{"attack", "hurt", "spawn", "explode"};
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private UUID trapped;
   private int trapTicks;

   public MemeCreeperEntity(EntityType<? extends Monster> var1, Level var2) {
      super(var1, var2);
   }

   private static RawAnimation loop(String var0) {
      return RawAnimation.begin().thenLoop("animation.meme_creeper." + var0);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.ATTACK_DAMAGE, 3.0);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(TRAPPING, false);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
   }

   public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity var1) {
      if (var1 instanceof ServerPlayer var2
         && this.trapped == null
         && !var2.isCreative()
         && !var2.isSpectator()
         && !HOLDING.containsKey(var2.getUUID())
         && this.level().getGameTime() >= READY.getOrDefault(var2.getUUID(), 0L)) {
         this.trap(var2);
         return true;
      } else {
         boolean var3 = super.doHurtTarget(level, var1);
         if (var3) {
            this.triggerAnim("main", "attack");
         }

         return var3;
      }
   }

   private void trap(ServerPlayer var1) {
      this.trapped = var1.getUUID();
      this.trapTicks = 0;
      HOLDING.put(this.trapped, this);
      this.entityData.set(TRAPPING, true);
      freeze(var1, true);
      this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.6F);
      MemeTrap.send(var1, true);
   }

   private static void freeze(ServerPlayer var0, boolean var1) {
      for (Holder<Attribute> var3 : List.of(Attributes.MOVEMENT_SPEED, Attributes.JUMP_STRENGTH)) {
         AttributeInstance var4 = var0.getAttribute(var3);
         if (var4 != null) {
            var4.removeModifier(FREEZE);
            if (var1) {
               var4.addTransientModifier(new AttributeModifier(FREEZE, -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
         }
      }
   }

   void release(ServerPlayer var1, boolean var2) {
      if (this.trapped != null) {
         HOLDING.remove(this.trapped);
         READY.put(this.trapped, this.level().getGameTime() + 200L);
         this.trapped = null;
         this.entityData.set(TRAPPING, false);
         if (var1 != null) {
            freeze(var1, false);
            MemeTrap.send(var1, false);
         }
      }
   }

   private ServerPlayer trappedPlayer() {
      return this.trapped != null && this.level().getServer() != null ? this.level().getServer().getPlayerList().getPlayer(this.trapped) : null;
   }

   protected void customServerAiStep(net.minecraft.server.level.ServerLevel level) {
      super.customServerAiStep(level);
      if (this.tickCount == 2) {
         this.triggerAnim("main", "spawn");
      }

      if (this.trapped != null) {
         ServerPlayer var1 = this.trappedPlayer();
         if (var1 != null && var1.isAlive() && var1.level() == this.level() && !(this.distanceToSqr(var1) > 25.0)) {
            this.getNavigation().stop();
            this.getLookControl().setLookAt(var1);
            this.trapTicks++;
            if (this.trapTicks == 200) {
               this.triggerAnim("main", "explode");
               this.playSound(SoundEvents.CREEPER_PRIMED, 1.2F, 0.5F);
            } else if (this.trapTicks >= 225) {
               this.release(var1, false);
               this.level().explode(this, this.getX(), this.getY(), this.getZ(), 3.0F, Level.ExplosionInteraction.MOB);
               this.discard();
            }
         } else {
            this.release(var1, false);
         }
      }
   }

   boolean exploding() {
      return this.trapTicks >= 200;
   }

   public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource var1, float var2) {
      if (this.trapped != null && var1.getEntity() instanceof ServerPlayer var3 && var3.getUUID().equals(this.trapped)) {
         return false;
      } else {
         boolean var5 = super.hurtServer(level, var1, var2);
         if (var5 && !this.level().isClientSide()) {
            this.triggerAnim("main", "hurt");
         }

         return var5;
      }
   }

   public void remove(Entity.RemovalReason var1) {
      if (this.trapped != null && !this.level().isClientSide()) {
         this.release(this.trappedPlayer(), false);
      }

      super.remove(var1);
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.CREEPER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.CREEPER_DEATH;
   }

   public void registerControllers(ControllerRegistrar var1) {
      AnimationController<MemeCreeperEntity> var2 = new AnimationController<>("main", 4, this::animate);

      for (String var6 : ACTIONS) {
         var2.triggerableAnim(var6, RawAnimation.begin().thenPlay("animation.meme_creeper." + var6));
      }

      var1.add(var2);
   }

   private PlayState animate(AnimationTest<MemeCreeperEntity> var1) {
      if ((Boolean)this.entityData.get(TRAPPING)) {
         return var1.setAndContinue(CHARGE);
      } else {
         return var1.isMoving() ? var1.setAndContinue(this.isAggressive() ? RUN : WALK) : var1.setAndContinue(IDLE);
      }
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
