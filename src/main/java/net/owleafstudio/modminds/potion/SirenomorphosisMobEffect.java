package net.owleafstudio.modminds.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SirenomorphosisMobEffect extends MobEffect {
   public SirenomorphosisMobEffect() {
      super(MobEffectCategory.NEUTRAL, -16777072);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
