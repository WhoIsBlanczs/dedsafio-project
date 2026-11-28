package com.watonstudio.creepermetalico;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item.Properties;

public class FloralCreeperRegistration implements ModInitializer {
   public static final EntityType<FloralCreeperEntity> FLORAL_CREEPER = (EntityType<FloralCreeperEntity>)Registry.register(
      BuiltInRegistries.ENTITY_TYPE,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "creeper_floral"),
      Builder.of(FloralCreeperEntity::new, MobCategory.MONSTER).sized(0.8F, 1.7F).clientTrackingRange(10).build()
   );
   public static final Item FLORAL_CREEPER_SPAWN_EGG = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "creeper_floral_spawn_egg"),
      new SpawnEggItem(FLORAL_CREEPER, 3772202, 10236616, new Properties().component(DataComponents.ITEM_NAME, Component.literal("Creeper Floral")))
   );

   public void onInitialize() {
      FabricDefaultAttributeRegistry.register(FLORAL_CREEPER, Creeper.createAttributes());
      ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register((ModifyEntries)entries -> entries.accept(FLORAL_CREEPER_SPAWN_EGG));
   }
}
