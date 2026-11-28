package net.ruleta.herobrine;

import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.ruleta.Ajustes;
import net.ruleta.Ruleta;
import net.ruleta.bunker.Bunkers;
import org.jetbrains.annotations.Nullable;

public class HerobrineEntity extends Monster {
   @Nullable
   private UUID objetivo;
   private int proximoTp = 200;
   private int proximoGiro;
   private float yawQuieto;
   private float pitchQuieto;
   private int esperaGolpe;
   private int tickMov;
   private int atasco;
   private Vec3 posAntes = Vec3.ZERO;

   private static float dano() {
      return Ajustes.decimal("dano.herobrine") * 2.0F;
   }

   public HerobrineEntity(EntityType<? extends Monster> tipo, Level nivel) {
      super(tipo, nivel);
      this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0);
      this.xpReward = 20;
   }

   public static Builder atributos() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 60.0)
         .add(Attributes.MOVEMENT_SPEED, 0.32)
         .add(Attributes.ATTACK_DAMAGE, 4.0)
         .add(Attributes.FOLLOW_RANGE, 96.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.6);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
   }

   protected void customServerAiStep(net.minecraft.server.level.ServerLevel level) {
      super.customServerAiStep(level);
      if (this.level() instanceof ServerLevel nivel) {
         ServerPlayer presa = this.objetivo == null ? null : (ServerPlayer)nivel.getPlayerByUUID(this.objetivo);
         if (this.objetivo == null || presa != null && presa.isAlive() && !presa.isSpectator() && !presa.isCreative()) {
            if (presa == null) {
               this.quieto(nivel);
            } else {
               this.cazar(nivel, presa);
            }
         } else {
            this.desaparecer();
         }
      }
   }

   private void quieto(ServerLevel nivel) {
      this.setSprinting(false);
      this.getNavigation().stop();
      this.setTarget(null);
      double vision = Ajustes.entero("herobrine.vision");

      for (ServerPlayer p : this.tickCount % 2 == 0 ? nivel.players() : List.<ServerPlayer>of()) {
         if (p.isAlive() && !p.isSpectator() && !p.isCreative() && this.distanceToSqr(p) < vision * vision && this.meMiraALosOjos(p)) {
            this.elegir(p);
            return;
         }
      }

      if (--this.proximoGiro <= 0) {
         this.proximoGiro = 40 + this.random.nextInt(120);
         this.yawQuieto = this.random.nextFloat() * 360.0F;
         this.pitchQuieto = (this.random.nextFloat() - 0.5F) * 40.0F;
      }

      this.setYRot(this.yawQuieto);
      this.yBodyRot = this.yawQuieto;
      this.yHeadRot = this.yawQuieto;
      this.setXRot(this.pitchQuieto);
      if (--this.proximoTp <= 0) {
         this.proximoTp = this.siguienteTp();
         Player cerca = nivel.getNearestPlayer(this, 128.0);
         if (cerca != null && !cerca.isSpectator()) {
            this.tpCercaDe(cerca, 10.0, 24.0);
         } else {
            this.tpAlAzar(16.0);
         }
      }
   }

   private int siguienteTp() {
      int min = Ajustes.entero("herobrine.tp_min");
      int max = Math.max(min, Ajustes.entero("herobrine.tp_max"));
      return (min + this.random.nextInt(max - min + 1)) * 20;
   }

   private void cazar(ServerLevel nivel, ServerPlayer presa) {
      if (this.getTarget() != presa) {
         this.setTarget(presa);
      }

      this.setSprinting(true);
      this.getLookControl().setLookAt(presa, 60.0F, 60.0F);
      double d2 = this.distanceToSqr(presa);
      double alcance = 2.4;
      if (this.esperaGolpe > 0) {
         this.esperaGolpe--;
      }

      if (d2 <= alcance * alcance && this.esperaGolpe <= 0 && this.hasLineOfSight(presa)) {
         this.esperaGolpe = 18;
         this.doHurtTarget((net.minecraft.server.level.ServerLevel)this.level(), presa);
      }

      if (d2 > 2.5600000000000005) {
         if (this.tickMov++ % 3 == 0) {
            boolean hayCamino = this.getNavigation().moveTo(presa, 1.5);
            if (!hayCamino) {
               this.getMoveControl().setWantedPosition(presa.getX(), presa.getY(), presa.getZ(), 1.5);
            }
         }

         if (this.getNavigation().isDone()) {
            this.getMoveControl().setWantedPosition(presa.getX(), presa.getY(), presa.getZ(), 1.5);
         }

         if (this.horizontalCollision && this.onGround()) {
            this.getJumpControl().jump();
         }
      }

      if (this.tickCount % 10 == 0) {
         double movido = this.position().distanceToSqr(this.posAntes);
         this.posAntes = this.position();
         if (d2 > 9.0 && movido < 0.36) {
            this.atasco++;
         } else {
            this.atasco = 0;
         }

         if (this.atasco == 1) {
            Vec3 dir = presa.position().subtract(this.position()).multiply(1.0, 0.0, 1.0).normalize();
            this.setDeltaMovement(dir.x * 0.5, 0.45, dir.z * 0.5);
         } else if (this.atasco >= 2) {
            this.atasco = 0;
            this.tpCercaDe(presa, 3.0, 6.0);
            this.getNavigation().stop();
         }
      }

      if (--this.proximoTp <= 0) {
         this.proximoTp = 40;
         if (d2 > 576.0) {
            this.tpCercaDe(presa, 3.0, 7.0);
         }
      }
   }

   public boolean meMiraALosOjos(Player p) {
      Vec3 vista = p.getViewVector(1.0F).normalize();
      Vec3 hacia = new Vec3(this.getX() - p.getX(), this.getEyeY() - p.getEyeY(), this.getZ() - p.getZ());
      double d = hacia.length();
      hacia = hacia.normalize();
      if (vista.dot(hacia) > 1.0 - 0.025 / d && p.hasLineOfSight(this)) {
         Vec3 cara = this.getViewVector(1.0F).normalize();
         Vec3 haciaJugador = new Vec3(p.getX() - this.getX(), p.getEyeY() - this.getEyeY(), p.getZ() - this.getZ()).normalize();
         return cara.dot(haciaJugador) > 0.4;
      } else {
         return false;
      }
   }

   private void elegir(ServerPlayer p) {
      this.objetivo = p.getUUID();
      this.setTarget(p);
      this.setPersistenceRequired();
      this.proximoTp = 60;
      this.playSound(SoundEvents.ENDERMAN_SCREAM, 1.0F, 0.6F);
   }

   public void setTarget(@Nullable LivingEntity nuevo) {
      if (this.objetivo != null
         && (nuevo == null || !this.objetivo.equals(nuevo.getUUID()))
         && this.level() instanceof ServerLevel nivel
         && nivel.getPlayerByUUID(this.objetivo) instanceof ServerPlayer presa
         && presa.isAlive()) {
         super.setTarget(presa);
      } else {
         super.setTarget(nuevo);
      }
   }

   public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource fuente, float cantidad) {
      return false;
   }

   public boolean isInvulnerableTo(net.minecraft.server.level.ServerLevel level, DamageSource fuente) {
      return true;
   }

   public boolean isInvulnerable() {
      return true;
   }

   public boolean isPickable() {
      return false;
   }

   public boolean isAttackable() {
      return false;
   }

   public boolean skipAttackInteraction(Entity atacante) {
      return true;
   }

   public boolean isPushable() {
      return false;
   }

   public boolean canBeAffected(MobEffectInstance efecto) {
      return false;
   }

   public boolean doHurtTarget(net.minecraft.server.level.ServerLevel level, Entity victima) {
      if (victima instanceof ServerPlayer p) {
         this.swing(InteractionHand.MAIN_HAND, net.minecraft.world.item.component.SwingAnimation.DEFAULT, true);
         if (p.isBlocking()) {
            Vec3 hacia = this.position().vectorTo(p.position());
            Vec3 plano = new Vec3(hacia.x, 0.0, hacia.z).normalize();
            if (plano.dot(p.getViewVector(1.0F)) < 0.0) {
               this.level().broadcastEntityEvent(p, (byte)29);
               p.getUseItem().hurtAndBreak(1 + Mth.floor(dano()), p, p.getUsedItemHand());
               return true;
            }
         }

         return p.hurtServer(level, Ruleta.fuenteLetalPublica(p, "ruleta.herobrine", " fue asesinado por Herobrine"), dano());
      } else {
         return super.doHurtTarget(level, victima);
      }
   }

   private boolean tpCercaDe(Entity e, double min, double max) {
      for (int i = 0; i < 24; i++) {
         double ang = this.random.nextDouble() * Math.PI * 2.0;
         double r = min + this.random.nextDouble() * (max - min);
         double x = e.getX() + Math.cos(ang) * r;
         double z = e.getZ() + Math.sin(ang) * r;
         double y = e.getY() + (this.random.nextInt(9) - 4);
         if (this.teletransportar(x, y, z)) {
            return true;
         }
      }

      return false;
   }

   private boolean tpAlAzar(double alcance) {
      for (int i = 0; i < 16; i++) {
         double x = this.getX() + (this.random.nextDouble() - 0.5) * 2.0 * alcance;
         double y = this.getY() + (this.random.nextInt(17) - 8);
         double z = this.getZ() + (this.random.nextDouble() - 0.5) * 2.0 * alcance;
         if (this.teletransportar(x, y, z)) {
            return true;
         }
      }

      return false;
   }

   private boolean teletransportar(double x, double y, double z) {
      MutableBlockPos pos = new MutableBlockPos(x, y, z);

      while (pos.getY() > this.level().getMinY() && !this.level().getBlockState(pos).isSolid()) {
         pos.move(0, -1, 0);
      }

      if (this.level().getBlockState(pos).isSolid() && !this.level().getBlockState(pos.above()).liquid()) {
         if (this.level() instanceof ServerLevel sl && Bunkers.interiorDe(sl, pos.above()) != null) {
            return false;
         } else {
            double ox = this.getX();
            double oy = this.getY();
            double oz = this.getZ();
            boolean ok = this.randomTeleport(x, pos.getY() + 1, z, true, (java.util.function.Predicate<net.minecraft.world.level.block.state.BlockState>)s -> true);
            if (ok) {
               this.level().playSound(null, ox, oy, oz, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 0.5F);
               this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 0.5F);
            }

            return ok;
         }
      } else {
         return false;
      }
   }

   public void desaparecer() {
      if (this.level() instanceof ServerLevel nivel) {
         nivel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 1.0, this.getZ(), 30, 0.3, 0.8, 0.3, 0.02);
         nivel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 0.4F);
      }

      this.discard();
   }

   public boolean removeWhenFarAway(double distancia) {
      return this.objetivo == null && distancia > 16384.0;
   }

   public boolean causeFallDamage(double distancia, float mult, DamageSource fuente) {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return null;
   }

   protected SoundEvent getHurtSound(DamageSource fuente) {
      return SoundEvents.PLAYER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PLAYER_DEATH;
   }

   public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput tag) {
      super.addAdditionalSaveData(tag);
      if (this.objetivo != null) {
         tag.store("Objetivo", net.minecraft.core.UUIDUtil.CODEC, this.objetivo);
      }
   }

   public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput tag) {
      super.readAdditionalSaveData(tag);
      this.objetivo = tag.read("Objetivo", net.minecraft.core.UUIDUtil.CODEC).orElse(null);
   }

   public int getMaxHeadYRot() {
      return this.objetivo == null ? 0 : super.getMaxHeadYRot();
   }

   static float limpiar(float a) {
      return Mth.wrapDegrees(a);
   }
}
