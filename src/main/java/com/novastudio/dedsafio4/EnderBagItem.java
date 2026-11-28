package com.novastudio.dedsafio4;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;

public final class EnderBagItem extends Item {
   public EnderBagItem() {
      super(new Item.Properties().stacksTo(1));
   }

   static ChestMenu createScreen(int var0, Inventory var1, PlayerEnderChestContainer var2) {
      return ChestMenu.threeRows(var0, var1, var2);
   }

   private static void open(Level var0, Player var1) {
      if (!var0.isClientSide()) {
         var1.openMenu(
            new SimpleMenuProvider((var0x, var1x, var2) -> createScreen(var0x, var1x, var2.getEnderChestInventory()), Component.translatable("container.enderchest"))
         );
      }
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      open(var1, var2);
      return InteractionResult.SUCCESS;
   }

   public InteractionResult useOn(UseOnContext var1) {
      Player var2 = var1.getPlayer();
      if (var2 == null) {
         return InteractionResult.PASS;
      } else {
         open(var1.getLevel(), var2);
         return InteractionResult.SUCCESS;
      }
   }
}
