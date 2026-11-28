package com.novastudio.dedsafio4;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class PrimitiveBagScreenHandler extends ShulkerBoxMenu {
   private final PrimitiveBagInventory bagInventory;

   public PrimitiveBagScreenHandler(int var1, Inventory var2, PrimitiveBagInventory var3) {
      super(var1, var2, var3);
      this.bagInventory = var3;

      for (int var4 = 0; var4 < this.slots.size(); var4++) {
         Slot var5 = (Slot)this.slots.get(var4);
         if (var5.container == var3) {
            Slot var6 = new Slot(var3, var5.getContainerSlot(), var5.x, var5.y) {
               public boolean mayPlace(ItemStack var1) {
                  return var1.getItem().canFitInsideContainerItems();
               }
            };
            var6.index = var4;
            this.slots.set(var4, var6);
         } else if (var5.container == var2 && var5.getContainerSlot() == var3.anchor) {
            Slot var7 = new Slot(var2, var3.anchor, var5.x, var5.y) {
               public boolean mayPlace(ItemStack var1) {
                  return false;
               }

               public boolean mayPickup(Player var1) {
                  return false;
               }
            };
            var7.index = var4;
            this.slots.set(var4, var7);
         }
      }
   }

   public boolean stillValid(Player var1) {
      return this.bagInventory.stillValid(var1);
   }

   public void clicked(int var1, int var2, ContainerInput var3, Player var4) {
      if (this.stillValid(var4) && (var3 != ContainerInput.SWAP || var2 != this.bagInventory.anchor)) {
         if (var1 >= 0 && var1 < this.slots.size()) {
            Slot var5 = (Slot)this.slots.get(var1);
            if (var5.container == this.bagInventory.playerInventory && var5.getContainerSlot() == this.bagInventory.anchor) {
               return;
            }
         }

         super.clicked(var1, var2, var3, var4);
         this.bagInventory.save();
      }
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      if (var2 >= 0 && var2 < this.slots.size()) {
         Slot var3 = (Slot)this.slots.get(var2);
         if (var3.container == this.bagInventory.playerInventory && var3.getContainerSlot() == this.bagInventory.anchor) {
            return ItemStack.EMPTY;
         } else if (var2 >= 27 && !var3.getItem().getItem().canFitInsideContainerItems()) {
            return ItemStack.EMPTY;
         } else {
            ItemStack var4 = super.quickMoveStack(var1, var2);
            this.bagInventory.save();
            return var4;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void removed(Player var1) {
      this.bagInventory.save();
      super.removed(var1);
   }
}
