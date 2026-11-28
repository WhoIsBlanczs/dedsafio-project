package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class Espacio2OverlaySapoMostrarEnElJuegoProcedure {
   public static boolean execute(Entity entity) {
      return entity == null
         ? false
         : !ModmindsModVariables.getVariables(entity).orElse(new ModmindsModVariables.PlayerVariables()).SapoEspacioLogic
            && CompatAccess.persistentData(entity).getBooleanOr("MostrarBarraEspaciadora", false)
            && entity instanceof LivingEntity livingEntity
            && livingEntity.hasEffect(CompatAccess.effect(ModmindsModMobEffects.ENGULLIDO_SAPO.get()));
   }
}
