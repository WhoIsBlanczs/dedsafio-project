package com.example.dedsafioimitationmod.item;

import com.example.dedsafioimitationmod.DedsafioImitationMod;
import com.example.dedsafioimitationmod.entity.ModEntities;
import java.util.function.Function;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;

public class ModItems {
   public static final String MOD_ID = "dedsafioimitationmod";
   public static final Item PLANTILLA_CEDRONIDA = registerItem("plantilla_de_mejora_cedronida");
   public static final Item PLANTILLA_GLEBA = registerItem("plantilla_de_mejora_de_gleba");
   public static final Item PLANTILLA_LIMBO = registerItem("plantilla_de_mejora_del_limbo");
   public static final Item PLANTILLA_PRIMITIVA = registerItem("plantilla_de_mejora_primitiva");
   public static final Item NAUTILUS_OSEO_G_SPAWN_EGG = huevo("nautilus_oseo_g_spawn_egg", ModEntities.NAUTILUS_OSEO_G);
   public static final Item CEREBRO_AMARILLO_SPAWN_EGG = huevo("cerebro_amarillo_spawn_egg", ModEntities.CEREBRO_AMARILLO);
   public static final Item CEREBRO_ROJO_SPAWN_EGG = huevo("cerebro_rojo_spawn_egg", ModEntities.CEREBRO_ROJO);
   public static final Item NAUTILUS_OSEO_P_SPAWN_EGG = huevo("nautilus_oseo_p_spawn_egg", ModEntities.NAUTILUS_OSEO_P);
   public static final Item ARCO_GLEBANOIDE = register("arco_glebanoide", ArcoGlebanoideItem::new,
      new Properties().durability(500).rarity(Rarity.EPIC).enchantable(1));
   public static final Item ARCO_DE_HUESOS = register("arco_de_huesos", ArcoDeHuesosItem::new,
      new Properties().durability(500).rarity(Rarity.EPIC).enchantable(1));
   public static final Item ESPADA_QUMARA = register("espada_qumara", Item::new,
      new Properties().rarity(Rarity.EPIC).fireResistant().sword(ModToolMaterial.QUMARA, 3.0F, -2.4F));
   public static final Item CEREBRO_GLEBANOIDE = registerItem("cerebro_glebanoide");
   public static final Item AMBAR = registerItem("ambar");
   public static final Item AMBAR_EN_BRUTO = registerItem("ambar_en_bruto");
   public static final Item AMBAR_CON_INSECTO = registerItem("ambar_con_insecto");
   public static final Item PELOS_DE_GLEBA = registerItem("pelos_de_gleba");
   public static final Item GARRA_GLEBA = registerItem("garra_gleba");
   public static final Item NI_IDEA_QUE_ES_ESTO = registerItem("ni_idea_que_es_esto");
   public static final Item CAPARAZON_DE_NAUTILIUS = registerItem("caparazon_de_nautilius");
   public static final Item GRASA = registerItem("grasa");
   public static final Item CORAZON_ESPADA = registerItem("corazon_espada");
   public static final Item CUERDAS = registerItem("cuerdas");
   public static final Item EXCREMENTO = registerItem("excremento");
   public static final Item PIEDRA_QUIU = registerItem("piedra_quiu");
   public static final Item CUERDA_QUIU = registerItem("cuerda_quiu");
   public static final Item TELA_QUIU = registerItem("tela_quiu");
   public static final Item FRUTA_GIGANTE = registerItem("fruta_gigante");

   private static Item register(String id, Function<Properties, Item> fabrica, Properties props) {
      ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, DedsafioImitationMod.id(id));
      return Registry.register(BuiltInRegistries.ITEM, key, fabrica.apply(props.setId(key)));
   }

   private static Item registerItem(String id) {
      return register(id, Item::new, new Properties().rarity(Rarity.EPIC));
   }

   private static Item huevo(String id, EntityType<?> tipo) {
      return register(id, SpawnEggItem::new, new Properties().spawnEgg(tipo));
   }

   public static void register() {
      CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
         for (Item i : new Item[]{PLANTILLA_CEDRONIDA, PLANTILLA_GLEBA, PLANTILLA_LIMBO, PLANTILLA_PRIMITIVA, CEREBRO_GLEBANOIDE, AMBAR,
               AMBAR_EN_BRUTO, AMBAR_CON_INSECTO, PELOS_DE_GLEBA, GARRA_GLEBA, NI_IDEA_QUE_ES_ESTO, CAPARAZON_DE_NAUTILIUS, GRASA,
               CORAZON_ESPADA, CUERDAS, EXCREMENTO, PIEDRA_QUIU, CUERDA_QUIU, TELA_QUIU, FRUTA_GIGANTE}) {
            entries.accept(i);
         }
      });
      CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> {
         entries.accept(NAUTILUS_OSEO_G_SPAWN_EGG);
         entries.accept(CEREBRO_AMARILLO_SPAWN_EGG);
         entries.accept(CEREBRO_ROJO_SPAWN_EGG);
         entries.accept(NAUTILUS_OSEO_P_SPAWN_EGG);
      });
      CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COMBAT).register(entries -> {
         entries.accept(ARCO_GLEBANOIDE);
         entries.accept(ARCO_DE_HUESOS);
         entries.accept(ESPADA_QUMARA);
      });
   }
}
