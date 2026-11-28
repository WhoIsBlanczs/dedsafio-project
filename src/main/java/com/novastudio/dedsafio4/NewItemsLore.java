package com.novastudio.dedsafio4;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

@Environment(EnvType.CLIENT)
public final class NewItemsLore {
   private NewItemsLore() {
   }

   public static void register() {
      lore("arcoglebanoide", 5625845, l("Dispara flechas e impacta"), l(13134847, "hasta 3 enemigos", 14211288, "."));
      lore("ambar_con_insecto", 13134847, l("Combina ", 5625845, "Insecto", 14211288, " con ", 5625845, "Ámbar"), l("para conseguirlo."));
      lore(
         "bateria_de_dilitio",
         5625845,
         l("Importante fuente de energía"),
         l("creada a partir de ", 6021979, "Dilitio", 14211288, ","),
         l(5625845, "Bioxita", 14211288, " y Netherita.")
      );
      lore("capsuladepastillavacia", 5625845, l("Utilízala para crear diferentes"), l("tipos de Pastillas o Píldoras."));
      int var0 = 7310335;
      int var1 = 16769866;
      int var2 = 16738877;
      int var3 = 9079434;
      lore(
         "pildoradezombificacion",
         5625845,
         l(var0, "Antibiótico (03:00)"),
         l(),
         l("Consúmelo para ser inmune al"),
         l(13134847, "Veneno Primitivo", 14211288, " durante un"),
         l("tiempo."),
         l(),
         l(var1, "⚠ Atención:", 14211288, " El ", 13134847, "Veneno Primitivo"),
         l("no permite que te cures, por lo"),
         l("que recibirás daño hasta morir."),
         l("Puedes evitarlo con la ", 13134847, "Máscara"),
         l(13134847, "Anti-Esporas", 14211288, " o el ", 5625845, "Antídoto Primitivo", 14211288, "."),
         l(),
         l(var3, "El ", 13134847, "Veneno Primitivo", var3, " es aplicado"),
         l(var3, "por los ", var2, "Reptisaurios", var3, " y las Nubes"),
         l(var3, "Esporas de la ", var2, "Planta Tóxica", var3, ".")
      );
      lore(
         "bolsa_glebanoide",
         13134847,
         l("Utilízala para guardar objetos sin"),
         l("ocupar lugares de tu inventario."),
         l(),
         l("Tiene ", 16765503, "36 Slots", 14211288, ".")
      );
   }

   private static Component l(Object... var0) {
      MutableComponent var1 = Component.empty();
      int var2 = 14211288;

      for (Object var6 : var0) {
         if (var6 instanceof Integer var7) {
            var2 = var7;
         } else {
            var1.append(Component.literal((String)var6).withColor(var2));
         }
      }

      return var1;
   }

   private static void lore(String var0, int var1, Component... var2) {
      ItemLore.define(var0, var1, var2);
   }
}
