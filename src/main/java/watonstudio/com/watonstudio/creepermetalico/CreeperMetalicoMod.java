package com.watonstudio.creepermetalico;

import com.watonstudio.creepermetalico.net.OpenPhoraS2C;
import com.watonstudio.creepermetalico.net.TradePhoraC2S;
import java.util.List;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemLore;

public class CreeperMetalicoMod implements ModInitializer {
   public static final String MOD_ID = "watonstudio";
   public static final SoundEvent ECLIPSE_START = (SoundEvent)Registry.register(
      BuiltInRegistries.SOUND_EVENT,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "start"),
      SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("watonstudio", "start"))
   );
   public static final SoundEvent ECLIPSE_DARK = (SoundEvent)Registry.register(
      BuiltInRegistries.SOUND_EVENT,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "dark"),
      SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("watonstudio", "dark"))
   );
   public static final SoundEvent RESORTE_EXPLOSION = (SoundEvent)Registry.register(
      BuiltInRegistries.SOUND_EVENT,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "resorte_explosion"),
      SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("watonstudio", "resorte_explosion"))
   );
   public static final SoundEvent RESORTE_WALKING = (SoundEvent)Registry.register(
      BuiltInRegistries.SOUND_EVENT,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "resorte_walking"),
      SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("watonstudio", "resorte_walking"))
   );
   public static final SoundEvent RESORTE_MUERTE = (SoundEvent)Registry.register(
      BuiltInRegistries.SOUND_EVENT,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "muerte"),
      SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("watonstudio", "muerte"))
   );
   public static final EntityType<MetalCreeperEntity> METAL_CREEPER = (EntityType<MetalCreeperEntity>)Registry.register(
      BuiltInRegistries.ENTITY_TYPE,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "creeper_resorte"),
      Builder.of(MetalCreeperEntity::new, MobCategory.MONSTER).sized(0.6F, 1.7F).clientTrackingRange(8).build()
   );
   public static final EntityType<CreeperNuclearEntity> NUCLEAR_CREEPER = (EntityType<CreeperNuclearEntity>)Registry.register(
      BuiltInRegistries.ENTITY_TYPE,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "creeper_nuclear"),
      Builder.of(CreeperNuclearEntity::new, MobCategory.MONSTER).sized(0.8F, 1.8F).clientTrackingRange(10).build()
   );
   public static final EntityType<FogataSummonEntity> FOGATA_SUMMONER = (EntityType<FogataSummonEntity>)Registry.register(
      BuiltInRegistries.ENTITY_TYPE,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "fogata"),
      Builder.of(FogataSummonEntity::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(32).updateInterval(1).build("watonstudio:fogata")
   );
   public static final Item CREEPER_RESORTE_SPAWN_EGG = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "creeper_resorte_spawn_egg"),
      new SpawnEggItem(METAL_CREEPER, 5592405, 65280, new Properties().component(DataComponents.ITEM_NAME, nombreBlanco("Creeper Resorte")))
   );
   public static final Item CREEPER_NUCLEAR_SPAWN_EGG = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "creeper_nuclear_spawn_egg"),
      new SpawnEggItem(NUCLEAR_CREEPER, 14679808, 16729344, new Properties().component(DataComponents.ITEM_NAME, nombreBlanco("Creeper Nuclear")))
   );
   public static final Item WATON_ICON_ITEM = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "waton_icon"),
      new Item(new Properties().component(DataComponents.ITEM_NAME, nombreCeleste("Waton Studio")))
   );
   public static final Item MANZANA_AMBAR = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "manzana_ambar"),
      new ManzanaAmbarItem(
         new Properties()
            .stacksTo(64)
            .component(DataComponents.ITEM_NAME, nombreCeleste("Manzana de Ambar"))
            .component(
               DataComponents.LORE,
               new ItemLore(
                  List.of(
                     Component.literal("Regeneracion II (00:05)").withStyle(s -> s.withColor(5592575).withItalic(false)),
                     Component.literal("Absorcion (02:00)").withStyle(s -> s.withColor(5592575).withItalic(false)),
                     Component.literal("").withStyle(s -> s.withItalic(false)),
                     Component.literal("Al aplicarse:").withStyle(s -> s.withColor(11141290).withItalic(false)),
                     Component.literal("Absorcion maxima: +4").withStyle(s -> s.withColor(5592575).withItalic(false)),
                     Component.literal("").withStyle(s -> s.withItalic(false)),
                     Component.literal("Combina Manzana Dorada con").withStyle(s -> s.withColor(11184810).withItalic(false)),
                     Component.literal("Mineral de Ambar con Insecto").withStyle(s -> s.withColor(11141290).withItalic(false)),
                     Component.literal("para conseguirla.").withStyle(s -> s.withColor(11184810).withItalic(false))
                  )
               )
            )
      )
   );
   public static final Item INSECTO = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "insecto"),
      new Item(
         new Properties()
            .stacksTo(64)
            .component(DataComponents.ITEM_NAME, nombreCeleste("Insecto"))
            .component(
               DataComponents.LORE,
               new ItemLore(
                  List.of(
                     Component.literal("Diminuto insecto atrapado").withStyle(s -> s.withColor(ChatFormatting.GRAY).withItalic(false)),
                     Component.literal("en las Criaturas del Centro de Quiu").withStyle(s -> s.withColor(ChatFormatting.GRAY).withItalic(false)),
                     Component.empty(),
                     Component.empty()
                        .append(Component.literal("Probabilidad").withStyle(s -> s.withColor(ChatFormatting.GOLD).withItalic(false)))
                        .append(Component.literal(" de conseguirlo").withStyle(s -> s.withColor(ChatFormatting.GRAY).withItalic(false))),
                     Component.literal("al eliminar:").withStyle(s -> s.withColor(ChatFormatting.GRAY).withItalic(false)),
                     Component.empty(),
                     Component.empty()
                        .append(Component.literal("\ud83d\udd39").withStyle(s -> s.withColor(5636095).withItalic(false)))
                        .append(Component.literal(" Dromoraptores").withStyle(s -> s.withColor(ChatFormatting.DARK_RED).withItalic(false)))
                  )
               )
            )
      )
   );
   public static final Item CORAZON_ROJO = (Item)Registry.register(
      BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("watonstudio", "corazon_rojo"), new CorazonRojoItem(new Properties().stacksTo(64))
   );
   public static final Item CORAZON_LIMBO = (Item)Registry.register(
      BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("watonstudio", "corazon_limbo"), new CorazonLimboItem(new Properties().stacksTo(64))
   );
   public static final Item ESPADA_HOJA_QUMARA = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "espada_hoja_qumara"),
      new SwordItem(
         Tiers.NETHERITE,
         new Properties()
            .durability(2030)
            .stacksTo(1)
            .component(DataComponents.ITEM_NAME, nombreCeleste("Espada de Hoja de Qumara"))
            .attributes(SwordItem.createAttributes(Tiers.DIAMOND, 6, -2.4F))
      )
   );
   public static final Item TOTEM_EON = (Item)Registry.register(
      BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("watonstudio", "totem_eon"), new TotemEonItem(new Properties())
   );
   public static final Item LINTERNA = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "linterna"),
      new LinternaItem(false, new Properties().stacksTo(1).component(DataComponents.ITEM_NAME, nombreCeleste("Linterna")))
   );
   public static final Item LINTERNA_ENCENDIDA = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "linterna_encendida"),
      new LinternaItem(true, new Properties().stacksTo(1).component(DataComponents.ITEM_NAME, nombreCeleste("Linterna")))
   );
   public static final Item SPOON = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "spoon"),
      new Item(new Properties().component(DataComponents.ITEM_NAME, nombreCeleste("Cuchara")))
   );
   public static final Item CUCHILLO_SIMPLE = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "cuchillosimple"),
      new Item(new Properties().component(DataComponents.ITEM_NAME, nombreCeleste("Cuchillo simple")))
   );
   public static final Item FORK = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "fork"),
      new Item(new Properties().component(DataComponents.ITEM_NAME, nombreCeleste("Tenedor")))
   );
   public static final Item RESURECTION_SPOON = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "resurection_spoon"),
      new Item(new Properties().component(DataComponents.ITEM_NAME, nombreCeleste("Cuchara de resurrección")))
   );
   public static final Item TENEDOR_RESURRECCION_PERMANENTE = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "tenedordelaresurrecionpermanente"),
      new Item(new Properties().component(DataComponents.ITEM_NAME, nombreCeleste("Tenedor de resurrección permanente")))
   );
   public static final Item CUCHADOR_RESURRECCION = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "cuchadordelaresurrecion"),
      new Item(new Properties().component(DataComponents.ITEM_NAME, nombreCeleste("Cuchador de resurrección")))
   );
   public static final Item CUCHILLO_RESURRECCION = (Item)Registry.register(
      BuiltInRegistries.ITEM,
      ResourceLocation.fromNamespaceAndPath("watonstudio", "cuchillodelaresurreccion"),
      new Item(new Properties().component(DataComponents.ITEM_NAME, nombreCeleste("Cuchillo de resurrección")))
   );
   public static final ResourceKey<CreativeModeTab> WATON_GROUP_KEY = ResourceKey.create(
      Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath("watonstudio", "waton_group")
   );
   public static final CreativeModeTab WATON_GROUP = (CreativeModeTab)Registry.register(
      BuiltInRegistries.CREATIVE_MODE_TAB,
      WATON_GROUP_KEY,
      FabricItemGroup.builder().icon(() -> new ItemStack(WATON_ICON_ITEM)).title(Component.literal("WatonStudio")).displayItems((context, entries) -> {
         entries.accept(WATON_ICON_ITEM);
         entries.accept(AldeanoPhoraRegistration.ALDEANO_PHORA_ITEM);
         entries.accept(AldeanoPhoraRegistration.LLAVE_COBRE_ITEM);
         entries.accept(LINTERNA);
         entries.accept(SPOON);
         entries.accept(CUCHILLO_SIMPLE);
         entries.accept(FORK);
         entries.accept(RESURECTION_SPOON);
         entries.accept(TENEDOR_RESURRECCION_PERMANENTE);
         entries.accept(CUCHADOR_RESURRECCION);
         entries.accept(CUCHILLO_RESURRECCION);
         entries.accept(CREEPER_RESORTE_SPAWN_EGG);
         entries.accept(CREEPER_NUCLEAR_SPAWN_EGG);
         entries.accept(NautilusOseoRegistration.NAUTILUS_OSEO_SPAWN_EGG);
         entries.accept(FloralCreeperRegistration.FLORAL_CREEPER_SPAWN_EGG);
         entries.accept(MANZANA_AMBAR);
         entries.accept(INSECTO);
         entries.accept(CorazonRojoItem.crearCorazon(CORAZON_ROJO));
         entries.accept(CorazonLimboItem.crearCorazonLimbo(CORAZON_LIMBO));
         entries.accept(ESPADA_HOJA_QUMARA);
         entries.accept(TotemEonItem.crearTotem(TOTEM_EON));
         entries.accept(FrutosQuiuRegistration.HUEVO_EBURIA);
         entries.accept(FrutosQuiuRegistration.ARANDANO_NOCTURNO);
         entries.accept(FrutosQuiuRegistration.PELO_EBURIA);
         entries.accept(FrutosQuiuRegistration.SEMILLA_EBURIA);
         entries.accept(FrutosQuiuRegistration.SEMILLA_ARANDANO_NOCTURNO);
      }).build()
   );

   private static Component nombreCeleste(String nombre) {
      return Component.literal(nombre)
         .setStyle(Style.EMPTY.withColor(5636095).withItalic(false).withBold(false).withUnderlined(false).withStrikethrough(false).withObfuscated(false));
   }

   private static Component nombreBlanco(String nombre) {
      return Component.literal(nombre)
         .setStyle(
            Style.EMPTY.withColor(ChatFormatting.WHITE).withItalic(false).withBold(false).withUnderlined(false).withStrikethrough(false).withObfuscated(false)
         );
   }

   public void onInitialize() {
      NautilusOseoRegistration.initialize();
      AldeanoPhoraRegistration.initialize();
      FrutosQuiuRegistration.initialize();
      TotemPopMessages.registerCommands();
      PayloadTypeRegistry.playS2C().register(TotemEonPayload.ID, TotemEonPayload.CODEC);
      PayloadTypeRegistry.playS2C().register(OpenPhoraS2C.ID, OpenPhoraS2C.CODEC);
      PayloadTypeRegistry.playC2S().register(TradePhoraC2S.ID, TradePhoraC2S.CODEC);
      ServerPlayNetworking.registerGlobalReceiver(TradePhoraC2S.ID, (payload, context) -> {
         if (context.player().level().getEntity(payload.entityId()) instanceof AldeanoPhoraEntity phora) {
            phora.executePhoraTrade(context.player(), payload.offerIndex());
         }
      });
      FabricDefaultAttributeRegistry.register(METAL_CREEPER, MetalCreeperEntity.createMetalCreeperAttributes());
      FabricDefaultAttributeRegistry.register(NUCLEAR_CREEPER, CreeperNuclearEntity.createCreeperNuclearAttributes());
      System.out.println("WatonStudio cargado correctamente.");
   }
}
