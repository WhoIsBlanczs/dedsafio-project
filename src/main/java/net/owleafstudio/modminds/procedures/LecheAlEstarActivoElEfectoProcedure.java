package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class LecheAlEstarActivoElEfectoProcedure {
   public static void execute(Entity entity) {
      if (entity != null && entity instanceof LivingEntity _entity) {
         _entity.removeAllEffects();
      }
   }
}
