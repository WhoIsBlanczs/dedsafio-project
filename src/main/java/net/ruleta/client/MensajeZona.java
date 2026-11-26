package net.ruleta.client;

import Nonenet.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.ruleta.client.ui.Ui;

public final class MensajeZona {
   private static String texto = "";
   private static long hasta;
   private static long desde;

   private MensajeZona() {
   }

   public static void mostrar(String t, int milisegundos) {
      long ahora = Util.getMillis();
      if (!t.equals(texto) || ahora > hasta) {
         desde = ahora;
      }

      texto = t;
      hasta = ahora + Math.max(300, milisegundos);
   }

   public static void dibujar(GuiGraphicsExtractor g, int ancho, int alto) {
      long ahora = Util.getMillis();
      if (!texto.isEmpty() && ahora < hasta) {
         Minecraft mc = Minecraft.getInstance();
         Font f = mc.font;
         float entra = Ui.ease((float)(ahora - desde) / 160.0F);
         float sale = Ui.ease((float)(hasta - ahora) / 300.0F);
         float a = Math.min(entra, sale);
         int w = Ui.anchoN(f, texto) + 26;
         int h = 18;
         int x = (ancho - w) / 2;
         int y = alto - 39 - 12 - h - Math.round((1.0F - entra) * 6.0F);
         Player p = mc.player;
         if (p != null && p.getArmorValue() > 0) {
            y -= 10;
         }

         float guardado = Ui.alfa;
         Ui.alfa = a;
         Ui.sombra(g, x, y, w, h, 5, 0.8F);
         Ui.cajaBorde(g, x, y, w, h, 5, Ui.c(1119259, 0.92F), Ui.c(16735340, 0.9F));
         Ui.caja(g, x + 6, y + 6, 6, 6, 3, Ui.c(16735340));
         Ui.textoN(g, f, texto, x + 18, y + 5, 15330549, 1.0F);
         Ui.alfa = guardado;
      }
   }
}
