package com.novastudio.dedsafio4;

import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;

public final class GhostsMod implements ModInitializer {
   public static final Map<String, EntityType<FantasmaEntity>> GHOST_TYPES = new LinkedHashMap<>();
   public static EntityType<ReaperEntity> REAPER;

   private static Identifier id(String path) {
      return Identifier.fromNamespaceAndPath("nexo", path);
   }

   private static void ghost(String color, int eggBg, int eggFg, double speed, float damage, int rest, boolean halves) {
      EntityType<FantasmaEntity> type = Builder.createMob(
            FantasmaEntity::new,
            MobCategory.MONSTER,
            b -> b.spawnPlacement(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules)
               .defaultAttributes(FantasmaEntity::createAttributes)
         )
         .sized(1.5F, 1.6F)
         .clientTrackingRange(10)
         .build(com.novastudio.dedsafio4.compat.Ids.entidad("fantasma_" + color));
      Registry.register(BuiltInRegistries.ENTITY_TYPE, id("fantasma_" + color), type);
      FantasmaEntity.KINDS.put(type, new FantasmaEntity.Kind(color, speed, damage, rest, halves));
      GHOST_TYPES.put(color, type);
      Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(id("fantasma_" + color + "_spawn_egg")), new SpawnEggItem((new Item.Properties()).spawnEgg(type)));
   }

   public void onInitialize() {
      ghost("blanco", 15263986, 9078440, 0.1, 1.0F, 40, true);
      ghost("amarillo", 15913024, 10119696, 0.35, 22.0F, 3, false);
      ghost("rojo", 13641768, 5900810, 0.2, 14.0F, 8, false);
      REAPER = (EntityType<ReaperEntity>)Registry.register(
         BuiltInRegistries.ENTITY_TYPE,
         id("grim_reaper"),
         Builder.createMob(ReaperEntity::new, MobCategory.MONSTER, b -> b.defaultAttributes(ReaperEntity::createAttributes))
            .sized(1.2F, 4.1F)
            .clientTrackingRange(10)
            .build(com.novastudio.dedsafio4.compat.Ids.entidad("grim_reaper"))
      );
      Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(id("grim_reaper_spawn_egg")), new SpawnEggItem((new Item.Properties()).spawnEgg(REAPER)));
      MiniReaper.register();
   }
}
