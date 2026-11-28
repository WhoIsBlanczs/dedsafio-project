package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.owleafstudio.modminds.init.ModmindsModItems;

public class PuertaBorgAzulClickDerechoProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
      if (entity != null
         && sourceentity != null
         && (
            (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getItem()
                  == ModmindsModItems.PUERTA_BORG_CHIP_AZUL.get()
               || (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem()
                  == ModmindsModItems.PUERTA_BORG_CHIP_AZUL.get()
         )) {
         if ((sourceentity instanceof LivingEntity _livEntxxx ? _livEntxxx.getMainHandItem() : ItemStack.EMPTY).getItem()
               != ModmindsModItems.PUERTA_BORG_CHIP_AZUL.get()
            && (sourceentity instanceof LivingEntity _livEntxx ? _livEntxx.getOffhandItem() : ItemStack.EMPTY).getItem()
               != ModmindsModItems.PUERTA_BORG_CHIP_AZUL.get()) {
            (sourceentity instanceof LivingEntity _livEntxxxx ? _livEntxxxx.getOffhandItem() : ItemStack.EMPTY).shrink(1);
         } else {
            (sourceentity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
         }

         PuertaDespawnearProcedure.execute(world, x, y, z, entity);
      }
   }
}
