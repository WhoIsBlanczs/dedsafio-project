package com.novastudio.dedsafio4;

import net.minecraft.world.InteractionResult;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;

public class PortalBucketItem extends Item {
   public PortalBucketItem() {
      super(new Item.Properties().stacksTo(1));
   }

   public InteractionResult use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
      if (hit.getType() != HitResult.Type.BLOCK) {
         return InteractionResult.PASS;
      } else {
         BlockPos target = hit.getBlockPos().relative(hit.getDirection());
         BlockState existing = level.getBlockState(target);
         if (level.mayInteract(player, hit.getBlockPos()) && player.mayUseItemAt(target, hit.getDirection(), stack) && existing.canBeReplaced()) {
            if (!level.isClientSide()) {
               level.setBlock(target, PortalWater.BLOCK.defaultBlockState(), 11);
               level.playSound(null, target, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.FAIL;
         }
      }
   }
}
