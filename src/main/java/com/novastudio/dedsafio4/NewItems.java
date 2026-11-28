package com.novastudio.dedsafio4;

import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public final class NewItems {
   static final String[] SWORDS = new String[]{"espadaglebanoide", "espadadellimbo", "espadaphora"};
   public static final String[] PLAIN = new String[]{
      "insecto",
      "moco_glebanoide",
      "plantilla_hami",
      "alma",
      "cuchillosimple",
      "cuchillodelaresurreccion",
      "cuchadordelaresurrecion",
      "tenedordelaresurrecionpermanente",
      "totemyunque",
      "ambar_con_insecto",
      "caparazon",
      "capsuladepastillavacia",
      "cenizadealma",
      "cerebroglebanoide",
      "corazonglebanoide",
      "cristaldemagiadelaimaginacion",
      "cristaldemagiadelcaos",
      "cuernoglebanoide",
      "fruto_quiu",
      "garraphora",
      "gotadesangre",
      "pastilladesnutrializadora",
      "peloglebanoide",
      "pildoradezombificacion",
      "placaphora",
      "plantillademejoradegleba",
      "plantillademejoradellimbo",
      "polvodealma",
      "proyectilphora",
      "totemdelsacrificio",
      "totemdimensional",
      "totemmontana",
      "vialdenergiaphora"
   };
   static final int BODY = 14211288;
   static final int AQUA = 5625845;
   static final int PURPLE = 13134847;
   static final int GREEN = 6021979;
   static final int GOLD = 16765503;

   private NewItems() {
   }

   public static void register() {
      for (String var3 : SWORDS) {
         item(var3, () -> new Item(new Item.Properties().sword(ToolMaterial.DIAMOND, 3.0F, -2.4F)));
      }

      for (String var10 : new String[]{"espacial", "hami", "limbo", "glebanoide"}) {
         armor("casco_" + var10, net.minecraft.world.item.equipment.ArmorType.HELMET);
         armor("pechera_" + var10, net.minecraft.world.item.equipment.ArmorType.CHESTPLATE);
         armor("pantalon_" + var10, net.minecraft.world.item.equipment.ArmorType.LEGGINGS);
         armor("botas_" + var10, net.minecraft.world.item.equipment.ArmorType.BOOTS);
      }

      item("arcoglebanoide", () -> new BowItem(new Item.Properties().durability(384).enchantable(1)));

      for (String var11 : PLAIN) {
         item(var11, () -> new Item(new Item.Properties()));
      }

      item("icono_nexo", () -> new Item(new Item.Properties()));
   }

   private static void armor(String var0, net.minecraft.world.item.equipment.ArmorType var1) {
      item(var0, () -> new Item(new Item.Properties().humanoidArmor(ArmorMaterials.CHAINMAIL, var1)));
   }

   private static void item(String var0, java.util.function.Supplier<? extends Item> var1) {
      Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(Identifier.fromNamespaceAndPath("nexo", var0)), var1.get());
   }
}
