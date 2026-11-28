package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.owleafstudio.modminds.init.ModmindsModItems;

public class CasinoAlHacerleClickDerechoProcedure {
   public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
      if (entity != null
         && sourceentity != null
         && Math.sqrt(
               Math.pow(sourceentity.getX() - entity.getX(), 2.0)
                  + Math.pow(sourceentity.getY() - entity.getY(), 2.0)
                  + Math.pow(sourceentity.getZ() - entity.getZ(), 2.0)
            )
            <= 3.0
         && (
            (sourceentity instanceof LivingEntity _livEntxxx ? _livEntxxx.getMainHandItem() : ItemStack.EMPTY).getItem()
                  == ModmindsModItems.DEDITA_NARANJA.get()
               || (sourceentity instanceof LivingEntity _livEntxx ? _livEntxx.getOffhandItem() : ItemStack.EMPTY).getItem()
                  == ModmindsModItems.DEDITA_NARANJA.get()
               || (sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getItem()
                  == ModmindsModItems.DEDITA_NARANJA.get()
               || (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem()
                  == ModmindsModItems.DEDITA_NARANJA.get()
         )) {
         if ((sourceentity instanceof LivingEntity _livEntxxxxx ? _livEntxxxxx.getMainHandItem() : ItemStack.EMPTY).getItem()
               != ModmindsModItems.DEDITA_NARANJA.get()
            && (sourceentity instanceof LivingEntity _livEntxxxx ? _livEntxxxx.getMainHandItem() : ItemStack.EMPTY).getItem()
               != ModmindsModItems.DEDITA_NARANJA.get()) {
            (sourceentity instanceof LivingEntity _livEntxxxxxx ? _livEntxxxxxx.getOffhandItem() : ItemStack.EMPTY).shrink(1);
         } else {
            (sourceentity instanceof LivingEntity _livEntxxxx ? _livEntxxxx.getMainHandItem() : ItemStack.EMPTY).shrink(1);
         }

         if (Math.random() <= 0.98) {
            NumeroPerdedorProcedure.execute(entity);
         } else {
            NumeroGanadorProcedure.execute(world, entity, sourceentity);
         }
      }
   }
}
