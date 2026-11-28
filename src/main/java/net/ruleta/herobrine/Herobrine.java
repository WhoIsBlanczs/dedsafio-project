package net.ruleta.herobrine;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item.Properties;
import net.ruleta.Reg;

public final class Herobrine {
   public static final Reg<EntityType<HerobrineEntity>> HEROBRINE = Reg.entidad(
      "ruletafanproject",
      "herobrine",
      () -> Builder.of(HerobrineEntity::new, MobCategory.MONSTER).sized(0.6F, 1.8F).eyeHeight(1.62F).clientTrackingRange(10).build(net.ruleta.compat.Ids.entidad("herobrine"))
   );
   public static final Reg<Item> HUEVO = Reg.item(
      "ruletafanproject", "herobrine_spawn_egg", () -> new SpawnEggItem((new Properties()).spawnEgg(HEROBRINE.get()))
   );

   private Herobrine() {
   }

   public static void init() {
      atributos();
   }

   private static void atributos() {
      FabricDefaultAttributeRegistry.register(HEROBRINE.get(), HerobrineEntity.atributos().build());
   }
}
