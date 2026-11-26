package com.supernova.client;

import com.supernova.SupernovaState;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.ViewportEvent;
import net.ruleta.client.reviil.CieloReviil;

public class ScreenEffectHandler {
   public static float skyMix;
   public static float skyBright;
   public static float skyDark;
   public static boolean haySupernova;
   public static float mulR = 1.0F;
   public static float mulG = 1.0F;
   public static float mulB = 1.0F;
   public static float addR;
   public static float addG;
   public static float addB;
   public static float flash;
   public static float fogOutR;
   public static float fogOutG;
   public static float fogOutB;
   public static float naranja;
   public static float dia = 1.0F;
   public static float inside;
   public static float fogAmt;
   public static float fogR;
   public static float fogG;
   public static float fogB;
   public static float starX;
   public static float starY = 1.0F;
   public static float starZ;
   public static float aparece = 99.0F;
   public static float apoc;
   public static float rayosI;
   public static float rayosW;
   public static final List<float[]> luces = new ArrayList<>();

   private static float[] rayosDe(SupernovaState s, float now) {
      float ri = 0.55F;
      float rw = 0.0F;
      if (s.isBlasting(now)) {
         float bt2 = s.blastTicks(now);
         float k2 = smooth(bt2 / 10.0F);
         ri = (Mth.lerp(k2, 0.75F, 1.0F) * (0.55F + 0.45F * (float)Math.exp(-bt2 / 160.0F)) + 1.1F * flashRayos(bt2)) * s.fade(now);
         rw = 1.0F;
      } else if (s.isDetonating()) {
         float k = s.charge(now);
         ri = 0.55F + 0.25F * smooth(s.activation(now));
         rw = smooth((k - 0.5F) / 0.5F);
      } else {
         ri *= s.fade(now);
      }

      return new float[]{ri * smooth(s.age(now) / 60.0F), rw};
   }

   private static float flashRayos(float bt) {
      return bt < 4.0F ? 1.0F : (float)Math.exp(-(bt - 4.0F) / 14.0F);
   }

   private static float sat(float v) {
      return Mth.clamp(v, 0.0F, 1.0F);
   }

   private static float smooth(float v) {
      return SupernovaState.smooth(v);
   }

   private static void reset() {
      luces.clear();
      skyDark = 0.0F;
      skyBright = 0.0F;
      skyMix = 0.0F;
      mulB = 1.0F;
      mulG = 1.0F;
      mulR = 1.0F;
      addB = 0.0F;
      addG = 0.0F;
      addR = 0.0F;
      naranja = 0.0F;
      inside = 0.0F;
      flash = 0.0F;
      rayosW = 0.0F;
      rayosI = 0.0F;
      apoc = 0.0F;
      fogB = 0.0F;
      fogG = 0.0F;
      fogR = 0.0F;
      fogAmt = 0.0F;
   }

