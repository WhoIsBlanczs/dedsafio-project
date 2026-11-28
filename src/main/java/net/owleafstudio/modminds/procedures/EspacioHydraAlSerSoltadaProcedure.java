package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class EspacioHydraAlSerSoltadaProcedure {
   public static void execute(Entity entity) {
      if (entity != null && entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CompatAccess.effect(ModmindsModMobEffects.HYDRA_ESPACIOS.get()))) {
         boolean _setval = false;
         ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
            capability.MostrarEspacioHydra = _setval;
            capability.syncPlayerVariables(entity);
         });
         double _setvalx = ModmindsModVariables.getVariables(entity).orElse(new ModmindsModVariables.PlayerVariables()).EspaciosHydra + 1.0;
         ModmindsModVariables.getVariables(entity).ifPresent(capability -> {
            capability.EspaciosHydra = _setvalx;
            capability.syncPlayerVariables(entity);
         });
      }
   }
}
