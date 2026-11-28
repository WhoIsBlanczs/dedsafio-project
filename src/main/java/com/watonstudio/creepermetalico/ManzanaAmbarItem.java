package com.watonstudio.creepermetalico;

import net.minecraft.world.InteractionResult;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class ManzanaAmbarItem extends Item {
   public ManzanaAmbarItem(Properties settings) {
      super(settings.food(new Builder().nutrition(4).saturationModifier(1.2F).alwaysEdible().build()));
   }

   public ItemUseAnimation getUseAnimation(ItemStack stack) {
      return ItemUseAnimation.EAT;
   }

   public int getUseDuration(ItemStack stack, LivingEntity user) {
      return 32;
   }

   public InteractionResult use(Level world, Player user, InteractionHand hand) {
      user.startUsingItem(hand);
      return InteractionResult.CONSUME;
   }

   public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
      ItemStack result = super.finishUsingItem(stack, world, user);
      if (!world.isClientSide() && user instanceof Player player) {
         player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
         player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0));
      }

      return result;
   }
}
