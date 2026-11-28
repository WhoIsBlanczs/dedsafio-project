package com.ghostsafio.mitomod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item.Properties;

public class ModMobs {
   public static final EntityType<NautilusEntity> NAUTILUS = (EntityType<NautilusEntity>)Registry.register(
      BuiltInRegistries.ENTITY_TYPE,
      Ghostsafio.id("nautilus"),
      Builder.of(NautilusEntity::new, MobCategory.MONSTER).sized(1.2F, 1.5F).clientTrackingRange(10).build("nautilus")
   );
   public static final EntityType<CerebroEntity> CEREBRO = (EntityType<CerebroEntity>)Registry.register(
      BuiltInRegistries.ENTITY_TYPE,
      Ghostsafio.id("cerebro_amarillo"),
      Builder.of(CerebroEntity::new, MobCategory.MONSTER).sized(0.8F, 1.0F).clientTrackingRange(10).updateInterval(1).build("cerebro_amarillo")
   );
   public static final EntityType<SkiffPhoraEntity> SKIFF_PHORA = (EntityType<SkiffPhoraEntity>)Registry.register(
      BuiltInRegistries.ENTITY_TYPE,
      Ghostsafio.id("skiff_phora"),
      Builder.of(SkiffPhoraEntity::new, MobCategory.MONSTER).sized(1.0F, 1.2F).clientTrackingRange(12).build("skiff_phora")
   );
   public static final EntityType<PodPhoraEntity> POD_PHORA = (EntityType<PodPhoraEntity>)Registry.register(
      BuiltInRegistries.ENTITY_TYPE,
      Ghostsafio.id("pod_phora"),
      Builder.of(PodPhoraEntity::new, MobCategory.MONSTER).sized(1.1F, 1.1F).clientTrackingRange(12).build("pod_phora")
   );
   public static final EntityType<PodPhoraLaserEntity> POD_LASER = (EntityType<PodPhoraLaserEntity>)Registry.register(
      BuiltInRegistries.ENTITY_TYPE,
      Ghostsafio.id("pod_phora_laser"),
      Builder.of(PodPhoraLaserEntity::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(8).updateInterval(1).build("pod_phora_laser")
   );
   public static final Item NAUTILUS_EGG = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      Ghostsafio.id("nautilus_spawn_egg"),
      new SpawnEggItem(NAUTILUS, 15322064, 10177164, new Properties().component(DataComponents.ITEM_NAME, Component.literal("Huevo de Nautilus")))
   );
   public static final Item CEREBRO_EGG = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      Ghostsafio.id("cerebro_amarillo_spawn_egg"),
      new SpawnEggItem(CEREBRO, 15043491, 14268190, new Properties().component(DataComponents.ITEM_NAME, Component.literal("Huevo de Cerebro amarillo")))
   );
   public static final Item SKIFF_PHORA_EGG = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      Ghostsafio.id("skiff_phora_spawn_egg"),
      new SpawnEggItem(SKIFF_PHORA, 3050379, 14721056, new Properties().component(DataComponents.ITEM_NAME, Component.literal("Huevo de Skiff phora")))
   );
   public static final Item POD_PHORA_EGG = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      Ghostsafio.id("pod_phora_spawn_egg"),
      new SpawnEggItem(POD_PHORA, 3042142, 14704656, new Properties().component(DataComponents.ITEM_NAME, Component.literal("Huevo de Pod phora")))
   );

   public static void register() {
      FabricDefaultAttributeRegistry.register(NAUTILUS, NautilusEntity.createAttributes());
      FabricDefaultAttributeRegistry.register(CEREBRO, CerebroEntity.createAttributes());
      FabricDefaultAttributeRegistry.register(SKIFF_PHORA, SkiffPhoraEntity.createAttributes());
      FabricDefaultAttributeRegistry.register(POD_PHORA, PodPhoraEntity.createAttributes());
      ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register((ModifyEntries)entries -> {
         entries.accept(NAUTILUS_EGG);
         entries.accept(CEREBRO_EGG);
         entries.accept(SKIFF_PHORA_EGG);
         entries.accept(POD_PHORA_EGG);
      });
      ServerTickEvents.END_SERVER_TICK.register(GlebaSpawner::tick);
      ServerTickEvents.END_SERVER_TICK.register(PhoraSpawner::tick);
      ServerTickEvents.END_SERVER_TICK.register(GlebaWater::tick);
   }
}
