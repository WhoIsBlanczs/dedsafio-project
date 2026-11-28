package com.novastudio.dedsafio4;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties;

public final class FruitFoods {
   private FruitFoods() {
   }

   public static FoodProperties food(String var0) {
      return new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).build();
   }

   /** Efectos al comer (antes iban dentro de FoodProperties). */
   public static net.minecraft.world.item.component.Consumable consumible(String var0) {
      net.minecraft.world.item.component.Consumable.Builder var1 = net.minecraft.world.item.component.Consumables.defaultFood();
      switch (var0) {
         case "huevo_de_eburia":
            var1.onConsume(new net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200, 0), 1.0F));
            break;
         case "arandano_nocturino":
            var1.onConsume(new net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 2400, 0), 1.0F));
            break;
         case "baya_uvina":
            var1.onConsume(new net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1), 1.0F));
            var1.onConsume(new net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 4800, 0), 1.0F));
      }

      return var1.build();
   }
}
