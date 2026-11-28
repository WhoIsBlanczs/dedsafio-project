package com.novastudio.dedsafio4;

import java.util.function.Predicate;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.core.Registry;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.network.syncher.SynchedEntityData;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public final class NexoMobs {
   public static EntityType<NexoMobs.Nave> NAVE;
   public static Predicate<Player> JUMPING = var0 -> false;
   public static EntityType<NexoMobs.Gusano> GUSANO;
   public static EntityType<NexoMobs.Nautilus> NAUTILUS;
   public static EntityType<NexoMobs.Cerearana> CEREARANA;

   static Identifier id(String var0) {
      return Identifier.fromNamespaceAndPath("nexo", var0);
   }

   static <T extends Entity> EntityType<T> type(String var0, EntityType.EntityFactory<T> var1, MobCategory var2, float var3, float var4) {
      return (EntityType<T>)Registry.register(
         BuiltInRegistries.ENTITY_TYPE, id(var0), EntityType.Builder.of(var1, var2).sized(var3, var4).clientTrackingRange(10).build(com.novastudio.dedsafio4.compat.Ids.entidad(var0))
      );
   }

   public static void register() {
      NAVE = type("nave_dilitio", NexoMobs.Nave::new, MobCategory.MISC, 2.2F, 5.0F);
      GUSANO = type("gusano_glebanoide", NexoMobs.Gusano::new, MobCategory.MONSTER, 0.8F, 0.5F);
      NAUTILUS = type("nautilus_glebanoide", NexoMobs.Nautilus::new, MobCategory.CREATURE, 0.75F, 0.75F);
      CEREARANA = type("cerearana", NexoMobs.Cerearana::new, MobCategory.MONSTER, 2.0F, 1.1F);
      FabricDefaultAttributeRegistry.register(
         GUSANO,
         Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 16.0)
            .add(Attributes.ATTACK_DAMAGE, 4.0)
            .add(Attributes.MOVEMENT_SPEED, 0.25)
      );
      FabricDefaultAttributeRegistry.register(
         NAUTILUS,
         Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 10.0)
            .add(Attributes.MOVEMENT_SPEED, 0.2)
            .add(Attributes.FLYING_SPEED, 0.4)
      );
      FabricDefaultAttributeRegistry.register(
         CEREARANA,
         Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 40.0)
            .add(Attributes.ATTACK_DAMAGE, 7.0)
            .add(Attributes.MOVEMENT_SPEED, 0.28)
      );
      egg("gusano_glebanoide", GUSANO, 7032381, 13214330);
      egg("nautilus_glebanoide", NAUTILUS, 5980014, 14731168);
      egg("cerearana", CEREARANA, 14258080, 3877424);
      UseBlockCallback.EVENT.register((UseBlockCallback)(var0, var1, var2, var3) -> {
         ItemStack var4 = var0.getItemInHand(var2);
         if (!BuiltInRegistries.ITEM.getKey(var4.getItem()).equals(id("nave"))) {
            return InteractionResult.PASS;
         } else {
            if (!var1.isClientSide()) {
               NexoMobs.Nave var5 = new NexoMobs.Nave(NAVE, var1);
               Vec3 var6 = Vec3.atBottomCenterOf(var3.getBlockPos().relative(var3.getDirection()));
               var5.snapTo(var6.x, var6.y, var6.z, var0.getYRot() + 180.0F, 0.0F);
               var1.addFreshEntity(var5);
               if (!var0.isCreative()) {
                  var4.shrink(1);
               }
            }

            return InteractionResult.SUCCESS;
         }
      });
   }

   static void egg(String var0, EntityType<? extends Mob> var1, int var2, int var3) {
      Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(id(var0 + "_spawn_egg")), new SpawnEggItem((new Item.Properties()).spawnEgg(var1)));
   }

   static void hostileGoals(Monster var0, GoalSelector var1, GoalSelector var2) {
      var1.addGoal(0, new FloatGoal(var0));
      var1.addGoal(2, new MeleeAttackGoal(var0, 1.0, false));
      var1.addGoal(5, new WaterAvoidingRandomStrollGoal(var0, 0.8, 0.001F));
      var1.addGoal(6, new LookAtPlayerGoal(var0, Player.class, 8.0F));
      var1.addGoal(7, new RandomLookAroundGoal(var0));
      var2.addGoal(1, new HurtByTargetGoal(var0, new Class[0]));
      var2.addGoal(2, new NearestAttackableTargetGoal(var0, Player.class, true));
   }

   public static final class Cerearana extends Monster implements GeoEntity {
      static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
      static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
      static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");
      static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");
      static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("death");
      private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
      static final RawAnimation LATCHED = RawAnimation.begin().thenLoop("attack_latched");

      public Cerearana(EntityType<? extends NexoMobs.Cerearana> var1, Level var2) {
         super(var1, var2);
      }

      protected void registerGoals() {
         NexoMobs.hostileGoals(this, this.goalSelector, this.targetSelector);
      }

      public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity var1) {
         return var1 instanceof Player var2 && !var2.isCreative() && !var2.isSpectator() && !this.isPassenger()
            ? this.startRiding(var2, true, true)
            : super.doHurtTarget(level, var1);
      }

      public void tick() {
         super.tick();
         if (!this.level().isClientSide() && this.getVehicle() instanceof Player var1) {
            if (!var1.isAlive() || var1.isCreative() || var1.isSpectator()) {
               this.stopRiding();
            } else if (this.tickCount % 20 == 0) {
               var1.hurt(this.damageSources().mobAttack(this), 4.0F);
            }
         }
      }

      protected void tickDeath() {
         if (++this.deathTime >= 40 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.discard();
         }
      }

      public void registerControllers(ControllerRegistrar var1) {
         var1.add(new AnimationController<>("main", 4, var1x -> {
            if (this.isDeadOrDying()) {
               return var1x.setAndContinue(DEATH);
            } else if (this.getVehicle() instanceof Player) {
               return var1x.setAndContinue(LATCHED);
            } else if (this.isSwinging()) {
               return var1x.setAndContinue(ATTACK);
            } else {
               return var1x.isMoving() ? var1x.setAndContinue(this.getTarget() != null ? RUN : WALK) : var1x.setAndContinue(IDLE);
            }
         }));
      }

      public AnimatableInstanceCache getAnimatableInstanceCache() {
         return this.cache;
      }
   }

   public static final class Gusano extends Monster implements GeoEntity {
      private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

      public Gusano(EntityType<? extends NexoMobs.Gusano> var1, Level var2) {
         super(var1, var2);
      }

      protected void registerGoals() {
         NexoMobs.hostileGoals(this, this.goalSelector, this.targetSelector);
      }

      public void registerControllers(ControllerRegistrar var1) {
      }

      public AnimatableInstanceCache getAnimatableInstanceCache() {
         return this.cache;
      }
   }

   public static final class Nautilus extends PathfinderMob implements GeoEntity {
      private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

      public Nautilus(EntityType<? extends NexoMobs.Nautilus> var1, Level var2) {
         super(var1, var2);
         this.moveControl = new FlyingMoveControl(this, 20, true);
         this.setNoGravity(true);
      }

      protected PathNavigation createNavigation(Level var1) {
         FlyingPathNavigation var2 = new FlyingPathNavigation(this, var1);
         var2.setCanOpenDoors(false);
         var2.setCanFloat(true);
         return var2;
      }

      protected void registerGoals() {
         this.goalSelector.addGoal(0, new FloatGoal(this));
         this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 0.8));
         this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
         this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
      }

      public void registerControllers(ControllerRegistrar var1) {
      }

      public AnimatableInstanceCache getAnimatableInstanceCache() {
         return this.cache;
      }
   }

   public static final class Nave extends Entity implements GeoEntity {
      private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

      public Nave(EntityType<? extends NexoMobs.Nave> var1, Level var2) {
         super(var1, var2);
      }

      protected void defineSynchedData(SynchedEntityData.Builder var1) {
      }

      protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput var1) {
      }

      protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput var1) {
      }

      public boolean isPickable() {
         return !this.isRemoved();
      }

      public boolean canBeCollidedWith(net.minecraft.world.entity.Entity otro$) {
         return true;
      }

      public InteractionResult interact(Player var1, InteractionHand var2, net.minecraft.world.phys.Vec3 toque$) {
         if (!var1.isShiftKeyDown() && this.canAddPassenger(var1)) {
            if (!this.level().isClientSide()) {
               var1.startRiding(this);
            }

            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      }

      protected boolean canAddPassenger(Entity var1) {
         return this.getPassengers().size() < 2;
      }

      protected void positionRider(Entity var1, Entity.MoveFunction var2) {
         int var3 = this.getPassengers().indexOf(var1);
         Vec3 var4 = new Vec3(var3 == 0 ? 0.45 : -0.45, 0.0, 0.0).yRot((float)Math.toRadians(-this.getYRot()));
         var2.accept(var1, this.getX() + var4.x, this.getY() + 2.6, this.getZ() + var4.z);
         var1.fallDistance = 0.0F;
      }

      public LivingEntity getControllingPassenger() {
         return !this.getPassengers().isEmpty() && this.getPassengers().get(0) instanceof Player var1 ? var1 : null;
      }

      public void tick() {
         super.tick();
         if (this.isLocalInstanceAuthoritative()) {
            Vec3 var1 = this.getDeltaMovement();
            if (this.getControllingPassenger() instanceof Player var2) {
               this.setYRot(var2.getYRot());
               double var11 = 0.6;
               double var5 = NexoMobs.JUMPING.test(var2) ? 0.45 : 0.0;
               Vec3 var7 = new Vec3(var2.xxa, 0.0, var2.zza).normalize().scale(var11);
               double var8 = Math.toRadians(var2.getXRot());
               var7 = new Vec3(var7.x, -Math.sin(var8) * var2.zza * var11 + var5, var7.z * Math.cos(var8))
                  .yRot((float)Math.toRadians(-this.getYRot()));
               var1 = var1.lerp(var7, 0.15);
            } else {
               var1 = new Vec3(var1.x * 0.9, this.onGround() ? 0.0 : Math.max(var1.y - 0.03, -0.35), var1.z * 0.9);
            }

            this.setDeltaMovement(var1);
            this.move(MoverType.SELF, var1);
         }
      }

      public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource var1, float var2) {
         if (!this.level().isClientSide() && !this.isRemoved() && var1.getEntity() instanceof Player var3) {
            if (!var3.isCreative()) {
               this.spawnAtLocation(level, new ItemStack((ItemLike)BuiltInRegistries.ITEM.getValue(NexoMobs.id("nave"))));
            }

            this.discard();
            return true;
         } else {
            return false;
         }
      }

      public void registerControllers(ControllerRegistrar var1) {
      }

      public AnimatableInstanceCache getAnimatableInstanceCache() {
         return this.cache;
      }
   }
}
