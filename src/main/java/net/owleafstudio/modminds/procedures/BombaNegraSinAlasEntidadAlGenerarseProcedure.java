package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level;
import net.owleafstudio.modminds.ModmindsMod;

public class BombaNegraSinAlasEntidadAlGenerarseProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         ModmindsMod.queueServerWork(80, () -> {
            if (world instanceof Level _level && !_level.isClientSide()) {
               _level.explode(null, entity.getX(), entity.getY() + 3.0, entity.getZ(), 4.0F, Level.ExplosionInteraction.MOB);
            }

            if (!entity.level().isClientSide()) {
               entity.discard();
            }
         });
      }
   }
}
