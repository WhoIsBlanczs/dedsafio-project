package com.ghostsafio.mitomod;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;

public class NaveEntity extends Entity {
   public static final double CLIMB_SPEED = 0.5;
   public static final double TAKEOFF_HEIGHT = 40.0;
   public static final double EXIT_ATMOSPHERE_ALTITUDE = 600.0;
   public static final double SPACE_ALTITUDE = 700.0;
   public static final double ORBIT_ALTITUDE = 750.0;
   public static final int CRUISE_TICKS = 400;
   public static final float MIN_FUEL_TO_LAUNCH = 0.5F;
   private static final float FUEL_PER_TICK = 1.6E-4F;
   private static final EntityDataAccessor<Boolean> DOOR_OPEN = SynchedEntityData.defineId(NaveEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> ENGINE = SynchedEntityData.defineId(NaveEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> FUEL = SynchedEntityData.defineId(NaveEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(NaveEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> BODY = SynchedEntityData.defineId(NaveEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Float> WARP = SynchedEntityData.defineId(NaveEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> TILT = SynchedEntityData.defineId(NaveEntity.class, EntityDataSerializers.FLOAT);
   public static final float SPACE_YAW = (float)Math.toDegrees(Math.atan2(0.8, -0.5));
   public static final float TILT_HORIZONTAL = 90.0F;
   private static final float TILT_SPEED = 2.5F;
   private static final float ORBIT_TILT_SPEED = 1.25F;
   private static final int ROLL_START = 10;
   private static final int ROLL_LENGTH = 90;
   private int inputFlags = 0;
   private int doorTimer = 0;
   private boolean hadPilot = false;
   private int phaseTicks = 0;
   private int cruiseTicks = 0;
   private int origin = 0;
   private double launchY = 0.0;
   private boolean hasHome = false;
   private double homeX = 0.0;
   private double homeZ = 0.0;
   private UUID pilotId = null;
   private boolean warnedNoDimension = false;
   private int spaceTicks = 0;
   private boolean pilotHidden = false;
   private int fxStage = 0;
   public float tiltO = 0.0F;
   public float tiltClient = 0.0F;
   public float rollO = 0.0F;
   public float rollClient = 0.0F;
   private int orbitTicksClient = 0;
   public int doorAnimTicks = 1000;
   private boolean lastDoorOpen = false;

   public NaveEntity(EntityType<? extends NaveEntity> type, Level level) {
      super(type, level);
      this.setNoGravity(true);
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(DOOR_OPEN, false);
      builder.define(ENGINE, 0);
      builder.define(FUEL, 1.0F);
      builder.define(PHASE, 0);
      builder.define(BODY, 0);
      builder.define(WARP, 0.0F);
      builder.define(TILT, 0.0F);
   }

   protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput tag) {
      if (tag.child("Fuel").isPresent()) {
         this.setFuel(tag.getFloatOr("Fuel", 0.0F));
      }

      if (tag.child("HomeX").isPresent()) {
         this.hasHome = true;
         this.homeX = tag.getDoubleOr("HomeX", 0.0);
         this.homeZ = tag.getDoubleOr("HomeZ", 0.0);
      }

      this.origin = tag.getIntOr("Origin", 0);
      this.setBody(tag.getIntOr("Body", 0));
   }

   protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput tag) {
      tag.putFloat("Fuel", this.getFuel());
      tag.putInt("Origin", this.origin);
      tag.putInt("Body", this.getBody());
      if (this.hasHome) {
         tag.putDouble("HomeX", this.homeX);
         tag.putDouble("HomeZ", this.homeZ);
      }
   }

   public boolean isDoorOpen() {
      return (Boolean)this.entityData.get(DOOR_OPEN);
   }

   private void setDoorOpen(boolean open) {
      this.entityData.set(DOOR_OPEN, open);
   }

   public int getEngine() {
      return (Integer)this.entityData.get(ENGINE);
   }

   private void setEngine(int value) {
      if (this.getEngine() != value) {
         this.entityData.set(ENGINE, value);
      }
   }

   public float getFuel() {
      return (Float)this.entityData.get(FUEL);
   }

   public void setFuel(float fuel) {
      this.entityData.set(FUEL, Mth.clamp(fuel, 0.0F, 1.0F));
   }

   public int getPhase() {
      return (Integer)this.entityData.get(PHASE);
   }

   private void setPhase(int phase) {
      this.entityData.set(PHASE, phase);
   }

   public int getBody() {
      return (Integer)this.entityData.get(BODY);
   }

   private void setBody(int body) {
      this.entityData.set(BODY, body);
   }

   public float getWarp() {
      return (Float)this.entityData.get(WARP);
   }

   private void setWarp(float warp) {
      this.entityData.set(WARP, Mth.clamp(warp, 0.0F, 1.0F));
   }

   public float getTilt() {
      return (Float)this.entityData.get(TILT);
   }

   private void setTilt(float tilt) {
      this.entityData.set(TILT, Mth.clamp(tilt, 0.0F, 90.0F));
   }

   private static float approachValue(float current, float target, float step) {
      return current < target ? Math.min(current + step, target) : Math.max(current - step, target);
   }

   public void setInput(int flags) {
      this.inputFlags = flags;
   }

   public boolean hurtServer(ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) {
      return false;
   }

   public boolean isPickable() {
      return !this.isRemoved();
   }

   public boolean isPushable() {
      return false;
   }

   protected boolean canAddPassenger(Entity passenger) {
      return this.getPassengers().isEmpty();
   }

   public InteractionResult interact(Player player, InteractionHand hand, net.minecraft.world.phys.Vec3 toque$) {
      if (player.isSecondaryUseActive()) {
         return InteractionResult.PASS;
      }

      if (!this.level().isClientSide() && this.getPassengers().isEmpty()) {
         player.startRiding(this);
      }

      return InteractionResult.SUCCESS;
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide()) {
         this.clientTick();
      } else {
         this.serverTick();
      }
   }

   private void clientTick() {
      this.tiltO = this.tiltClient;
      this.tiltClient = this.tiltClient + (this.getTilt() - this.tiltClient) * 0.5F;
      if (this.level().dimension() == ModNave.SPACE && this.getPhase() == 3) {
         this.orbitTicksClient++;
      } else {
         this.orbitTicksClient = 0;
      }

      float rt = Mth.clamp((this.orbitTicksClient - 10) / 90.0F, 0.0F, 1.0F);
      float nextRoll = rt * rt * (3.0F - 2.0F * rt) * 360.0F;
      this.rollO = nextRoll < this.rollClient - 180.0F ? nextRoll : this.rollClient;
      this.rollClient = nextRoll;
      boolean open = this.isDoorOpen();
      if (open != this.lastDoorOpen) {
         this.lastDoorOpen = open;
         this.doorAnimTicks = 0;
      } else if (this.doorAnimTicks < 1000) {
         this.doorAnimTicks++;
      }
   }

   private void serverTick() {
      if (this.level() instanceof ServerLevel serverLevel) {
         MinecraftServer var9 = serverLevel.getServer();
         ServerPlayer pilot = this.getFirstPassenger() instanceof ServerPlayer sp ? sp : null;
         boolean piloted = pilot != null;
         if (piloted) {
            this.pilotId = pilot.getUUID();
         }

         int phase = this.getPhase();
         boolean inSpace = this.level().dimension() == ModNave.SPACE;
         if (pilot != null) {
            pilot.fallDistance = 0.0F;
         }

         this.fallDistance = 0.0F;
         if (pilot != null && phase > 0) {
            pilot.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 40, 0, false, false, false));
            this.pilotHidden = true;
         } else if (this.pilotHidden && pilot != null) {
            pilot.removeEffect(MobEffects.INVISIBILITY);
            this.pilotHidden = false;
         }

         if (phase > 0 && !piloted && this.pilotId != null) {
            ServerPlayer p = var9.getPlayerList().getPlayer(this.pilotId);
            if (p != null && p.level() == serverLevel && p.distanceToSqr(this) < 900.0) {
               p.startRiding(this, true, true);
            }
         }

         if (piloted != this.hadPilot) {
            this.hadPilot = piloted;
            if (phase == 0) {
               this.setDoorOpen(true);
               this.doorTimer = 50;
            }

            if (!piloted) {
               this.inputFlags = 0;
            }
         }

         if (this.doorTimer > 0 && --this.doorTimer == 0) {
            this.setDoorOpen(false);
         }

         if (phase == 0) {
            this.parkedTick(pilot, piloted, inSpace);
         } else {
            this.flightTick(serverLevel, var9, phase, inSpace);
         }
      }
   }

   private void parkedTick(ServerPlayer pilot, boolean piloted, boolean inSpace) {
      Vec3 d = this.getDeltaMovement();
      double vy = inSpace ? d.y * 0.98 : Math.max(d.y - 0.04, -0.7);
      double vx = d.x * 0.8;
      double vz = d.z * 0.8;
      this.setDeltaMovement(vx, vy, vz);
      this.move(MoverType.SELF, this.getDeltaMovement());
      if (this.onGround()) {
         this.setDeltaMovement(0.0, 0.0, 0.0);
      }

      this.setWarp(0.0F);
      this.setTilt(0.0F);
      this.setEngine(piloted ? 1 : 0);
      float fuel = this.getFuel();
      if (this.onGround() && fuel < 1.0F) {
         this.setFuel(fuel + 0.002F);
      }

      if (piloted && (this.inputFlags & 1) != 0) {
         this.inputFlags &= -2;
         if (this.isDoorOpen() || this.doorTimer > 0) {
            com.ghostsafio.mitomod.compat.Compat.mensaje(pilot, Component.literal("Espera a que se cierre la puerta..."), true);
         } else if (inSpace) {
            com.ghostsafio.mitomod.compat.Compat.mensaje(pilot, Component.literal("La nave está en el espacio sin ruta de vuelo."), true);
         } else if (this.getFuel() < 0.5F) {
            com.ghostsafio.mitomod.compat.Compat.mensaje(pilot, Component.literal("COMBUSTIBLE INSUFICIENTE (mínimo 50%)"), true);
         } else {
            this.startProgram();
         }
      }
   }

   private void startProgram() {
      if (this.getFirstPassenger() instanceof ServerPlayer sp) {
         this.setYRot(sp.getYRot());
         this.yRotO = sp.getYRot();
      }

      this.origin = this.level().dimension() == ModNave.RED_PLANET ? 1 : 0;
      this.setBody(this.origin);
      this.setTilt(0.0F);
      this.launchY = this.getY();
      if (this.origin == 0) {
         this.hasHome = true;
         this.homeX = this.getX();
         this.homeZ = this.getZ();
      }

      this.cruiseTicks = 0;
      this.enterPhase(1);
      this.setEngine(2);
   }

   private void enterPhase(int phase) {
      this.setPhase(phase);
      this.phaseTicks = 0;
   }

   private void flightTick(ServerLevel serverLevel, MinecraftServer server, int phase, boolean inSpace) {
      this.phaseTicks++;
      this.flightEffects(serverLevel, phase, inSpace);
      this.setEngine(2);
      this.setFuel(this.getFuel() - 1.6E-4F);

      float targetTilt = switch (phase) {
         case 3 -> inSpace && this.getY() >= 745.0 ? 90.0F : 0.0F;
         case 4, 5 -> 90.0F;
         case 6 -> this.phaseTicks < 40 ? 90.0F : 0.0F;
         default -> 0.0F;
      };
      this.setTilt(approachValue(this.getTilt(), targetTilt, phase == 3 ? 1.25F : 2.5F));
      Vec3 d = this.getDeltaMovement();
      double yaw = Math.toRadians(this.getYRot());
      double fx = -Math.sin(yaw);
      double fz = Math.cos(yaw);
      double vx = 0.0;
      double vy = d.y;
      double vz = 0.0;
      float warp = this.getWarp();
      switch (phase) {
         case 1:
            vy = Math.min(d.y + 0.012, 0.5);
            if (this.getY() - this.launchY >= 40.0) {
               this.enterPhase(2);
            }
            break;
         case 2:
            vy = 0.5;
            if (this.getY() >= 600.0) {
               this.enterPhase(3);
            }
            break;
         case 3:
            if (!inSpace) {
               vy = 0.5;
               if (this.getY() >= 700.0) {
                  ServerLevel space = server.getLevel(ModNave.SPACE);
                  if (space != null) {
                     this.transferTo(space, this.getX(), 701.0, this.getZ());
                     return;
                  }

                  vy = 0.0;
                  this.warnNoDimension(serverLevel, "ghostsafio:espacio");
               }
            } else if (this.getY() < 750.0) {
               vy = 0.5;
            } else {
               vy = 0.0;
               vx = fx * 0.5;
               vz = fz * 0.5;
               if (++this.cruiseTicks >= 400) {
                  this.enterPhase(4);
               }
            }
            break;
         case 4:
            warp = Math.min(1.0F, this.phaseTicks / 60.0F);
            this.setWarp(warp);
            if (this.origin == 0 && this.phaseTicks == 10) {
               ServerLevel gleba = server.getLevel(ModNave.RED_PLANET);
               if (gleba != null) {
                  GlebaPad.ensure(gleba);
               }
            }

            double speedx = 0.5;
            vy = 0.0;
            vx = fx * speedx;
            vz = fz * speedx;
            if (this.phaseTicks == 120) {
               this.setBody(1 - this.origin);
            }

            if (this.phaseTicks >= 200) {
               this.enterPhase(5);
            }
            break;
         case 5:
            warp = Math.max(0.0F, 1.0F - this.phaseTicks / 160.0F);
            this.setWarp(warp);
            double speed = 0.5;
            vy = 0.0;
            vx = fx * speed;
            vz = fz * speed;
            if (this.phaseTicks >= 160) {
               this.setWarp(0.0F);
               this.enterPhase(6);
            }
            break;
         case 6:
            vy = -0.5;
            vx = fx * 0.5 * 0.5;
            vz = fz * 0.5 * 0.5;
            if (this.getY() <= 700.0) {
               ServerLevel dest = this.origin == 0 ? server.getLevel(ModNave.RED_PLANET) : server.overworld();
               if (dest != null) {
                  double lx = 0.5;
                  double lz = 0.5;
                  if (this.origin == 0) {
                     GlebaPad.ensure(dest);
                     lx = 0.5 + (this.getRandom().nextDouble() - 0.5) * 30.0;
                     lz = 0.5 + (this.getRandom().nextDouble() - 0.5) * 30.0;
                  }

                  if (this.origin == 1) {
                     if (this.hasHome) {
                        lx = this.homeX;
                        lz = this.homeZ;
                     } else {
                        BlockPos spawn = com.ghostsafio.mitomod.compat.Compat.spawnMundo(server.overworld());
                        lx = spawn.getX() + 0.5;
                        lz = spawn.getZ() + 0.5;
                     }
                  }

                  this.phaseTicks = 0;
                  this.transferTo(dest, lx, 698.0, lz, 7);
                  return;
               }

               vy = 0.0;
               this.warnNoDimension(serverLevel, "ghostsafio:gleba");
            }
            break;
         default:
            double ground = serverLevel.getHeight(Types.MOTION_BLOCKING, Mth.floor(this.getX()), Mth.floor(this.getZ()));
            double above = this.getY() - ground;
            double speedy = Mth.clamp(above / 60.0, 0.35, 1.5);
            vy = -speedy;
            if (this.onGround() || above <= 0.5) {
               this.finishProgram();
               return;
            }
      }

      this.setDeltaMovement(Mth.lerp(0.2, d.x, vx), vy, Mth.lerp(0.2, d.z, vz));
      this.move(MoverType.SELF, this.getDeltaMovement());
      if (phase == 7 && this.onGround()) {
         this.finishProgram();
      }
   }

   private void sound(ServerLevel level, SoundEvent sound, float volume, float pitch) {
      level.playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.NEUTRAL, volume, pitch);
   }

