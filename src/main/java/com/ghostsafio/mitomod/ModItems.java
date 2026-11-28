package com.ghostsafio.mitomod;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class ModItems {
   public static final Item TOTEM_MONTANA = register("totem_montana", () -> new Item(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
   public static final Item CORAZON_ROJO = register("corazon_rojo", () -> new CorazonRojoItem(new Properties().stacksTo(16).rarity(Rarity.RARE).food(new Builder().nutrition(0).saturationModifier(0.0F).alwaysEdible().build())));
   public static final Item ARCO_GLEBANOIDE = register("arco_glebanoide", () -> new ArcoGlebanoideItem(new Properties().durability(500).rarity(Rarity.RARE)));
   public static final Item MANZANA_DE_AMBAR = register("manzana_de_ambar", () -> new Item(
         new Properties()
            .rarity(Rarity.RARE)
            .food(
               new Builder()
                  .nutrition(4)
                  .saturationModifier(1.6F)
                  .alwaysEdible().build(), net.minecraft.world.item.component.Consumables.defaultFood().onConsume(new net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 2), 1.0F)).onConsume(new net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.ABSORPTION, 3600, 1), 1.0F)).build())
      ));

   private static Item register(String name, java.util.function.Supplier<Item> item) {
      return (Item)Registry.register(BuiltInRegistries.ITEM, com.ghostsafio.mitomod.compat.Ids.item(Ghostsafio.id(name)), item.get());
   }

   public static void initialize() {
      CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(entries -> {
         entries.accept(TOTEM_MONTANA);
         entries.accept(ARCO_GLEBANOIDE);
      });
      CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> {
         entries.accept(CORAZON_ROJO);
         entries.accept(MANZANA_DE_AMBAR);
      });
   }
}
