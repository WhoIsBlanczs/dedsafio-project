package net.minecraftforge.common;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class ForgeSpawnEggItem extends SpawnEggItem {
   public ForgeSpawnEggItem(RegistryObject<? extends EntityType<?>> type, int primaryColor, int secondaryColor, Item.Properties properties) {
      super(resolve(type), primaryColor, secondaryColor, properties);
   }

   private static EntityType<? extends Mob> resolve(RegistryObject<? extends EntityType<?>> type) {
      EntityType<?> resolved = (EntityType<?>)type.get();
      if (resolved == null) {
         throw new IllegalStateException("Spawn egg entity type " + type.getId() + " is not registered yet");
      } else {
         return (EntityType<? extends Mob>)resolved;
      }
   }
}
