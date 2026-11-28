package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;

public class EfectoQuemaduraAlEstarActivoElEfectoProcedure {
   public static void execute(Entity entity) {
      if (entity != null) {
         entity.setRemainingFireTicks(20);
      }
   }
}
