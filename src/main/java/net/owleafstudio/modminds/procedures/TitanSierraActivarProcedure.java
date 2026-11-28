package net.owleafstudio.modminds.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class TitanSierraActivarProcedure {
   public static void execute(LevelAccessor world) {
      ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques = 10.0;
      ModmindsModVariables.MapVariables.get(world).syncData(world);
   }
}
