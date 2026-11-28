package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class EngullidoSapoCuandoElEfectoEmpiezaAAplicarseProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
            capability.SapoContadorDeEspacios = 0.0;
            capability.syncPlayerVariables(entity);
         });
         CompatAccess.persistentData(entity).putDouble("MontarJugadorSapo", 0.0);
      }
   }
}
