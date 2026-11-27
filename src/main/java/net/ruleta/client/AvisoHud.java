package net.ruleta.client;

import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.ruleta.Mensajes;

public final class AvisoHud {
   public static final KeyMapping OCULTAR = new KeyMapping("key.ruleta.ocultar_aviso", Type.KEYSYM, 79, "key.categories.ruleta");
   private static final int X = 10;
   private static final int Y = 10;
   private static final int ANCHO = 240;
   private static final int ALTO = 80;
   private static final int MARGEN = 8;
   private static final int TITULO = 6;
   private static final int CUERPO = 21;
   private static final int PISTA = 4;
   private static final float LINEA = 11.5F;
   private static final float[] ESCALAS = new float[]{1.0F, 0.8F, 0.7F, 0.6F, 0.5F};
   private static final int[] LINEAS = new int[]{3, 4, 4, 4, 4};
   private static final int FONDO = 94;
   private static final float COORDS = 11.0F;
   private static final float OCULTAR_SEG = 0.25F;
   private static Component titulo;
   private static List<FormattedCharSequence> lineas = List.of();
   private static float escala = 1.0F;
   private static boolean saliendo;
   private static boolean oculto;
   private static final long DURA_NS = 10000000000L;
   private static long puesto;
   private static final Fundido fundido = new Fundido(0.2F, 1.5F);
   private static final Fundido bajada = new Fundido(0.3F, 0.3F);

   private AvisoHud() {
   }

   public static void poner(Component tituloNuevo, Component aviso) {
      titulo = tituloNuevo;
      Font tr = Minecraft.getInstance().font;

      for (int i = 0; i < ESCALAS.length; i++) {
         escala = ESCALAS[i];
         lineas = tr.split(aviso, (int)(224.0F / escala));
         if (lineas.size() <= LINEAS[i]) {
            break;
         }
      }

      saliendo = false;
      oculto = false;
      puesto = System.nanoTime();
   }

   public static boolean visible() {
      return titulo != null && !saliendo;
   }

   public static void quitar() {
      if (titulo != null) {
         saliendo = true;
      }
   }

   static void limpiar() {
      titulo = null;
      saliendo = false;
      fundido.cero();
   }

   public static void alternar() {
      if (titulo != null && !saliendo) {
         oculto = !oculto;
      }
   }

   static void render(GuiGraphicsExtractor ctx, Font tr) { }

   static void coordenadas(GuiGraphicsExtractor ctx, Font tr) {
      coordenadas(ctx, tr, 255);
   }

   private static void coordenadas(GuiGraphicsExtractor ctx, Font tr, int alfa) {
      LocalPlayer p = Minecraft.getInstance().player;
      if (p != null && OpcionesCliente.coordenadas) {
         String txt = String.format(Locale.ROOT, "X: %.2f, Y: %.2f, Z: %.2f", p.getX(), p.getY(), p.getZ());
         ctx.text(tr, txt, 10, 10, alfa << 24 | 16777215, true);
      }
   }

   private static void tituloMetalico(GuiGraphicsExtractor ctx, Font tr, Component t, int x, int y, int alfa) { }

   private static int mezcla(int a, int b, float k) {
      k = Math.max(0.0F, Math.min(1.0F, k));
      int r = Math.round((a >> 16 & 0xFF) + ((b >> 16 & 0xFF) - (a >> 16 & 0xFF)) * k);
      int g = Math.round((a >> 8 & 0xFF) + ((b >> 8 & 0xFF) - (a >> 8 & 0xFF)) * k);
      int bl = Math.round((a & 0xFF) + ((b & 0xFF) - (a & 0xFF)) * k);
      return r << 16 | g << 8 | bl;
   }
}
