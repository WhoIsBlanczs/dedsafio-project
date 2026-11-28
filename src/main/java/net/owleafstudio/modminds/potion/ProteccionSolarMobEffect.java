package net.owleafstudio.modminds.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.owleafstudio.modminds.procedures.ProteccionSolarAlEstarActivoElEfectoProcedure;

public class ProteccionSolarMobEffect extends MobEffect {
   public ProteccionSolarMobEffect() {
      super(MobEffectCategory.NEUTRAL, -39424);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      ProteccionSolarAlEstarActivoElEfectoProcedure.execute(entity);
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
