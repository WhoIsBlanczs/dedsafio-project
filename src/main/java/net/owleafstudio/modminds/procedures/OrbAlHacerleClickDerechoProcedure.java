package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.owleafstudio.modminds.entity.OrbEntity;
import net.owleafstudio.modminds.init.ModmindsModItems;

public class OrbAlHacerleClickDerechoProcedure {
   public static void execute(Entity entity, Entity sourceentity) {
      if (entity != null && sourceentity != null) {
         if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == ModmindsModItems.ORBE_AMARILLO.get()
            )
          {
            if (entity instanceof OrbEntity animatable) {
               animatable.setTexture("orbeamarillo");
            }
         } else if ((sourceentity instanceof LivingEntity _livEntx ? _livEntx.getMainHandItem() : ItemStack.EMPTY).getItem()
            == ModmindsModItems.ORBE_AZUL.get()) {
            if (entity instanceof OrbEntity animatable) {
               animatable.setTexture("orbeazul");
            }
         } else if ((sourceentity instanceof LivingEntity _livEntxx ? _livEntxx.getMainHandItem() : ItemStack.EMPTY).getItem()
            == ModmindsModItems.ORBE_CYAN.get()) {
            if (entity instanceof OrbEntity animatable) {
               animatable.setTexture("orbecyan");
            }
         } else if ((sourceentity instanceof LivingEntity _livEntxxx ? _livEntxxx.getMainHandItem() : ItemStack.EMPTY).getItem()
            == ModmindsModItems.ORBE_MORADO.get()) {
            if (entity instanceof OrbEntity animatable) {
               animatable.setTexture("orbemorado");
            }
         } else if ((sourceentity instanceof LivingEntity _livEntxxxx ? _livEntxxxx.getMainHandItem() : ItemStack.EMPTY).getItem()
            == ModmindsModItems.ORBE_NARANJA.get()) {
            if (entity instanceof OrbEntity animatable) {
               animatable.setTexture("orbenaranja");
            }
         } else if ((sourceentity instanceof LivingEntity _livEntxxxxx ? _livEntxxxxx.getMainHandItem() : ItemStack.EMPTY).getItem()
            == ModmindsModItems.ORBE_ROJO.get()) {
            if (entity instanceof OrbEntity animatable) {
               animatable.setTexture("orberojo");
            }
         } else if ((sourceentity instanceof LivingEntity _livEntxxxxxx ? _livEntxxxxxx.getMainHandItem() : ItemStack.EMPTY).getItem()
            == ModmindsModItems.ORBE_ROSA.get()) {
            if (entity instanceof OrbEntity animatable) {
               animatable.setTexture("orberosa");
            }
         } else if ((sourceentity instanceof LivingEntity _livEntxxxxxxx ? _livEntxxxxxxx.getMainHandItem() : ItemStack.EMPTY).getItem()
               == ModmindsModItems.ORBE_VERDE.get()
            && entity instanceof OrbEntity animatable) {
            animatable.setTexture("orbeverde");
         }
      }
   }
}
