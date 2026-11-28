package net.owleafstudio.modminds.procedures;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ZombificacionSiempreEstarActivaProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null
         && ModmindsModVariables.MapVariables.get(world).PersistenciaZombificacion
         && !(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CompatAccess.effect(ModmindsModMobEffects.PILDORA_ZOMBIFICACION.get())))
         && entity instanceof LivingEntity _entity
         && !_entity.level().isClientSide()) {
         _entity.addEffect(new MobEffectInstance(CompatAccess.effect(ModmindsModMobEffects.ZOMBIFICACION.get()), 2400, 0, false, false));
      }
   }
}
