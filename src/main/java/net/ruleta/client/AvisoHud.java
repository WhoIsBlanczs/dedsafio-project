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

   static void render(GuiGraphicsExtractor ctx, Font tr) {
      if (titulo != null && !saliendo && System.nanoTime() - puesto > 10000000000L) {
         saliendo = true;
      }

      if (titulo != null) {
         float k = fundido.avanzar(!saliendo && !oculto && OpcionesCliente.aviso && !HudCliente.tapado(), !oculto && OpcionesCliente.aviso ? 1.5F : 0.25F);
         if (saliendo && fundido.apagado()) {
            limpiar();
         } else {
            int centro = ctx.guiWidth() / 2;
            boolean choca = 250 > centro - tr.width("00:00") / 2 - 4;
            float y0 = 10.0F + 8.0F * bajada.avanzar(choca && TemporizadorHud.activo());
            int alfa = Math.round(k * 255.0F);
            if (alfa >= 4) {
               int alto = Math.max(80, 21 + Math.round(lineas.size() * 11.5F * escala) + 6);
               int x = 18;
               int color = alfa << 24 | 16777215;
               PoseStack m = ctx.pose();
               m.pushPose();
               m.translate(0.0F, y0 + 11.0F, 2951.0F);
               ctx.fill(10, 0, 250, alto, Math.round(94.0F * k) << 24);
               m.translate(0.0F, 0.0F, 1.0F);
               tituloMetalico(ctx, tr, titulo, x, 6, alfa);
               m.pushPose();
               m.translate(x, 21.0F, 0.0F);
               m.scale(escala, escala, 1.0F);

               for (FormattedCharSequence linea : lineas) {
                  ctx.text(tr, linea, 0, 0, color);
                  m.translate(0.0F, 11.5F, 0.0F);
               }

               m.popPose();
               if (!OCULTAR.isUnbound()) {
                  Component pista = Component.literal("[Presiona ")
                     .append(OCULTAR.getTranslatedKeyMessage())
                     .append("] para ocultar aviso")
                     .withStyle(ChatFormatting.GRAY);
                  ctx.text(tr, pista, x, alto + 4, color);
               }

               m.popPose();
            }
         }
      }
   }

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

   private static void tituloMetalico(GuiGraphicsExtractor ctx, Font tr, Component t, int x, int y, int alfa) {
      int[] base = new int[]{-1};
      List<Object[]> trozos = new ArrayList<>();
      t.visit((estilox, textox) -> {
         trozos.add(new Object[]{estilox, textox});
         if (base[0] < 0 && !textox.isBlank() && textox.codePointAt(0) < 57344) {
            int deFuente = Mensajes.baseDeFuente(estilox.getFont());
            if (deFuente >= 0) {
               base[0] = deFuente;
            } else if (estilox.getColor() != null) {
               base[0] = estilox.getColor().getValue();
            }
         }

         return Optional.empty();
      }, Style.EMPTY);
      if (base[0] < 0) {
         ctx.text(tr, t, x, y, alfa << 24 | 16777215);
      } else {
         int claro = Mensajes.claro(base[0]);
         int oscuro = mezcla(claro, 0, 0.3F);
         float ancho = Math.max(1, tr.width(t));
         double s = System.nanoTime() / 1.0E9 % 4.4;
         float pasada = (float)(s < 2.2 ? s / 2.2 : 2.0 - s / 2.2);
         float centro = -0.2F + 1.4F * (pasada * pasada * (3.0F - 2.0F * pasada));
         int cx = x;

         for (Object[] tr0 : trozos) {
            Style estilo = (Style)tr0[0];
            String texto = (String)tr0[1];
            int k = 0;

            while (k < texto.length()) {
               int cp = texto.codePointAt(k);
               k += Character.charCount(cp);
               Component letra = Component.literal(new String(Character.toChars(cp)));
               MutableComponent var27;
               int col;
               if (cp >= 57344 && cp <= 63743) {
                  var27 = letra.copy().setStyle(estilo.withColor(16777215));
                  col = 16777215;
               } else {
                  float u = (cx - x) / ancho;
                  float onda = 0.5F + 0.5F * (float)Math.cos((u - centro) * Math.PI * 2.2);
                  int colx = mezcla(oscuro, claro, onda);
                  float brillo = (float)Math.exp(-Math.pow((u - centro) / 0.12F, 2.0));
                  col = mezcla(colx, 16774898, brillo * 0.8F);
                  var27 = letra.copy().setStyle(estilo.withColor(col));
               }

               ctx.text(tr, var27, cx, y, alfa << 24 | col);
               cx += tr.width(var27);
            }
         }
      }
   }

   private static int mezcla(int a, int b, float k) {
      k = Math.max(0.0F, Math.min(1.0F, k));
      int r = Math.round((a >> 16 & 0xFF) + ((b >> 16 & 0xFF) - (a >> 16 & 0xFF)) * k);
      int g = Math.round((a >> 8 & 0xFF) + ((b >> 8 & 0xFF) - (a >> 8 & 0xFF)) * k);
      int bl = Math.round((a & 0xFF) + ((b & 0xFF) - (a & 0xFF)) * k);
      return r << 16 | g << 8 | bl;
   }
}
