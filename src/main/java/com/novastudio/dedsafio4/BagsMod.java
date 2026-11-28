package com.novastudio.dedsafio4;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.item.Item;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;

public final class BagsMod implements ModInitializer {
   private static final String[] COLORS = new String[]{
      "azul",
      "naranja",
      "gris_claro",
      "morado",
      "marron",
      "rojo",
      "rosa",
      "lima",
      "azul_claro",
      "amarillo",
      "magenta",
      "cian",
      "blanco",
      "gris",
      "negro",
      "verde"
   };

   private static void item(String id, java.util.function.Supplier<? extends Item> item) {
      Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(Identifier.fromNamespaceAndPath("nexo", id)), item.get());
   }

   public void onInitialize() {
      item("bolsa_glebanoide", () -> new GlebanoideBag());
      item("ender_bag", () -> new EnderBagItem());
      item("bolsa_primitiva", () -> new PrimitiveBagItem());

      for (String color : COLORS) {
         String id = "bolsa_primitiva_" + color;
         item(id, () -> new GlebanoideBagItem(id));
      }
   }
}
