package net.owleafstudio.modminds.procedures;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageTypes;

public class LimboTitanAlJugadorColisionarConLaEntidadProcedure {
   public static void execute(LevelAccessor world, Entity sourceentity) {
      if (sourceentity != null) {
         sourceentity.hurt(new DamageSource(world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.MOB_ATTACK)), 100.0F);
      }
   }
}
