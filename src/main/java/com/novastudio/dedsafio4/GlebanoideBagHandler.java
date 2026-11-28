package com.novastudio.dedsafio4;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.MenuType;

public final class GlebanoideBagHandler extends ChestMenu {
   private final GlebanoideBagInventory bagInventory;

   public GlebanoideBagHandler(int var1, Inventory var2, GlebanoideBagInventory var3) {
      super(MenuType.GENERIC_9x3, var1, var2, var3, 3);
      this.bagInventory = var3;

      for (int var4 = 0; var4 < this.slots.size(); var4++) {
         Slot var5 = (Slot)this.slots.get(var4);
         Slot var6 = null;
         if (var5.container == var3) {
            var6 = new Slot(var3, var5.getContainerSlot(), var5.x, var5.y) {
               public boolean mayPlace(ItemStack var1) {
                  return var1.getItem().canFitInsideContainerItems();
               }
            };
         } else if (var5.container == var2 && var5.getContainerSlot() == var3.anchor) {
            var6 = new Slot(var2, var3.anchor, var5.x, var5.y) {
               public boolean mayPlace(ItemStack var1) {
                  return false;
               }

               public boolean mayPickup(Player var1) {
                  return false;
               }
            };
         }

         if (var6 != null) {
            var6.index = var4;
            this.slots.set(var4, var6);
         }
      }
   }

   public boolean stillValid(Player var1) {
      return this.bagInventory.stillValid(var1);
   }

   private boolean locked(int var1) {
      return var1 >= 0
         && var1 < this.slots.size()
         && ((Slot)this.slots.get(var1)).container == this.bagInventory.playerInventory
         && ((Slot)this.slots.get(var1)).getContainerSlot() == this.bagInventory.anchor;
   }

   public void clicked(int var1, int var2, ContainerInput var3, Player var4) {
      if (this.stillValid(var4) && !this.locked(var1) && (var3 != ContainerInput.SWAP || var2 != this.bagInventory.anchor)) {
         super.clicked(var1, var2, var3, var4);
         this.bagInventory.save();
      }
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      if (var2 < 0
         || var2 >= this.slots.size()
         || this.locked(var2)
         || var2 >= 27 && !((Slot)this.slots.get(var2)).getItem().getItem().canFitInsideContainerItems()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack var3 = super.quickMoveStack(var1, var2);
         this.bagInventory.save();
         return var3;
      }
   }

   public void removed(Player var1) {
      this.bagInventory.save();
      super.removed(var1);
   }
}
