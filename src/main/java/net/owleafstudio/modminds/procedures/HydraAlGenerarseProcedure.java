package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.owleafstudio.modminds.entity.HydraEntity;

public class HydraAlGenerarseProcedure {
   public static void execute(Entity entity) {
      if (entity != null && entity instanceof HydraEntity) {
         ((HydraEntity)entity).setAnimation("emerge");
      }
   }
}
