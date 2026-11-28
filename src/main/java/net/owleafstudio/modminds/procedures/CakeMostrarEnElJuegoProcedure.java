package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class CakeMostrarEnElJuegoProcedure {
   public static boolean execute(Entity entity) {
      return entity == null ? false : ModmindsModVariables.getVariables(entity).orElse(new ModmindsModVariables.PlayerVariables()).CreeperCakeLogica;
   }
}
