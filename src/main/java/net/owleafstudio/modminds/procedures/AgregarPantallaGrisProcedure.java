package net.owleafstudio.modminds.procedures;

import java.util.ArrayList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.owleafstudio.modminds.compat.CompatAccess;
import net.owleafstudio.modminds.init.ModmindsModMobEffects;

public class AgregarPantallaGrisProcedure {
   public static void execute(LevelAccessor world) {
      for (Entity entityiterator : new ArrayList(world.players())) {
         if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(CompatAccess.effect(ModmindsModMobEffects.PANTALLA_GRIS_EFECTO.get()), Integer.MAX_VALUE, 0, false, false));
         }
      }
   }
}
