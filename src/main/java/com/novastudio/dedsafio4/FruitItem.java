package com.novastudio.dedsafio4;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item;

public final class FruitItem extends Item {
   private static final int MAX_TOOLTIP_CHARS = 26;
   private static final Pattern WORDS = Pattern.compile("\\s*\\S+\\s*");
   private static final int GRAY = 11184810;
   private static final int AQUA = 5636095;
   private static final int GOLD = 16755200;
   private static final int YELLOW = 16777045;
   private static final int RED = 11141120;
   private static final int BLUE = 5592575;
   private static final int PURPLE = 11141290;
   private final String id;

   private FruitItem(String var1) {
      super(new Item.Properties().food(food(var1), FruitFoods.consumible(var1)));
      this.id = var1;
   }

   private static FoodProperties food(String var0) {
      return FruitFoods.food(var0);
   }

   public static Item register(String var0) {
      return (Item)Registry.register(BuiltInRegistries.ITEM, com.novastudio.dedsafio4.compat.Ids.item(Identifier.fromNamespaceAndPath("nexo", var0)), new FruitItem(var0));
   }

   public void appendHoverText(ItemStack var1, net.minecraft.world.item.Item.TooltipContext var2, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay$, java.util.function.Consumer<Component> tooltipAdder$, TooltipFlag var4) {
      java.util.List<Component> var3 = new java.util.ArrayList<>();
      this.appendHoverText$viejo(var1, var2, tooltipDisplay$, var3, var4);
      var3.forEach(tooltipAdder$);
   }

   private void appendHoverText$viejo(ItemStack var1, net.minecraft.world.item.Item.TooltipContext var2, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay$, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, tooltipDisplay$, var3::add, var4);
      String var5 = this.id;
      switch (var5) {
         case "fruta_solaria":
            solaria(var3);
            break;
         case "arandano_nocturino":
            blueberry(var3);
            break;
         case "huevo_de_eburia":
            eburia(var3);
            break;
         case "baya_uvina":
            uvina(var3);
      }
   }

   private static MutableComponent text(String var0, int var1) {
      return Component.literal(var0).setStyle(Style.EMPTY.withColor(var1));
   }

   private static MutableComponent line(Object... var0) {
      MutableComponent var1 = Component.literal("");

      for (byte var2 = 0; var2 < var0.length; var2 = (byte)(var2 + 2)) {
         var1.append(text((String)var0[var2], (Integer)var0[var2 + 1]));
      }

      return var1;
   }

   private static void add(List<Component> var0, MutableComponent... var1) {
      var0.addAll(List.of(var1));
   }

   private static void addWrapped(List<Component> var0, Object... var1) {
      ArrayList<MutableComponent> var2 = new ArrayList<>();
      MutableComponent var3 = Component.literal("");
      int var4 = 0;

      for (byte var5 = 0; var5 < var1.length; var5 = (byte)(var5 + 2)) {
         String var6 = (String)var1[var5];
         int var7 = (Integer)var1[var5 + 1];
         Matcher var8 = WORDS.matcher(var6);

         while (var8.find()) {
            String var9 = var8.group();
            if (var4 == 0) {
               var9 = var9.stripLeading();
            }

            int var10 = var9.stripTrailing().length();
            if (var4 > 0 && var4 + var10 > 26) {
               var2.add(var3);
               var3 = Component.literal("");
               var4 = 0;
               var9 = var9.stripLeading();
            }

            var3.append(text(var9, var7));
            var4 += var9.length();
         }
      }

      if (var4 > 0) {
         var2.add(var3);
      }

      var0.addAll(var2);
   }

   private static void warning(List<Component> var0) {
      addWrapped(
         var0,
         "⚠ Atención: ",
         16777045,
         "Al alcanzar su punto máximo de crecimiento debes recogerlo lo antes posible. Después de un tiempo se pudrirá si no lo cosechas.",
         11184810
      );
   }

   private static void harvest(List<Component> var0, String var1) {
      addWrapped(
         var0,
         "Utiliza un ",
         11184810,
         "Hacha",
         16755200,
         " mientras tienes ",
         11184810,
         var1,
         5636095,
         " en la ",
         11184810,
         "Mano Secundaria",
         16755200,
         " para extraer la ",
         11184810,
         "Semilla",
         5636095,
         ".",
         11184810
      );
   }

   private static void location(List<Component> var0) {
      addWrapped(var0, "Encuéntralo y coséchalo en los árboles del Centro de Qiu.", 11184810);
   }

   private static void solaria(List<Component> var0) {
      location(var0);
      add(var0, text("", 11184810));
      addWrapped(
         var0,
         "Dale de comer la ",
         11184810,
         "Fruta",
         5636095,
         " a un ",
         11184810,
         "Flumosaurio",
         11141120,
         " para tener la ",
         11184810,
         "probabilidad",
         16777045,
         " de que suelte ",
         11184810,
         "Excremento",
         5636095,
         ".",
         11184810
      );
      add(var0, text("", 11184810));
      warning(var0);
      add(var0, text("", 11184810));
      harvest(var0, "la Fruta");
   }

   private static void blueberry(List<Component> var0) {
      location(var0);
      add(var0, text("", 11184810));
      warning(var0);
      add(var0, text("", 11184810));
      harvest(var0, "el Arándano");
   }

   private static void uvina(List<Component> var0) {
      location(var0);
      add(var0, text("", 11184810));
      warning(var0);
      add(var0, text("", 11184810));
      harvest(var0, "la Baya");
   }

   private static void eburia(List<Component> var0) {
      add(
         var0,
         text("Salud mejorada (01:00)", 5592575),
         text("", 11184810),
         text("Al aplicarse:", 11141290),
         text("Salud máxima: +4", 5592575),
         text("", 11184810)
      );
      location(var0);
      add(var0, text("", 11184810));
      warning(var0);
      add(var0, text("", 11184810));
      addWrapped(
         var0,
         "Utiliza unas ",
         11184810,
         "Tijeras",
         16755200,
         " mientras tienes el ",
         11184810,
         "Huevo",
         5636095,
         " en la ",
         11184810,
         "Mano Secundaria",
         16755200,
         " para extraer ",
         11184810,
         "Pelo de Eburia",
         5636095,
         ".",
         11184810
      );
      add(var0, text("", 11184810));
      harvest(var0, "un Huevo");
   }

   public Component getName(ItemStack var1) {
      MutableComponent var2 = (MutableComponent)super.getName(var1);
      return var2.setStyle(Style.EMPTY.withColor(5636095));
   }
}
