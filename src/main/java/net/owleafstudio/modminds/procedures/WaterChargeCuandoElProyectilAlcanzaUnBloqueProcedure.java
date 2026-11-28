package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.owleafstudio.modminds.init.ModmindsModEntities;

public class WaterChargeCuandoElProyectilAlcanzaUnBloqueProcedure {
   private static final int MAX_POOLS = 3;
   private static final double POOL_SPACING = 32.0;

   public static void execute(LevelAccessor world, double x, double y, double z) {
      if (world instanceof ServerLevel _level) {
         Vec3 at = new Vec3(x, y, z);
         if (_level.getEntitiesOfClass(AreaEffectCloud.class, new AABB(at, at).inflate(32.0), e -> !e.isRemoved() && e.entityTags().contains("harming_potion"))
               .size()
            >= 3) {
            return;
         }

         Entity entityToSpawn = ModmindsModEntities.WATER_CHARGE_ENTIDAD
            .get()
            .spawn(_level, BlockPos.containing(x, y + 1.0, z), EntitySpawnReason.MOB_SUMMONED);
         if (entityToSpawn != null) {
            entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
         }
      }
   }
}
