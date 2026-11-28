package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;

public class PildoraZombificacionAlEstarActivoElEfectoProcedure {
   public static void execute(Entity entity) {
      if (entity != null && entity instanceof LivingEntity _entity) {
         _entity.removeEffect(CompatAccess.effect(ModmindsModMobEffects.ZOMBIFICACION.get()));
      }
   }
}
