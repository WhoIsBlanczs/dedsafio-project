package net.owleafstudio.modminds.procedures;

import java.util.Comparator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.ReactorHydraEntity;
import net.owleafstudio.modminds.init.ModmindsModEntities;

public class ReactorHydraBoneSpiderProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z) {
      Vec3 _center = new Vec3(x, y, z);

      for (Entity entityiterator : world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(150.0), e -> true)
         .stream()
         .sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center)))
         .toList()) {
         if (entityiterator instanceof ReactorHydraEntity) {
            for (int index0 = 0; index0 < 6; index0++) {
               CompatAccess.persistentData(entityiterator)
                  .putDouble("iReactorHydra", CompatAccess.persistentData(entityiterator).getDoubleOr("iReactorHydra", 0.0) + 1.0);
               CompatAccess.persistentData(entityiterator).putDouble("RadioReactorHydra", 7.0);
               CompatAccess.persistentData(entityiterator)
                  .putDouble("AngleReactorHydra", CompatAccess.persistentData(entityiterator).getDoubleOr("iReactorHydra", 0.0) * 2.0 * Math.PI / 6.0);
               CompatAccess.persistentData(entityiterator)
                  .putDouble(
                     "XReactorHydra",
                     entityiterator.getX()
                        + Math.cos(CompatAccess.persistentData(entityiterator).getDoubleOr("AngleReactorHydra", 0.0))
                           * CompatAccess.persistentData(entityiterator).getDoubleOr("RadioReactorHydra", 0.0)
                  );
               CompatAccess.persistentData(entityiterator).putDouble("YReactorHydra", entityiterator.getY());
               CompatAccess.persistentData(entityiterator)
                  .putDouble(
                     "ZReactorHydra",
                     entityiterator.getZ()
                        + Math.sin(CompatAccess.persistentData(entityiterator).getDoubleOr("AngleReactorHydra", 0.0))
                           * CompatAccess.persistentData(entityiterator).getDoubleOr("RadioReactorHydra", 0.0)
                  );
               if (world instanceof ServerLevel _level) {
                  Entity entityToSpawn = ModmindsModEntities.BONE_SPIDER
                     .get()
                     .spawn(
                        _level,
                        BlockPos.containing(
                           CompatAccess.persistentData(entityiterator).getDoubleOr("XReactorHydra", 0.0),
                           CompatAccess.persistentData(entityiterator).getDoubleOr("YReactorHydra", 0.0),
                           CompatAccess.persistentData(entityiterator).getDoubleOr("ZReactorHydra", 0.0)
                        ),
                        EntitySpawnReason.MOB_SUMMONED
                     );
                  if (entityToSpawn != null) {
                     entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
                  }
               }

               if (CompatAccess.persistentData(entityiterator).getDoubleOr("iReactorHydra", 0.0) >= 6.0) {
                  CompatAccess.persistentData(entityiterator).putDouble("iReactorHydra", 0.0);
               }
            }
         }
      }
   }
}
