package com.novastudio.dedsafio4;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

public final class MiniReaper extends ReaperEntity {
   public static EntityType<MiniReaper> TYPE;

   public MiniReaper(EntityType<? extends Monster> var1, Level var2) {
      super(var1, var2);
   }

   static AttributeSupplier.Builder attributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 14.0)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3)
         .add(Attributes.FOLLOW_RANGE, 64.0);
   }

   public static void register() {
      TYPE = (EntityType<MiniReaper>)Registry.register(
         BuiltInRegistries.ENTITY_TYPE,
         Identifier.fromNamespaceAndPath("nexo", "gream_reaper_pequena"),
         Builder.createMob(MiniReaper::new, MobCategory.MONSTER, var0 -> var0.defaultAttributes(MiniReaper::attributes))
            .sized(0.6F, 1.2F)
            .clientTrackingRange(64)
            .build(com.novastudio.dedsafio4.compat.Ids.entidad("gream_reaper_pequena"))
      );
      Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(Identifier.fromNamespaceAndPath("nexo", "gream_reaper_pequena_spawn_egg")), new SpawnEggItem((new Item.Properties()).spawnEgg(TYPE)));
   }
}
