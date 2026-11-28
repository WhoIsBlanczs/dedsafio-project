package com.novastudio.dedsafio4;

import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public final class CreeperVariantsContent {
   public static final Map<String, EntityType<CreeperGeoEntity>> TYPES = new LinkedHashMap<>();

   private CreeperVariantsContent() {
   }

   public static void register() {
      registerCreeper("creeper_nuclear", "creeper", 0.8F, 1.8F, 2329157, 8583633);
      registerCreeper("creeper_dorado", "dorado", 0.8F, 1.8F, 14001429, 16773280);
      registerCreeper("creeper_floral", "floral", 1.0F, 2.1F, 6329415, 15499947);
      registerCreeper("creeper_metalico", "metalico", 0.9F, 2.0F, 9017249, 3423556);
      registerCreeper("creeper_tronco", "tronco", 0.9F, 2.0F, 7753773, 10268499);
   }

   private static void registerCreeper(String id, String animId, float width, float height, int eggBg, int eggFg) {
      EntityType<CreeperGeoEntity> type = EntityType.Builder.<CreeperGeoEntity>of((t, level) -> new CreeperGeoEntity(t, level, animId), MobCategory.MONSTER)
         .sized(width, height)
         .clientTrackingRange(10)
         .updateInterval(3)
         .build(com.novastudio.dedsafio4.compat.Ids.entidad(id));
      Registry.register(BuiltInRegistries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("nexo", id), type);
      FabricDefaultAttributeRegistry.register(type, Creeper.createAttributes());
      TYPES.put(id, type);
      Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(Identifier.fromNamespaceAndPath("nexo", id + "_spawn_egg")), new SpawnEggItem((new Item.Properties()).spawnEgg(type)));
   }
}
