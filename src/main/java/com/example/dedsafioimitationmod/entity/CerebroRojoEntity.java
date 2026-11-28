package com.example.dedsafioimitationmod.entity;

import com.example.dedsafioimitationmod.sound.ModSounds;
import java.util.Comparator;
import java.util.Optional;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public class CerebroRojoEntity extends Monster implements GeoEntity {
   private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
   private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("death");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final float DRENAJE_DANO = 1.2F;
   private static final int DRENAJE_ESPERA = 15;
   private static final int DURACION_MAXIMA_TICKS = 0;
   private static final int REENGANCHE_ESPERA = 100;
   private static final double CONTROL_BUSQUEDA = 5.0;
   private static final double CONTROL_ALCANCE = 3.5;
   private static final int CONTROL_INTERVALO = 15;
   private static final EntityDataAccessor<Byte> SPIDER_FLAGS = SynchedEntityData.defineId(CerebroRojoEntity.class, EntityDataSerializers.BYTE);
   private int drenajeContador = 0;
   private int ticksPegado = 0;
   private int reenganche = 0;
   private int controlContador = 0;

   public CerebroRojoEntity(EntityType<? extends Monster> type, Level world) {
      super(type, world);
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(SPIDER_FLAGS, (byte)0);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 50.0)
         .add(Attributes.ATTACK_DAMAGE, 1.0)
         .add(Attributes.MOVEMENT_SPEED, 0.35)
         .add(Attributes.FOLLOW_RANGE, 30.0);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, true) {
         public void tick() {
            if (this.mob.getTarget() instanceof Player p && CerebroRojoEntity.this.tieneCerebroEncima(p)) {
               if (this.mob.distanceToSqr(p) > 6.25) {
                  this.mob.getNavigation().moveTo(p, 1.2);
               } else {
                  this.mob.getNavigation().stop();
                  this.mob.getLookControl().setLookAt(p, 30.0F, 30.0F);
               }
            } else {
               super.tick();
            }
         }
      });
      this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8));
      this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   private boolean tieneCerebroEncima(Player player) {
      return player.getPassengers().stream().anyMatch(e -> e instanceof CerebroRojoEntity || e instanceof CerebroAmarilloEntity);
   }

   private void sincronizar(Player jugador) {
      if (jugador instanceof ServerPlayer sp) {
         sp.connection.send(new ClientboundSetPassengersPacket(sp));
      }
   }

   public boolean isPushable() {
      return false;
   }

   protected PathNavigation createNavigation(Level world) {
      return new WallClimberNavigation(this, world);
   }

   protected void doPush(Entity entity) {
   }

   private boolean intentarSubirse(Player player) {
      if (!this.isPassenger()
         && this.reenganche <= 0
         && player.isAlive()
         && !player.isCreative()
         && !player.isSpectator()
         && !this.tieneCerebroEncima(player)
         && this.startRiding(player, true, true)) {
         player.setDeltaMovement(Vec3.ZERO);
         player.needsSync = true;
         this.sincronizar(player);
         this.ticksPegado = 0;
         this.drenajeContador = 0;
         this.controlContador = 0;
         this.getNavigation().stop();
         return true;
      } else {
         return false;
      }
   }

   public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity target) {
      return target instanceof Player player ? this.intentarSubirse(player) : false;
   }

   private boolean hayMuroAltoDelante() {
      Vec3 frente = this.getViewVector(1.0F).scale(0.8);
      BlockPos posSegundoBloque = BlockPos.containing(
         this.getX() + frente.x, this.getY() + 1.1, this.getZ() + frente.z
      );
      return this.level().getBlockState(posSegundoBloque).isSolid();
   }

   private void controlMental(Player host) {
      this.controlContador++;
      if (this.controlContador >= 15) {
         this.controlContador = 0;
         Optional<Player> victima = this.level()
            .getEntitiesOfClass(Player.class, host.getBoundingBox().inflate(5.0), p -> p != host && p.isAlive() && !p.isSpectator())
            .stream()
            .filter(p -> host.distanceTo(p) <= 3.5)
            .min(Comparator.comparingDouble(host::distanceToSqr));
         if (victima.isPresent()) {
            float dano = (float)host.getAttributeValue(Attributes.ATTACK_DAMAGE);
            host.swing(InteractionHand.MAIN_HAND, net.minecraft.world.item.component.SwingAnimation.DEFAULT, true);
            victima.get().hurt(this.damageSources().mobAttack(this), dano);
         }
      }
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         boolean debeEscalar = this.horizontalCollision
            && !this.isPassenger()
            && this.getTarget() != null
            && this.getTarget().getY() > this.getY() + 1.5
            && this.hayMuroAltoDelante();
         this.setClimbingWall(debeEscalar);
         if (this.reenganche > 0) {
            this.reenganche--;
         }

         if (!this.isPassenger() && this.getTarget() instanceof Player targetPlayer && this.distanceToSqr(targetPlayer) <= 1.96) {
            this.intentarSubirse(targetPlayer);
         }

         if (this.getVehicle() instanceof Player host) {
            if (!host.isAlive()) {
               this.soltar();
               return;
            }

            if (this.tickCount % 20 == 0) {
               this.sincronizar(host);
            }

            this.setYRot(host.getYRot());
            this.yBodyRot = host.getYRot();
            this.yHeadRot = host.getYRot();
            this.drenajeContador++;
            if (this.drenajeContador >= 15) {
               this.drenajeContador = 0;
               Vec3 velocidadActual = host.getDeltaMovement();
               host.hurt(this.damageSources().indirectMagic(this, this), 1.2F);
               host.setDeltaMovement(velocidadActual);
               host.needsSync = true;
            }

            this.controlMental(host);
            this.ticksPegado++;
            this.ticksPegado++;
            if (this.ticksPegado == 1 || this.ticksPegado % 30 == 0) {
               this.playSound(ModSounds.CEREBRO_ATAQUE, 1.0F, 1.0F);
            }
         }
      }
   }

   private void soltar() {
      Entity anterior = this.getVehicle();
      this.stopRiding();
      if (anterior instanceof Player p) {
         this.sincronizar(p);
      }

      this.reenganche = 100;
      this.ticksPegado = 0;
      this.drenajeContador = 0;
      this.controlContador = 0;
      Vec3 empuje = new Vec3(this.getRandom().nextDouble() - 0.5, 0.4, this.getRandom().nextDouble() - 0.5);
      this.setDeltaMovement(empuje);
      this.needsSync = true;
   }

   public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
      return this.getVehicle() instanceof Player host && source.getEntity() == host ? false : super.hurtServer(level, source, amount);
   }

   public boolean removeWhenFarAway(double distanceSquared) {
      return !this.isPassenger() && super.removeWhenFarAway(distanceSquared);
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController<>("controller", 5, state -> {
         if (this.isDeadOrDying()) {
            return state.setAndContinue(DEATH);
         } else if (this.isPassenger()) {
            return state.setAndContinue(WALK);
         } else {
            return state.isMoving() ? state.setAndContinue(WALK) : state.setAndContinue(IDLE);
         }
      }));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public boolean causeFallDamage(double fallDistance, float damageMultiplier, DamageSource damageSource) {
      return false;
   }

   public boolean onClimbable() {
      return ((Byte)this.entityData.get(SPIDER_FLAGS) & 1) != 0;
   }

   public void setClimbingWall(boolean climbing) {
      byte b = (Byte)this.entityData.get(SPIDER_FLAGS);
      if (climbing) {
         b = (byte)(b | 1);
      } else {
         b = (byte)(b & -2);
      }

      this.entityData.set(SPIDER_FLAGS, b);
   }

   protected SoundEvent getDeathSound() {
      return ModSounds.CEREBRO_MUERTE;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return null;
   }
}
