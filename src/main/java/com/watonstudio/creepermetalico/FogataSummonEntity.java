package com.watonstudio.creepermetalico;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import com.geckolib.util.GeckoLibUtil;

public final class FogataSummonEntity extends Entity implements GeoEntity {
   public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) {
      return false;
   }

   private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
   private boolean campfireHandled = false;

   public FogataSummonEntity(EntityType<? extends FogataSummonEntity> type, Level world) {
      super(type, world);
      this.setNoGravity(true);
   }

   protected void defineSynchedData(Builder builder) {
   }

   protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
      this.campfireHandled = nbt.getBooleanOr("CampfireHandled", false);
   }

   protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
      nbt.putBoolean("CampfireHandled", this.campfireHandled);
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.geoCache;
   }

   public AABB getBoundingBoxForCulling() {
      return this.getBoundingBox().inflate(3.5, 6.5, 3.5);
   }

   public boolean shouldRenderAtSqrDistance(double distance) {
      return distance < 262144.0;
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide() && !this.campfireHandled) {
         if (this.level() instanceof ServerLevel serverWorld) {
            BlockPos var5 = this.blockPosition();
            if (!canPlaceCampfire(serverWorld, var5)) {
               var5 = var5.below();
            }

            if (canPlaceCampfire(serverWorld, var5)) {
               Direction facing = Direction.fromYRot(this.getYRot());
               BlockState campfire = (BlockState)((BlockState)((BlockState)((BlockState)Blocks.CAMPFIRE
                           .defaultBlockState()
                           .setValue(CampfireBlock.FACING, facing))
                        .setValue(CampfireBlock.LIT, true))
                     .setValue(CampfireBlock.SIGNAL_FIRE, false))
                  .setValue(CampfireBlock.WATERLOGGED, false);
               serverWorld.setBlock(var5, campfire, 3);
               this.snapTo(var5.getX() + 0.5, var5.getY(), var5.getZ() + 0.5, this.getYRot(), this.getXRot());
               this.setDeltaMovement(0.0, 0.0, 0.0);
            }

            this.campfireHandled = true;
         }
      }
   }

   private static boolean canPlaceCampfire(ServerLevel world, BlockPos pos) {
      if (pos.getY() > world.getMinY() && pos.getY() < world.getMaxY()) {
         BlockState current = world.getBlockState(pos);
         BlockPos supportPos = pos.below();
         BlockState support = world.getBlockState(supportPos);
         boolean replaceable = current.isAir() || current.canBeReplaced();
         boolean hasSupport = support.isFaceSturdy(world, supportPos, Direction.UP);
         return replaceable && hasSupport;
      } else {
         return false;
      }
   }
}
