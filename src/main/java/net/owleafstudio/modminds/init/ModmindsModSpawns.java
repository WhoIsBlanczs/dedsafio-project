package net.owleafstudio.modminds.init;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.owleafstudio.modminds.ModmindsMod;

public final class ModmindsModSpawns {
   private ModmindsModSpawns() {
   }

   public static void register() {
      add(ModmindsModEntities.MURCIELAGO_WARDEN.get(), MobCategory.CREATURE, 12, 3, 4, "deep_dark");
      add(
         ModmindsModEntities.SAPITO.get(),
         MobCategory.CREATURE,
         12,
         3,
         4,
         "badlands",
         "bamboo_jungle",
         "beach",
         "birch_forest",
         "cherry_grove",
         "dark_forest",
         "desert",
         "forest",
         "grove",
         "ice_spikes",
         "jungle",
         "mangrove_swamp",
         "meadow",
         "old_growth_birch_forest",
         "old_growth_pine_taiga",
         "old_growth_spruce_taiga",
         "plains",
         "river",
         "savanna",
         "savanna_plateau",
         "sparse_jungle",
         "swamp",
         "taiga"
      );
      add(ModmindsModEntities.TORTUGA_NETHERITA.get(), MobCategory.MONSTER, 20, 2, 2, "nether_wastes", "basalt_deltas");
   }

   private static void add(EntityType<?> type, MobCategory category, int weight, int min, int max, String... biomes) {
      ResourceKey<Biome>[] keys = new ResourceKey[biomes.length];

      for (int i = 0; i < biomes.length; i++) {
         keys[i] = ResourceKey.create(Registries.BIOME, Identifier.withDefaultNamespace(biomes[i]));
      }

      BiomeModifications.addSpawn(BiomeSelectors.includeByKey(keys), category, type, weight, min, max);
      ModmindsMod.LOGGER.info("Registered natural spawns for {} in {} biome(s)", type, biomes.length);
   }
}
