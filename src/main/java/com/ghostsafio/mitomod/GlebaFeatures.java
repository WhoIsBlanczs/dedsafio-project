package com.ghostsafio.mitomod;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class GlebaFeatures {
   public static final ResourceKey<PlacedFeature> GRASA_ORE = ResourceKey.create(Registries.PLACED_FEATURE, Ghostsafio.id("grasa_ore"));
   public static final ResourceKey<PlacedFeature> ARBOL_GLEBA = ResourceKey.create(Registries.PLACED_FEATURE, Ghostsafio.id("arbol_gleba"));
   public static final ResourceKey<Biome> GLEBA_BIOME = ResourceKey.create(Registries.BIOME, Ghostsafio.id("gleba"));

   public static void register() {
      BiomeModifications.addFeature(BiomeSelectors.includeByKey(new ResourceKey[]{GLEBA_BIOME}), Decoration.UNDERGROUND_ORES, GRASA_ORE);
      BiomeModifications.addFeature(BiomeSelectors.includeByKey(new ResourceKey[]{GLEBA_BIOME}), Decoration.VEGETAL_DECORATION, ARBOL_GLEBA);
   }
}
