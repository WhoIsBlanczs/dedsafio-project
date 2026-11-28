package com.watonstudio.creepermetalico.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

@Environment(EnvType.CLIENT)
public final class ClientEclipse {
   public static final float DURATION_TICKS = 1000.0F;
   public static final float EXIT_TICKS = 300.0F;
   private static final ResourceLocation BLANK_TEXTURE = ResourceLocation.fromNamespaceAndPath("eclipse", "dynamic/blank");
   private static boolean blankReady = false;
   private static boolean on = false;
   private static boolean exiting = false;
   private static float prev = 0.0F;
   private static float cur = 0.0F;
   private static float qPrev = 0.0F;
   private static float qCur = 0.0F;
   private static float exitFromP = 0.0F;

   public static ResourceLocation blankTexture() {
      if (!blankReady) {
         blankReady = true;
         NativeImage image = new NativeImage(16, 16, true);
         image.fillRect(0, 0, 16, 16, 0);
         Minecraft.getInstance().getTextureManager().register(BLANK_TEXTURE, new DynamicTexture(image));
      }

      return BLANK_TEXTURE;
   }

   private ClientEclipse() {
   }

   public static void apply(boolean enable, int ageTicks) {
      if (enable) {
         boolean fresh = !on && ageTicks == 0;
         exiting = false;
         qCur = 0.0F;
         qPrev = 0.0F;
         on = true;
         float start = Mth.clamp(ageTicks / 1000.0F, 0.0F, 1.0F);
         if (start > cur) {
            cur = start;
            prev = start;
         } else if (cur > 0.0F) {
            prev = cur;
         }

         if (fresh) {
            EclipseAudio.playStart();
         }
      } else {
         if (!exiting && (on || cur > 0.0F)) {
            exiting = true;
            exitFromP = cur;
            qCur = 0.0F;
            qPrev = 0.0F;
         }

         on = false;
      }
   }

   private static void clearState() {
      on = false;
      exiting = false;
      prev = 0.0F;
      cur = 0.0F;
      qPrev = 0.0F;
      qCur = 0.0F;
      exitFromP = 0.0F;
   }

   public static void reset() {
      EclipseAudio.stopAll();
      clearState();
   }

   public static void tick() {
      prev = cur;
      qPrev = qCur;
      if (on) {
         cur = Math.min(1.0F, cur + 0.001F);
      } else if (exiting) {
         qCur = Math.min(1.0F, qCur + 0.0033333334F);
         if (qCur >= 1.0F && qPrev >= 1.0F) {
            reset();
         }
      }

      ClientEclipse.Look lk = look(1.0F);
      EclipseAudio.tick(isRunning(), on && cur >= 1.0F, lk.flare(), lk.e());
   }

   public static boolean isRunning() {
      return on || exiting || cur > 0.0F;
   }

   public static boolean inOverworld() {
      Minecraft mc = Minecraft.getInstance();
      return mc.level != null && mc.level.dimension() == Level.OVERWORLD;
   }

   public static float ambientDarkness(float partialTick) {
      if (!isRunning() || !inOverworld()) {
         return 0.0F;
      }

      if (!exiting) {
         return smoothstep(0.0F, 1.0F, progress(partialTick));
      }

      float darkness = smoothstep(0.0F, 1.0F, exitFromP);
      return darkness * (1.0F - smoothstep(0.0F, 1.0F, exitProgress(partialTick)));
   }

   private static float progress(float pt) {
      return Mth.lerp(pt, prev, cur);
   }

   private static float exitProgress(float pt) {
      return Mth.lerp(pt, qPrev, qCur);
   }

   public static float darkness(float partialTick) {
      return isRunning() && inOverworld() ? look(partialTick).e() : 0.0F;
   }

   public static ClientEclipse.Look look(float pt) {
      if (!exiting) {
         return enterLook(progress(pt));
      }

      float q = exitProgress(pt);
      ClientEclipse.Look b = enterLook(exitFromP);
      float s = Mth.lerp(smoothstep(0.0F, 1.0F, q), b.s(), -1.0F);
      float e = b.e() * (1.0F - smoothstep(0.12F, 0.55F, q));
      float c = b.c() * (1.0F - smoothstep(0.0F, 0.2F, q));
      float glow = Math.max(b.glow(), smoothstep(0.05F, 0.5F, q));
      float dk = b.dk() * (1.0F - smoothstep(0.3F, 0.7F, q));
      float gate = b.gate() * (1.0F - smoothstep(0.1F, 0.5F, q));
      float flare = bell(q, 0.1F, 0.06F) * b.c();
      float moonA = 1.0F - smoothstep(0.6F, 0.95F, q);
      float skyA = b.skyA() * (1.0F - smoothstep(0.75F, 1.0F, q));
      return new ClientEclipse.Look(s, e, c, glow, dk, gate, flare, 1.0F, moonA, skyA);
   }

   private static ClientEclipse.Look enterLook(float p) {
      float s = 1.0F - smoothstep(0.0F, 0.88F, p);
      float e = smoothstep(0.4F, 0.58F, p);
      float c = smoothstep(0.84F, 0.95F, p);
      float glow = 1.0F - 0.96F * smoothstep(0.55F, 0.88F, p);
      float dk = smoothstep(0.35F, 0.55F, p);
      float gate = smoothstep(0.6F, 0.95F, p);
      float flare = bell(p, 0.84F, 0.07F);
      float skyA = smoothstep(0.0F, 0.08F, p);
      return new ClientEclipse.Look(s, e, c, glow, dk, gate, flare, -1.0F, 1.0F, skyA);
   }

   private static float bell(float x, float center, float width) {
      float b = Math.max(0.0F, 1.0F - Math.abs(x - center) / width);
      return b * b;
   }

   public static float smoothstep(float a, float b, float x) {
      float t = Mth.clamp((x - a) / (b - a), 0.0F, 1.0F);
      return t * t * (3.0F - 2.0F * t);
   }

   @Environment(EnvType.CLIENT)
   public record Look(float s, float e, float c, float glow, float dk, float gate, float flare, float flareSide, float moonA, float skyA) {
   }
}
