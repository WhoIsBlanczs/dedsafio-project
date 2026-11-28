package com.ghostsafio.mitomod.client;

import com.ghostsafio.mitomod.NaveEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class OrbitFx {
   private static final int INTRO_TICKS = 230;
   private static final double[] CALL_ALT = new double[]{40.0, 300.0, 500.0, 600.0};
   private static final String[] CALL_TITLE = new String[]{"DESPEGUE CONFIRMADO", "MAX-Q", "ESTRATÓSFERA", "LÍNEA DE KÁRMÁN"};
   private static final String[] CALL_SUB = new String[]{
      "Motores al 100 %", "Presión dinámica máxima", "Atravesando la capa de nubes", "Límite del espacio · 600 m"
   };
   private static double lastAlt = -1.0;
   private static int activeCall = -1;
   private static long callStartNanos = 0L;

   public static float introTicks(NaveEntity nave, float partial) {
      if (nave != null && nave.getPhase() == 3 && SpaceClient.isSpaceDimension()) {
         int ticks = SpaceClient.ticksInSpace();
         if (ticks < 0) {
            return -1.0F;
         }

         float t = ticks + partial;
         return t > 230.0F ? -1.0F : t;
      } else {
         return -1.0F;
      }
   }

   private static NaveEntity ship() {
      Minecraft mc = Minecraft.getInstance();
      return mc.player != null && mc.player.getVehicle() instanceof NaveEntity n ? n : null;
   }

   public static float introIntensity() {
      float t = introTicks(ship(), 0.0F);
      return t < 0.0F ? 0.0F : Mth.sin((float) Math.PI * Mth.clamp(t / 230.0F, 0.0F, 1.0F));
   }

   public static float cameraZoom() {
      float t = introTicks(ship(), 0.0F);
      if (t < 0.0F) {
         return 3.0F;
      }

      float k = Mth.sin((float) Math.PI * Mth.clamp((t - 10.0F) / 200.0F, 0.0F, 1.0F));
      return 3.0F + 3.0F * k;
   }

   public static float shake(NaveEntity nave) {
      if (nave == null) {
         return 0.0F;
      }

      int phase = nave.getPhase();
      if (phase == 0) {
         return 0.0F;
      }

      if (SpaceClient.isSpaceDimension()) {
         if (phase != 3) {
            return 0.0F;
         }

         int t = SpaceClient.ticksInSpace();
         return t >= 0 && t < 60 ? 0.6F * (1.0F - t / 60.0F) : 0.0F;
      } else {
         if (phase > 3) {
            return 0.0F;
         }

         double y = nave.getY();
         double d = (y - 380.0) / 110.0;
         float k = 0.35F + 0.65F * (float)Math.exp(-d * d);
         float fade = 1.0F - smooth((y - 560.0) / 100.0);
         return k * fade;
      }
   }

   private static float smooth(double x) {
      double c = Math.max(0.0, Math.min(1.0, x));
      return (float)(c * c * (3.0 - 2.0 * c));
   }

   public static void renderHud(GuiGraphicsExtractor g, DeltaTracker tracker, NaveEntity nave, int w, int h) {
      Minecraft mc = Minecraft.getInstance();
      Font font = mc.font;
      float partial = tracker.getGameTimeDeltaPartialTick(false);
      int phase = nave.getPhase();
      boolean space = SpaceClient.isSpaceDimension();
      double alt = nave.getY();
      if (!space && phase >= 1 && phase <= 3) {
         float heat = smooth((alt - 120.0) / 220.0) * (1.0F - smooth((alt - 600.0) / 70.0));
         if (heat > 0.02F) {
            for (int i = 0; i < 14; i++) {
               float f = 1.0F - i / 14.0F;
               int a = (int)(heat * 90.0F * f * f);
               if (a >= 4) {
                  int col = a << 24 | 16742938;
                  int b = i * 6;
                  g.fill(0, b, w, b + 6, col);
                  g.fill(0, h - b - 6, w, h - b, col);
                  g.fill(b, 0, b + 6, h, col);
                  g.fill(w - b - 6, 0, w - b, h, col);
               }
            }
         }

         float streak = smooth((alt - 60.0) / 200.0) * (1.0F - smooth((alt - 640.0) / 80.0));
         if (streak > 0.02F) {
            double time = System.nanoTime() / 1.0E9 % 10000.0;

            for (int i = 0; i < 48; i++) {
               float len = 40.0F + 90.0F * hash(i * 5 + 2);
               float spd = 700.0F + 900.0F * hash(i * 7 + 3);
               double yy = (time * spd + hash(i * 3 + 7) * (h + len)) % (h + len) - len;
               int x = (int)(hash(i * 2 + 1) * w);
               int a = (int)(streak * (60.0F + 90.0F * hash(i * 11 + 4)));
               if (a >= 4) {
                  g.fill(x, (int)yy, x + 1, (int)(yy + len), a << 24 | 13627391);
               }
            }
         }

         updateCallouts(alt);
      } else {
         lastAlt = -1.0;
      }

      drawCallout(g, font, w, h);
      float t = introTicks(nave, partial);
      if (t >= 0.0F) {
         float in = Mth.clamp(t / 20.0F, 0.0F, 1.0F);
         float out = Mth.clamp((230.0F - t) / 25.0F, 0.0F, 1.0F);
         float k = Math.min(in, out);
         k = k * k * (3.0F - 2.0F * k);
         int bar = (int)(h * 0.09F * k);
         g.fill(0, 0, w, bar, -16777216);
         g.fill(0, h - bar, w, h, -16777216);

         for (int ring = 0; ring < 2; ring++) {
            float rt = (t - ring * 10.0F) / 55.0F;
            if (rt > 0.0F && rt < 1.0F) {
               float e = 1.0F - (1.0F - rt) * (1.0F - rt);
               float r = e * Math.max(w, h) * 0.75F;
               int a = (int)((1.0F - rt) * 190.0F);
               if (a >= 4) {
                  int col = a << 24 | (ring == 0 ? 15268607 : 8841471);

                  for (int s = 0; s < 180; s++) {
                     double ang = s * 0.03490658503988659;
                     int px = (int)(w / 2.0 + Math.cos(ang) * r);
                     int py = (int)(h / 2.0 + Math.sin(ang) * r);
                     g.fill(px - 1, py - 1, px + 2, py + 2, col);
                  }
               }
            }
         }

         float ta = Mth.clamp((t - 25.0F) / 15.0F, 0.0F, 1.0F) * Mth.clamp((200.0F - t) / 20.0F, 0.0F, 1.0F);
         if (ta > 0.02F) {
            int a = Math.max(5, (int)(ta * 255.0F));
            int cx = w / 2;
            int ty = (int)(h * 0.27F);
            String title = nave.getBody() == 0 ? "ÓRBITA TERRESTRE" : "ÓRBITA DE GLEBA";
            textCenter(g, font, title, cx, ty, a << 24 | 15268607, 3.2F);
            int half = (int)(30.0F + 150.0F * Mth.clamp((t - 25.0F) / 40.0F, 0.0F, 1.0F));
            g.fill(cx - half, ty + 36, cx + half, ty + 37, a << 24 | 8841471);
            String sub = "INSERCIÓN ORBITAL · ALTITUD 750 m";
            int n = Mth.clamp((int)((t - 45.0F) * 0.8F), 0, sub.length());
            if (n > 0) {
               textCenter(g, font, sub.substring(0, n), cx, ty + 44, a << 24 | 8841471, 1.1F);
            }

            if (t > 120.0F) {
               textCenter(g, font, "SISTEMAS NOMINALES  ✔", cx, ty + 60, a << 24 | 16765803, 0.9F);
            }
         }
      }
   }

   private static void updateCallouts(double alt) {
      if (lastAlt >= 0.0) {
         for (int i = 0; i < CALL_ALT.length; i++) {
            if (lastAlt < CALL_ALT[i] && alt >= CALL_ALT[i]) {
               activeCall = i;
               callStartNanos = System.nanoTime();
            }
         }
      }

      lastAlt = alt;
   }

   private static void drawCallout(GuiGraphicsExtractor g, Font font, int w, int h) {
      if (activeCall >= 0) {
         float age = (float)(System.nanoTime() - callStartNanos) / 1.0E9F;
         if (age > 3.2F) {
            activeCall = -1;
         } else {
            float k = Math.min(1.0F, age / 0.3F) * Math.min(1.0F, (3.2F - age) / 0.6F);
            int a = Math.max(5, (int)(k * 255.0F));
            int cx = w / 2;
            int y = (int)(h * 0.22F);
            int main = activeCall == 3 ? 16765803 : 15268607;
            textCenter(g, font, CALL_TITLE[activeCall], cx, y, a << 24 | main, 1.7F);
            int half = (int)(20.0F + 90.0F * Mth.clamp(age / 0.5F, 0.0F, 1.0F));
            g.fill(cx - half, y + 16, cx + half, y + 17, a << 24 | 8841471);
            textCenter(g, font, CALL_SUB[activeCall], cx, y + 22, a << 24 | 8841471, 0.9F);
         }
      }
   }

   private static float hash(int n) {
      return Mth.frac(Mth.sin(n * 12.9898F) * 43758.547F);
   }

   private static void textCenter(GuiGraphicsExtractor g, Font font, String s, float x, float y, int color, float scale) {
      org.joml.Matrix3x2fStack ps = g.pose();
      ps.pushMatrix();
      ps.translate(x - font.width(s) * scale / 2.0F, y);
      ps.scale(scale, scale);
      g.text(font, s, 0, 0, (color & 0xFF000000) == 0 ? color | 0xFF000000 : color, false);
      ps.popMatrix();
   }
}
