package com.novastudio.dedsafio4;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;

public final class PrimitiveBagItem extends Item {
   public PrimitiveBagItem() {
      super(new Item.Properties().stacksTo(1));
   }

   public boolean canFitInsideContainerItems() {
      return false;
   }

   static void open(Level var0, Player var1, InteractionHand var2) {
      if (!var0.isClientSide()) {
         ItemStack var3 = var1.getItemInHand(var2);
         int var4 = var2 == InteractionHand.MAIN_HAND ? var1.getInventory().getSelectedSlot() : 40;
         if (var3.getItem() instanceof PrimitiveBagItem) {
            var1.openMenu(
               new SimpleMenuProvider(
                  (var2x, var3xx, var4xx) -> new PrimitiveBagScreenHandler(var2x, var3xx, new PrimitiveBagInventory(var3, var3xx, var4)),
                  Component.translatable("item.nexo.bolsa_primitiva")
               )
            );
         }
      }
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      open(var1, var2, var3);
      return InteractionResult.SUCCESS;
   }

   public InteractionResult useOn(UseOnContext var1) {
      Player var2 = var1.getPlayer();
      if (var2 == null) {
         return InteractionResult.PASS;
      } else {
         open(var1.getLevel(), var2, var1.getHand());
         return InteractionResult.SUCCESS;
      }
   }
}
