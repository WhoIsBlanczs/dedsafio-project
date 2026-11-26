package net.ruleta.cosmeticos;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class EfectoCosmetico extends MobEffect {
   public EfectoCosmetico(int color) {
      super(MobEffectCategory.BENEFICIAL, color);
   }

   public boolean shouldApplyEffectTickThisTick(int duracion, int amplificador) {
      return false;
   }
}
