package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class EspacioPresionarHydraMostrarEnElJuegoProcedure {
   public static boolean execute(Entity entity) {
      return entity == null
         ? false
         : entity instanceof LivingEntity _livEnt0
            && _livEnt0.hasEffect(CompatAccess.effect(ModmindsModMobEffects.HYDRA_ESPACIOS.get()))
            && ModmindsModVariables.getVariables(entity).orElse(new ModmindsModVariables.PlayerVariables()).MostrarEspacioHydra;
   }
}
