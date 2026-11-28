package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;

public class PantallaGrisAlternaMostrarEnElJuegoProcedure {
   public static boolean execute(Entity entity) {
      return entity == null
         ? false
         : entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CompatAccess.effect(ModmindsModMobEffects.PANTALLA_GRIS_EFECTO.get()));
   }
}
