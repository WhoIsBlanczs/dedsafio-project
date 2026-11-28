package com.novastudio.dedsafio4;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public final class MemeMod implements ModInitializer {
   public static EntityType<MemeCreeperEntity> MEME_CREEPER;

   public void onInitialize() {
      MEME_CREEPER = (EntityType<MemeCreeperEntity>)Registry.register(
         BuiltInRegistries.ENTITY_TYPE,
         Identifier.fromNamespaceAndPath("nexo", "meme_creeper"),
         Builder.createMob(MemeCreeperEntity::new, MobCategory.MONSTER, b -> b.defaultAttributes(MemeCreeperEntity::createAttributes))
            .sized(0.6F, 1.7F)
            .clientTrackingRange(8)
            .build(com.novastudio.dedsafio4.compat.Ids.entidad("meme_creeper"))
      );
      Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(Identifier.fromNamespaceAndPath("nexo", "meme_creeper_spawn_egg")), new SpawnEggItem((new Item.Properties()).spawnEgg(MEME_CREEPER)));
      MemeTrap.register();
   }
}
