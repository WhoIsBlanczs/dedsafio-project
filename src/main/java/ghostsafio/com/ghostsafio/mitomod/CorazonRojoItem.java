package com.ghostsafio.mitomod;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class CorazonRojoItem extends Item {
   public CorazonRojoItem(Properties properties) {
      super(properties);
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      return HeartHelper.getExtraHealth(player) >= 40.0 ? InteractionResultHolder.fail(stack) : super.use(level, player, hand);
   }

   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
      if (!level.isClientSide && entity instanceof Player player) {
         HeartHelper.addHeart(player);
      }

      return super.finishUsingItem(stack, level, entity);
   }
}
