package net.owleafstudio.modminds.procedures;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageTypes;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;
import net.owleafstudio.modminds.network.ModmindsModVariables;

public class ZombificacionAlFinalizarElEfectoProcedure {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null
         && ModmindsModVariables.MapVariables.get(world).PersistenciaZombificacion
         && !(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(CompatAccess.effect(ModmindsModMobEffects.PILDORA_ZOMBIFICACION.get())))) {
         entity.hurt(new DamageSource(world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.GENERIC)), 99.0F);
      }
   }
}
