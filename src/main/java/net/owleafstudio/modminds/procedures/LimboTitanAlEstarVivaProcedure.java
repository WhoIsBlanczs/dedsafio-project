package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class LimboTitanAlEstarVivaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         if (ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques == 1.0) {
            TerraPunchProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques == 2.0) {
            UltimateJumpProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques == 3.0) {
            RollProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques == 4.0) {
            PunchProcedure.execute(world, x, y, z, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques == 5.0) {
            TitanQuiebraProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques == 6.0) {
            TitanCorralProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques == 7.0) {
            TitanMuroProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques == 8.0) {
            TitanSueloProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques == 9.0) {
            TitanBolaFuegoProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques == 10.0) {
            TitanSierraProcedure.execute(world, entity);
         } else if (ModmindsModVariables.MapVariables.get(world).TitanLimboAtaques == 11.0) {
            TitanSaltoTripleProcedure.execute(world, entity);
         }
      }
   }
}
