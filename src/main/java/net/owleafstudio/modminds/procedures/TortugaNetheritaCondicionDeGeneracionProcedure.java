package net.owleafstudio.modminds.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class TortugaNetheritaCondicionDeGeneracionProcedure {
   public static boolean execute(LevelAccessor world) {
      return !ModmindsModVariables.MapVariables.get(world).ComandoTortugaNetheritaSpawn;
   }
}
