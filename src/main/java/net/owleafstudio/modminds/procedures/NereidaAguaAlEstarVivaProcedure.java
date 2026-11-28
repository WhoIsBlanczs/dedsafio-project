package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.owleafstudio.modminds.init.ModmindsModEntities;

public class NereidaAguaAlEstarVivaProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         double Speed = 0.0;
         double PitchRadians = 0.0;
         double YawRadians = 0.0;
         double Wpressd = 0.0;
         if (!entity.isInWater()) {
            if (!entity.level().isClientSide()) {
               entity.discard();
            }

            if (world instanceof ServerLevel _level) {
               Entity entityToSpawn = ModmindsModEntities.NEREIDA.get().spawn(_level, BlockPos.containing(x, y, z), EntitySpawnReason.MOB_SUMMONED);
               if (entityToSpawn != null) {
                  entityToSpawn.setYRot(world.getRandom().nextFloat() * 360.0F);
               }
            }
         } else if ((entity instanceof Mob _mobEnt ? _mobEnt.getTarget() : null) != null) {
            YawRadians = Math.toRadians(entity.getYRot());
            PitchRadians = Math.toRadians(entity.getXRot());
            Speed = 0.3;
            Wpressd = 1.0;
            entity.setDeltaMovement(
               new Vec3(
                  Math.sin(YawRadians) * -1.0 * Math.cos(PitchRadians) * Speed * Wpressd,
                  Math.sin(PitchRadians) * -1.0 * Speed * Wpressd,
                  Math.cos(YawRadians) * Math.cos(PitchRadians) * Speed * Wpressd
               )
            );
         }
      }
   }
}
