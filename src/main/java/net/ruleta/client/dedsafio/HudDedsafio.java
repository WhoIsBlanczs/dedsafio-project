package net.ruleta.client.dedsafio;

import com.mojang.blaze3d.vertex.Object;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Object;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public final class HudDedsafio {
   private static final int IDLE = 0;
   private static final int CERRANDO = 1;
   private static final int NEGRO = 2;
   private static final int ABRIR_PEND = 3;
   private static final int ABRIENDO = 4;
   private static int estado = 0;
   private static long t0;
   private static long tNegro;
   private static long abrirAutoEn = -1L;
   private static Runnable alCerrar;
   private static final float[] CIERRE_T = new float[]{0.0F, 0.2F, 0.4F, 0.6F, 0.8F, 1.0F, 1.2F, 1.4F, 1.6F, 1.8F, 2.0F, 2.2F};
   private static final float[] CIERRE_A = new float[]{0.9F, 0.72F, 0.28F, 0.0F, 0.0F, 0.55F, 0.55F, 0.5F, 0.42F, 0.12F, 0.0F, 0.0F};
   private static final float[] ABRE_T = new float[]{
      0.0F,
      0.1F,
      0.2F,
      0.3F,
      0.4F,
      0.5F,
      0.6F,
      0.7F,
      0.8F,
      0.9F,
      1.0F,
      1.1F,
      1.2F,
      1.3F,
      1.4F,
      1.5F,
      1.6F,
      1.7F,
      1.8F,
      1.9F,
      2.0F,
      2.1F,
      2.2F,
      2.3F,
      2.4F,
      2.5F,
      2.6F,
      2.7F,
      2.8F,
      2.9F,
      3.0F
   };
   private static final float[] ABRE_A = new float[]{
      0.03F,
      0.35F,
      0.4F,
      0.49F,
      0.57F,
      0.62F,
      0.61F,
      0.49F,
      0.36F,
      0.01F,
      0.01F,
      0.36F,
      0.44F,
      0.58F,
      0.74F,
      0.88F,
      0.95F,
      0.93F,
      0.73F,
      0.51F,
      0.5F,
      0.67F,
      0.89F,
      0.97F,
      0.97F,
      0.9F,
      0.81F,
      0.93F,
      1.0F,
      1.0F,
      1.0F
   };
   private static final float[] ABRE_B = new float[]{
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.12F,
      0.2F,
      0.25F,
      0.25F,
      0.18F,
      0.1F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F,
      0.0F
   };
   private static final float CIERRE_FIN = 2.2F;
   private static final float ABRE_FIN = 3.0F;

   private HudDedsafio() {
   }

   public static boolean ocupado() {
      return estado != 0;
   }

   public static void cerrar(Runnable alTerminar) {
      estado = 1;
      t0 = System.nanoTime();
      alCerrar = alTerminar;
      abrirAutoEn = -1L;
   }

   public static void abrirSiNoCambia(float segundos) {
      abrirAutoEn = System.nanoTime() + (long)(segundos * 1.0E9F);
   }

   public static void llegada() {
      if (estado != 4) {
         estado = 3;
         abrirAutoEn = -1L;
      }
   }

   private static float suave(float x) {
      x = Math.max(0.0F, Math.min(1.0F, x));
      return x * x * (3.0F - 2.0F * x);
   }

   private static float curva(float[] ts, float[] vs, float t) {
      if (t <= ts[0]) {
         return vs[0];
      }

      for (int i = 1; i < ts.length; i++) {
         if (t < ts[i]) {
            float k = (t - ts[i - 1]) / (ts[i] - ts[i - 1]);
            return vs[i - 1] + (vs[i] - vs[i - 1]) * k;
         }
      }

      return vs[vs.length - 1];
   }

   public static void dibujar(GuiGraphicsExtractor g) {
      Minecraft mc = Minecraft.getInstance();
      int w = g.guiWidth();
      int h = g.guiHeight();
      subtitulos(g, mc, w, h);
      ojo(g, mc, w, h);
   }

   private static void ojo(GuiGraphicsExtractor g, Minecraft mc, int w, int h) {
      long ahora = System.nanoTime();
      if (estado == 1) {
         float t = (float)(ahora - t0) / 1.0E9F;
         if (!(t >= 2.2F)) {
            mascara(g, w, h, curva(CIERRE_T, CIERRE_A, t), 0.0F);
            return;
         }

         Runnable r = alCerrar;
         alCerrar = null;
         estado = 2;
         tNegro = ahora;
         if (r != null) {
            r.run();
         }
      }

      if (estado != 2) {
         if (estado == 3) {
            mascara(g, w, h, 0.0F, 0.0F);
            if (mc.screen == null) {
               estado = 4;
               t0 = ahora;
            }
         } else {
            if (estado == 4) {
               float t = (float)(ahora - t0) / 1.0E9F;
               if (t >= 3.0F) {
                  estado = 0;
                  return;
               }

               mascara(g, w, h, curva(ABRE_T, ABRE_A, t), curva(ABRE_T, ABRE_B, t));
            }
         }
      } else {
         mascara(g, w, h, 0.0F, 0.0F);
         if (abrirAutoEn > 0L && ahora >= abrirAutoEn || ahora - tNegro > 20000000000L) {
            estado = 3;
            abrirAutoEn = -1L;
         }
      }
   }

   public static void pantallaCarga(GuiGraphicsExtractor g) {
      if (estado == 2 || estado == 3) {
         Minecraft mc = Minecraft.getInstance();
         int w = g.guiWidth();
         int h = g.guiHeight();
         g.fill(0, 0, w, h, -16777216);
         g.centeredText(mc.font, Component.translatable("multiplayer.downloadingTerrain"), w / 2, h / 2 - 50, 16777215);
      }
   }

   private static void mascara(GuiGraphicsExtractor g, int w, int h, float aper, float blanco) { }

   private static void quad(Object bb, Matrix4f m, float x0, float y0, float x1, float y1, float aIzq, float aDer) {
      if (!(x1 - x0 < 0.01F)) {
         bb.addVertex(m, x0, y0, 0.0F).setColor(0.0F, 0.0F, 0.0F, aIzq);
         bb.addVertex(m, x0, y1, 0.0F).setColor(0.0F, 0.0F, 0.0F, aIzq);
         bb.addVertex(m, x1, y1, 0.0F).setColor(0.0F, 0.0F, 0.0F, aDer);
         bb.addVertex(m, x1, y0, 0.0F).setColor(0.0F, 0.0F, 0.0F, aDer);
      }
   }

   private static void subtitulos(GuiGraphicsExtractor g, Minecraft mc, int w, int h) {
      String texto = EscenaDedsafio.subtitulo();
      if (texto != null) {
         int maxAncho = Math.max(120, (int)(w * 0.36F));
         List<FormattedCharSequence> lineas = new ArrayList<>();

         for (String parte : texto.split("\n")) {
            lineas.addAll(mc.font.split(Component.literal(parte), maxAncho));
         }

         int ancho = 0;

         for (FormattedCharSequence l : lineas) {
            ancho = Math.max(ancho, mc.font.width(l));
         }

         int alto = lineas.size() * 10;
         int x0 = (w - ancho) / 2 - 4;
         int y0 = (int)(h * 0.7F);
         float desv = Math.min(1.0F, EscenaDedsafio.subtituloRestante() / 0.5F);
         int aFondo = (int)(144.0F * desv);
         int aTexto = Math.max(4, (int)(255.0F * desv));
         g.fill(x0, y0 - 3, x0 + ancho + 8, y0 + alto + 2, aFondo << 24);
         int y = y0;

         for (FormattedCharSequence l : lineas) {
            g.text(mc.font, l, (w - mc.font.width(l)) / 2, y, aTexto << 24 | 16777215, true);
            y += 10;
         }
      }
   }
}
