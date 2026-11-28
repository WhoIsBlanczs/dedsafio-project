package net.owleafstudio.modminds.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilCoronaActivarProcedure {
   public static void execute(LevelAccessor world) {
      ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 10.0;
      ModmindsModVariables.MapVariables.get(world).syncData(world);
   }
}
