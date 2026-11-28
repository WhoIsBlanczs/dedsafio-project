package com.novastudio.dedsafio4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.core.registries.BuiltInRegistries;

@Environment(EnvType.CLIENT)
public final class ItemLore {
   private static final int BODY = 14211288;
   private static final int WHITE = 16777215;
   private static final int GRAY = 11184810;
   private static final int GREEN = 5635925;
   private static final int LIME = 11206485;
   private static final int AQUA = 5636095;
   private static final int TURQUOISE = 638655;
   private static final int PALE_LIME = 14548821;
   private static final int STEEL = 10137788;
   private static final int YELLOW = 16777045;
   private static final int GOLD = 16755200;
   private static final int RED = 16733525;
   private static final int BLUE = 5592575;
   private static final int PURPLE = 16733695;
   private static final int MOSS = 8960853;
   private static final int DARK_GREEN = 43520;
   private static final Style COIN_FONT = Style.EMPTY.withFont(new net.minecraft.network.chat.FontDescription.Resource(Identifier.fromNamespaceAndPath("nexo", "dedita_coin"))).withColor(16777215);
   private static final Map<String, ItemLore.Lore> LORE = new HashMap<>();

   private ItemLore() {
   }

   public static void register() {
      define(
         "dedita",
         5592575,
         l(),
         l(14211288, "La divisa principal de DEDSAFIO."),
         l(14211288, "Utilízala para comprar y vender"),
         l(14211288, "objetos en el ", 5635925, "Mercado", 14211288, " o en las"),
         l(11206485, "Subastas", 14211288, " de la Montaña."),
         l(),
         l(14211288, "Puedes obtenerla haciendo Misiones"),
         l(14211288, "o comerciando el Dilitio entregado"),
         l(14211288, "por los Aldeanos Phora."),
         l(),
         price(16755200, "Valor Monetario: ", "1", null)
      );
      define(
         "manuscrito_hermandad",
         5635925,
         price(16755200, "Valor de compra: ", "1", " por 1"),
         l(),
         l(14211288, "Utilízalo para crear tu propia"),
         l(14211288, "Hermandad. Las Hermandades"),
         l(14211288, "cuentan con:"),
         l(),
         l(5635925, "✦ Banco Compartido"),
         l(5635925, "✦ Capas Personalizables"),
         l(5635925, "✦ Waypoints Propios"),
         l(5635925, "✦ Tablón de Anuncios"),
         l(5635925, "✦ Entre otras características."),
         l(),
         l(14211288, "Requiere que al menos ", 16755200, "3 Miembros"),
         l(14211288, "firmen el Manuscrito para fundar"),
         l(14211288, "una Hermandad. Pulsa ", 16777045, "H", 14211288, " para acceder"),
         l(14211288, "a la Interfaz de Hermandades."),
         l(),
         l(14211288, "Una vez hayan firmado, el ", 5636095, "Maestro"),
         l(5636095, "de la Hermandad", 14211288, " debe utilizar"),
         l(14211288, "el comando ", 16777045, "/guild found", 14211288, " para"),
         l(14211288, "crearla."),
         l(),
         l(16777045, "⚠ Atención: Solamente es necesario UN"),
         l(16777045, "Manuscrito para crear una Hermandad."),
         l(),
         l(11184810, "Puedes adquirirlo en las ", 8960853, "Nutrias"),
         l(8960853, "de Piedra de la Montaña", 11184810, ".")
      );
      define(
         "dilitio",
         5635925,
         price(16755200, "Valor de venta: ", "1", " por 4"),
         price(16777045, " - Por unidad: ", "0.50", null),
         l(),
         l(14211288, "Utiliza un ", 16777045, "Pico de Diamante"),
         l(14211288, "o superior sobre un ", 5635925, "Bloque"),
         l(5635925, "de Dilitio", 14211288, " para extraerlo.")
      );
      define(
         "dedita_orange",
         16755200,
         price(16755200, "Valor de compra: ", "1", " por 1"),
         l(),
         l(14211288, "Utilízala en las Máquinas Tragadeditas"),
         l(14211288, "del ", 5635925, "Casino de la Montaña", 14211288, " para intentar"),
         l(14211288, "ganar valiosas recompensas.")
      );
      define(
         "dedita_cyan",
         638655,
         l(),
         l(14211288, "Canjéalo en el ", 638655, "Pedestal de la"),
         l(638655, "Montaña", 14211288, " para reclamar"),
         l(14211288, "la ", 638655, "Misión Principal", 14211288, " del día.")
      );
      define("mejora_floral", 14548821);
      define(
         "totem_eon",
         16755200,
         l(),
         l(14211288, "Popea aunque esté en"),
         l(14211288, "tu inventario."),
         l(),
         l(14211288, "Otorga ", 16733695, "Resistencia (Nivel Máx. 1s)"),
         l(14211288, "al popear, evitando el InstaKill"),
         l(14211288, "si dos ataques se ejecutan"),
         l(14211288, "demasiado rápido.")
      );
      define(
         "tenedor_revivir",
         10137788,
         l(),
         l(14211288, "Permite revivir ", 16755200, "SIN", 14211288, " utilizar un"),
         l(5636095, "Alma", 14211288, " en la Fogata de la Montaña."),
         l(),
         l(16777045, "⚠ Atención: Sólo pueden ser utilizados"),
         l(16777045, "el MISMO día de su obtención.")
      );
      seed(
         "semilla_huevo_de_eburia",
         l(14211288, "un ", 5636095, "Huevo Eburia", 14211288, " en la ", 16755200, "Mano"),
         l(16755200, "Secundaria", 14211288, " para extraerla.")
      );
      seed(
         "semilla_baya_uvina",
         l(14211288, "la ", 5636095, "Baya Uvina", 14211288, " en la ", 16755200, "Mano"),
         l(16755200, "Secundaria", 14211288, " para extraerla.")
      );
      seed(
         "semilla_arandano_nocturino",
         l(14211288, "un ", 5636095, "Arándano Nocturino", 14211288, " en la"),
         l(16755200, "Mano Secundaria", 14211288, " para extraerla.")
      );
      seed(
         "semilla_fruta_solaria",
         l(14211288, "la ", 5636095, "Fruta Solaria", 14211288, " en la ", 16755200, "Mano"),
         l(16755200, "Secundaria", 14211288, " para extraerla.")
      );
      define("corazon_rojo", 5636095, l(), l(14211288, "Consúmelo para obtener"), l(16755200, "1", 16733695, " Corazón de Salud Extra."));
      define(
         "corazon_dorado",
         5636095,
         l(5592575, "Absorción V (03:00)"),
         l(),
         l(16733695, "Al aplicarse:"),
         l(5592575, "Absorción máxima: +20"),
         l(),
         l(14211288, "Consúmelo para conseguir ", 16733695, "una"),
         l(16733695, "Barra Completa de Absorción.")
      );
      define(
         "corazon_limbo",
         5636095,
         l(),
         l(14211288, "Consúmelo para ", 16777045, "reestablecer"),
         l(16733695, "tu Barra de Salud al máximo (20)."),
         l(),
         l(16777045, "⚠ Atención: ", 16733525, "Si tienes Corazones"),
         l(16733525, "Extra, los PERDERÁS al consumirlo.")
      );
      define(
         "pelos_de_eburia",
         5636095,
         l(),
         l(14211288, "Utiliza unas ", 16755200, "Tijeras", 14211288, " mientras"),
         l(14211288, "tienes el ", 5636095, "Huevo Eburia", 14211288, " en la ", 16755200, "Mano"),
         l(16755200, "Secundaria", 14211288, " para conseguirlos.")
      );
      define("tronco_roble_quiu_dino", 5636095);
      define("tronco_abedul_dino", 5636095);
      define("cuchara_revivir", 16755200, l(), l(14211288, "Permite revivir utilizando un ", 5636095, "Alma"), l(14211288, "en la Fogata de la Montaña."));
      define(
         "caca_de_dino",
         5636095,
         l(),
         l(14211288, "Dale de comer la ", 5636095, "Fruta Solaria", 14211288, " a un"),
         l(16733525, "Plumosaurio", 14211288, " para tener la ", 16755200, "probabilidad"),
         l(14211288, "de que suelte ", 5636095, "Excremento", 14211288, "."),
         l(),
         l(14211288, "Utiliza un ", 16755200, "Pico", 14211288, " para extraerlo.")
      );
      define(
         "block_aldeano",
         5635925,
         l(),
         l(14211288, "Trabajador Phora, llegado en"),
         l(14211288, "los Cubos. Utilízalo para realizar"),
         l(14211288, "diferentes intercambios."),
         l(),
         l(14211288, "Requiere una ", 5636095, "Llave de Cobre"),
         l(14211288, "para encenderlo por primera vez."),
         l(),
         l(14211288, "Puedes ", 16777045, "reiniciarlo", 14211288, " para obtener"),
         l(14211288, "tradeos distintos."),
         l(),
         l(16777045, "⚠ Atención:", 14211288, " Si lo enciendes, ", 16733525, "NO"),
         l(14211288, "podrás apagarlo. Debes reiniciarlo"),
         l(14211288, "si quieres romperlo y moverlo.")
      );
      define(
         "martillo",
         16733695,
         l(),
         l(14211288, "Repara la durabilidad de"),
         l(14211288, "los objetos a cambio de ", 5635925, "XP", 14211288, "."),
         l(14211288, "Tiene dos formas de uso:"),
         l(),
         l(14211288, "⛏ Haz ", 16777045, "Click Derecho", 14211288, " sobre alguien,"),
         l(14211288, "y repararás poco a poco"),
         l(14211288, "las piezas de su armadura."),
         l(),
         l(14211288, "⛏ Haz ", 16777045, "Click Derecho", 14211288, " para"),
         l(14211288, "reparar lo que tengas"),
         l(14211288, "en la Mano Secundaria."),
         l(),
         l(11184810, "En la mano principal:"),
         l(43520, " Daño por golpe: 1"),
         l(43520, " Velocidad de ataque: 1")
      );
      define(
         "racimo_de_dilitio",
         5636095,
         l(),
         l(14211288, "Combina 9 de ", 5635925, "Dilitio", 14211288, " en una ", 16777045, "Mesa"),
         l(16777045, "de Crafteo", 14211288, " para conseguirlo.")
      );
      define(
         "totem_hierro_golem",
         16777045,
         l(),
         l(14211288, "Al utilizarlo en la ", 16755200, "Mano"),
         l(16755200, "Secundaria", 14211288, " obtienes ", 16733695, "dos"),
         l(14211288, "corazones extra."),
         l(),
         l(11184810, "En la mano secundaria:"),
         l(5592575, "Salud máxima: +4")
      );
      define(
         "dedita_green",
         5635925,
         l(),
         l(14211288, "La divisa principal de DEDSAFIO."),
         l(14211288, "Utilízala para comprar y vender"),
         l(14211288, "objetos en el ", 5635925, "Mercado", 14211288, " o en las"),
         l(11206485, "Subastas", 14211288, " de la Montaña."),
         l(),
         l(14211288, "Puedes obtenerla haciendo Misiones"),
         l(14211288, "o comerciando el Dilitio entregado"),
         l(14211288, "por los Aldeanos Phora."),
         l(),
         price(16755200, "Valor Monetario: ", "100", null)
      );
      define(
         "bioxita",
         5635925,
         l(),
         l(14211288, "Utiliza un ", 16755200, "Pico de Diamante"),
         l(14211288, "o superior sobre un ", 5635925, "Mineral"),
         l(5635925, "de Bioxita", 14211288, " para extraerla.")
      );
      define(
         "manzana_ambar",
         5636095,
         l(5592575, "Regeneración II (00:05)"),
         l(5592575, "Absorción (02:00)"),
         l(),
         l(16733695, "Al aplicarse:"),
         l(5592575, "Absorción máxima: +4"),
         l(),
         l(14211288, "Combina ", 5636095, "Manzana Dorada", 14211288, " con"),
         l(16733695, "Mineral de Ámbar con Insecto"),
         l(14211288, "para conseguirla.")
      );
      define(
         "tela_primitiva",
         5636095,
         l(),
         l(14211288, "Tejido de extrema resistencia,"),
         l(14211288, "obtenido a partir de los ", 5636095, "Huevos"),
         l(5636095, "Eburia", 14211288, ".")
      );
      define(
         "chip_phora",
         5636095,
         l(),
         l(14211288, "Utilízalo para crear"),
         l(14211288, "Cofres Protegidos."),
         l(),
         l(14211288, "Se encuentra en los"),
         l(14211288, "Cubos Phora.")
      );
      define(
         "cuerda_resistente",
         5636095,
         l(),
         l(14211288, "Fibra de alta resistencia y"),
         l(14211288, "durabilidad extrema. Muy útil"),
         l(14211288, "para diferentes creaciones.")
      );
      Object[] var0 = new Object[]{l(), l(14211288, "Utilízala para guardar objetos sin"), l(14211288, "ocupar lugares de tu inventario.")};
      define("bolsa_primitiva", 16733695, var0);

      for (String var4 : new String[]{
         "azul",
         "naranja",
         "gris_claro",
         "morado",
         "marron",
         "rojo",
         "rosa",
         "lima",
         "azul_claro",
         "amarillo",
         "magenta",
         "cian",
         "blanco",
         "gris",
         "negro",
         "verde"
      }) {
         define("bolsa_primitiva_" + var4, 16733695, var0);
      }

      ItemTooltipCallback.EVENT.register((ItemTooltipCallback)(var0x, var1, var2, var3) -> {
         Identifier var4x = BuiltInRegistries.ITEM.getKey(var0x.getItem());
         ItemLore.Lore var5 = var4x.getNamespace().equals("nexo") ? LORE.get(var4x.getPath()) : null;
         if (var5 != null && !var3.isEmpty()) {
            ArrayList var6 = new ArrayList();

            for (int var7 = 1; var7 < var3.size(); var7++) {
               if (isAdvancedFooter((Component)var3.get(var7), var4x)) {
                  var6.add((Component)var3.get(var7));
               }
            }

            MutableComponent var8 = ((Component)var3.get(0)).copy().withColor(var5.nameColor());
            var3.clear();
            var3.add(var8);
            var3.addAll(var5.lines());
            var3.addAll(var6);
         }
      });
   }

