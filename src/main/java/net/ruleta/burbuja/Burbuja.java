package net.ruleta.burbuja;

import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.MoveFunction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.ruleta.compat.ExplosionInmune;

public class Burbuja extends Entity implements ExplosionInmune {
   private static final EntityDataAccessor<Float> DIAMETRO = SynchedEntityData.defineId(Burbuja.class, EntityDataSerializers.FLOAT);
   public static Consumer<Burbuja> movimientoCliente = b -> {};
   private UUID propietario;
   private int pasos;
   private boolean hubo;
   private double lx;
   private double ly;
   private double lz;
   public float[] abolladura;
   public float[] abolladuraVel;

   public Burbuja(EntityType<? extends Burbuja> tipo, Level nivel) {
      super(tipo, nivel);
      this.noPhysics = false;
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DIAMETRO, 2.2F);
   }

   public float getDiametro() {
      return (Float)this.entityData.get(DIAMETRO);
   }

   public void setDiametro(float d) {
      this.entityData.set(DIAMETRO, Mth.clamp(d, 0.9F, 6.0F));
   }

   public UUID getPropietario() {
      return this.propietario;
   }

   public void setPropietario(UUID id) {
      this.propietario = id;
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> clave) {
      if (DIAMETRO.equals(clave)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(clave);
   }

   public EntityDimensions getDimensions(Pose pose) {
      float d = this.getDiametro();
      return EntityDimensions.fixed(d, d);
   }

   public boolean isInvulnerable() {
      return true;
   }

   public boolean isInvulnerableTo(DamageSource fuente) {
      return !fuente.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
   }

   public boolean hurt(DamageSource fuente, float cantidad) {
      return false;
   }

   public boolean isAttackable() {
      return false;
   }

   public boolean skipAttackInteraction(Entity atacante) {
      return true;
   }

   public boolean canBeHitByProjectile() {
      return false;
   }

   public boolean isPickable() {
      return !this.isRemoved();
   }

   public boolean isPushable() {
      return false;
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean isNoGravity() {
      return true;
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public Entity changeDimension(TeleportTransition destino) {
      return null;
   }

   public InteractionResult interact(Player jugador, InteractionHand mano) {
      if (jugador.isSecondaryUseActive()) {
         if (jugador.isPassenger()) {
            return InteractionResult.PASS;
         }

         boolean puede = jugador.getUUID().equals(this.propietario) || jugador.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER);
         if (!puede) {
            return InteractionResult.PASS;
         }

         if (!this.level().isClientSide()) {
            this.ejectPassengers();
            this.discard();
         }

         return InteractionResult.SUCCESS;
      } else if (this.getPassengers().size() < 2 && !jugador.isPassenger()) {
         if (!this.level().isClientSide()) {
            jugador.startRiding(this);
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   protected boolean canAddPassenger(Entity pasajero) {
      return this.getPassengers().size() < 2 && pasajero instanceof Player;
   }

   public LivingEntity getControllingPassenger() {
      return !this.getPassengers().isEmpty() && this.getPassengers().get(0) instanceof Player p ? p : null;
   }

   protected void positionRider(Entity pasajero, MoveFunction mover) {
      if (this.hasPassenger(pasajero)) {
         int i = this.getPassengers().indexOf(pasajero);
         float d = this.getDiametro();
         float altura = pasajero instanceof Player p ? p.getDimensions(Pose.STANDING).height() : 1.8F;
         double lado = this.getPassengers().size() > 1 ? (i == 0 ? -0.32 : 0.32) * (altura / 1.8F) : 0.0;
         double rad = Math.toRadians(this.getYRot());
         double x = this.getX() + Math.cos(rad) * lado;
         double z = this.getZ() + Math.sin(rad) * lado;
         double y = this.getY() + d / 2.0 - 0.6 * altura;
         mover.accept(pasajero, x, y, z);
      }
   }

   public Vec3 getDismountLocationForPassenger(LivingEntity pasajero) {
      Vec3 base = pasajero.position();

      for (int k = 0; k < 8; k++) {
         Vec3 p = base.add(0.0, k * 0.5, 0.0);
         if (this.level().noCollision(pasajero, pasajero.getBoundingBox().move(p.subtract(pasajero.position())))) {
            return p;
         }
      }

      return new Vec3(this.getX(), this.getY() + 0.05, this.getZ());
   }

   public void move(MoverType tipo, Vec3 delta) {
      this.setPos(this.getX() + delta.x, this.getY() + delta.y, this.getZ() + delta.z);
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide()) {
         if (this.isControlledByLocalInstance()) {
            this.pasos = 0;
            this.lx = this.getX();
            this.ly = this.getY();
            this.lz = this.getZ();
            movimientoCliente.accept(this);
         } else if (this.pasos > 0) {
            double p = 1.0 / this.pasos;
            this.setPos(this.getX() + (this.lx - this.getX()) * p, this.getY() + (this.ly - this.getY()) * p, this.getZ() + (this.lz - this.getZ()) * p);
            this.pasos--;
         }
      } else if (this.getPassengers().isEmpty()) {
         if (this.hubo) {
            this.discard();
         }
      } else {
         this.hubo = true;
         if (this.tickCount % 10 != 0) {
            return;
         }

         float mayor = 0.0F;

         for (Entity e : this.getPassengers()) {
            if (e instanceof Player p) {
               mayor = Math.max(mayor, p.getDimensions(Pose.STANDING).height());
            }
         }

         float d = Mth.clamp(mayor * 1.25F + 0.45F, 0.9F, 6.0F);
         if (Math.abs(d - this.getDiametro()) > 0.01F) {
            this.setDiametro(d);
         }
      }
   }

   public void lerpTo(double x, double y, double z, float yRot, float xRot, int pasos) {
      if (!this.isControlledByLocalInstance()) {
         this.lx = x;
         this.ly = y;
         this.lz = z;
         this.pasos = 3;
         this.setYRot(yRot);
      }
   }

   protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
      if (nbt.contains("Propietario")) {
         this.propietario = nbt.read("Propietario", net.minecraft.core.UUIDUtil.CODEC).orElse(null);
      }

      if (nbt.contains("Diametro")) {
         this.setDiametro(nbt.getFloatOr("Diametro", 0.0F));
      }
   }

   protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
      if (this.propietario != null) {
         nbt.store("Propietario", net.minecraft.core.UUIDUtil.CODEC, this.propietario);
      }

      nbt.putFloat("Diametro", this.getDiametro());
   }
}
