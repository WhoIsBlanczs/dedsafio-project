package net.owleafstudio.modminds.procedures;

import java.util.ArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;

public class RayoEfectosEspecialesProcedure {
   public static void execute(LevelAccessor world) {
      for (Entity entityiterator : new ArrayList<>(world.players())) {
         if (world instanceof ServerLevel _level) {
            LightningBolt entityToSpawn = (LightningBolt)net.minecraft.world.entity.EntityTypes.LIGHTNING_BOLT.create(_level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
            entityToSpawn.moveTo(
               Vec3.atBottomCenterOf(BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()))
            );
            entityToSpawn.setVisualOnly(true);
            _level.addFreshEntity(entityToSpawn);
         }
      }
   }
}
