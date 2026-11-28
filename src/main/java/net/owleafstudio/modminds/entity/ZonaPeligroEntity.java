package net.owleafstudio.modminds.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.network.syncher.SynchedEntityData;

public class ZonaPeligroEntity extends Entity {
   private static final EntityDataAccessor<Float> RADIO = SynchedEntityData.defineId(ZonaPeligroEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(ZonaPeligroEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> VIDA = SynchedEntityData.defineId(ZonaPeligroEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> ONDA = SynchedEntityData.defineId(ZonaPeligroEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> GROSOR = SynchedEntityData.defineId(ZonaPeligroEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> LARGO = SynchedEntityData.defineId(ZonaPeligroEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> ANGULO = SynchedEntityData.defineId(ZonaPeligroEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> ATACANTE = SynchedEntityData.defineId(ZonaPeligroEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> FORMA = SynchedEntityData.defineId(ZonaPeligroEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> SEMILLA = SynchedEntityData.defineId(ZonaPeligroEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> ALTO = SynchedEntityData.defineId(ZonaPeligroEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> ESPEJO = SynchedEntityData.defineId(ZonaPeligroEntity.class, EntityDataSerializers.INT);

   public ZonaPeligroEntity(EntityType<ZonaPeligroEntity> type, Level world) {
      super(type, world);
      this.setNoGravity(true);
      this.noPhysics = true;
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      builder.define(RADIO, 3.0F);
      builder.define(COLOR, 15864338);
      builder.define(VIDA, 30);
      builder.define(ONDA, 0.0F);
      builder.define(GROSOR, 0.0F);
      builder.define(LARGO, 0.0F);
      builder.define(ANGULO, 0.0F);
      builder.define(ATACANTE, -1);
      builder.define(FORMA, 0);
      builder.define(SEMILLA, 0);
      builder.define(ALTO, 0.0F);
      builder.define(ESPEJO, -1);
   }

   public void setShape(double radius, int rgb, int lifeTicks, double waveTicks) {
      this.setShape(radius, rgb, lifeTicks, waveTicks, 0.0);
   }

   public void setShape(double radius, int rgb, int lifeTicks, double waveTicks, double depth) {
      this.entityData.set(RADIO, (float)radius);
      this.entityData.set(COLOR, rgb);
      this.entityData.set(VIDA, lifeTicks);
      this.entityData.set(ONDA, (float)waveTicks);
      this.entityData.set(GROSOR, (float)depth);
   }

   public void setBeam(double length, double width, double angle, int rgb, int lifeTicks) {
      this.entityData.set(LARGO, (float)length);
      this.entityData.set(GROSOR, (float)width);
      this.entityData.set(ANGULO, (float)angle);
      this.entityData.set(COLOR, rgb);
      this.entityData.set(VIDA, lifeTicks);
   }

   public float getBeamLength() {
      return (Float)this.entityData.get(LARGO);
   }

   public float getBeamAngle() {
      return (Float)this.entityData.get(ANGULO);
   }

   public void follow(Entity attacker) {
      this.entityData.set(ATACANTE, attacker.getId());
   }

   public void setCrack(double length, double angle, int seed, int rgb, int lifeTicks) {
      this.entityData.set(RADIO, (float)length);
      this.entityData.set(ANGULO, (float)angle);
      this.entityData.set(SEMILLA, seed);
      this.entityData.set(COLOR, rgb);
      this.entityData.set(VIDA, lifeTicks);
      this.entityData.set(FORMA, 1);
   }

   public boolean isCrack() {
      return (Integer)this.entityData.get(FORMA) == 1;
   }

   public int getForma() {
      return (Integer)this.entityData.get(FORMA);
   }

   public void setRayCircle(double radius, int rgb, int lifeTicks, double boltHeight, int mirror) {
      this.entityData.set(RADIO, (float)radius);
      this.entityData.set(COLOR, rgb);
      this.entityData.set(VIDA, lifeTicks);
      this.entityData.set(ALTO, (float)boltHeight);
      this.entityData.set(ESPEJO, mirror);
      this.entityData.set(FORMA, 2);
   }

   public void setBlackHole(double radius, double depth, int seed, int rgb, int lifeTicks) {
      this.entityData.set(RADIO, (float)radius);
      this.entityData.set(ALTO, (float)depth);
      this.entityData.set(SEMILLA, seed);
      this.entityData.set(COLOR, rgb);
      this.entityData.set(VIDA, lifeTicks);
      this.entityData.set(FORMA, 3);
   }

   public void setColumns(double radius, int count, double height, int seed, int rgb, int lifeTicks, double riseTicks) {
      this.entityData.set(RADIO, (float)radius);
      this.entityData.set(LARGO, (float)count);
      this.entityData.set(ALTO, (float)height);
      this.entityData.set(SEMILLA, seed);
      this.entityData.set(ONDA, (float)riseTicks);
      this.entityData.set(COLOR, rgb);
      this.entityData.set(VIDA, lifeTicks);
      this.entityData.set(FORMA, 4);
   }

   public static Vec3 columnSpot(int c, int count, double area, int seed) {
      double ang = c * 2.39996 + seed * 0.13;
      double radio = area * Math.sqrt((c + 0.5) / count);
      return new Vec3(Math.cos(ang) * radio, 0.0, Math.sin(ang) * radio);
   }

   public static int columnCount(int requested) {
      return Math.max(1, Math.min(14, Math.round((float)requested)));
   }

   public static double columnArrival(int c, double riseTicks) {
      return riseTicks + c * 1.6;
   }

   public void setField(double radius, double angle, double wander, int seed, int rgb, int lifeTicks) {
      this.entityData.set(RADIO, (float)radius);
      this.entityData.set(GROSOR, (float)wander);
      this.entityData.set(ANGULO, (float)angle);
      this.entityData.set(SEMILLA, seed);
      this.entityData.set(COLOR, rgb);
      this.entityData.set(VIDA, lifeTicks);
      this.entityData.set(FORMA, 5);
   }

   public void setFurrow(double length, double width, double angle, int seed, int rgb, int lifeTicks, double travelTicks) {
      this.entityData.set(LARGO, (float)length);
      this.entityData.set(GROSOR, (float)width);
      this.entityData.set(ANGULO, (float)angle);
      this.entityData.set(SEMILLA, seed);
      this.entityData.set(ONDA, (float)travelTicks);
      this.entityData.set(COLOR, rgb);
      this.entityData.set(VIDA, lifeTicks);
      this.entityData.set(FORMA, 6);
   }

   public void setAvalanche(double length, double width, double height, double angle, int seed, int rgb, int lifeTicks) {
      this.entityData.set(LARGO, (float)length);
      this.entityData.set(GROSOR, (float)width);
      this.entityData.set(ALTO, (float)height);
      this.entityData.set(ANGULO, (float)angle);
      this.entityData.set(SEMILLA, seed);
      this.entityData.set(COLOR, rgb);
      this.entityData.set(VIDA, lifeTicks);
      this.entityData.set(FORMA, 7);
   }

   public int getMirrorId() {
      return (Integer)this.entityData.get(ESPEJO);
   }

   public void setMirror(int otherZoneId) {
      this.entityData.set(ESPEJO, otherZoneId);
   }

   public float getHeight() {
      return (Float)this.entityData.get(ALTO);
   }

   public float getCount() {
      return (Float)this.entityData.get(LARGO);
   }

   public double wander() {
      return ((Float)this.entityData.get(GROSOR)).floatValue();
   }

   public int getSeed() {
      return (Integer)this.entityData.get(SEMILLA);
   }

   public int getFollowId() {
      return (Integer)this.entityData.get(ATACANTE);
   }

   public float getRadius() {
      return (Float)this.entityData.get(RADIO);
   }

   public int getColor() {
      return (Integer)this.entityData.get(COLOR);
   }

   public int getLife() {
      return (Integer)this.entityData.get(VIDA);
   }

   public float getWave() {
      return (Float)this.entityData.get(ONDA);
   }

   public float getDepth() {
      return (Float)this.entityData.get(GROSOR);
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity);
   }

   public Vec3 paintCentre(float partialTick) {
      Entity anchor = this.level().getEntity(this.getFollowId());
      return anchor != null ? anchor.getPosition(partialTick) : this.getPosition(partialTick);
   }

   public Vec3 mirrorPaintCentre(float partialTick) {
      return this.level().getEntity(this.getMirrorId()) instanceof ZonaPeligroEntity mirror ? mirror.paintCentre(partialTick) : null;
   }

   public void tick() {
      super.tick();
      if (this.tickCount >= this.getLife()) {
         this.discard();
      }
   }

   public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount) {
      return false;
   }

   public boolean isInvulnerableTo(net.minecraft.server.level.ServerLevel level, DamageSource source) {
      return true;
   }

   public boolean isPickable() {
      return false;
   }

   public boolean isPushable() {
      return false;
   }

   public void push(Entity entity) {
   }

   public void push(double x, double y, double z) {
   }

   protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput tag) {
   }

   protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput tag) {
   }
}
