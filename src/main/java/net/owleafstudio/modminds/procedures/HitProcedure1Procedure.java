package net.owleafstudio.modminds.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;

public class HitProcedure1Procedure {
   public static boolean execute(Entity entity) {
      return entity == null
         ? false
         : (
               entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(CompatAccess.effect(ModmindsModMobEffects.HIT_EFECTO.get()))
                  ? _livEnt.getEffect(CompatAccess.effect(ModmindsModMobEffects.HIT_EFECTO.get())).getAmplifier()
                  : 0
            )
            == 1;
   }
}
