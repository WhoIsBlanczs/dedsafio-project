package com.watonstudio.creepermetalico;

import net.minecraft.world.InteractionResult;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class LinternaItem extends Item {
   private static final int TIEMPO_USO = 40;
   private final boolean encendida;

   public LinternaItem(boolean encendida, Properties settings) {
      super(settings);
      this.encendida = encendida;
   }

   public boolean isLit() {
      return this.encendida;
   }

   public InteractionResult use(Level world, Player player, InteractionHand hand) {
      player.startUsingItem(hand);
      return InteractionResult.CONSUME;
   }

   public int getUseDuration(ItemStack stack, LivingEntity user) {
      return 40;
   }

   public ItemUseAnimation getUseAnimation(ItemStack stack) {
      return ItemUseAnimation.BLOCK;
   }

   public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
      if (!(user instanceof Player)) {
         return stack;
      }

      if (!world.isClientSide()) {
         user.playSound(this.encendida ? SoundEvents.STONE_BUTTON_CLICK_OFF : SoundEvents.STONE_BUTTON_CLICK_ON, 0.8F, this.encendida ? 0.9F : 1.15F);
      }

      Item siguiente = this.encendida ? CreeperMetalicoMod.LINTERNA : CreeperMetalicoMod.LINTERNA_ENCENDIDA;
      return new ItemStack(siguiente);
   }
}