   public static void compute(float pt) {
      reset();
      Minecraft mc = Minecraft.getInstance();
      if (mc.level != null && mc.player != null) {
         Vec3 eye = mc.player.getEyePosition(pt);
         float now = (float)mc.level.getGameTime() + pt;
         SupernovaState best = null;
         float bestNear = 0.0F;
         float edad = 0.0F;
         float queda = 0.0F;

         for (SupernovaState s : SupernovaGhosts.active(now)) {
            edad = Math.max(edad, s.age(now));
            queda = Math.max(queda, s.fade(now));
         }

         haySupernova = false;
         int bestRank = -1;

         for (SupernovaState s : SupernovaGhosts.active(now)) {
            Vec3 haciaS = s.center().subtract(eye);
            float d = (float)haciaS.length();
            float near = 1.0F + 1.0F / (1.0F + d);
            int rank = s.isBlasting(now) ? 2 : (s.isDetonating() ? 1 : 0);
            if (rank > bestRank || rank == bestRank && near > bestNear) {
               bestRank = rank;
               bestNear = near;
               best = s;
            }

            if (haciaS.lengthSqr() > 1.0E-6) {
               Vec3 u = haciaS.normalize();
               float[] r = rayosDe(s, now);
               luces.add(new float[]{(float)u.x, (float)u.y, (float)u.z, r[0], r[1]});
            }
         }

         if (best != null && !(bestNear <= 0.001F)) {
            haySupernova = true;
            aparece = best.age(now) / 20.0F;
            Vec3 haciaEstrella = best.center().subtract(eye);
            if (haciaEstrella.lengthSqr() > 1.0E-6) {
               haciaEstrella = haciaEstrella.normalize();
               starX = (float)haciaEstrella.x;
               starY = (float)haciaEstrella.y;
               starZ = (float)haciaEstrella.z;
            }

            float dx = (float)eye.distanceTo(best.center());
            float amount = Math.min(1.0F, bestNear) * smooth(edad / 100.0F) * queda;
            float diaL = mc.level.getSkyDarken(pt);
            float mr = Mth.lerp(diaL, 0.86F, 0.88F);
            float mg = Mth.lerp(diaL, 0.66F, 0.82F);
            float mb = Mth.lerp(diaL, 0.84F, 1.0F);
            float ar = Mth.lerp(diaL, 0.04F, 0.045F);
            float ag = Mth.lerp(diaL, 0.008F, 0.035F);
            float ab = Mth.lerp(diaL, 0.035F, 0.085F);
            float fr = Mth.lerp(diaL, 0.28F, 0.4F);
            float fg = Mth.lerp(diaL, 0.07F, 0.32F);
            float fb = Mth.lerp(diaL, 0.2F, 0.55F);
            float bright = 0.0F;
            float darkSky = 0.0F;
            float fl = 0.0F;
            float nar = 0.0F;
            if (best.isDetonating() && !best.isBlasting(now)) {
               float k = best.charge(now);
               float kk = smooth(k / 0.12F);
               mr = Mth.lerp(kk, mr, 0.96F);
               mg = Mth.lerp(kk, mg, 0.84F);
               mb = Mth.lerp(kk, mb, 1.0F);
               float lit = kk * (0.7F + 0.3F * k);
               ar += 0.2F * lit;
               ag += 0.17F * lit;
               ab += 0.3F * lit;
               float white = smooth((k - 0.88F) / 0.12F);
               ar += 0.5F * white;
               ag += 0.48F * white;
               ab += 0.5F * white;
               fr = Mth.lerp(kk, fr, 0.72F);
               fg = Mth.lerp(kk, fg, 0.46F);
               fb = Mth.lerp(kk, fb, 0.82F);
               bright = kk;
            } else if (best.isBlasting(now)) {
               float bt = best.blastTicks(now);
               float sec = bt / 20.0F;
               fl = (bt < 8.0F ? 1.0F : (float)Math.exp(-(bt - 8.0F) / 14.0F)) * best.fade(now);
               float r = best.radius(now);
               if (dx < r) {
                  inside = sat((r - dx) / 15.0F) * best.fade(now);
               }
            }

            float ri = 0.55F;
            float rw = 0.0F;
            if (best.isBlasting(now)) {
               float bt2 = best.blastTicks(now);
               float k2 = smooth(bt2 / 10.0F);
               ri = (Mth.lerp(k2, 0.75F, 1.0F) * (0.55F + 0.45F * (float)Math.exp(-bt2 / 160.0F)) + 1.1F * flashRayos(bt2)) * best.fade(now);
               rw = 1.0F;
            } else if (best.isDetonating()) {
               float k = best.charge(now);
               ri = 0.55F + 0.25F * smooth(best.activation(now));
               rw = smooth((k - 0.5F) / 0.5F);
            }

            rayosI = ri * Math.min(1.0F, bestNear) * smooth(edad / 60.0F);
            rayosW = rw;
            naranja = nar;
            dia = diaL;
            skyMix = amount;
            skyBright = bright;
            skyDark = darkSky;
            mulR = Mth.lerp(amount, 1.0F, mr);
            mulG = Mth.lerp(amount, 1.0F, mg);
            mulB = Mth.lerp(amount, 1.0F, mb);
            addR = ar * amount;
            addG = ag * amount;
            addB = ab * amount;
            flash = fl;
            fogAmt = amount;
            fogR = fr;
            fogG = fg;
            fogB = fb;
            if (CieloReviil.visible()) {
               skyMix = 0.0F;
               skyDark = 0.0F;
               fogAmt = 0.0F;
            }
         }
      }
   }

   @SubscribeEvent
   public static void onFogColor(ViewportEvent.ComputeFogColor e) {
      compute(e.getPartialTick());
      if (fogAmt > 0.001F) {
         e.setRed(Mth.lerp(fogAmt, e.getRed(), fogR));
         e.setGreen(Mth.lerp(fogAmt, e.getGreen(), fogG));
         e.setBlue(Mth.lerp(fogAmt, e.getBlue(), fogB));
      }

      fogOutR = e.getRed();
      fogOutG = e.getGreen();
      fogOutB = e.getBlue();
   }
}
