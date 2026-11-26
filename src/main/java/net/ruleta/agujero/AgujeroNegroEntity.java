package net.ruleta.agujero;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.ruleta.Ajustes;
import net.ruleta.Ruleta;

public class AgujeroNegroEntity extends Entity {
   public static final float RADIO_DEFECTO = 15.0F;
   public static final float RADIO_MIN = 1.0F;
   public static final float RADIO_MAX = 64.0F;
   private static final EntityDataAccessor<Float> RADIO = SynchedEntityData.defineId(AgujeroNegroEntity.class, EntityDataSerializers.FLOAT);
   private static final double VELOCIDAD_MAX = 1.4;
   private static final float DANO = 5.0F;

   public AgujeroNegroEntity(EntityType<?> tipo, Level nivel) {
      super(tipo, nivel);
      this.noPhysics = true;
      this.setNoGravity(true);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(RADIO, 15.0F);
   }

   public double radio() {
      return ((Float)this.entityData.get(RADIO)).floatValue();
   }

   public void fijarRadio(float r) {
      this.entityData.set(RADIO, Mth.clamp(r, 1.0F, 64.0F));
   }

   private double dentro() {
      return this.radio() + 1.5;
   }

   private double alcance() {
      return 100.0;
   }

   public void tick() {
      super.tick();
      this.setDeltaMovement(Vec3.ZERO);
      if (this.level() instanceof ServerLevel mundo) {
         if (!ComandoAgujero.vivo(this.getUUID())) {
            this.discard();
         } else {
            for (ServerPlayer jugador : mundo.players()) {
               if (!jugador.isSpectator() && !jugador.isCreative() && jugador.isAlive()) {
                  this.atraer(jugador);
               }
            }
         }
      }
   }

   private void atraer(ServerPlayer jugador) {
      Vec3 hacia = this.position().subtract(jugador.position().add(0.0, jugador.getBbHeight() * 0.5, 0.0));
      double d = hacia.length();
      double alcance = this.alcance();
      if (!(d > alcance) && !(d < 0.001)) {
         Vec3 dir = hacia.scale(1.0 / d);
         Vec3 v = jugador.getDeltaMovement();
         if (d < this.dentro()) {
            v = v.scale(0.6).add(dir.scale(0.08));
            if (this.tickCount % (20 * Math.max(1, Ajustes.entero("agujeros.intervalo"))) == 0) {
               jugador.setInvulnerableTime(0);
               jugador.hurt(
                  Ruleta.fuenteLetalPublica(jugador, "ruleta.agujero", " fue absorbido por un Agujero Negro de Reviil"),
                  Ajustes.decimal("agujeros.dano") * 2.0F
               );
            }
         } else {
            double k = 1.0 - d / alcance;
            v = v.add(dir.scale(0.03 + 0.17 * Math.pow(k, 1.3)));
         }

         double vmax = Ajustes.valor("agujeros.velocidad");
         if (v.length() > vmax) {
            v = v.normalize().scale(vmax);
         }

         jugador.setDeltaMovement(v);
         jugador.hurtMarked = true;
         jugador.fallDistance = 0.0F;
      }
   }

   public boolean isPickable() {
      return false;
   }

   public boolean isPushable() {
      return false;
   }

   public boolean hurt(DamageSource fuente, float cantidad) {
      return false;
   }

   public boolean shouldRenderAtSqrDistance(double distancia) {
      return true;
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      if (tag.contains("Radio")) {
         this.fijarRadio(tag.getFloat("Radio"));
      }
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putFloat("Radio", (float)this.radio());
   }
}
