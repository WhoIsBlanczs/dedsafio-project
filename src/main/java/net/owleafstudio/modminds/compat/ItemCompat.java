package net.owleafstudio.modminds.compat;

import net.minecraft.world.item.ItemStack;

public final class ItemCompat {
   private ItemCompat() {
   }

   public static boolean hurt(ItemStack stack, int amount) {
      if (!stack.isEmpty() && amount > 0 && stack.isDamageableItem()) {
         int max = stack.getMaxDamage();
         if (max <= 0) {
            return false;
         } else {
            int damage = stack.getDamageValue() + amount;
            if (damage >= max) {
               stack.setDamageValue(0);
               return true;
            } else {
               stack.setDamageValue(damage);
               return false;
            }
         }
      } else {
         return false;
      }
   }
}
