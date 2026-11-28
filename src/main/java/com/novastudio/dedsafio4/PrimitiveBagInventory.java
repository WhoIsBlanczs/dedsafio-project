package com.novastudio.dedsafio4;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.core.component.DataComponents;

public final class PrimitiveBagInventory extends SimpleContainer {
   final ItemStack bag;
   final Inventory playerInventory;
   final int anchor;
   private boolean loaded;

   public PrimitiveBagInventory(ItemStack var1, Inventory var2, int var3) {
      super(27);
      this.bag = var1;
      this.playerInventory = var2;
      this.anchor = var3;
      NonNullList<ItemStack> var4 = NonNullList.withSize(27, ItemStack.EMPTY);
      ((ItemContainerContents)var1.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)).copyInto(var4);

      for (int var5 = 0; var5 < 27; var5++) {
         super.setItem(var5, (ItemStack)var4.get(var5));
      }

      this.loaded = true;
   }

   public void save() {
      if (this.loaded) {
         NonNullList<ItemStack> var1 = NonNullList.withSize(27, ItemStack.EMPTY);

         for (int var2 = 0; var2 < 27; var2++) {
            var1.set(var2, this.getItem(var2).copy());
         }

         this.bag.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(var1));
         if (this.playerInventory != null) {
            this.playerInventory.setChanged();
         }
      }
   }

   public void setChanged() {
      super.setChanged();
      this.save();
   }

   public boolean stillValid(Player var1) {
      return this.playerInventory == null || this.playerInventory.getItem(this.anchor) == this.bag;
   }
}
