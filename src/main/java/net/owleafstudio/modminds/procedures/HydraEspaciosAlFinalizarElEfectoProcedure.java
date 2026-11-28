package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class HydraEspaciosAlFinalizarElEfectoProcedure {
   public static void execute(Entity entity) {
      if (entity != null && entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
         _livingEntity0.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1);
      }
   }
}
