package net.owleafstudio.modminds.procedures;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.entity.BalaDeDilitioProyectilEntity;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;

public class BalaDeDilitioHitProcedure {
   public static void execute(Entity immediatesourceentity, Entity sourceentity) {
      if (immediatesourceentity != null
         && sourceentity != null
         && immediatesourceentity instanceof BalaDeDilitioProyectilEntity
         && sourceentity instanceof LivingEntity _entity
         && !_entity.level().isClientSide()) {
         _entity.addEffect(new MobEffectInstance(CompatAccess.effect(ModmindsModMobEffects.HIT_EFECTO.get()), 15, 1, true, false));
      }
   }
}
