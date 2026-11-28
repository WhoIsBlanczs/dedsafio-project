package com.example.dedsafioimitationmod.entity;

import com.example.dedsafioimitationmod.DedsafioImitationMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType.Builder;

public class ModEntities {
   public static final EntityType<NautilusOseoGEntity> NAUTILUS_OSEO_G = tipo("nautilus_oseo_g", Builder.of(NautilusOseoGEntity::new, MobCategory.MONSTER).sized(1.0F, 1.4F));
   public static final EntityType<CerebroAmarilloEntity> CEREBRO_AMARILLO = tipo("cerebro_amarillo", Builder.of(CerebroAmarilloEntity::new, MobCategory.MONSTER).sized(0.7F, 0.7F));
   public static final EntityType<NautilusOseoPEntity> NAUTILUS_OSEO_P = tipo("nautilus_oseo_p", Builder.of(NautilusOseoPEntity::new, MobCategory.MONSTER).sized(0.8F, 0.8F));
   public static final EntityType<CerebroRojoEntity> CEREBRO_ROJO = tipo("cerebro_rojo", Builder.of(CerebroRojoEntity::new, MobCategory.MONSTER).sized(0.7F, 0.7F));

   private static <T extends net.minecraft.world.entity.Entity> EntityType<T> tipo(String id, Builder<T> b) {
      net.minecraft.resources.ResourceKey<EntityType<?>> key = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, DedsafioImitationMod.id(id));
      return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, b.build(key));
   }

   public static void register() {
      FabricDefaultAttributeRegistry.register(NAUTILUS_OSEO_G, NautilusOseoGEntity.createAttributes());
      FabricDefaultAttributeRegistry.register(CEREBRO_AMARILLO, CerebroAmarilloEntity.createAttributes());
      FabricDefaultAttributeRegistry.register(NAUTILUS_OSEO_P, NautilusOseoPEntity.createAttributes());
      FabricDefaultAttributeRegistry.register(CEREBRO_ROJO, CerebroRojoEntity.createAttributes());
   }
}
