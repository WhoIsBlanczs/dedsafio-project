package net.minecraftforge.event.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.eventbus.api.Event;

public class EntityAttributeCreationEvent extends Event {
   private int registered;

   public void put(EntityType<? extends LivingEntity> type, AttributeSupplier.Builder builder) {
      if (type != null && builder != null) {
         FabricDefaultAttributeRegistry.register(type, builder);
         this.registered++;
      }
   }

   public void put(EntityType<? extends LivingEntity> type, AttributeSupplier attributes) {
      if (type != null && attributes != null) {
         FabricDefaultAttributeRegistry.register(type, attributes);
         this.registered++;
      }
   }

   public int getRegisteredCount() {
      return this.registered;
   }
}
