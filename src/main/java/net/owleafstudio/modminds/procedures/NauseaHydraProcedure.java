package net.owleafstudio.modminds.procedures;

import java.util.ArrayList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;

public class NauseaHydraProcedure {
   public static void execute(LevelAccessor world) {
      for (Entity entityiterator : new ArrayList(world.players())) {
         if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
            _entity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, Integer.MAX_VALUE, 4, false, false));
         }
      }
   }
}
