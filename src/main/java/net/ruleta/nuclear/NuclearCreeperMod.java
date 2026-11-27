package net.ruleta.nuclear;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item.Properties;
import net.ruleta.Reg;
import net.ruleta.nuclear.entity.NuclearCreeperEntity;
import net.ruleta.nuclear.item.AbsorptionHeartItem;
import net.ruleta.nuclear.item.RedHeartItem;
import net.ruleta.nuclear.item.TotemItem;
import net.ruleta.nuclear.network.ModNetwork;

public class NuclearCreeperMod {
   public static final String MODID = "nuclearcreeper";
   public static final double MAX_HEALTH = 25.0;
   public static final double DETECTION_RANGE = 40.0;
   public static final Reg<EntityType<NuclearCreeperEntity>> NUCLEAR_CREEPER = Reg.entidad(
      "nuclearcreeper",
      "nuclear_creeper",
      () -> Builder.of(NuclearCreeperEntity::new, MobCategory.MONSTER).sized(0.8F, 1.25F).clientTrackingRange(8).build(net.ruleta.Reg.ekey())
   );
   public static final Reg<Item> NUCLEAR_CREEPER_SPAWN_EGG = Reg.item(
      "nuclearcreeper", "nuclear_creeper_spawn_egg", () -> new SpawnEggItem(net.ruleta.Reg.props().spawnEgg(NUCLEAR_CREEPER.get()))
   );
   private static final FoodProperties HEART_FOOD = new net.minecraft.world.food.FoodProperties.Builder()
      .nutrition(0)
      .saturationModifier(0.0F)
      .alwaysEdible()
      .build();
   public static final Reg<Item> RED_HEART = Reg.item(
      "nuclearcreeper", "red_heart", () -> new RedHeartItem(net.ruleta.Reg.props().food(HEART_FOOD).rarity(Rarity.RARE))
   );
   public static final Reg<Item> ABSORPTION_HEART = Reg.item(
      "nuclearcreeper", "absorption_heart", () -> new AbsorptionHeartItem(net.ruleta.Reg.props().food(HEART_FOOD).rarity(Rarity.RARE))
   );
   public static final Reg<Item> GLEBANOIDE_TOTEM = Reg.item(
      "nuclearcreeper", "glebanoide_totem", () -> new TotemItem(net.ruleta.Reg.props().stacksTo(1).rarity(Rarity.COMMON), TotemItem.Kind.GLEBANOIDE)
   );
   public static final Reg<Item> NUTRITOTEM = Reg.item(
      "nuclearcreeper", "nutritotem", () -> new TotemItem(net.ruleta.Reg.props().stacksTo(1).rarity(Rarity.COMMON), TotemItem.Kind.NUTRITOTEM)
   );
   public static final Reg<Item> EON_TOTEM = Reg.item(
      "nuclearcreeper", "eon_totem", () -> new TotemItem(net.ruleta.Reg.props().stacksTo(1).rarity(Rarity.COMMON), TotemItem.Kind.EON)
   );
   public static final Reg<Item> YUNQUE_TOTEM = Reg.item(
      "nuclearcreeper", "yunque_totem", () -> new TotemItem(net.ruleta.Reg.props().stacksTo(1).rarity(Rarity.COMMON), TotemItem.Kind.YUNQUE)
   );
   public static final Reg<Item> GOLEM_TOTEM = Reg.item(
      "nuclearcreeper", "golem_totem", () -> new TotemItem(net.ruleta.Reg.props().stacksTo(1).rarity(Rarity.COMMON), TotemItem.Kind.GOLEM)
   );
   public static final Reg<SoundEvent> RADIATION_LOOP = Reg.sonido(
      "nuclearcreeper", "radiation_loop", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("nuclearcreeper", "radiation_loop"))
   );

   public static void init() {
      FabricDefaultAttributeRegistry.register(
         NUCLEAR_CREEPER.get(), Creeper.createAttributes().add(Attributes.MAX_HEALTH, 25.0).add(Attributes.FOLLOW_RANGE, 40.0).build()
      );
      ModNetwork.register();
   }
}
