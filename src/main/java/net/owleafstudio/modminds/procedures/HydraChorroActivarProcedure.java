package net.owleafstudio.modminds.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class HydraChorroActivarProcedure {
   public static void execute(LevelAccessor world) {
      ModmindsModVariables.MapVariables.get(world).HydraAtaques = 8.0;
      ModmindsModVariables.MapVariables.get(world).syncData(world);
   }
}
