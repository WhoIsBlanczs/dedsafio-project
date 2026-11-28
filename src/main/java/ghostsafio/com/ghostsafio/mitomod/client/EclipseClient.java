package com.ghostsafio.mitomod.client;

import com.ghostsafio.mitomod.EclipsePayload;
import com.ghostsafio.mitomod.Ghostsafio;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class EclipseClient {
   private static final ResourceLocation T_WHITE = Ghostsafio.id("textures/effect/eclipse_white.png");
   private static final ResourceLocation T_DOT = Ghostsafio.id("textures/effect/eclipse_dot.png");
   private static final ResourceLocation T_SUN = Ghostsafio.id("textures/effect/eclipse_sun.png");
   private static final ResourceLocation T_GLOW = Ghostsafio.id("textures/effect/eclipse_glow.png");
   private static final ResourceLocation T_MOON = Ghostsafio.id("textures/effect/eclipse_moon.png");
   private static final ResourceLocation T_RIM = Ghostsafio.id("textures/effect/eclipse_rim.png");
   private static final ResourceLocation T_FLARE = Ghostsafio.id("textures/effect/eclipse_flare.png");
   private static final ResourceLocation T_CORONA = Ghostsafio.id("textures/effect/eclipse_corona.png");
   private static final double MOON_RADIUS_DEG = 5.5;
   private static final double SUN_RATIO = 0.8;
   private static final double CORONA_SCALE = 4.0;
   private static final float WORLD_DARKNESS = 0.86F;
   private static final int STAR_COUNT = 450;
   private static final int SPARKLES = 18;
   private static boolean active;
   private static int duration = 1200;
   private static int ticks;
   private static int lastTickSeen = -1;
   private static long lastTickNano;
   private static Vec3[] starDir;
   private static float[] starSize;
   private static float[] starBright;
   private static float[] starPhase;
   private static ShaderInstance activeShader;

   public static void register() {
      EclipseShaders.register();
      ClientPlayNetworking.registerGlobalReceiver(EclipsePayload.TYPE, (payload, context) -> {
         if (payload.start()) {
            boolean fresh = !active;
            active = true;
            duration = Math.max(20, payload.durationTicks());
            if (fresh || Math.abs(ticks - payload.elapsedTicks()) > 2) {
               ticks = payload.elapsedTicks();
            }

            if (fresh) {
               initStars();
            }
         } else {
            active = false;
         }
      });
      ClientPlayConnectionEvents.DISCONNECT.register((Disconnect)(handler, client) -> active = false);
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)client -> {
         if (active && !client.isPaused()) {
            ticks++;
            if (ticks > duration) {
               active = false;
            }
         }
      });
      WorldRenderEvents.AFTER_TRANSLUCENT.register(EclipseClient::render);
   }

   private static void initStars() {
      Random r = new Random(20261109L);
      starDir = new Vec3[450];
      starSize = new float[450];
      starBright = new float[450];
      starPhase = new float[450];

      for (int i = 0; i < 450; i++) {
         double y = -0.1 + r.nextDouble() * 1.1;
         double a = r.nextDouble() * Math.PI * 2.0;
         double h = Math.sqrt(Math.max(0.0, 1.0 - y * y));
         starDir[i] = new Vec3(Math.cos(a) * h, y, Math.sin(a) * h).normalize();
         starSize[i] = 0.0015F + r.nextFloat() * r.nextFloat() * 0.0045F;
         starBright[i] = 0.35F + r.nextFloat() * 0.65F;
         starPhase[i] = r.nextFloat() * 6.28F;
      }
   }

   private static double smooth(double t) {
      t = Math.max(0.0, Math.min(1.0, t));
      return t * t * (3.0 - 2.0 * t);
   }

   private static float domeAlpha(double p) {
      return (float)Math.min(smooth(p / 0.04), 1.0 - smooth((p - 0.96) / 0.04));
   }

   private static double moonOffset(double p) {
      if (p < 0.5) {
         return -2.4 * (1.0 - smooth((p - 0.08) / 0.42));
      } else {
         return p <= 0.66 ? 0.0 : 2.4 * smooth((p - 0.66) / 0.3);
      }
   }

   private static double coverage(double d) {
      double R = 1.0;
      double r = 0.8;
      if (d >= R + r) {
         return 0.0;
      }

      if (d <= R - r) {
         return 1.0;
      }

      double a1 = Math.acos(Math.max(-1.0, Math.min(1.0, (d * d + r * r - R * R) / (2.0 * d * r))));
      double a2 = Math.acos(Math.max(-1.0, Math.min(1.0, (d * d + R * R - r * r) / (2.0 * d * R))));
      double lens = r * r * a1 + R * R * a2 - 0.5 * Math.sqrt(Math.max(0.0, (-d + r + R) * (d + r - R) * (d - r + R) * (d + r + R)));
      return Math.max(0.0, Math.min(1.0, lens / (Math.PI * r * r)));
   }

   private static float partialFrac() {
      long now = System.nanoTime();
      if (ticks != lastTickSeen) {
         lastTickSeen = ticks;
         lastTickNano = now;
      }

      return Math.min(1.0F, (float)(now - lastTickNano) / 5.0E7F);
   }

   private static void render(WorldRenderContext context) {
      if (active) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level != null && mc.level.dimension() == Level.OVERWORLD) {
            float part = partialFrac();
            double p = Math.min(1.0, (double)(ticks + part) / duration);
            float da = domeAlpha(p);
            if (!(da <= 0.002F)) {
               double angle = mc.level.getTimeOfDay(part) * 2.0 * Math.PI;
               Vec3 n = new Vec3(-Math.sin(angle), Math.cos(angle), 0.0).normalize();
               Vec3 e1 = frameX(n);
               Vec3 e2 = frameY(n);
               double o = moonOffset(p);
               double ao = Math.abs(o);
               double rm = Math.tan(Math.toRadians(5.5));
               double rs = rm * 0.8;
               float t = (float)System.nanoTime() / 1.0E9F % 1000.0F;
               float cover = (float)coverage(ao);
               float k = (float)Math.pow(cover, 1.7);
               float totality = (float)(1.0 - smooth(ao / 0.7));
               drawScreenTint(0.01F, 0.012F, 0.04F, k * da * 0.86F);
               Vec3 sky = mc.level.getSkyColor(mc.gameRenderer.getMainCamera().getPosition(), part);
               float nr = 0.004F;
               float ng = 0.007F;
               float nb = 0.02F;
               drawSkyDome((float)(sky.x + (nr - sky.x) * k), (float)(sky.y + (ng - sky.y) * k), (float)(sky.z + (nb - sky.z) * k), da);
               float starFade = (float)smooth((k - 0.55) / 0.4);
               drawStars(context, starFade * da, t);
               float tw = (float)smooth((k - 0.45) / 0.45) * da;
               drawTwilight(context, tw, n);
               Vec3 sunDir = n;
               float halo = (1.0F - cover) * (1.0F - cover);
               if (EclipseShaders.ok(EclipseShaders.SUN)) {
                  fx(context, EclipseShaders.SUN, T_SUN, sunDir, rs / 0.3, 1.0F, 1.0F, 1.0F, da, true, 0.0F, 0.0F, 0.0F);
               } else {
                  quad(context, T_GLOW, sunDir, rs * 6.0, 1.0F, 0.95F, 0.8F, 0.75F * halo * da, true);
                  quad(context, T_SUN, sunDir, rs / 0.9, 1.0F, 1.0F, 1.0F, da, false);
               }

               float pulse = 0.95F + 0.05F * (float)Math.sin(t * 1.3);
               fx(context, EclipseShaders.CORONA, T_CORONA, sunDir, rm * 4.0, 1.0F, 1.0F, 1.0F, totality * da, true, pulse, 0.0F, 0.0F);
               Vec3 moonDir = n.add(e1.scale(o * rm)).normalize();
               float moonA = (float)smooth((p - 0.04) / 0.06) * (1.0F - (float)smooth((p - 0.94) / 0.05));
               fx(context, EclipseShaders.MOON, T_MOON, moonDir, rm / 0.9, 1.0F, 1.0F, 1.0F, moonA, false, 0.0F, 0.0F, 0.0F);
               float rimA = (float)(0.9 * (1.0 - smooth((ao - 0.1) / 1.2)));
               fx(context, EclipseShaders.RIM, T_RIM, moonDir, rm / 0.92, 1.0F, 0.85F, 0.8F, rimA * moonA, true, 0.0F, 0.0F, 0.0F);
               double dv = 1.0 - Math.abs(ao - 0.33) / 0.26;
               if (dv > 0.0) {
                  float f = (float)Math.pow(dv, 1.6);
                  Vec3 flareDir = n.add(e1.scale(-Math.signum(o) * rs * 0.92)).normalize();
                  fx(context, EclipseShaders.FLARE, T_FLARE, flareDir, rm * 2.2, 1.0F, 1.0F, 1.0F, f * da, true, 1.4F, 0.0F, 0.0F);
               }

               if (totality > 0.02F) {
                  for (int i = 0; i < 18; i++) {
                     double ang = i * (Math.PI / 9) + sparkPhase(i) * 0.4;
                     double wave = 0.5 + 0.5 * Math.sin(t * (1.2 + 0.35 * (i % 5)) + sparkPhase(i));
                     float a = (float)Math.pow(wave, 4.0) * totality * da;
                     double size = rm * (0.55 + 0.75 * (i * 37 % 10) / 10.0);
                     Vec3 dir = moonDir.add(e1.scale(Math.cos(ang) * rm * 1.02)).add(e2.scale(Math.sin(ang) * rm * 1.02)).normalize();
                     fx(context, EclipseShaders.FLARE, T_FLARE, dir, size, 1.0F, 0.97F, 0.9F, a, true, 1.0F, (float)(sparkPhase(i) + t * 0.1), 0.0F);
                  }
               }
            }
         }
      }
   }

   private static void fx(
      WorldRenderContext context,
      ShaderInstance shader,
      ResourceLocation fallback,
      Vec3 dir,
      double halfTan,
      float r,
      float g,
      float b,
      float alpha,
      boolean additive,
      float p1,
      float p2,
      float p3
   ) {
      if (!(alpha <= 0.003F)) {
         if (EclipseShaders.ok(shader)) {
            EclipseShaders.params(shader, p1, p2, p3);
            activeShader = shader;

            try {
               quad(context, T_WHITE, dir, halfTan, r, g, b, alpha, additive);
            } finally {
               activeShader = null;
            }
         } else {
            quad(context, fallback, dir, halfTan, r, g, b, alpha, additive);
         }
      }
   }

   private static void drawTwilight(WorldRenderContext context, float strength, Vec3 sunDir) {
      if (!(strength <= 0.01F)) {
         double d = skyDistance();
         int seg = 72;
         double[] ys = new double[]{-0.03, 0.012, 0.07, 0.24};
         float[][] cols = new float[][]{{1.0F, 0.5F, 0.18F}, {1.0F, 0.46F, 0.16F}, {0.95F, 0.33F, 0.3F}, {0.45F, 0.3F, 0.75F}};
         float[] al = new float[]{0.0F, 0.95F, 0.55F, 0.0F};
         double sx = sunDir.x;
         double sz = sunDir.z;
         double sl = Math.sqrt(sx * sx + sz * sz);
         if (sl > 1.0E-4) {
            sx /= sl;
            sz /= sl;
         }

         Matrix4f view = new Matrix4f().rotation(context.camera().rotation().conjugate(new Quaternionf()));
         Matrix4fStack mv = RenderSystem.getModelViewStack();
         mv.pushMatrix();
         mv.identity();
         RenderSystem.applyModelViewMatrix();
         RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
         RenderSystem.setShaderTexture(0, T_WHITE);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         BufferBuilder buf = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

         for (int i = 0; i < 72; i++) {
            double a0 = i * 2.0 * Math.PI / 72.0;
            double a1 = (i + 1) * 2.0 * Math.PI / 72.0;
            double x0 = Math.cos(a0);
            double z0 = Math.sin(a0);
            double x1 = Math.cos(a1);
            double z1 = Math.sin(a1);
            float f0 = (float)(0.7 + 0.3 * Math.max(0.0, x0 * sx + z0 * sz));
            float f1 = (float)(0.7 + 0.3 * Math.max(0.0, x1 * sx + z1 * sz));

            for (int j = 0; j < 3; j++) {
               double ya = ys[j] * d;
               double yb = ys[j + 1] * d;
               float[] ca = cols[j];
               float[] cb = cols[j + 1];
               float aa0 = al[j] * strength * f0;
               float aa1 = al[j] * strength * f1;
               float ab0 = al[j + 1] * strength * f0;
               float ab1 = al[j + 1] * strength * f1;
               buf.addVertex(view, (float)(x0 * d), (float)ya, (float)(z0 * d)).setUv(0.0F, 1.0F).setColor(ca[0], ca[1], ca[2], aa0);
               buf.addVertex(view, (float)(x1 * d), (float)ya, (float)(z1 * d)).setUv(1.0F, 1.0F).setColor(ca[0], ca[1], ca[2], aa1);
               buf.addVertex(view, (float)(x1 * d), (float)yb, (float)(z1 * d)).setUv(1.0F, 0.0F).setColor(cb[0], cb[1], cb[2], ab1);
               buf.addVertex(view, (float)(x0 * d), (float)yb, (float)(z0 * d)).setUv(0.0F, 0.0F).setColor(cb[0], cb[1], cb[2], ab0);
            }
         }

         BufferUploader.drawWithShader(buf.buildOrThrow());
         RenderSystem.enableCull();
         RenderSystem.depthMask(true);
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         mv.popMatrix();
         RenderSystem.applyModelViewMatrix();
      }
   }

   private static double sparkPhase(int i) {
      return i * 2.399963 % 6.2831853;
   }

   private static void drawScreenTint(float r, float g, float b, float a) {
      if (!(a <= 0.0F)) {
         Matrix4f oldProj = new Matrix4f(RenderSystem.getProjectionMatrix());
         VertexSorting oldSorting = RenderSystem.getVertexSorting();
         RenderSystem.setProjectionMatrix(new Matrix4f(), VertexSorting.ORTHOGRAPHIC_Z);
         Matrix4fStack mv = RenderSystem.getModelViewStack();
         mv.pushMatrix();
         mv.identity();
         RenderSystem.applyModelViewMatrix();
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         BufferBuilder buf = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
         buf.addVertex(-1.0F, -1.0F, 0.0F).setColor(r, g, b, a);
         buf.addVertex(1.0F, -1.0F, 0.0F).setColor(r, g, b, a);
         buf.addVertex(1.0F, 1.0F, 0.0F).setColor(r, g, b, a);
         buf.addVertex(-1.0F, 1.0F, 0.0F).setColor(r, g, b, a);
         BufferUploader.drawWithShader(buf.buildOrThrow());
         RenderSystem.enableDepthTest();
         RenderSystem.enableCull();
         RenderSystem.depthMask(true);
         RenderSystem.disableBlend();
         mv.popMatrix();
         RenderSystem.applyModelViewMatrix();
         RenderSystem.setProjectionMatrix(oldProj, oldSorting);
      }
   }

   private static void drawSkyDome(float r, float g, float b, float a) {
      if (!(a <= 0.0F)) {
         Matrix4f oldProj = new Matrix4f(RenderSystem.getProjectionMatrix());
         VertexSorting oldSorting = RenderSystem.getVertexSorting();
         RenderSystem.setProjectionMatrix(new Matrix4f(), VertexSorting.ORTHOGRAPHIC_Z);
         Matrix4fStack mv = RenderSystem.getModelViewStack();
         mv.pushMatrix();
         mv.identity();
         RenderSystem.applyModelViewMatrix();
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.enableDepthTest();
         RenderSystem.depthFunc(515);
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         float z = 0.99998F;
         BufferBuilder buf = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
         buf.addVertex(-1.0F, -1.0F, z).setColor(r, g, b, a);
         buf.addVertex(1.0F, -1.0F, z).setColor(r, g, b, a);
         buf.addVertex(1.0F, 1.0F, z).setColor(r, g, b, a);
         buf.addVertex(-1.0F, 1.0F, z).setColor(r, g, b, a);
         BufferUploader.drawWithShader(buf.buildOrThrow());
         RenderSystem.enableCull();
         RenderSystem.depthMask(true);
         RenderSystem.disableBlend();
         mv.popMatrix();
         RenderSystem.applyModelViewMatrix();
         RenderSystem.setProjectionMatrix(oldProj, oldSorting);
      }
   }

   private static void drawStars(WorldRenderContext context, float k, float t) {
      if (starDir != null) {
         double d = skyDistance();
         Matrix4f view = new Matrix4f().rotation(context.camera().rotation().conjugate(new Quaternionf()));
         Matrix4fStack mv = RenderSystem.getModelViewStack();
         mv.pushMatrix();
         mv.identity();
         RenderSystem.applyModelViewMatrix();
         RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
         RenderSystem.setShaderTexture(0, T_DOT);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         BufferBuilder b = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

         for (int i = 0; i < starDir.length; i++) {
            Vec3 dir = starDir[i];
            Vec3 c = dir.scale(d);
            Vec3 x = frameX(dir).scale(starSize[i] * d);
            Vec3 y = frameY(dir).scale(starSize[i] * d);
            float sr = 0.8F + 0.2F * starBright[i];
            float sg = 0.88F;
            float sb = 1.0F - 0.25F * (1.0F - starBright[i]);
            float a = k * starBright[i] * (0.75F + 0.25F * (float)Math.sin(t * 2.3F + starPhase[i]));
            b.addVertex(view, (float)(c.x - x.x - y.x), (float)(c.y - x.y - y.y), (float)(c.z - x.z - y.z)).setUv(0.0F, 1.0F).setColor(sr, sg, sb, a);
            b.addVertex(view, (float)(c.x + x.x - y.x), (float)(c.y + x.y - y.y), (float)(c.z + x.z - y.z)).setUv(1.0F, 1.0F).setColor(sr, sg, sb, a);
            b.addVertex(view, (float)(c.x + x.x + y.x), (float)(c.y + x.y + y.y), (float)(c.z + x.z + y.z)).setUv(1.0F, 0.0F).setColor(sr, sg, sb, a);
            b.addVertex(view, (float)(c.x - x.x + y.x), (float)(c.y - x.y + y.y), (float)(c.z - x.z + y.z)).setUv(0.0F, 0.0F).setColor(sr, sg, sb, a);
         }

         BufferUploader.drawWithShader(b.buildOrThrow());
         RenderSystem.enableCull();
         RenderSystem.depthMask(true);
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         mv.popMatrix();
         RenderSystem.applyModelViewMatrix();
      }
   }

   private static void quad(
      WorldRenderContext context, ResourceLocation texture, Vec3 dir, double halfTan, float r, float g, float b, float alpha, boolean additive
   ) {
      if (!(alpha <= 0.003F)) {
         double d = skyDistance();
         float half = (float)(halfTan * d);
         Vec3 offset = dir.normalize().scale(d);
         Matrix4f view = new Matrix4f().rotation(context.camera().rotation().conjugate(new Quaternionf()));
         Matrix4f matrix = new Matrix4f(view).translate((float)offset.x, (float)offset.y, (float)offset.z).mul(skyBasis(offset));
         Matrix4fStack mv = RenderSystem.getModelViewStack();
         mv.pushMatrix();
         mv.identity();
         RenderSystem.applyModelViewMatrix();
         ShaderInstance custom = activeShader;
         if (custom != null) {
            RenderSystem.setShader(() -> custom);
         } else {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
         }

         RenderSystem.setShaderTexture(0, texture);
         RenderSystem.enableBlend();
         if (additive) {
            RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
         } else {
            RenderSystem.defaultBlendFunc();
         }

         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         BufferBuilder buf = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         buf.addVertex(matrix, -half, -half, 0.0F).setUv(0.0F, 1.0F).setColor(r, g, b, alpha);
         buf.addVertex(matrix, half, -half, 0.0F).setUv(1.0F, 1.0F).setColor(r, g, b, alpha);
         buf.addVertex(matrix, half, half, 0.0F).setUv(1.0F, 0.0F).setColor(r, g, b, alpha);
         buf.addVertex(matrix, -half, half, 0.0F).setUv(0.0F, 0.0F).setColor(r, g, b, alpha);
         BufferUploader.drawWithShader(buf.buildOrThrow());
         RenderSystem.enableCull();
         RenderSystem.depthMask(true);
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         mv.popMatrix();
         RenderSystem.applyModelViewMatrix();
      }
   }

   private static Vec3 frameX(Vec3 dir) {
      Vec3 n = dir.normalize();
      Vec3 helper = Math.abs(n.y) < 0.9 ? new Vec3(0.0, 1.0, 0.0) : new Vec3(1.0, 0.0, 0.0);
      return n.cross(helper).normalize();
   }

   private static Vec3 frameY(Vec3 dir) {
      Vec3 n = dir.normalize();
      return n.cross(frameX(n));
   }

   private static Matrix4f skyBasis(Vec3 dir) {
      Vec3 n = dir.normalize();
      Vec3 x = frameX(n);
      Vec3 y = frameY(n).scale(-1.0);
      Vec3 z = n.scale(-1.0);
      return new Matrix4f(
         (float)x.x, (float)x.y, (float)x.z, 0.0F, (float)y.x, (float)y.y, (float)y.z, 0.0F, (float)z.x, (float)z.y, (float)z.z, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F
      );
   }

   private static double skyDistance() {
      double far = Minecraft.getInstance().options.getEffectiveRenderDistance() * 16.0 * 4.0;
      return Math.max(100.0, far * 0.85);
   }
}
