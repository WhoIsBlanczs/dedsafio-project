package net.ruleta.estatua;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;

public class MenuEstatuaNutria extends MerchantMenu {
   public MenuEstatuaNutria(int id, Inventory inventario, Merchant comerciante) {
      super(id, inventario, comerciante);
   }

   public ItemStack quickMoveStack(Player jugador, int indice) {
      ItemStack copia = ItemStack.EMPTY;
      Slot ranura = (Slot)this.slots.get(indice);
      if (ranura != null && ranura.hasItem()) {
         ItemStack pila = ranura.getItem();
         copia = pila.copy();
         if (indice == 2) {
            if (!this.moveItemStackTo(pila, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            ranura.onQuickCraft(pila, copia);
         } else if (indice != 0 && indice != 1) {
            if (indice >= 3 && indice < 30) {
               if (!this.moveItemStackTo(pila, 30, 39, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (indice >= 30 && indice < 39 && !this.moveItemStackTo(pila, 3, 30, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(pila, 3, 39, false)) {
            return ItemStack.EMPTY;
         }

         if (pila.isEmpty()) {
            ranura.set(ItemStack.EMPTY);
         } else {
            ranura.setChanged();
         }

         if (pila.getCount() == copia.getCount()) {
            return ItemStack.EMPTY;
         }

         ranura.onTake(jugador, pila);
      }

      return copia;
   }
}
