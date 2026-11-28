package com.novastudio.dedsafio4;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;

public final class HeartsMod implements ModInitializer {
   private static void registerFood(String id, HeartItem.Kind kind) {
      FoodProperties food = new FoodProperties.Builder().nutrition(4).saturationModifier(0.3F).alwaysEdible().build();
      Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(Identifier.fromNamespaceAndPath("nexo", id)), new HeartItem(new Item.Properties().food(food), kind));
   }

   public void onInitialize() {
      registerFood("corazon_dorado", HeartItem.Kind.DORADO);
      registerFood("corazon_limbo", HeartItem.Kind.LIMBO);
      registerFood("corazon_rojo", HeartItem.Kind.ROJO);
      HeartItem.registerEvents();
   }
}
