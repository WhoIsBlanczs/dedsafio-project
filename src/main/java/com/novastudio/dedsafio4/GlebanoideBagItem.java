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

public final class GlebanoideBagItem extends Item {
   private final String screenTitleKey;

   public GlebanoideBagItem() {
      this("bolsa_primitiva");
   }

   public GlebanoideBagItem(String var1) {
      super(new Item.Properties().stacksTo(1));
      this.screenTitleKey = "item.nexo." + var1;
   }

   public boolean canFitInsideContainerItems() {
      return false;
   }

   static void open(Level var0, Player var1, InteractionHand var2) {
      if (!var0.isClientSide()) {
         ItemStack var4 = var1.getItemInHand(var2);
         int var3 = var2 == InteractionHand.MAIN_HAND ? var1.getInventory().getSelectedSlot() : 40;
         if (var4.getItem() instanceof GlebanoideBagItem) {
            GlebanoideBagItem var6 = (GlebanoideBagItem)var4.getItem();
            var1.openMenu(
               new SimpleMenuProvider(
                  (var2x, var3xx, var4xx) -> new GlebanoideBagHandler(var2x, var3xx, new GlebanoideBagInventory(var4, var3xx, var3)),
                  Component.translatable(var6.screenTitleKey)
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
      if (var1.getPlayer() == null) {
         return InteractionResult.PASS;
      } else {
         open(var1.getLevel(), var1.getPlayer(), var1.getHand());
         return InteractionResult.SUCCESS;
      }
   }
}
