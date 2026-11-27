package net.ruleta.compat;

import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Pequeños ayudantes para APIs que cambiaron en 26.3. */
public final class Util {
   private Util() {
   }

   public static void dar(Player p, ItemStack pila) {
      if (!p.getInventory().add(pila) && p.level() instanceof ServerLevel sl) {
         p.spawnAtLocation(sl, pila);
      }
   }

   public static List<ItemStack> armadura(LivingEntity e) {
      return List.of(
         e.getItemBySlot(EquipmentSlot.HEAD), e.getItemBySlot(EquipmentSlot.CHEST), e.getItemBySlot(EquipmentSlot.LEGS), e.getItemBySlot(EquipmentSlot.FEET)
      );
   }
}
