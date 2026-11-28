package net.owleafstudio.modminds.procedures;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;

public class PildoraDelExperimento110ElJugadorTerminaUsandoElItemProcedure {
   public static void execute(Entity entity) {
      if (entity != null && entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
         _entity.addEffect(new MobEffectInstance(CompatAccess.effect(ModmindsModMobEffects.PILDORA_ZOMBIFICACION.get()), 36000, 0, false, true));
      }
   }
}
