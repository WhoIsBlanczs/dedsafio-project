package net.owleafstudio.modminds.procedures;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModEntities;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;

public class BombaNegraAlJugadorColisionarConLaEntidadProcedure {
   public static void execute(LevelAccessor world, Entity entity, Entity sourceentity) {
      if (entity != null
         && sourceentity != null
         && !(sourceentity instanceof Player _plr && _plr.getAbilities().instabuild)
         && !(
            sourceentity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(CompatAccess.effect(ModmindsModMobEffects.PROTECCION_CONTRA_LAS_BOMBAS.get()))
         )) {
         if (!entity.level().isClientSide()) {
            entity.discard();
         }

         if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(CompatAccess.effect(ModmindsModMobEffects.BOMBA_NEGRA_EFECTO.get()), 200, 0, false, false));
         }

         if (world instanceof ServerLevel _level) {
            Entity entityToSpawn = ModmindsModEntities.BOMBA_NEGRA_SIN_ALAS
               .get()
               .spawn(
                  _level,
                  BlockPos.containing(sourceentity.getX(), sourceentity.getY() + 1.81, sourceentity.getZ()),
                  EntitySpawnReason.MOB_SUMMONED
               );
            if (entityToSpawn != null) {
               entityToSpawn.setDeltaMovement(0.0, 0.0, 0.0);
            }
         }
      }
   }
}
