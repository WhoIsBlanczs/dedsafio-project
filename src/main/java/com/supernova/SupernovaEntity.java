package com.supernova;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.ruleta.Ajustes;

public class SupernovaEntity extends Entity {
   private static final int FIRE_SECONDS = 15;
   private static final float SHOCK_ARMOR_PERCENT_PER_SECOND = 0.025F;
   private static final float LETHAL_DAMAGE = 1000000.0F;
   private static final double MESSAGE_RANGE = 3000.0;
   private static final EquipmentSlot[] ARMOR = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private static final EntityDataAccessor<Float> MAX_RADIUS = SynchedEntityData.defineId(SupernovaEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> BORN = SynchedEntityData.defineId(SupernovaEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DETONATED = SynchedEntityData.defineId(SupernovaEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> HOLD = SynchedEntityData.defineId(SupernovaEntity.class, EntityDataSerializers.INT);
   private final Set<UUID> hit = new HashSet<>();
   private final Set<UUID> shockHit = new HashSet<>();
   private static final int SHOCK_FIRE_SECONDS = 5;
   private boolean blastStarted;
   private SupernovaRomper.Trabajo romper;

   public SupernovaEntity(EntityType<?> type, Level level) {
      super(type, level);
      this.noPhysics = true;
      if (!level.isClientSide()) {
         this.entityData.set(BORN, (int)level.getGameTime());
         this.entityData.set(MAX_RADIUS, SupernovaAjustes.limitarTamano(SupernovaAjustes.tamano()));
         this.entityData.set(HOLD, SupernovaAjustes.limitarTicks(SupernovaAjustes.tiempoTicks));
      }
   }

   public float getMaxRadius() {
      return (Float)this.entityData.get(MAX_RADIUS);
   }

   public void setMaxRadius(float r) {
      this.entityData.set(MAX_RADIUS, SupernovaAjustes.limitarTamano(r));
   }

   public int getHoldTicks() {
      return (Integer)this.entityData.get(HOLD);
   }

   public void setHoldTicks(int ticks) {
      this.entityData.set(HOLD, SupernovaAjustes.limitarTicks(ticks));
   }

   public boolean isDetonating() {
      return (Integer)this.entityData.get(DETONATED) >= 0;
   }

   public int bornTick() {
      return (Integer)this.entityData.get(BORN);
   }

   public int detonatedTick() {
      return (Integer)this.entityData.get(DETONATED);
   }

   public SupernovaState state() {
      return new SupernovaState(
         this.getId(),
         this.level().dimension().location().toString(),
         this.getX(),
         this.getY(),
         this.getZ(),
         this.getMaxRadius(),
         this.bornTick(),
         this.detonatedTick(),
         this.getHoldTicks()
      );
   }

   public boolean detonate() {
      if (!this.level().isClientSide() && !this.isDetonating()) {
         ServerLevel sl = (ServerLevel)this.level();
         this.entityData.set(DETONATED, (int)sl.getGameTime());
         SupernovaNet.broadcast(this, false);
         return true;
      } else {
         return false;
      }
   }

   private void tell(ServerLevel sl, Component msg) {
      for (ServerPlayer p : sl.players()) {
         if (p.distanceToSqr(this) < 9000000.0) {
            p.sendOverlayMessage(msg);
         }
      }
   }

   private void countdownMessage(ServerLevel sl, int seconds) {
      this.tell(sl, Component.literal("La estrella colapsará en " + seconds + " segundos. ¡Aléjate todo lo que puedas!").withStyle(ChatFormatting.WHITE));
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         ServerLevel sl = (ServerLevel)this.level();
         if (this.isDetonating()) {
            int c = (int)(sl.getGameTime() - ((Integer)this.entityData.get(DETONATED)).intValue());
            if (c < SupernovaState.countdown()) {
               int left = (SupernovaState.countdown() - c) / 20;
               if (c > 0 && c % 20 == 0 && left <= 10) {
                  this.countdownMessage(sl, left);
               }
            } else {
               int b = c - SupernovaState.countdown();
               if (!this.blastStarted) {
                  this.blastStarted = true;
                  this.tell(sl, Component.literal("¡Supernova! La onda expansiva se acerca.").withStyle(ChatFormatting.WHITE));
                  if (SupernovaRomper.activo) {
                     Vec3 centro = this.state().center();
                     this.romper = SupernovaRomper.empezar(sl, centro.x, centro.z, this.state().maxRadius);
                  }
               }

               if (this.romper != null) {
                  this.romper.limite = Math.max(this.romper.limite, this.state().radius((float)sl.getGameTime()));
               }

               this.sweep(sl);
               if (b >= SupernovaState.blast() + this.getHoldTicks()) {
                  if (this.romper != null) {
                     this.romper.limite = this.state().maxRadius;
                  }

                  this.discard();
               }
            }
         }
      }
   }

   private static boolean afectable(LivingEntity e) {
      if (!e.isAlive()) {
         return false;
      } else if (e instanceof AbstractVillager) {
         return false;
      } else if (e instanceof TamableAnimal t && t.isTame()) {
         return false;
      } else {
         return e instanceof AbstractHorse h && h.isTamed() ? false : !(e instanceof Player p && (p.isCreative() || p.isSpectator()));
      }
   }

   private DamageSource lethalSource(ServerLevel sl) {
      return new DamageSource(sl.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SupernovaMod.SUPERNOVA_DAMAGE), this);
   }

   private DamageSource shockSource(ServerLevel sl) {
      return new DamageSource(sl.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SupernovaMod.SUPERNOVA_DAMAGE), this) {
         public String getMsgId() {
            return "supernova.onda";
         }

         public Component getLocalizedDeathMessage(LivingEntity victima) {
            return Component.empty().append(victima.getDisplayName()).append(Component.literal(" fue alcanzado por la Onda Expansiva"));
         }
      };
   }

