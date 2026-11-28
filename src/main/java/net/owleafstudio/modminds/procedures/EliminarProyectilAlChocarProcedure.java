package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;

public class EliminarProyectilAlChocarProcedure {
   public static void execute(Entity immediatesourceentity) {
      if (immediatesourceentity != null && !immediatesourceentity.level().isClientSide()) {
         immediatesourceentity.discard();
      }
   }
}
