package com.novastudio.dedsafio4;

import java.util.Arrays;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;

public final class PlantsMod implements ModInitializer {
   private static final String[] SEEDS = new String[]{"semilla_huevo_de_eburia", "semilla_baya_uvina", "semilla_arandano_nocturino", "semilla_fruta_solaria"};

   private static Identifier id(String path) {
      return Identifier.fromNamespaceAndPath("nexo", path);
   }

   public void onInitialize() {
      new DinoTreeContent().onInitialize();
      FruitItem.register("baya_uvina");
      FruitItem.register("fruta_solaria");
      FruitItem.register("arandano_nocturino");
      FruitItem.register("huevo_de_eburia");
      Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(id("fruta_del_espacio_entre_dimensiones")), new FrutaEspacioItem());

      for (String seed : SEEDS) {
         int kind = Arrays.asList(DinoFruitRender.FRUITS).indexOf(seed.substring("semilla_".length()));
         Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(id(seed)), new SeedItem(kind));
      }

      SeedItem.registerEvents();
   }
}
