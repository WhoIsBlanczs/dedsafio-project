package net.owleafstudio.modminds.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ReviilSalActivarProcedure {
   public static void execute(LevelAccessor world) {
      ModmindsModVariables.MapVariables.get(world).ReviilAtaques = 21.0;
      ModmindsModVariables.MapVariables.get(world).syncData(world);
   }
}
