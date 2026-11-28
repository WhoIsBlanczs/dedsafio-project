package com.ghostsafio.mitomod.client;

import com.ghostsafio.mitomod.NaveEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class NaveHud {
   private static final int CYAN = -7935745;
   private static final int WHITE = -1508609;
   private static final int DIM = -2004424449;
   private static final int WARN = -34182;
   private static final int YELLOW = -11413;
   public static final String[] PHASES = new String[]{
      "DESPEGUE", "TRÁNSITO ATMOSFÉRICO", "SALIDA DE ATMÓSFERA", "SALTO TEMPORAL", "ESTABILIZACIÓN", "APROXIMACIÓN", "ATERRIZAJE"
   };
   private static double smoothSpeed = 0.0;

   public static void render(GuiGraphicsExtractor g, DeltaTracker tracker) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null) {
         int w = g.guiWidth();
         int h = g.guiHeight();
         float haze = SpaceClient.cloudHaze();
         if (haze > 0.01F) {
            g.fill(0, 0, w, h, Math.min(255, (int)(haze * 150.0F)) << 24 | 10466521);
         }

         float flash = SpaceClient.transitionFlash();
         if (flash > 0.01F) {
            g.fill(0, 0, w, h, Math.min(255, (int)(flash * 255.0F)) << 24 | 14479103);
         }

         if (mc.player.getVehicle() instanceof NaveEntity nave && true) {
            OrbitFx.renderHud(g, tracker, nave, w, h);
            Font font = mc.font;
            int cx = w / 2;
            int phase = nave.getPhase();
            double alt = nave.getY();
            boolean warning = phase == 4 || phase == 5;
            int valueColor = warning ? -34182 : -1508609;
            double dx = nave.getX() - nave.xo;
            double dy = nave.getY() - nave.yo;
            double dz = nave.getZ() - nave.zo;
            double speed = Math.sqrt(dx * dx + dy * dy + dz * dz) * 20.0;
            smoothSpeed = smoothSpeed + (speed - smoothSpeed) * 0.25;
            String arrow = dy > 0.05 ? " ↑" : (dy < -0.05 ? " ↓" : "");
            textCenter(g, font, "ALTITUD / Y", cx, 8.0F, -2004424449, 0.8F);
            textCenter(g, font, Math.max(0L, Math.round(alt)) + " m", cx, 19.0F, valueColor, 2.0F);
            int rw = 260;
            int x0 = cx - rw / 2;
            int ry = 62;
            g.fill(x0, ry, x0 + rw, ry + 1, -2004424449);

            for (int m = 0; m <= 1200; m += 50) {
               int x = x0 + m * rw / 1200;
               boolean major = m % 300 == 0;
               g.fill(x, ry - (major ? 5 : 3), x + 1, ry, -2004424449);
               if (major) {
                  textCenter(g, font, String.valueOf(m), x, ry - 15, -2004424449, 0.75F);
               }
            }

            int mx = x0 + (int)(Mth.clamp(alt, 0.0, 1200.0) * rw / 1200.0);
            int markColor = warning ? -34182 : -7935745;
            g.fill(mx, ry + 3, mx + 1, ry + 4, markColor);
            g.fill(mx - 1, ry + 4, mx + 2, ry + 5, markColor);
            g.fill(mx - 2, ry + 5, mx + 3, ry + 6, markColor);
            int vx = cx + 190;
            textCenter(g, font, "VELOCIDAD", vx, 8.0F, -2004424449, 0.8F);
            textCenter(g, font, String.format(Locale.ROOT, "%04.1f", smoothSpeed), vx, 19.0F, valueColor, 2.0F);
            textCenter(g, font, "m/s" + arrow, vx, 38.0F, -2004424449, 0.8F);
            int right = w - 14;
            int y0 = h / 2 - 62;
            textRight(g, font, "RUTA DE VUELO", right, y0, -1508609, 0.9F);
            g.fill(right - 120, y0 + 12, right, y0 + 13, -2004424449);
            textRight(g, font, "FASE " + phase + "/" + PHASES.length, right, y0 + 18, -2004424449, 0.8F);

            for (int i = 0; i < PHASES.length; i++) {
               int number = i + 1;
               int y = y0 + 34 + i * 14;
               String mark;
               int color;
               if (number < phase) {
                  mark = " ✔";
                  color = -7935745;
               } else if (number == phase) {
                  mark = " ◉";
                  color = -1508609;
               } else {
                  mark = " ○";
                  color = -2004424449;
               }

               textRight(g, font, PHASES[i] + mark, right, y, color, 0.85F);
               if (number == phase) {
                  textRight(g, font, "◀", right + 11, y, -11413, 0.85F);
               }
            }

            int fy = h / 2 - 40;
            text(g, font, "COMBUSTIBLE", 14.0F, fy, -2004424449, 0.8F);
            float fuel = nave.getFuel();
            int bars = 5;

            for (int k = 0; k < bars; k++) {
               int by = fy + 12 + (bars - 1 - k) * 9;
               boolean on = fuel * bars > k + 0.01F;
               int onColor = fuel < 0.25F ? -34182 : -7935745;
               g.fill(14, by, 34, by + 6, on ? onColor : 864479487);
            }

            text(g, font, "100", 40.0F, fy + 12, -2004424449, 0.7F);
            text(g, font, "50", 40.0F, fy + 12 + 18 + 1, -2004424449, 0.7F);
            text(g, font, "0", 40.0F, fy + 12 + 36, -2004424449, 0.7F);
            text(g, font, Math.round(fuel * 100.0F) + "%", 62.0F, fy + 12 + 18, -1508609, 0.85F);
            text(g, font, "RESERVA", 14.0F, fy + 12 + bars * 9 + 4, -2004424449, 0.7F);
            int messageColor = -7935745;
            String message;
            if (phase > 0) {
               message = PHASES[phase - 1] + " · EN CURSO";
            } else if (nave.isDoorOpen()) {
               message = "ESPERANDO A QUE SE CIERRE LA PUERTA";
               messageColor = -2004424449;
            } else if (nave.getFuel() < 0.5F) {
               message = "COMBUSTIBLE INSUFICIENTE";
               messageColor = -34182;
            } else {
               message = "PULSA ESPACIO PARA DESPEGAR";
               messageColor = -11413;
            }

            textCenter(g, font, message, cx, h - 74, messageColor, 0.9F);
         }
      }
   }

   private static void text(GuiGraphicsExtractor g, Font font, String s, float x, float y, int color, float scale) {
      org.joml.Matrix3x2fStack ps = g.pose();
      ps.pushMatrix();
      ps.translate(x, y);
      ps.scale(scale, scale);
      g.text(font, s, 0, 0, (color & 0xFF000000) == 0 ? color | 0xFF000000 : color, false);
      ps.popMatrix();
   }

   private static void textCenter(GuiGraphicsExtractor g, Font font, String s, float x, float y, int color, float scale) {
      text(g, font, s, x - font.width(s) * scale / 2.0F, y, color, scale);
   }

   private static void textRight(GuiGraphicsExtractor g, Font font, String s, float x, float y, int color, float scale) {
      text(g, font, s, x - font.width(s) * scale, y, color, scale);
   }
}
