package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class FlashBang0AlEstarActivoElEfectoProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         boolean _setval = true;
         ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
            capability.FlashBang = _setval;
            capability.syncPlayerVariables(entity);
         });
      }
   }
}
