package com.watonstudio.creepermetalico;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item.Properties;

public final class NautilusOseoRegistration {
   public static final EntityType<NautilusOseoEntity> NAUTILUS_OSEO = (EntityType<NautilusOseoEntity>)Registry.register(
      BuiltInRegistries.ENTITY_TYPE,
      Identifier.fromNamespaceAndPath("watonstudio", "nautilus_oseo"),
      Builder.of(NautilusOseoEntity::new, MobCategory.MONSTER).sized(0.9F, 1.8F).clientTrackingRange(10).build(com.watonstudio.creepermetalico.compat.Ids.entidad(Identifier.fromNamespaceAndPath("watonstudio", "nautilus_oseo")))
   );
   public static final Item NAUTILUS_OSEO_SPAWN_EGG = (Item)Registry.register(BuiltInRegistries.ITEM, com.watonstudio.creepermetalico.compat.Ids.item(Identifier.fromNamespaceAndPath("watonstudio", "nautilus_oseo_spawn_egg")), new SpawnEggItem((new Properties()).spawnEgg(NAUTILUS_OSEO)));

   public static void initialize() {
      FabricDefaultAttributeRegistry.register(NAUTILUS_OSEO, NautilusOseoEntity.createAttributes());
   }

   private NautilusOseoRegistration() {
   }
}
