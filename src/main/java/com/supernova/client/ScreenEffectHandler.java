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

   public static void compute(float pt) { }

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
