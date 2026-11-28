package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.owleafstudio.modminds.entity.GloboEntity;
import net.owleafstudio.modminds.init.ModmindsModItems;

public class GloboAlHacerleClickDerechoProcedure {
   public static void execute(Entity entity, Entity sourceentity) {
      if (entity != null
         && sourceentity != null
         && (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem()
            == ModmindsModItems.CAMBIAR_COLOR_GLOBO.get()) {
         if ((entity instanceof GloboEntity animatable ? animatable.getTexture() : "null").equals("globo1")) {
            if (entity instanceof GloboEntity animatablex) {
               animatablex.setTexture("globo2");
            }
         } else if ((entity instanceof GloboEntity animatablex ? animatablex.getTexture() : "null").equals("globo2")) {
            if (entity instanceof GloboEntity animatablexx) {
               animatablexx.setTexture("globo3");
            }
         } else if ((entity instanceof GloboEntity animatablexx ? animatablexx.getTexture() : "null").equals("globo3")) {
            if (entity instanceof GloboEntity animatablexxx) {
               animatablexxx.setTexture("globo4");
            }
         } else if ((entity instanceof GloboEntity animatablexxxx ? animatablexxxx.getTexture() : "null").equals("globo4")
            && entity instanceof GloboEntity animatablexxx) {
            animatablexxx.setTexture("globo1");
         }
      }
   }
}
