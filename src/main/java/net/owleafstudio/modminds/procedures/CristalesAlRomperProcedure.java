package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.LevelAccessor;
import net.owleafstudio.modminds.compat.CompatAccess;

public class CristalesAlRomperProcedure {
   public static boolean execute(LevelAccessor world, Entity entity) {
      return entity == null
         ? false
         : EnchantmentHelper.getItemEnchantmentLevel(
               CompatAccess.enchantment(Enchantments.SILK_TOUCH), entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY
            )
            != 0;
   }
}
