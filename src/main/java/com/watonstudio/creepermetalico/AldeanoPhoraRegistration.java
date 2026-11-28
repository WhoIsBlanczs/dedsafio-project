package com.watonstudio.creepermetalico;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public final class AldeanoPhoraRegistration {
   public static final SoundEvent PHORA_ACTIVAR_SOUND = (SoundEvent)Registry.register(
      BuiltInRegistries.SOUND_EVENT,
      Identifier.fromNamespaceAndPath("watonstudio", "phora_activar"),
      SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("watonstudio", "phora_activar"))
   );
   public static final SoundEvent PHORA_AMBIENT_SOUND = (SoundEvent)Registry.register(
      BuiltInRegistries.SOUND_EVENT,
      Identifier.fromNamespaceAndPath("watonstudio", "phora_ambient"),
      SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("watonstudio", "phora_ambient"))
   );
   public static final EntityType<AldeanoPhoraEntity> ALDEANO_PHORA = (EntityType<AldeanoPhoraEntity>)Registry.register(
      BuiltInRegistries.ENTITY_TYPE,
      Identifier.fromNamespaceAndPath("watonstudio", "aldeano_phora"),
      Builder.of(AldeanoPhoraEntity::new, MobCategory.MISC).sized(0.65F, 0.85F).clientTrackingRange(10).build(com.watonstudio.creepermetalico.compat.Ids.entidad(Identifier.fromNamespaceAndPath("watonstudio", "aldeano_phora")))
   );
   public static final AldeanoPhoraItem ALDEANO_PHORA_ITEM = (AldeanoPhoraItem)Registry.register(BuiltInRegistries.ITEM, com.watonstudio.creepermetalico.compat.Ids.item(Identifier.fromNamespaceAndPath("watonstudio", "aldeano_phora")), new AldeanoPhoraItem(new Properties().stacksTo(1)));
   public static final Item LLAVE_COBRE_ITEM = (Item)Registry.register(BuiltInRegistries.ITEM, com.watonstudio.creepermetalico.compat.Ids.item(Identifier.fromNamespaceAndPath("watonstudio", "llave_cobre")), new Item(new Properties().stacksTo(16)));
   public static final Item FRAGMENTO_PHORA_ITEM = (Item)Registry.register(BuiltInRegistries.ITEM, com.watonstudio.creepermetalico.compat.Ids.item(Identifier.fromNamespaceAndPath("watonstudio", "fragmento_phora")), new Item(new Properties().stacksTo(64)));

   public static void initialize() {
      FabricDefaultAttributeRegistry.register(ALDEANO_PHORA, AldeanoPhoraEntity.createAttributes());
   }

   private AldeanoPhoraRegistration() {
   }
}
