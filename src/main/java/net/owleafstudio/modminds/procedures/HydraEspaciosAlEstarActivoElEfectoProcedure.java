package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class HydraEspaciosAlEstarActivoElEfectoProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         entity.setDeltaMovement(new Vec3(0.0, -0.35, 0.0));
         if (entity instanceof LivingEntity _livingEntity1 && _livingEntity1.getAttributes().hasAttribute(Attributes.MOVEMENT_SPEED)) {
            _livingEntity1.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
         }
      }
   }
}
