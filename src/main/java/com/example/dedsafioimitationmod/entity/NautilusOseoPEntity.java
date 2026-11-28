package com.example.dedsafioimitationmod.entity;

import com.example.dedsafioimitationmod.sound.ModSounds;
import java.util.EnumSet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public class NautilusOseoPEntity extends Monster implements GeoEntity {
   private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
   private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
   private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("death");
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final double VELOCIDAD_ATAQUE = 0.8;
   private static final int ESPERA_GOLPE_MIN = 20;
   private static final double DASH_VELOCIDAD = 0.2;
   private static final int DASH_DURACION = 8;
   private static final int DASH_ESPERA_MIN = 60;
   private static final int DASH_ESPERA_EXTRA = 40;
   private static final float DASH_DISTANCIA_MIN = 5.0F;
   private static final int LEVITACION_TICKS = 100;
   private static final Identifier SIN_KB_ID = Identifier.fromNamespaceAndPath("dedsafioimitationmod", "sin_kb_p");
   private int dashRestante = 0;
   private int dashEspera = 40;
   private int golpeEspera = 0;

   public NautilusOseoPEntity(EntityType<? extends Monster> type, Level world) {
      super(type, world);
      this.moveControl = new FlyingMoveControl(this, 5, true);
      this.setNoGravity(true);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 50.0)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.MOVEMENT_SPEED, 1.0)
         .add(Attributes.FLYING_SPEED, 2.0)
         .add(Attributes.FOLLOW_RANGE, 40.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.8, false));
      this.goalSelector.addGoal(2, new NautilusOseoPEntity.VueloAleatorioGoal(this));
      this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0F));
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   protected PathNavigation createNavigation(Level world) {
      return new FlyingPathNavigation(this, world);
   }

   public boolean isPushable() {
      return false;
   }

   protected void doPush(Entity entity) {
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public boolean isInvulnerableTo(net.minecraft.server.level.ServerLevel level, DamageSource source) {
      return source.is(DamageTypes.FALL)
         || source.is(DamageTypes.DROWN)
         || source.is(DamageTypes.IN_WALL)
         || super.isInvulnerableTo(level, source);
   }

   public boolean causeFallDamage(double fallDistance, float damageMultiplier, DamageSource damageSource) {
      return false;
   }

   private void actualizarDash() {
      LivingEntity objetivo = this.getTarget();
      if (objetivo == null || !objetivo.isAlive()) {
         this.dashRestante = 0;
      } else if (this.dashRestante > 0) {
         this.dashRestante--;
         Vec3 haciaObjetivo = objetivo.position()
            .add(0.0, objetivo.getBbHeight() * 0.5, 0.0)
            .subtract(this.position().add(0.0, this.getBbHeight() * 0.5, 0.0));
         if (haciaObjetivo.lengthSqr() < 1.0) {
            this.dashRestante = 0;
         } else {
            this.setDeltaMovement(haciaObjetivo.normalize().scale(0.2));
            this.needsSync = true;
         }
      } else if (this.dashEspera > 0) {
         this.dashEspera--;
      } else {
         if (this.distanceTo(objetivo) > 5.0F && this.hasLineOfSight(objetivo)) {
            this.dashRestante = 8;
            this.dashEspera = 60 + this.getRandom().nextInt(40);
         }
      }
   }

   public void aiStep() {
      super.aiStep();
      this.setAirSupply(this.getMaxAirSupply());
      if (!this.level().isClientSide()) {
         if (this.golpeEspera > 0) {
            this.golpeEspera--;
         }

         this.actualizarDash();
         if ((this.onGround() || this.isInWater()) && this.dashRestante <= 0) {
            Vec3 v = this.getDeltaMovement();
            this.setDeltaMovement(v.x, Math.max(v.y, 0.15), v.z);
            this.needsSync = true;
         }

         if (this.getTarget() == null) {
            int distanciaAlSuelo = 0;
            BlockPos pos = this.blockPosition();

            while (distanciaAlSuelo <= 12 && this.level().isEmptyBlock(pos.below(distanciaAlSuelo))) {
               distanciaAlSuelo++;
            }

            Vec3 v = this.getDeltaMovement();
            if (distanciaAlSuelo < 3) {
               this.setDeltaMovement(v.x, Math.max(v.y, 0.08), v.z);
            } else if (distanciaAlSuelo > 12 && v.y > -0.05) {
               this.setDeltaMovement(v.x, -0.08, v.z);
            }
         }
      }
   }

   public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity target) {
      if (this.golpeEspera > 0) {
         return false;
      } else {
         AttributeInstance resistencia = null;
         if (target instanceof LivingEntity vivo) {
            resistencia = vivo.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            if (resistencia != null) {
               resistencia.removeModifier(SIN_KB_ID);
               resistencia.addTransientModifier(new AttributeModifier(SIN_KB_ID, 1.0, AttributeModifier.Operation.ADD_VALUE));
            }
         }

         boolean hit;
         try {
            hit = super.doHurtTarget(level, target);
         } finally {
            if (resistencia != null) {
               resistencia.removeModifier(SIN_KB_ID);
            }
         }

         if (hit) {
            this.golpeEspera = 20;
         }

         if (hit && target instanceof Player player) {
            player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 100, 0));
            this.level().playSound(null, this.blockPosition(), ModSounds.NAUTILUS_P_ATTACK, this.getSoundSource(), 1.0F, 1.0F);
         }

         return hit;
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController<>("controller", 5, state -> {
         if (this.isDeadOrDying()) {
            return state.setAndContinue(DEATH);
         } else {
            return state.isMoving() ? state.setAndContinue(WALK) : state.setAndContinue(IDLE);
         }
      }));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   protected SoundEvent getDeathSound() {
      return ModSounds.NAUTILUS_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return null;
   }

   static class VueloAleatorioGoal extends Goal {
      private final NautilusOseoPEntity nautilus;
      private double destX;
      private double destY;
      private double destZ;
      private int tiempoCambio = 0;

      public VueloAleatorioGoal(NautilusOseoPEntity nautilus) {
         this.nautilus = nautilus;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         return this.nautilus.getTarget() == null;
      }

      public boolean canContinueToUse() {
         return this.nautilus.getTarget() == null;
      }

      public void start() {
         this.elegirNuevoDestino();
      }

      public void tick() {
         this.tiempoCambio++;
         double dx = this.destX - this.nautilus.getX();
         double dy = this.destY - this.nautilus.getY();
         double dz = this.destZ - this.nautilus.getZ();
         double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
         if (!(dist < 4.0) && this.tiempoCambio <= 100 && !this.nautilus.horizontalCollision) {
            double velocidad = 0.15;
            this.nautilus
               .setDeltaMovement(
                  this.nautilus
                     .getDeltaMovement()
                     .scale(0.8)
                     .add(dx / dist * velocidad * 0.2, dy / dist * velocidad * 0.2, dz / dist * velocidad * 0.2)
               );
            float yawObjetivo = (float)(Math.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
            this.nautilus.setYRot(Mth.rotLerp(0.08F, this.nautilus.getYRot(), yawObjetivo));
            this.nautilus.yBodyRot = this.nautilus.getYRot();
            this.nautilus.yHeadRot = this.nautilus.getYRot();
         } else {
            this.elegirNuevoDestino();
         }
      }

      private void elegirNuevoDestino() {
         this.tiempoCambio = 0;
         RandomSource random = this.nautilus.getRandom();

         for (int i = 0; i < 10; i++) {
            double nx = this.nautilus.getX() + (random.nextFloat() * 2.0F - 1.0F) * 24.0F;
            double ny = this.nautilus.getY() + (random.nextFloat() * 2.0F - 1.0F) * 4.0F;
            double nz = this.nautilus.getZ() + (random.nextFloat() * 2.0F - 1.0F) * 24.0F;
            BlockPos pos = BlockPos.containing(nx, ny, nz);
            if (this.nautilus.level().isEmptyBlock(pos) && this.nautilus.level().isEmptyBlock(pos.above())) {
               this.destX = nx;
               this.destY = ny;
               this.destZ = nz;
               break;
            }
         }
      }
   }
}