   private static boolean isAdvancedFooter(Component var0, Identifier var1) {
      return var0.getContents() instanceof TranslatableContents var2
         ? var2.getKey().equals("item.components") || var2.getKey().equals("item.durability")
         : var0.getString().equals(var1.toString());
   }

   private static void seed(String var0, Component var1, Component var2) {
      define(
         var0,
         5636095,
         l(),
         l(14211288, "Puedes plantarla en ", 5636095, "Troncos"),
         l(5636095, "de Abedúl", 14211288, " y ", 5636095, "Roble de Quiu"),
         l(14211288, "para hacerla crecer."),
         l(),
         l(16777045, "⚠ Atención:", 14211288, " Al alcanzar su punto"),
         l(14211288, "máximo de crecimiento debes"),
         l(14211288, "recogerlo lo antes posible."),
         l(14211288, "Después de un tiempo se pudrirá"),
         l(14211288, "si no lo cosechas."),
         l(),
         l(14211288, "Utiliza un ", 16755200, "Hacha", 14211288, " mientras tienes"),
         var1,
         var2
      );
   }

   static void define(String var0, int var1, Object... var2) {
      ArrayList var3 = new ArrayList();

      for (Object var7 : var2) {
         var3.add((Component)var7);
      }

      LORE.put(var0, new ItemLore.Lore(var1, List.copyOf(var3)));
   }

   private static Component l(Object... var0) {
      MutableComponent var1 = Component.literal("");

      for (byte var2 = 0; var2 < var0.length; var2 = (byte)(var2 + 2)) {
         var1.append(Component.literal((String)var0[var2 + 1]).withColor((Integer)var0[var2]));
      }

      return var1;
   }

   private static Component price(int var0, String var1, String var2, String var3) {
      MutableComponent var4 = Component.literal("");
      var4.append(Component.literal(var1).withColor(var0));
      var4.append(Component.literal(var2 + " ").withColor(16777215));
      var4.append(Component.literal("\ue000").setStyle(COIN_FONT));
      if (var3 != null) {
         var4.append(Component.literal(var3).withColor(var0));
      }

      return var4;
   }

   @Environment(EnvType.CLIENT)
   private record Lore(int nameColor, List<Component> lines) {
   }
}
