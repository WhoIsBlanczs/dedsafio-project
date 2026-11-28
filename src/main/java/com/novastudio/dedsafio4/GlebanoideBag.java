package com.novastudio.dedsafio4;

import net.minecraft.world.InteractionResult;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;

public final class GlebanoideBag extends Item {
   public GlebanoideBag() {
      super(new Item.Properties().stacksTo(1));
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      if (!var1.isClientSide()) {
         int var5 = var3 == InteractionHand.MAIN_HAND ? var2.getInventory().getSelectedSlot() : -1;
         var2.openMenu(new SimpleMenuProvider((var3x, var4xx, var5xx) -> {
            SimpleContainer var6 = new SimpleContainer(36) {
               public void stopOpen(net.minecraft.world.entity.ContainerUser var1$u) {
      Player var1 = var1$u.getLivingEntity() instanceof Player var1$p ? var1$p : null;
                  var4.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));
               }
            };
            ((ItemContainerContents)var4.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)).copyInto(var6.getItems());
            return new ChestMenu(MenuType.GENERIC_9x4, var3x, var4xx, var6, 4) {
               public void clicked(int var1, int var2x, ContainerInput var3xx, Player var4xxx) {
                  if (var5 < 0 || var1 != 63 + var5 && (var3xx != ContainerInput.SWAP || var2x != var5)) {
                     super.clicked(var1, var2x, var3xx, var4xxx);
                  }
               }
            };
         }, var4.getHoverName()));
      }

      return InteractionResult.SUCCESS;
   }
}
