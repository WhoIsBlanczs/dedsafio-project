package net.ruleta.mobs;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item.Properties;
import net.ruleta.Reg;

public final class MobsNuevos {
   public static final Reg<EntityType<BombaWarden>> BOMBA_WARDEN = Reg.entidad(
      "ruleta",
      "bomba_warden",
      () -> Builder.of(BombaWarden::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(10).fireImmune().build("ruleta:bomba_warden")
   );
   public static final Reg<Item> HUEVO_BOMBA_WARDEN = Reg.item(
      "ruleta", "bomba_warden_spawn_egg", () -> new SpawnEggItem(BOMBA_WARDEN.get(), 736586, 4182472, new Properties())
   );

   private MobsNuevos() {
   }

   public static void init() {
      atributos();
   }

   private static void atributos() {
      FabricDefaultAttributeRegistry.register(BOMBA_WARDEN.get(), Vex.createAttributes().add(Attributes.FOLLOW_RANGE, 48.0).build());
   }
}
