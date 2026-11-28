package com.novastudio.dedsafio4;

import java.util.ArrayList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.core.component.DataComponents;

public final class GlebanoideBagInventory extends SimpleContainer {
   static final int SIZE = 27;
   final ItemStack bag;
   final Inventory playerInventory;
   final int anchor;
   private boolean loaded;

   public GlebanoideBagInventory(ItemStack var1, Inventory var2, int var3) {
      super(27);
      this.bag = var1;
      this.playerInventory = var2;
      this.anchor = var3;
      NonNullList<ItemStack> var4 = NonNullList.withSize(256, ItemStack.EMPTY);
      ((ItemContainerContents)var1.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)).copyInto(var4);
      ArrayList<ItemStack> var5 = new ArrayList<>();

      for (int var6 = 0; var6 < var4.size(); var6++) {
         ItemStack var7 = (ItemStack)var4.get(var6);
         if (!var7.isEmpty()) {
            if (var6 < 27) {
               super.setItem(var6, var7);
            } else {
               var5.add(var7);
            }
         }
      }

      this.loaded = true;
      if (!var5.isEmpty()) {
         for (ItemStack var11 : var5) {
            int var8 = -1;

            for (int var9 = 0; var9 < 27 && var8 < 0; var9++) {
               if (this.getItem(var9).isEmpty()) {
                  var8 = var9;
               }
            }

            if (var8 >= 0) {
               super.setItem(var8, var11);
            } else if (var2 != null) {
               var2.placeItemBackInInventory(var11, net.minecraft.util.Prediction.SERVER_ONLY);
            }
         }

         this.save();
      }
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
