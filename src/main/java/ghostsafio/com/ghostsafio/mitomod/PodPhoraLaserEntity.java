package com.ghostsafio.mitomod;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class PodPhoraLaserEntity extends Projectile {
   private static final ResourceKey<DamageType> POD_LASER = ResourceKey.create(Registries.DAMAGE_TYPE, Ghostsafio.id("pod_phora_laser"));
   private static final int LIFETIME = 100;

   public PodPhoraLaserEntity(EntityType<? extends PodPhoraLaserEntity> type, Level level) {
      super(type, level);
   }

   protected void defineSynchedData(Builder builder) {
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
         if (hit.getType() != Type.MISS) {
            this.onHit(hit);
         }
      }

      Vec3 v = this.getDeltaMovement();
      this.setPos(this.getX() + v.x, this.getY() + v.y, this.getZ() + v.z);
      if (this.level().isClientSide) {
         this.updateRotation();
         this.level().addParticle(ParticleTypes.ELECTRIC_SPARK, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
      } else if (this.tickCount > 100 || !this.level().isLoaded(this.blockPosition())) {
         this.discard();
      }
   }

   protected boolean canHitEntity(Entity entity) {
      return super.canHitEntity(entity) && entity != this.getOwner();
   }

   private DamageSource laserDamage() {
      Holder<DamageType> holder = this.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(POD_LASER);
      return new DamageSource(holder, this, this.getOwner());
   }

   protected void onHitEntity(EntityHitResult result) {
      if (!this.level().isClientSide) {
         Entity target = result.getEntity();
         if (target instanceof Player player && this.playerBlocks(player)) {
            PodPhoraEntity.onLaserBlocked(this.level(), player);
            this.discard();
         } else {
            if (target.hurt(this.laserDamage(), 6.0F)) {
               if (target instanceof Player player) {
                  PodPhoraEntity.onLaserHitPlayer(player);
               }

               if (target instanceof LivingEntity living) {
                  living.knockback(0.3, this.getX() - living.getX(), this.getZ() - living.getZ());
               }
            }

            this.discard();
         }
      }
   }

   private boolean playerBlocks(Player player) {
      if (player.isBlocking() && player.getUseItem().is(Items.SHIELD)) {
         Vec3 toLaser = this.position().subtract(player.position());
         Vec3 flat = new Vec3(toLaser.x, 0.0, toLaser.z);
         if (flat.lengthSqr() < 1.0E-4) {
            return true;
         }

         Vec3 look = player.calculateViewVector(0.0F, player.getYHeadRot());
         Vec3 lookFlat = new Vec3(look.x, 0.0, look.z).normalize();
         return flat.normalize().dot(lookFlat) > -0.25;
      } else {
         return false;
      }
   }

   protected void onHitBlock(BlockHitResult result) {
      if (!this.level().isClientSide) {
         ((ServerLevel)this.level()).sendParticles(ParticleTypes.ELECTRIC_SPARK, this.getX(), this.getY(), this.getZ(), 10, 0.15, 0.15, 0.15, 0.08);
         this.playSound(SoundEvents.FIREWORK_ROCKET_BLAST, 0.5F, 1.5F + this.level().random.nextFloat() * 0.3F);
         this.discard();
      }
   }

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity);
   }

   public boolean isPickable() {
      return false;
   }
}
