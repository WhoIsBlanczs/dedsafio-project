package com.novastudio.dedsafio4;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public final class DinoTreeContent implements ModInitializer {
   private static final String[] FRUITS = new String[]{"fruta_solaria", "arandano_nocturino", "huevo_de_eburia", "baya_uvina"};

   public void onInitialize() {
      registerTree("abedul_dino", () -> new DinoSaplingBlock(true));
      registerTree("roble_quiu_dino", () -> new DinoSaplingBlock(false));

      for (String var4 : new String[]{"tronco_abedul_dino", "tronco_roble_quiu_dino"}) {
         registerBlock(var4, DinoTreeTrunkBlock::new, true);
      }

      for (int var6 = 0; var6 < FRUITS.length; var6++) {
         String var7 = FRUITS[var6];
         int indice = var6;
         registerBlock(var7 + "_tree_fruit", () -> new DinoFruitGeoBlock(indice), false);
      }

      DinoFruitRender.registerType();
   }

   private static void registerTree(String var0, java.util.function.Supplier<? extends Block> var1) {
      registerBlock(var0, var1, true);
   }

   private static void registerBlock(String var0, java.util.function.Supplier<? extends Block> f, boolean var2) {
      Identifier var3 = Identifier.fromNamespaceAndPath("nexo", var0);
      Block var1 = Registry.register(BuiltInRegistries.BLOCK, com.novastudio.dedsafio4.compat.Ids.bloque(var3), f.get());
      if (var2) {
         registerBlockItem(var0, var1);
      }
   }

   private static void registerBlockItem(String var0, Block var1) {
      Identifier var2 = Identifier.fromNamespaceAndPath("nexo", var0);
      Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(var2), new BlockItem(var1, new Item.Properties()));
   }
}
