package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class CreeperCakeEfectoAlFinalizarElEfectoProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         boolean _setval = false;
         ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
            capability.CreeperCakeLogica = _setval;
            capability.syncPlayerVariables(entity);
         });
      }
   }
}
