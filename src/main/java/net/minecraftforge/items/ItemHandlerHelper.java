package net.minecraftforge.items;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class ItemHandlerHelper {
   public static void giveItemToPlayer(Player player, ItemStack stack) {
      if (!stack.isEmpty()) {
         if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
         }
      }
   }

   private ItemHandlerHelper() {
   }
}
