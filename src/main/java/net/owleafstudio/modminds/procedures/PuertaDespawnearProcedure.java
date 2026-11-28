package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level;
import net.owleafstudio.modminds.ModmindsMod;
import net.owleafstudio.modminds.compat.CompatAccess;

public class PuertaDespawnearProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         CompatAccess.persistentData(entity).putDouble("ChipPuesto?", 1.0);
         ModmindsMod.queueServerWork(60, () -> {
            if (world instanceof Level _level && !_level.isClientSide()) {
               _level.explode(null, x, y + 2.5, z, 0.0F, Level.ExplosionInteraction.NONE);
            }

            if (!entity.level().isClientSide()) {
               entity.discard();
            }
         });
      }
   }
}
