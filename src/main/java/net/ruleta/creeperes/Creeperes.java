package net.ruleta.creeperes;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item.Properties;
import net.ruleta.Reg;

public final class Creeperes {
   public static final Reg<EntityType<CreeperCespedEntity>> CESPED = Reg.entidad(
      "ruleta",
      "creeper_cesped",
      () -> Builder.of(CreeperCespedEntity::new, MobCategory.MONSTER).sized(0.3F, 0.5F).clientTrackingRange(8).build("creeper_cesped")
   );
   public static final Reg<Item> HUEVO_CESPED = Reg.item(
      "ruleta", "creeper_cesped_spawn_egg", () -> new SpawnEggItem(CESPED.get(), 4160042, 1784340, new Properties())
   );

   private Creeperes() {
   }

   public static void init() {
      atributos();
   }

   private static void atributos() {
      FabricDefaultAttributeRegistry.register(CESPED.get(), Creeper.createAttributes().add(Attributes.MOVEMENT_SPEED, 0.42).build());
   }
}
