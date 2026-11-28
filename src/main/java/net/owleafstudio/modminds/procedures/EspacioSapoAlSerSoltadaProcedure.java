package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class EspacioSapoAlSerSoltadaProcedure {
   public static void execute(Entity entity) {
      if (entity != null
         && CompatAccess.persistentData(entity).getBooleanOr("MostrarBarraEspaciadora", false)
         && entity instanceof LivingEntity _livEnt
         && _livEnt.hasEffect(CompatAccess.effect(ModmindsModMobEffects.ENGULLIDO_SAPO.get()))) {
         ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
            capability.SapoEspacioLogic = true;
            capability.SapoContadorDeEspacios++;
            capability.syncPlayerVariables(entity);
         });
      }
   }
}
