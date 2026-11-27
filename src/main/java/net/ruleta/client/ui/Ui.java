package net.ruleta.client.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public final class Ui {
   public static final int SUPERFICIE = 1119259;
   public static final int SUPERFICIE_2 = 1711656;
   public static final int SUPERFICIE_3 = 2369846;
   public static final int LINEA = 3028037;
   public static final int TEXTO = 15330549;
   public static final int TENUE = 9344683;
   public static final int ACENTO = 7113983;
   public static final int PELIGRO = 16735340;
   public static final int OK = 4906624;
   public static final int AVISO = 16762967;
   public static final Style FUENTE = Style.EMPTY;
   public static final Style FUENTE_NEGRITA = FUENTE.withBold(true);
   public static float alfa = 1.0F;

   private Ui() {
   }

   public static int c(int rgb, float a) {
      int al = Math.max(0, Math.min(255, Math.round(a * alfa * 255.0F)));
      return al << 24 | rgb & 16777215;
   }

   public static int c(int rgb) {
      return c(rgb, 1.0F);
   }

   public static int mezcla(int a, int b, float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      int r = Math.round((a >> 16 & 0xFF) * (1.0F - t) + (b >> 16 & 0xFF) * t);
      int g = Math.round((a >> 8 & 0xFF) * (1.0F - t) + (b >> 8 & 0xFF) * t);
      int bl = Math.round((a & 0xFF) * (1.0F - t) + (b & 0xFF) * t);
      return r << 16 | g << 8 | bl;
   }

   public static float ease(float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      return 1.0F - (1.0F - t) * (1.0F - t) * (1.0F - t);
   }

   public static void caja(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int argb) {
      if (w > 0 && h > 0 && argb >>> 24 != 0) {
         r = Math.max(0, Math.min(r, Math.min(w, h) / 2));
         if (r == 0) {
            g.fill(x, y, x + w, y + h, argb);
         } else {
            g.fill(x, y + r, x + w, y + h - r, argb);

            for (int i = 0; i < r; i++) {
               double dy = r - i - 0.5;
               int inset = r - (int)Math.round(Math.sqrt(Math.max(0.0, (double)r * r - dy * dy)));
               g.fill(x + inset, y + i, x + w - inset, y + i + 1, argb);
               g.fill(x + inset, y + h - 1 - i, x + w - inset, y + h - i, argb);
            }
         }
      }
   }

   public static void cajaBorde(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, int relleno, int borde) {
      caja(g, x, y, w, h, r, borde);
      caja(g, x + 1, y + 1, w - 2, h - 2, Math.max(0, r - 1), relleno);
   }

   public static void sombra(GuiGraphicsExtractor g, int x, int y, int w, int h, int r, float fuerza) {
      for (int i = 5; i >= 1; i--) {
         caja(g, x - i, y - i + 3, w + i * 2, h + i * 2, r + i, c(0, fuerza * 0.07F));
      }
   }

   public static void linea(GuiGraphicsExtractor g, int x, int y, int w, int argb) {
      g.fill(x, y, x + w, y + 1, argb);
   }

   public static Component t(String s) {
      return Component.literal(s).withStyle(FUENTE);
   }

   public static Component tb(String s) {
      return Component.literal(s).withStyle(FUENTE_NEGRITA);
   }

   public static int ancho(Font f, String s) {
      return f.width(t(s));
   }

   public static int anchoN(Font f, String s) {
      return f.width(tb(s));
   }

   public static void texto(GuiGraphicsExtractor g, Font f, String s, int x, int y, int rgb, float a) {
      int col = c(rgb, a);
      if (col >>> 24 >= 8 && !s.isEmpty()) {
         g.text(f, t(s), x, y, col, false);
      }
   }

   public static int oscuro(int rgb) {
      if ((rgb & 16777215) == 16777215) {
         return rgb;
      }

      int r = (int)((rgb >> 16 & 0xFF) * 0.82F);
      int gr = (int)((rgb >> 8 & 0xFF) * 0.82F);
      int b = (int)((rgb & 0xFF) * 0.82F);
      return r << 16 | gr << 8 | b;
   }

   public static void textoN(GuiGraphicsExtractor g, Font f, String s, int x, int y, int rgb, float a) {
      int col = c(oscuro(rgb), a);
      if (col >>> 24 >= 8 && !s.isEmpty()) {
         g.text(f, tb(s), x, y, col, false);
      }
   }

   public static void textoCentro(GuiGraphicsExtractor g, Font f, String s, int cx, int y, int rgb, float a, boolean negrita) {
      int w = negrita ? anchoN(f, s) : ancho(f, s);
      if (negrita) {
         textoN(g, f, s, cx - w / 2, y, rgb, a);
      } else {
         texto(g, f, s, cx - w / 2, y, rgb, a);
      }
   }

   public static void textoDerecha(GuiGraphicsExtractor g, Font f, String s, int xDerecha, int y, int rgb, float a) {
      texto(g, f, s, xDerecha - ancho(f, s), y, rgb, a);
   }

   public static String recortar(Font f, String s, int ancho) {
      if (ancho(f, s) <= ancho) {
         return s;
      }

      String r = s;

      while (r.length() > 1 && ancho(f, r + "…") > ancho) {
         r = r.substring(0, r.length() - 1);
      }

      return r + "…";
   }

   public static List<String> lineas(Font f, String s, int ancho, int maxLineas) {
      List<String> r = new ArrayList<>();
      StringBuilder actual = new StringBuilder();

      for (String palabra : s.split(" ")) {
         String prueba = actual.length() == 0 ? palabra : actual + " " + palabra;
         if (ancho(f, prueba) > ancho && actual.length() > 0) {
            r.add(actual.toString());
            actual = new StringBuilder(palabra);
         } else {
            actual = new StringBuilder(prueba);
         }
      }

      if (actual.length() > 0) {
         r.add(actual.toString());
      }

      if (r.size() > maxLineas) {
         r = new ArrayList<>(r.subList(0, maxLineas));
         r.set(maxLineas - 1, recortar(f, r.get(maxLineas - 1) + "…", ancho));
      }

      return r;
   }

   public static List<String> lineasN(Font f, String s, int ancho, int maxLineas) {
      List<String> r = new ArrayList<>();
      StringBuilder actual = new StringBuilder();

      for (String palabra : s.split(" ")) {
         String prueba = actual.length() == 0 ? palabra : actual + " " + palabra;
         if (anchoN(f, prueba) > ancho && actual.length() > 0) {
            r.add(actual.toString());
            actual = new StringBuilder(palabra);
         } else {
            actual = new StringBuilder(prueba);
         }
      }

      if (actual.length() > 0) {
         r.add(actual.toString());
      }

      if (r.size() > maxLineas) {
         r = new ArrayList<>(r.subList(0, maxLineas));
         r.set(maxLineas - 1, recortarN(f, r.get(maxLineas - 1) + "…", ancho));
      }

      return r;
   }

   public static String recortarN(Font f, String s, int ancho) {
      if (anchoN(f, s) <= ancho) {
         return s;
      }

      String r = s;

      while (r.length() > 1 && anchoN(f, r + "…") > ancho) {
         r = r.substring(0, r.length() - 1);
      }

      return r + "…";
   }

   public static void titulo3D(GuiGraphicsExtractor g, Font f, String s, int x, int y, float escala, int cara, int caraClara, int lado, int contorno) { }

   public static String mmss(int segundos) {
      int h = segundos / 3600;
      int m = segundos / 60 % 60;
      int s = segundos % 60;
      return h > 0 ? String.format("%d:%02d:%02d", h, m, s) : String.format("%02d:%02d", m, s);
   }

   public static boolean dentro(double mx, double my, int x, int y, int w, int h) {
      return mx >= x && mx < x + w && my >= y && my < y + h;
   }
}