   private void ring(ServerLevel level, ParticleOptions particle, int count, double speed) {
      for (int i = 0; i < count; i++) {
         double a = i * ((Math.PI * 2) / count);
         level.sendParticles(particle, this.getX(), this.getY() + 2.0, this.getZ(), 0, Math.cos(a), 0.0, Math.sin(a), speed);
      }
   }

   private void flightEffects(ServerLevel level, int phase, boolean inSpace) {
      if (inSpace) {
         this.spaceTicks++;
      }

      double yawRad = Math.toRadians(this.getYRot());
      double fx = -Math.sin(yawRad);
      double fz = Math.cos(yawRad);
      double tiltRad = Math.toRadians(this.getTilt());
      double up = Math.cos(tiltRad);
      double fw = Math.sin(tiltRad);
      double pivot = 2.31F;
      double nx = this.getX() - pivot * fw * fx;
      double ny = this.getY() + pivot - pivot * up;
      double nz = this.getZ() - pivot * fw * fz;
      double dx = -fw * fx;
      double dy = -up;
      double dz = -fw * fz;

      for (int i = 0; i < 2; i++) {
         double jx = (this.getRandom().nextDouble() - 0.5) * 0.5;
         double jy = (this.getRandom().nextDouble() - 0.5) * 0.3;
         double jz = (this.getRandom().nextDouble() - 0.5) * 0.5;
         double speed = 0.45 + this.getRandom().nextDouble() * 0.45;
         level.sendParticles(ParticleTypes.FLAME, nx + jx, ny + jy, nz + jz, 0, dx, dy, dz, speed);
      }

      if (!inSpace) {
         for (int i = 0; i < 1; i++) {
            double jx = (this.getRandom().nextDouble() - 0.5) * 0.7;
            double jz = (this.getRandom().nextDouble() - 0.5) * 0.7;
            level.sendParticles(ParticleTypes.LARGE_SMOKE, nx + jx, ny, nz + jz, 0, dx, dy, dz, 0.25);
         }
      } else {
         level.sendParticles(ParticleTypes.END_ROD, nx, ny, nz, 0, dx, dy, dz, 0.7);
      }

      if (phase == 1 && this.phaseTicks == 1) {
         this.sound(level, SoundEvents.LIGHTNING_BOLT_THUNDER, 4.0F, 0.55F);
         this.sound(level, SoundEvents.FIREWORK_ROCKET_LAUNCH, 3.0F, 0.6F);
         this.ring(level, ParticleTypes.LARGE_SMOKE, 36, 0.5);
         this.ring(level, ParticleTypes.FLAME, 24, 0.35);
      }

      if (!inSpace && phase == 2 && this.fxStage < 1 && this.getY() >= 380.0) {
         this.fxStage = 1;
         this.sound(level, SoundEvents.ENDER_DRAGON_GROWL, 3.0F, 0.5F);
      }

      if (inSpace && phase == 3 && this.spaceTicks == 2) {
         this.sound(level, SoundEvents.WITHER_SPAWN, 1.6F, 1.5F);
         this.sound(level, SoundEvents.LIGHTNING_BOLT_THUNDER, 3.0F, 0.7F);
         level.sendParticles(ParticleTypes.SONIC_BOOM, this.getX(), this.getY() + 2.0, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
         this.ring(level, ParticleTypes.END_ROD, 60, 0.9);
      }

      if (inSpace && phase == 3 && this.spaceTicks == 40) {
         this.sound(level, SoundEvents.BEACON_POWER_SELECT, 2.0F, 1.2F);
         this.ring(level, ParticleTypes.END_ROD, 40, 0.5);
      }

      if (inSpace && phase == 3 && this.cruiseTicks == 1) {
         this.sound(level, SoundEvents.BEACON_ACTIVATE, 3.0F, 0.8F);
         level.sendParticles(ParticleTypes.SONIC_BOOM, this.getX(), this.getY() + 2.0, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
         this.ring(level, ParticleTypes.FIREWORK, 72, 0.6);
         this.ring(level, ParticleTypes.END_ROD, 48, 1.1);
      }
   }

   private void warnNoDimension(ServerLevel level, String name) {
      if (!this.warnedNoDimension) {
         this.warnedNoDimension = true;
         if (this.getFirstPassenger() instanceof ServerPlayer p) {
            com.ghostsafio.mitomod.compat.Compat.mensaje(p, Component.literal("No se encontró la dimensión " + name + " (revisa data/ghostsafio/dimension)."), false);
         }
      }
   }

   private void finishProgram() {
      if (this.getFirstPassenger() instanceof ServerPlayer sp) {
         sp.removeEffect(MobEffects.INVISIBILITY);
         sp.fallDistance = 0.0F;
      }

      this.pilotHidden = false;
      this.setPhase(0);
      this.setWarp(0.0F);
      this.setTilt(0.0F);
      this.setDeltaMovement(Vec3.ZERO);
      this.setEngine(this.getFirstPassenger() != null ? 1 : 0);
      this.setDoorOpen(true);
      this.doorTimer = 80;
      this.hadPilot = this.getFirstPassenger() != null;
      this.inputFlags = 0;
   }

   private void transferTo(ServerLevel destination, double x, double y, double z) {
      this.transferTo(destination, x, y, z, this.getPhase());
   }

   private void transferTo(ServerLevel destination, double x, double y, double z, int newPhase) {
      ServerPlayer pilot = this.getFirstPassenger() instanceof ServerPlayer sp ? sp : null;
      float yaw = destination.dimension() == ModNave.SPACE ? SPACE_YAW : this.getYRot();
      NaveEntity copy = new NaveEntity(ModNave.NAVE, destination);
      copy.snapTo(x, y, z, yaw, 0.0F);
      copy.setDeltaMovement(this.getDeltaMovement());
      copy.setFuel(this.getFuel());
      copy.setPhase(newPhase);
      copy.setBody(this.getBody());
      copy.setWarp(this.getWarp());
      copy.setTilt(this.getTilt());
      copy.setEngine(2);
      copy.phaseTicks = this.phaseTicks;
      copy.cruiseTicks = this.cruiseTicks;
      copy.fxStage = this.fxStage;
      copy.pilotHidden = this.pilotHidden;
      copy.origin = this.origin;
      copy.launchY = this.launchY;
      copy.hasHome = this.hasHome;
      copy.homeX = this.homeX;
      copy.homeZ = this.homeZ;
      copy.pilotId = this.pilotId;
      copy.hadPilot = true;
      destination.getChunkAt(BlockPos.containing(x, y, z));
      if (pilot != null) {
         com.ghostsafio.mitomod.compat.Compat.teleport(pilot, destination, x, y, z, yaw, pilot.getXRot());
      }

      destination.addFreshEntity(copy);
      if (pilot != null) {
         pilot.startRiding(copy, true, true);
         pilot.fallDistance = 0.0F;
      }

      this.discard();
   }
}
