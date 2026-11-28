package com.novastudio.dedsafio4;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.resources.Identifier;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ExtrasMod implements ModInitializer {
   private static Identifier id(String path) {
      return Identifier.fromNamespaceAndPath("nexo", path);
   }

   private static void item(String path, java.util.function.Supplier<? extends Item> item) {
      Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(id(path)), item.get());
   }

   public void onInitialize() {
      item("ambar", () -> new Item(new Item.Properties()));

      for (String name : new String[]{"cuchara_revivir", "tenedor_revivir", "cuchara"}) {
         item(name, () -> new Item(new Item.Properties().stacksTo(1)));
      }

      item("chip_phora", () -> new Item(new Item.Properties()));
      item("bioxita", () -> new Item(new Item.Properties()));

      for (String name : new String[]{"gema_fantasma_blanca", "gema_fantasma_amarilla", "gema_fantasma_negra", "gema_fantasma_roja", "ambar_bruto"}) {
         item(name, () -> new Item(new Item.Properties()));
      }

      item("gigantofruta", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationModifier(0.6F).build())));

      for (String name : new String[]{"mena_de_bioxita", "mena_de_ambar"}) {
         Block block = (Block)Registry.register(BuiltInRegistries.BLOCK, com.novastudio.dedsafio4.compat.Ids.bloque(id(name)), new DropExperienceBlock(UniformInt.of(1, 3), BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE)));
         item(name, () -> new BlockItem(block, new Item.Properties()));
      }

      item("manzana_ambar", () -> new Item(
            new Item.Properties()
               .food(
                  new FoodProperties.Builder()
                     .nutrition(4)
                     .saturationModifier(1.2F)
                     .alwaysEdible()
                     .build(),
                  net.minecraft.world.item.component.Consumables.defaultFood()
                     .onConsume(new net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1), 1.0F))
                     .onConsume(new net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0), 1.0F))
                     .build()
               )
         ));
      MartilloItem.register();
   }
}
