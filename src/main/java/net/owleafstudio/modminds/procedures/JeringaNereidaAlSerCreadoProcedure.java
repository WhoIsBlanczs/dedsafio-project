package net.owleafstudio.modminds.procedures;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.owleafstudio.modminds.compat.ItemCompat;

public class JeringaNereidaAlSerCreadoProcedure {
   public static void execute(LevelAccessor world, ItemStack itemstack) {
      if (ItemCompat.hurt(itemstack, 100)) {
         itemstack.shrink(1);
         itemstack.setDamageValue(0);
      }
   }
}
