package net.owleafstudio.modminds.compat;

import java.util.function.Predicate;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/** Reemplazo de Inventory#clearOrCountMatchingItems(pred, max, craftSlots) de 1.21.1. */
public final class InvCompat {
   private InvCompat() {
   }

   public static int remove(Inventory inv, Predicate<ItemStack> pred, int max) {
      int removed = 0;
      for (int i = 0; i < inv.getContainerSize(); i++) {
         ItemStack s = inv.getItem(i);
         if (!s.isEmpty() && pred.test(s)) {
            if (max == 0) {
               removed += s.getCount();
               continue;
            }
            int take = max < 0 ? s.getCount() : Math.min(max - removed, s.getCount());
            s.shrink(take);
            removed += take;
            if (max > 0 && removed >= max) break;
         }
      }
      inv.setChanged();
      return removed;
   }

   public static int rgb(float r, float g, float b) {
      return 0xFF000000 | (Math.round(Math.max(0, Math.min(1, r)) * 255) << 16) | (Math.round(Math.max(0, Math.min(1, g)) * 255) << 8) | Math.round(Math.max(0, Math.min(1, b)) * 255);
   }

   public static int rgb(double r, double g, double b) {
      return rgb((float)r, (float)g, (float)b);
   }

   public static java.util.List<ItemStack> armor(net.minecraft.world.entity.LivingEntity e) {
      java.util.List<ItemStack> l = new java.util.ArrayList<>();
      for (net.minecraft.world.entity.EquipmentSlot slot : new net.minecraft.world.entity.EquipmentSlot[]{
         net.minecraft.world.entity.EquipmentSlot.FEET, net.minecraft.world.entity.EquipmentSlot.LEGS,
         net.minecraft.world.entity.EquipmentSlot.CHEST, net.minecraft.world.entity.EquipmentSlot.HEAD}) {
         l.add(e.getItemBySlot(slot));
      }
      return l;
   }

   public static int rgb(org.joml.Vector3f v) {
      return rgb(v.x(), v.y(), v.z());
   }

   public static int rgb(int argb) {
      return argb;
   }

   public static final net.minecraft.core.particles.ColorParticleOption FLASH =
      net.minecraft.core.particles.ColorParticleOption.create(net.minecraft.core.particles.ParticleTypes.FLASH, -1);
}