   private void sweep(ServerLevel sl) {
      SupernovaState s = this.state();
      float now = (float)sl.getGameTime();
      float r = s.radius(now);
      if (!(r <= 0.0F)) {
         Vec3 c = s.center();

         for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(c, c).inflate(r))) {
            if (afectable(e) && e.getBoundingBox().getCenter().distanceTo(c) <= r) {
               this.smite(sl, e, c, this.hit.add(e.getUUID()));
            }
         }

         float sr = s.shockRadius(now);
         if (!(sr <= r)) {
            boolean everySecond = sl.getGameTime() % 20L == 0L;

            for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(c, c).inflate(sr))) {
               if (afectable(e)) {
                  double d = e.getBoundingBox().getCenter().distanceTo(c);
                  if (!(d > sr) && !(d <= r)) {
                     UUID id = e.getUUID();
                     if (!this.hit.contains(id) && this.shockHit.add(id)) {
                        this.shock(sl, e, c, (float)d, s);
                     } else if (everySecond) {
                        wearArmor(e);
                     }
                  }
               }
            }
         }
      }
   }

   private void shock(ServerLevel sl, LivingEntity e, Vec3 center, float d, SupernovaState s) {
      float t = SupernovaState.sat((d - s.maxRadius) / Math.max(1.0F, s.shockMax() - s.maxRadius));
      e.igniteForSeconds(Ajustes.entero("supernova.fuego_onda"));
      e.hurt(this.shockSource(sl), 1000000.0F);
      if (e.isAlive()) {
         Vec3 dir = e.position().subtract(center);
         dir = dir.lengthSqr() < 1.0E-4 ? new Vec3(0.0, 1.0, 0.0) : dir.multiply(1.0, 0.0, 1.0).normalize();
         double k = 3.5 - 2.0 * t;
         e.push(dir.x * k, 0.9, dir.z * k);
         e.needsSync = true;
      }
   }

   private void smite(ServerLevel sl, LivingEntity e, Vec3 center, boolean first) {
      e.igniteForSeconds(Ajustes.entero("supernova.fuego"));

      for (EquipmentSlot slot : ARMOR) {
         ItemStack st = e.getItemBySlot(slot);
         if (!st.isEmpty() && st.isDamageableItem()) {
            e.onEquippedItemBroken(st.getItem(), slot);
            e.setItemSlot(slot, ItemStack.EMPTY);
         }
      }

      e.hurt(this.lethalSource(sl), 1000000.0F);
      if (first && e.isAlive()) {
         Vec3 dir = e.position().subtract(center);
         dir = dir.lengthSqr() < 1.0E-4 ? new Vec3(0.0, 1.0, 0.0) : dir.normalize();
         e.push(dir.x * 3.0, 1.0, dir.z * 3.0);
         e.needsSync = true;
      }
   }

   private static void wearArmor(LivingEntity e) {
      for (EquipmentSlot slot : ARMOR) {
         ItemStack st = e.getItemBySlot(slot);
         if (!st.isEmpty() && st.isDamageableItem()) {
            int max = st.getMaxDamage();
            int loss = Math.max(1, Math.round(max * Ajustes.decimal("supernova.armadura") / 100.0F));
            int now = st.getDamageValue() + loss;
            if (now >= max) {
               e.onEquippedItemBroken(st.getItem(), slot);
               e.setItemSlot(slot, ItemStack.EMPTY);
            } else {
               st.setDamageValue(now);
            }
         }
      }
   }

   protected void defineSynchedData(Builder builder) {
      builder.define(MAX_RADIUS, 250.0F);
      builder.define(BORN, 0);
      builder.define(DETONATED, -1);
      builder.define(HOLD, 0);
   }

   protected void readAdditionalSaveData(CompoundTag tag) {
      if (tag.contains("MaxRadius")) {
         this.setMaxRadius(tag.getFloatOr("MaxRadius", 0.0F));
      }

      if (tag.contains("HoldTicks")) {
         this.setHoldTicks(tag.getIntOr("HoldTicks", 0));
      }
   }

   protected void addAdditionalSaveData(CompoundTag tag) {
      tag.putFloat("MaxRadius", this.getMaxRadius());
      tag.putInt("HoldTicks", this.getHoldTicks());
   }
}
