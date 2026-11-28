package com.novastudio.dedsafio4;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;

public final class CreeperNuke {
   public static final int RADIUS = 18;
   public static final int HALF_HEIGHT = 4;
   public static final float DAMAGE = 100.0F;

   private CreeperNuke() {
   }

   public static void explode(Entity var0) {
      ServerLevel var1 = (ServerLevel)var0.level();
      double var2 = var0.getX();
      double var4 = var0.getY();
      double var6 = var0.getZ();
      DamageSource var8 = var1.damageSources().explosion(var0, var0);
      AABB var9 = new AABB(var2 - 18.0, var4 - 4.0, var6 - 18.0, var2 + 18.0, var4 + 4.0, var6 + 18.0);

      for (LivingEntity var11 : var1.getEntitiesOfClass(LivingEntity.class, var9, var1x -> var1x != var0 && var1x.isAlive())) {
         double var12 = Math.hypot(var11.getX() - var2, var11.getZ() - var6);
         if (!(var12 > 18.0) && !(Math.abs(var11.getY() - var4) > 4.0)) {
            if (var11 instanceof Player var14) {
               if (breakRaisedShield(var1, var14)) {
                  continue;
               }

               popTotemInHand(var14);
            }

            var11.hurt(var8, 100.0F);
         }
      }

      carve(var1, var0.blockPosition());
      var1.playSound(null, var2, var4, var6, (SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 4.0F, 0.7F);
      var1.sendParticles(ParticleTypes.EXPLOSION_EMITTER, var2, var4, var6, 12, 9.0, 2.0, 9.0, 0.0);
      var0.discard();
   }

   private static boolean breakRaisedShield(ServerLevel var0, Player var1) {
      if (var1.isUsingItem() && var1.getUseItem().getItem() instanceof ShieldItem) {
         InteractionHand var2 = var1.getUsedItemHand();
         var1.releaseUsingItem();
         var1.setItemInHand(var2, ItemStack.EMPTY);
         var0.broadcastEntityEvent(var1, (byte)(var2 == InteractionHand.MAIN_HAND ? 47 : 48));
         return true;
      } else {
         return false;
      }
   }

   private static void popTotemInHand(Player var0) {
      CreeperAbilities.popTotem(var0);
   }

   private static void carve(ServerLevel var0, BlockPos var1) {
      if (var0.getGameRules().get(GameRules.MOB_GRIEFING)) {
         BlockState var2 = Blocks.AIR.defaultBlockState();
         BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();
         double var4 = 342.25;

         for (int var6 = -18; var6 <= 18; var6++) {
            for (int var7 = -18; var7 <= 18; var7++) {
               if (!(var6 * var6 + var7 * var7 > var4)) {
                  for (int var8 = -4; var8 <= 4; var8++) {
                     var3.set(var1.getX() + var6, var1.getY() + var8, var1.getZ() + var7);
                     BlockState var9 = var0.getBlockState(var3);
                     if (!var9.isAir() && !(var9.getDestroySpeed(var0, var3) < 0.0F) && !(var9.getBlock().getExplosionResistance() >= 1200.0F)) {
                        var0.setBlock(var3, var2, 18);
                     }
                  }
               }
            }
         }
      }
   }
}
