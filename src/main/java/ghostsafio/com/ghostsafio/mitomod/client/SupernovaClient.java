package com.ghostsafio.mitomod.client;

import com.ghostsafio.mitomod.Ghostsafio;
import com.ghostsafio.mitomod.SupernovaPayload;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class SupernovaClient {
   private static final ResourceLocation SUN_TEXTURE = Ghostsafio.id("textures/effect/supernova_sun.png");
   private static final ResourceLocation STAR_TEXTURE = Ghostsafio.id("textures/effect/supernova_star.png");
   private static final ResourceLocation RING_TEXTURE = Ghostsafio.id("textures/effect/supernova_ring.png");
   private static final ResourceLocation GLOW_TEXTURE = Ghostsafio.id("textures/effect/supernova_glow.png");
   private static final ResourceLocation DOT_TEXTURE = Ghostsafio.id("textures/effect/supernova_dot.png");
   private static final ResourceLocation NEBULA_TEXTURE = Ghostsafio.id("textures/effect/supernova_nebula.png");
   private static final ResourceLocation JET_TEXTURE = Ghostsafio.id("textures/effect/supernova_jet.png");
   private static final ResourceLocation BLAST_TEXTURE = Ghostsafio.id("textures/effect/supernova_blast.png");
   private static final ResourceLocation SHOCK_TEXTURE = Ghostsafio.id("textures/effect/supernova_shock.png");
   private static final ResourceLocation RAYS_TEXTURE = Ghostsafio.id("textures/effect/supernova_rays.png");
   private static final ResourceLocation SUN_SURFACE = Ghostsafio.id("textures/effect/supernova_sun_surface.png");
   private static final ResourceLocation CORONA_TEXTURE = Ghostsafio.id("textures/effect/supernova_corona.png");
   private static final ResourceLocation FIRE_SURFACE = Ghostsafio.id("textures/effect/supernova_fire_surface.png");
   private static final int RING_FRAMES = 8;
   private static final ResourceLocation[] RING_ANIM = new ResourceLocation[8];
   private static final ResourceLocation REMNANT_TEXTURE;
   private static final double SUN_SIZE = 120.0;
   private static final double RING_SIZE_START = 100.0;
   private static final double RING_SIZE_END = 260.0;
   private static final double RING_GROWTH_CURVE = 1.25;
   private static final double REMNANT_SIZE_START = 70.0;
   private static final double REMNANT_SIZE_END = 300.0;
   private static final double NEBULA_SCALE = 1.55;
   private static final float NEBULA_INTENSITY = 0.72F;
   private static final double JET_LENGTH_FACTOR = 1.7;
   private static final double JET_WIDTH_FACTOR = 0.2;
   private static final float JET_SPIN = 0.018F;
   private static final double FIREBALL_SIZE = 900.0;
   private static final double BLAST_SIZE_START = 30.0;
   private static final double BLAST_SIZE_END = 340.0;
   private static final double SHOCK_SIZE_END = 1300.0;
   private static final int CHARGE_TICKS = 24;
   private static final float SHAKE_STRENGTH = 1.0F;
   private static final float SHAKE_YAW_DEG = 5.0F;
   private static final float SHAKE_PITCH_DEG = 3.4F;
   private static final int PULSE_START_TICKS = 150;
   private static final int PULSE_PERIOD_TICKS = 100;
   private static final double RAYS_SIZE = 1100.0;
   private static final boolean SHOW_SPARKS = false;
   private static final int SPARK_COUNT = 40;
   private static final boolean SHOW_AURORA = true;
   private static final int AURORA_CURTAINS = 5;
   private static final int AURORA_COLUMNS = 110;
   private static final float AURORA_STRENGTH = 1.0F;
   private static final int DOT_COUNT = 360;
   private static final double DOT_DISTANCE = 200.0;
   private static final int DEBRIS_COUNT = 140;
   private static final double DEBRIS_DISTANCE = 200.0;
   private static final double CONSUME_START = 0.35;
   private static final double CONSUME_END = 0.8;
   private static final double DOT_TRAVEL = 0.12;
   private static final double REF_DISTANCE = 250.0;
   private static final double MIN_ELEVATION = 0.17;
   private static final float FLASH_PEAK = 1.0F;
   private static final int FLASH_RISE_TICKS = 2;
   private static final int FLASH_HOLD_TICKS = 8;
   private static final int FLASH_FADE_TICKS = 100;
   private static ShaderInstance activeShader;
   private static int lastTickSeen;
   private static long lastTickNano;
   private static final ResourceLocation WHITE_TEXTURE;
   private static boolean active;
   private static int ticks;
   private static int countdownTicks;
   private static int waveTicks;
   private static int fadeTicks;
   private static double cx;
   private static double cy;
   private static double cz;
   private static Vec3 skyDir;
   private static boolean skyDirSet;
   private static Vec3 skyE1;
   private static Vec3 skyE2;
   private static final Random SHAKE_RANDOM;
   private static float shakeYaw;
   private static float shakePitch;
   private static final Vec3[] dotDir;
   private static final double[] dotSize;
   private static final double[] dotDelay;
   private static final double[] dotConsume;
   private static final float[][] dotColor;
   private static final int EJECTA_COUNT = 64;
   private static final Vec3[] debrisOff;
   private static final double[] debrisSize;
   private static final boolean[] debrisDark;
   private static final double[] debrisDrift;

   public static void register() {
      SupernovaShaders.register();
      ClientPlayNetworking.registerGlobalReceiver(SupernovaPayload.TYPE, (payload, context) -> {
         if (payload.start()) {
            boolean fresh = !active;
            int serverTicks = payload.elapsedTicks();
            active = true;
            countdownTicks = payload.countdownTicks();
            waveTicks = Math.max(1, payload.waveTicks());
            fadeTicks = Math.max(1, payload.fadeTicks());
            cx = payload.x();
            cy = payload.y();
            cz = payload.z();
            if (fresh) {
               initDots();
               skyDirSet = false;
               ticks = serverTicks;
            } else if (Math.abs(ticks - serverTicks) > 2) {
               ticks = serverTicks;
            }
         } else {
            active = false;
         }
      });
      ClientPlayConnectionEvents.DISCONNECT.register((Disconnect)(handler, client) -> active = false);
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)client -> {
         if (client.player != null && (shakeYaw != 0.0F || shakePitch != 0.0F)) {
            client.player.setYRot(client.player.getYRot() - shakeYaw);
            client.player.setXRot(Mth.clamp(client.player.getXRot() - shakePitch, -90.0F, 90.0F));
         }

         shakeYaw = 0.0F;
         shakePitch = 0.0F;
         if (active && !client.isPaused()) {
            ticks++;
            float s = Math.max(shakeIntensity(), 0.35F * beat()) * 1.0F;
            if (client.player != null && s > 0.0F) {
               float beforePitch = client.player.getXRot();
               shakeYaw = (SHAKE_RANDOM.nextFloat() - 0.5F) * 2.0F * 5.0F * s;
               client.player.setYRot(client.player.getYRot() + shakeYaw);
               float afterPitch = Mth.clamp(beforePitch + (SHAKE_RANDOM.nextFloat() - 0.5F) * 2.0F * 3.4F * s, -90.0F, 90.0F);
               client.player.setXRot(afterPitch);
               shakePitch = afterPitch - beforePitch;
            }
         }
      });
      WorldRenderEvents.AFTER_TRANSLUCENT.register(SupernovaClient::renderWorld);
      HudRenderCallback.EVENT.register((HudRenderCallback)(graphics, tickCounter) -> {
         float alpha = Math.max(flashAlpha(), 0.2F * beat());
         if (!(alpha <= 0.0F)) {
            Minecraft mc = Minecraft.getInstance();
            int a = (int)(alpha * 255.0F);
            graphics.fill(0, 0, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight(), a << 24 | 16777215);
         }
      });
   }

   private static void initDots() {
      Random random = new Random(12345L);

      for (int i = 0; i < 360; i++) {
         Vec3 v;
         do {
            v = new Vec3(random.nextGaussian(), random.nextGaussian(), random.nextGaussian());
         } while (v.lengthSqr() < 0.01 || v.normalize().y < 0.02);

         dotDir[i] = v.normalize();
         dotSize[i] = 2.0 + random.nextDouble() * random.nextDouble() * 9.0;
         dotDelay[i] = random.nextDouble() * 0.45;
         dotConsume[i] = 0.35 + random.nextDouble() * 0.45000000000000007;
         double c = random.nextDouble();
         float[] col = c < 0.5
            ? new float[]{0.78F, 0.88F, 1.0F}
            : (c < 0.78 ? new float[]{1.0F, 1.0F, 1.0F} : (c < 0.92 ? new float[]{1.0F, 0.92F, 0.7F} : new float[]{1.0F, 0.72F, 0.52F}));
         dotColor[i] = col;
      }

      for (int i = 0; i < 140; i++) {
         debrisOff[i] = new Vec3(random.nextGaussian(), random.nextGaussian(), random.nextGaussian()).scale(0.9);
         debrisSize[i] = 0.8 + random.nextDouble() * random.nextDouble() * 3.0;
         debrisDark[i] = random.nextDouble() < 0.55;
         debrisDrift[i] = 0.2 + random.nextDouble();
      }
   }

   private static void renderWorld(WorldRenderContext context) {
      if (active) {
         ensureSkyDir(context);
         if (ticks < countdownTicks) {
            double t = (double)ticks / Math.max(1, countdownTicks);
            drawDots(context, t);
            drawSun(context, t);
            int left = countdownTicks - ticks;
            if (left <= 24) {
               drawCharge(context, 1.0 - left / 24.0);
            }
         } else {
            drawWave(context);
         }
      }
   }

   private static double consumedFraction(double t) {
      double total = 0.0;

      for (int i = 0; i < 360; i++) {
         total += smooth((t - dotConsume[i]) / 0.12);
      }

      return total / 360.0;
   }

   private static void drawSun(WorldRenderContext context, double t) {
      float fadeIn = (float)Math.min(1.0, t / 0.08);
      double eaten = consumedFraction(t);
      double whiten = smooth(eaten);
      double size = 120.0 * (1.0 - 0.7 * eaten);
      float orange = fadeIn * (float)(1.0 - whiten);
      float flick = 1.0F + (0.03F + 0.07F * (float)t) * (float)Math.sin(ticks * 0.9);
      float tf = ticks + partialFrac();
      if (SupernovaShaders.ok(SupernovaShaders.CORONA) && SupernovaShaders.ok(SupernovaShaders.SUN)) {
         fx(context, SupernovaShaders.CORONA, null, size * 1.2 * flick, -tf * 7.0E-4F, 1.0F, 1.0F, 1.0F, orange * 0.95F, true, 0.5F / flick, 0.0F, 0.0F);
      } else {
         drawBillboardTint(context, CORONA_TEXTURE, size * 1.2 * flick, -ticks * 7.0E-4F, 1.0F, 1.0F, 1.0F, orange * 0.95F, true);
      }

      drawBillboardTint(context, GLOW_TEXTURE, size * 1.9, 0.0F, 1.0F, 0.55F, 0.2F, orange * 0.5F, true);
      if (SupernovaShaders.ok(SupernovaShaders.SUN)) {
         fxSphere(context, SupernovaShaders.SUN, size * 0.6, tf * 0.0011F, 0.38F, 1.0F, 1.0F, 1.0F, orange, false, (float)whiten * 0.9F);
      } else {
         drawSphere(context, SUN_SURFACE, size * 0.6, ticks * 0.0011F, 0.38F, 1.0F, 1.0F, 1.0F, orange, false, 0.28F, 0.55F);
      }

      drawBillboardTint(context, GLOW_TEXTURE, size * 0.95, 0.0F, 1.0F, 0.8F, 0.45F, orange * 0.35F * flick, true);
      double pulse = 1.0 + 0.12 * Math.sin(ticks * 0.8) * whiten;
      starFx(context, size * 1.3 * pulse, 0.0F, 1.0F, 1.0F, 1.0F, fadeIn * (float)whiten);
      drawBillboardTint(context, GLOW_TEXTURE, size * 1.1 * pulse, 0.0F, 1.0F, 0.95F, 0.85F, fadeIn * (float)whiten * 0.8F, true);
   }

   private static void drawDots(WorldRenderContext context, double t) {
      Vec3 sunDir = skyDir;
      double d = skyDistance();
      double k = d / 200.0;
      Camera camera = context.camera();
      Vector3f rightV = new Vector3f(1.0F, 0.0F, 0.0F).rotate(camera.rotation());
      Vector3f upV = new Vector3f(0.0F, 1.0F, 0.0F).rotate(camera.rotation());
      Matrix4f view = new Matrix4f().rotation(camera.rotation().conjugate(new Quaternionf()));
      BufferBuilder buf = spriteBegin(DOT_TEXTURE, true);

      for (int i = 0; i < 360; i++) {
         double appear = smooth((t - dotDelay[i]) / 0.08);
         if (!(appear <= 0.0)) {
            double travel = Math.max(0.0, Math.min(1.0, (t - dotConsume[i]) / 0.12));
            if (!(travel >= 1.0)) {
               double pull = smooth(travel);
               Vec3 dir = dotDir[i].scale(1.0 - pull).add(sunDir.scale(pull)).normalize();
               float twinkle = 0.7F + 0.3F * (float)Math.sin((ticks + i * 7) * 0.2 + Math.sin(i * 1.7) * 3.0);
               float a = (float)appear * twinkle * (float)(1.0 - 0.5 * travel);
               float half = (float)(dotSize[i] * 0.9 * (1.0 - 0.6 * travel) * k);
               sprite(buf, view, rightV, upV, dir.scale(d), half, dotColor[i], a);
            }
         }
      }

      spriteEnd(buf);

      for (int i = 0; i < 360; i++) {
         if (!(dotSize[i] < 8.0)) {
            double appear = smooth((t - dotDelay[i]) / 0.08);
            double travel = Math.max(0.0, Math.min(1.0, (t - dotConsume[i]) / 0.12));
            if (!(appear <= 0.0) && !(travel >= 1.0)) {
               double pull = smooth(travel);
               Vec3 dir = dotDir[i].scale(1.0 - pull).add(sunDir.scale(pull)).normalize();
               float h = (float)(dotSize[i] * 2.2 * (1.0 - 0.6 * travel) * k);
               float tw = 0.65F + 0.35F * (float)Math.sin((ticks + i * 5) * 0.15);
               if (SupernovaShaders.ok(SupernovaShaders.STAR)) {
                  SupernovaShaders.params(SupernovaShaders.STAR, 1.0F, 0.0F, 0.0F);
                  activeShader = SupernovaShaders.STAR;
               }

               try {
                  drawQuad(
                     context,
                     STAR_TEXTURE,
                     dir.scale(d),
                     -h,
                     h,
                     -h,
                     h,
                     i * 0.37F,
                     dotColor[i][0],
                     dotColor[i][1],
                     dotColor[i][2],
                     (float)appear * tw * 0.75F,
                     true
                  );
               } finally {
                  activeShader = null;
               }
            }
         }
      }
   }

   private static BufferBuilder spriteBegin(ResourceLocation texture, boolean additive) {
      ShaderInstance starShader = SupernovaShaders.ok(SupernovaShaders.STAR) ? SupernovaShaders.STAR : null;
      if (starShader != null) {
         SupernovaShaders.params(starShader, 0.35F, 0.0F, 0.0F);
      }

      Matrix4fStack modelView = RenderSystem.getModelViewStack();
      modelView.pushMatrix();
      modelView.identity();
      RenderSystem.applyModelViewMatrix();
      if (starShader != null) {
         RenderSystem.setShader(() -> starShader);
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
      return Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
   }

   private static void sprite(BufferBuilder b, Matrix4f view, Vector3f right, Vector3f up, Vec3 c, float h, float[] col, float a) {
      if (!(a <= 0.0F)) {
         float rx = right.x * h;
         float ry = right.y * h;
         float rz = right.z * h;
         float ux = up.x * h;
         float uy = up.y * h;
         float uz = up.z * h;
         float cx = (float)c.x;
         float cy = (float)c.y;
         float cz = (float)c.z;
         b.addVertex(view, cx - rx - ux, cy - ry - uy, cz - rz - uz).setUv(0.0F, 1.0F).setColor(col[0], col[1], col[2], a);
         b.addVertex(view, cx + rx - ux, cy + ry - uy, cz + rz - uz).setUv(1.0F, 1.0F).setColor(col[0], col[1], col[2], a);
         b.addVertex(view, cx + rx + ux, cy + ry + uy, cz + rz + uz).setUv(1.0F, 0.0F).setColor(col[0], col[1], col[2], a);
         b.addVertex(view, cx - rx + ux, cy - ry + uy, cz - rz + uz).setUv(0.0F, 0.0F).setColor(col[0], col[1], col[2], a);
      }
   }

   private static void spriteEnd(BufferBuilder b) {
      MeshData mesh = b.build();
      if (mesh != null) {
         BufferUploader.drawWithShader(mesh);
      }

      RenderSystem.enableCull();
      RenderSystem.depthMask(true);
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
      RenderSystem.getModelViewStack().popMatrix();
      RenderSystem.applyModelViewMatrix();
   }

   private static void drawSphere(
      WorldRenderContext context,
      ResourceLocation texture,
      double size,
      float spin,
      float tilt,
      float r,
      float g,
      float b,
      float alpha,
      boolean additive,
      float limbMin,
      float limbPow
   ) {
      if (!(alpha <= 0.0F)) {
         Camera camera = context.camera();
         double d = skyDistance();
         float radius = (float)(size * (d / 250.0) / 2.0);
         Matrix4f view = new Matrix4f().rotation(camera.rotation().conjugate(new Quaternionf()));
         Matrix4f matrix = new Matrix4f(view)
            .translate((float)(skyDir.x * d), (float)(skyDir.y * d), (float)(skyDir.z * d))
            .mul(skyBasis(skyDir))
            .rotateZ(tilt);
         Matrix4fStack modelView = RenderSystem.getModelViewStack();
         modelView.pushMatrix();
         modelView.identity();
         RenderSystem.applyModelViewMatrix();
         ShaderInstance customSphere = activeShader;
         if (customSphere != null) {
            RenderSystem.setShader(() -> customSphere);
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
         int lat = 40;
         int lon = 40;
         BufferBuilder buf = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

         for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 40; j++) {
               sphereVertex(buf, matrix, radius, i, j, 40, 40, spin, r, g, b, alpha, limbMin, limbPow);
               sphereVertex(buf, matrix, radius, i, j + 1, 40, 40, spin, r, g, b, alpha, limbMin, limbPow);
               sphereVertex(buf, matrix, radius, i + 1, j + 1, 40, 40, spin, r, g, b, alpha, limbMin, limbPow);
               sphereVertex(buf, matrix, radius, i + 1, j, 40, 40, spin, r, g, b, alpha, limbMin, limbPow);
            }
         }

         BufferUploader.drawWithShader(buf.buildOrThrow());
         RenderSystem.enableCull();
         RenderSystem.depthMask(true);
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         modelView.popMatrix();
         RenderSystem.applyModelViewMatrix();
      }
   }

   private static void sphereVertex(
      BufferBuilder buf,
      Matrix4f matrix,
      float radius,
      int i,
      int j,
      int lat,
      int lon,
      float spin,
      float r,
      float g,
      float b,
      float alpha,
      float limbMin,
      float limbPow
   ) {
      double la = (-Math.PI / 2) + Math.PI * i / lat;
      double lo = (-Math.PI / 2) + Math.PI * j / lon;
      float nx = (float)(Math.cos(la) * Math.sin(lo));
      float ny = (float)Math.sin(la);
      float nz = (float)(Math.cos(la) * Math.cos(lo));
      float shade = limbMin + (1.0F - limbMin) * (float)Math.pow(Math.max(nz, 0.0F), limbPow);
      float red = Math.min(1.0F, shade * 1.1F);
      float blu = shade * (0.85F + 0.15F * nz);
      if (activeShader != null) {
         buf.addVertex(matrix, nx * radius, ny * radius, nz * radius).setUv(nx, ny).setColor(r, g, b, alpha);
      } else {
         float u = (float)(lo / (Math.PI * 2) + 0.25 + spin);
         float v = (float)(0.5 - la / Math.PI);
         buf.addVertex(matrix, nx * radius, ny * radius, nz * radius).setUv(u, v).setColor(r * red, g * shade, b * blu, alpha);
      }
   }

   private static void drawWave(WorldRenderContext context) {
      int since = ticks - countdownTicks;
      double p = Math.min(1.0, (double)since / waveTicks);
      float fade = 1.0F;
      if (since > waveTicks) {
         fade = 1.0F - (float)(since - waveTicks) / fadeTicks;
      }

      if (!(fade <= 0.0F)) {
         float beat = beat();
         double ringSize = 100.0 + 160.0 * Math.pow(p, 1.25);
         ringSize *= 1.0 + 0.06 * beat;
         float vanish = (float)(1.0 - smooth((p - 0.86) / 0.14));
         float glowAlpha = (float)(smooth((p - 0.1) / 0.6) * 0.95) * vanish;
         float ringAlpha = (float)(0.95 * smooth((since - 25) / 45.0)) * vanish;
         float nebulaAlpha = (float)(smooth((since - 15) / 60.0) * (1.0 - 0.35 * smooth((p - 0.3) / 0.7))) * 0.72F;
         float jetAlpha = (float)(smooth((since - 55) / 45.0) * (1.0 - 0.5 * smooth((p - 0.5) / 0.5))) * vanish;
         float raysAlpha = (float)(smooth((since - 40) / 60.0) * (0.45 + 0.3 * beat)) * vanish;
         drawDebris(context, since, fade);
         drawBillboardTint(context, NEBULA_TEXTURE, ringSize * 1.55, since * 0.0015F, 1.0F, 0.9F, 0.94F, nebulaAlpha * fade, false);
         drawBillboardTint(context, NEBULA_TEXTURE, ringSize * 2.0, -since * 0.0012F, 0.45F, 0.95F, 1.0F, nebulaAlpha * 0.3F * fade, true);
         drawBillboardTint(context, NEBULA_TEXTURE, ringSize * 2.8, since * 8.0E-4F, 1.0F, 0.5F, 0.85F, nebulaAlpha * 0.24F * fade, true);
         float remAlpha = (float)(smooth((since - 8) / 50.0) * (1.0 - 0.55 * smooth((p - 0.4) / 0.6))) * vanish;
         double remSize = 70.0 + 230.0 * easeOut(Math.min(1.0, since / (waveTicks * 1.1)));
         fx(context, SupernovaShaders.NEBULA, REMNANT_TEXTURE, remSize, since * 9.0E-4F, 1.0F, 1.0F, 1.0F, remAlpha * 0.95F * fade, true, 0.58F, 0.0F, 0.0F);
         fx(
            context,
            SupernovaShaders.NEBULA,
            REMNANT_TEXTURE,
            remSize * 0.62,
            -since * 0.0014F,
            0.8F,
            1.0F,
            1.0F,
            remAlpha * 0.55F * fade,
            true,
            0.5F,
            0.0F,
            0.0F
         );
         float auroraEnv = (float)(smooth((since - waveTicks) / 100.0) * (1.0 - smooth((since - (waveTicks + fadeTicks - 160)) / 160.0)));
         drawAurora(context, auroraEnv, ticks + partialFrac());
         fx(context, SupernovaShaders.RAYS, RAYS_TEXTURE, 1100.0, since * 0.0035F, 1.0F, 0.92F, 0.96F, raysAlpha * fade, true, 0.0F, 0.0F, 0.0F);
         fx(context, SupernovaShaders.RAYS, RAYS_TEXTURE, 880.0, -since * 0.0024F, 0.75F, 0.95F, 1.0F, raysAlpha * 0.6F * fade, true, 0.0F, 0.0F, 0.0F);
         drawExplosion(context, since, fade);
         drawBillboardEx(context, GLOW_TEXTURE, ringSize * 1.15, 0.0F, Math.min(1.0F, glowAlpha * (1.0F + 0.35F * beat)) * fade, true);
         drawRingAnimated(context, ringSize, since, p, ringAlpha * fade);
         drawPulseWave(context, since, fade);
         double endP = (p - 0.88) / 0.12;
         if (endP > 0.0 && endP < 1.0) {
            fx(
               context,
               SupernovaShaders.SHOCK,
               SHOCK_TEXTURE,
               ringSize * (1.0 + 1.6 * easeOut(endP)),
               0.0F,
               1.0F,
               0.8F,
               0.9F,
               (float)Math.pow(1.0 - endP, 1.5) * 0.8F * fade,
               true,
               0.0F,
               0.0F,
               0.0F
            );
         }

         float spin = since * 0.018F + 0.6F;
         double jetLen = ringSize * 1.7 * (1.0 + 0.9 * beat);
         float jetA = Math.min(1.0F, jetAlpha * (1.0F + 0.5F * beat)) * fade;

         for (int i = 0; i < 2; i++) {
            drawJet(context, jetLen, ringSize * 0.2, spin + i * (float) Math.PI, jetA);
            drawJet(context, jetLen * 0.62, ringSize * 0.2 * 0.55, -spin * 0.7F + 1.1F + i * (float) Math.PI, jetA * 0.8F);
         }

         drawSunrise(context, since);
      }
   }

   private static void drawCharge(WorldRenderContext context, double c) {
      double e = c * c;
      float vibrate = 1.0F + 0.06F * (float)Math.sin(ticks * 2.3);
      drawBillboardEx(context, GLOW_TEXTURE, (20.0 + 170.0 * e) * vibrate, 0.0F, (float)(0.75 * c), true);
      starFx(context, (50.0 + 150.0 * e) * vibrate, (float)(c * 2.5), 1.0F, 1.0F, 1.0F, (float)Math.min(1.0, 0.6 + c));
   }

   private static void drawExplosion(WorldRenderContext context, int since, float fade) {
      if (since <= 170) {
         if (since < 80) {
            double d = skyDistance();
            double k = d / 250.0;
            float a = (float)(Math.min(1.0, since / 2.0) * (1.0 - smooth(since / 80.0))) * fade;
            float lx = (float)(2200.0 * k / 2.0);
            drawQuad(context, GLOW_TEXTURE, skyDir.scale(d), -lx, lx, (float)(-46.0 * k), (float)(46.0 * k), 0.0F, 0.7F, 0.88F, 1.0F, a, true);
            drawQuad(context, GLOW_TEXTURE, skyDir.scale(d), -lx, lx, (float)(-12.0 * k), (float)(12.0 * k), 0.0F, 1.0F, 1.0F, 1.0F, a, true);
         }

         if (since < 16) {
            float a = (float)(1.0 - smooth(since / 16.0));
            starFx(context, 220.0 * (1.0 + since / 10.0), since * 0.05F, 1.0F, 1.0F, 1.0F, a * fade);
         }

         double bp = Math.min(1.0, since / 70.0);
         double blastSize = 30.0 + 310.0 * easeOut(bp);
         float blastAlpha = (float)(smooth(since / 6.0) * (1.0 - smooth((since - 50) / 90.0)));
         drawBillboardEx(context, BLAST_TEXTURE, blastSize, since * 0.002F, blastAlpha * fade, false);
         if (since < 200) {
            double fp = Math.min(1.0, since / 80.0);
            double fsz = 30.0 + 600.0 * easeOut(fp);
            float fa = (float)(Math.min(1.0, since / 3.0) * (1.0 - smooth((since - 70) / 125.0)));
            float cool = (float)smooth(since / 130.0);
            float spinA = since * 0.0045F * (float)(1.0 - 0.6 * fp);
            if (SupernovaShaders.ok(SupernovaShaders.FIRE)) {
               float sf = since + partialFrac();
               fxSphere(context, SupernovaShaders.FIRE, fsz, sf * 0.0045F * (float)(1.0 - 0.6 * fp), 0.4F, 1.0F, 1.0F, 1.0F, fa * 0.95F * fade, true, cool);
               fxSphere(
                  context,
                  SupernovaShaders.FIRE,
                  fsz * 0.8,
                  -sf * 0.0065F + 0.4F,
                  -0.55F,
                  1.0F,
                  0.92F,
                  0.85F,
                  fa * 0.6F * fade,
                  true,
                  Math.min(1.0F, cool * 1.15F)
               );
            } else {
               drawSphere(context, FIRE_SURFACE, fsz, spinA, 0.4F, 1.0F, 1.0F - 0.55F * cool, 0.92F - 0.75F * cool, fa * 0.95F * fade, true, 0.3F, 0.7F);
               drawSphere(
                  context,
                  FIRE_SURFACE,
                  fsz * 0.8,
                  -since * 0.0065F,
                  -0.55F,
                  1.0F,
                  0.85F - 0.45F * cool,
                  0.55F - 0.45F * cool,
                  fa * 0.6F * fade,
                  true,
                  0.45F,
                  0.8F
               );
            }
         }

         if (since < 170) {
            for (int i = 0; i < 64; i++) {
               double speed = 0.6 + 0.8 * frac(i * 0.618034);
               double delay = frac(i * 0.381966) * 8.0;
               double sx = since - delay;
               if (!(sx < 0.0) && !(sx > 150.0)) {
                  double q = easeOut(sx / (70.0 + 60.0 / speed));
                  double phi = i * 2.399963 + 0.7;
                  double ang = 0.02 + 0.55 * speed * q;
                  Vec3 radial = skyE1.scale(Math.cos(phi)).add(skyE2.scale(Math.sin(phi)));
                  Vec3 pos = skyDir.scale(Math.cos(ang)).add(radial.scale(Math.sin(ang))).normalize();
                  Vec3 outward = skyDir.scale(-Math.sin(ang)).add(radial.scale(Math.cos(ang)));
                  float heat = (float)smooth(sx / 110.0);
                  float al = (float)Math.pow(1.0 - sx / 150.0, 1.3) * 0.95F * fade;
                  drawStreak(
                     context,
                     pos,
                     outward,
                     25.0 + 150.0 * speed * (1.0 - 0.5 * q),
                     2.2 + 3.0 * frac(i * 0.7071),
                     1.0F,
                     0.95F - 0.45F * heat,
                     0.8F - 0.65F * heat,
                     al
                  );
               }
            }
         }

         double fp = Math.min(1.0, since / 40.0);
         double fireSize = 40.0 + 860.0 * easeOut(fp);
         float fireAlpha = (float)(Math.min(1.0, since / 2.0) * (1.0 - smooth((since - 10) / 70.0)));
         drawBillboardEx(context, GLOW_TEXTURE, fireSize, 0.0F, fireAlpha * 0.85F * fade, true);
         float[][] shockColors = new float[][]{{1.0F, 1.0F, 1.0F}, {1.0F, 0.6F, 0.85F}, {0.55F, 0.95F, 1.0F}};

         for (int i = 0; i < 3; i++) {
            int s = since - i * 9;
            if (s >= 0 && s <= 56) {
               double sp = s / 56.0;
               double shockSize = 20.0 + 1280.0 * easeOut(sp);
               float shockAlpha = (float)Math.pow(1.0 - sp, 1.5);
               fx(
                  context,
                  SupernovaShaders.SHOCK,
                  SHOCK_TEXTURE,
                  shockSize,
                  0.0F,
                  shockColors[i][0],
                  shockColors[i][1],
                  shockColors[i][2],
                  shockAlpha * fade,
                  true,
                  0.0F,
                  0.0F,
                  0.0F
               );
            }
         }
      }
   }

   private static void drawAurora(WorldRenderContext context, float alpha, float time) {
      if (!(alpha <= 0.01F)) {
         Camera camera = context.camera();
         Matrix4f matrix = new Matrix4f().rotation(camera.rotation().conjugate(new Quaternionf()));
         double d = skyDistance();
         Matrix4fStack modelView = RenderSystem.getModelViewStack();
         modelView.pushMatrix();
         modelView.identity();
         RenderSystem.applyModelViewMatrix();
         ShaderInstance auroraShader = SupernovaShaders.ok(SupernovaShaders.AURORA) ? SupernovaShaders.AURORA : null;
         if (auroraShader != null) {
            SupernovaShaders.params(auroraShader, 0.0F, 0.0F, 0.0F);
            RenderSystem.setShader(() -> auroraShader);
         } else {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
         }

         RenderSystem.setShaderTexture(0, WHITE_TEXTURE);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(false);
         RenderSystem.disableCull();
         BufferBuilder buffer = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         float[][][] palette = new float[][][]{
            {{0.25F, 1.0F, 0.45F}, {0.15F, 0.9F, 0.7F}, {0.65F, 0.3F, 1.0F}},
            {{0.3F, 1.0F, 0.55F}, {0.2F, 0.95F, 0.85F}, {0.9F, 0.35F, 0.85F}},
            {{0.2F, 0.95F, 0.5F}, {0.35F, 0.85F, 1.0F}, {0.55F, 0.35F, 1.0F}},
            {{0.45F, 1.0F, 0.4F}, {0.2F, 0.9F, 0.65F}, {1.0F, 0.3F, 0.6F}},
            {{0.2F, 1.0F, 0.6F}, {0.3F, 0.8F, 1.0F}, {0.75F, 0.3F, 0.95F}}
         };

         for (int c = 0; c < 5; c++) {
            float[][] col = palette[c % palette.length];
            double baseAz = c * (Math.PI * 2.0 / 5.0) + 0.6;
            double span = 2.0 + 0.5 * Math.sin(c * 1.7);
            double e0 = 0.09 + 0.05 * Math.sin(c * 2.3);
            double ph = c * 2.1;
            double[] az = new double[111];
            double[] eb = new double[111];
            double[] hh = new double[111];
            float[] br = new float[111];

            for (int i = 0; i <= 110; i++) {
               double u = i / 110.0;
               az[i] = baseAz
                  + span * (u - 0.5)
                  + 0.3 * Math.sin(u * 5.0 + time * 0.011 + ph)
                  + 0.14 * Math.sin(u * 12.0 - time * 0.017 + ph)
                  + 0.05 * Math.sin(u * 31.0 + time * 0.04 + ph);
               eb[i] = e0 + 0.045 * Math.sin(u * 7.0 + time * 0.013 + ph);
               hh[i] = 0.34 + 0.2 * (0.5 + 0.5 * Math.sin(u * 8.0 + time * 0.017 + ph * 1.3)) + 0.08 * Math.sin(u * 23.0 - time * 0.029 + ph);
               double ray = 0.5 + 0.5 * Math.sin(i * 0.65 + 2.6 * Math.sin(u * 17.0 + time * 0.031 + ph) + time * 0.02);
               double ray2 = 0.5 + 0.5 * Math.sin(i * 1.9 - time * 0.037 + ph);
               double env = Math.pow(Math.sin(Math.PI * u), 0.65);
               double shimmer = 0.72 + 0.28 * Math.sin(time * 0.045 + u * 9.0 + ph * 2.0);
               br[i] = (float)(env * (0.2 + 0.6 * ray * ray + 0.35 * ray2 * ray) * shimmer);
            }

            for (int layer = 0; layer < 2; layer++) {
               float lw = layer == 0 ? 1.0F : 0.38F;
               double hs = layer == 0 ? 1.0 : 1.4;
               double wide = layer == 0 ? 0.0 : 0.012;

               for (int i = 0; i < 110; i++) {
                  int j = i + 1;
                  float s0 = layer == 0 ? br[i] : 0.55F + 0.45F * br[i];
                  float s1 = layer == 0 ? br[j] : 0.55F + 0.45F * br[j];
                  float a0 = alpha * 1.0F * s0 * lw * (float)Math.min(1.0, br[i] * 3.0 + 0.2);
                  float a1 = alpha * 1.0F * s1 * lw * (float)Math.min(1.0, br[j] * 3.0 + 0.2);
                  double ti = eb[i] + hh[i] * hs;
                  double tj = eb[j] + hh[j] * hs;
                  double mi = eb[i] + hh[i] * hs * 0.38;
                  double mj = eb[j] + hh[j] * hs * 0.38;
                  double ai = az[i] - wide;
                  double aj = az[j] + wide;
                  float ui = c * 7.3F + i / 110.0F;
                  float uj = c * 7.3F + j / 110.0F;
                  auroraVertex(buffer, matrix, ai, eb[i], d, col[0], a0 * 0.95F, ui, 0.0F);
                  auroraVertex(buffer, matrix, aj, eb[j], d, col[0], a1 * 0.95F, uj, 0.0F);
                  auroraVertex(buffer, matrix, aj, mj, d, col[1], a1 * 0.6F, uj, 0.38F);
                  auroraVertex(buffer, matrix, ai, mi, d, col[1], a0 * 0.6F, ui, 0.38F);
                  auroraVertex(buffer, matrix, ai, mi, d, col[1], a0 * 0.6F, ui, 0.38F);
                  auroraVertex(buffer, matrix, aj, mj, d, col[1], a1 * 0.6F, uj, 0.38F);
                  auroraVertex(buffer, matrix, aj, tj, d, col[2], 0.0F, uj, 1.0F);
                  auroraVertex(buffer, matrix, ai, ti, d, col[2], 0.0F, ui, 1.0F);
               }
            }
         }

         BufferUploader.drawWithShader(buffer.buildOrThrow());
         RenderSystem.enableCull();
         RenderSystem.depthMask(true);
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         modelView.popMatrix();
         RenderSystem.applyModelViewMatrix();
      }
   }

   private static void auroraVertex(BufferBuilder buffer, Matrix4f matrix, double az, double el, double dist, float[] c, float a, float u, float v) {
      float x = (float)(Math.cos(el) * Math.cos(az) * dist);
      float y = (float)(Math.sin(el) * dist);
      float z = (float)(Math.cos(el) * Math.sin(az) * dist);
      buffer.addVertex(matrix, x, y, z).setUv(u, v).setColor(c[0], c[1], c[2], Math.max(0.0F, Math.min(1.0F, a)));
   }

   private static void drawRingAnimated(WorldRenderContext context, double size, int since, double p, float alpha) {
      if (!(alpha <= 0.0F)) {
         double open = smooth((since + partialFrac() - 20.0F) / Math.max(1.0, waveTicks * 0.92));
         open = Math.min(1.0, open + 0.06 * beat());
         if (SupernovaShaders.ok(SupernovaShaders.RING)) {
            fx(context, SupernovaShaders.RING, null, size, since * 0.0035F, 1.0F, 1.0F, 1.0F, alpha, false, (float)open, 0.0F, 0.0F);
            drawBillboardTint(context, GLOW_TEXTURE, size * 1.1, 0.0F, 1.0F, 0.55F, 0.85F, alpha * 0.22F * (1.0F - 0.5F * (float)open), true);
         } else {
            double f = open * 7.0;
            int lo = Math.min(7, (int)Math.floor(f));
            int hi = Math.min(7, lo + 1);
            float mix = (float)(f - lo);
            float roll = since * 0.0035F;
            drawBillboardTint(context, RING_ANIM[lo], size, roll, 1.0F, 1.0F, 1.0F, alpha, false);
            if (hi != lo && mix > 0.01F) {
               drawBillboardTint(context, RING_ANIM[hi], size, roll, 1.0F, 1.0F, 1.0F, alpha * mix, false);
            }

            drawBillboardTint(context, GLOW_TEXTURE, size * 1.1, 0.0F, 1.0F, 0.55F, 0.85F, alpha * 0.22F * (1.0F - 0.5F * (float)open), true);
         }
      }
   }

   private static double easeOut(double p) {
      double q = 1.0 - Math.max(0.0, Math.min(1.0, p));
      return 1.0 - q * q * q;
   }

   private static float beat() {
      if (!active) {
         return 0.0F;
      } else {
         int since = ticks - countdownTicks;
         if (since >= 150 && since <= waveTicks) {
            int phase = (since - 150) % 100;
            return (float)Math.exp(-phase / 14.0);
         } else {
            return 0.0F;
         }
      }
   }

   public static float skyPulse() {
      return beat();
   }

   private static void drawPulseWave(WorldRenderContext context, int since, float fade) {
      int local = since - 150;
      if (local >= 0 && since <= waveTicks) {
         int phase = local % 100;
         if (phase <= 70) {
            double pp = phase / 70.0;
            double size = 60.0 + 1100.0 * easeOut(pp);
            float a = (float)Math.pow(1.0 - pp, 1.4) * 0.75F * fade;
            fx(context, SupernovaShaders.SHOCK, SHOCK_TEXTURE, size, 0.0F, 1.0F, 0.78F, 0.9F, a, true, 0.0F, 0.0F, 0.0F);
         }
      }
   }

   private static void drawSparks(WorldRenderContext context, int since, float fade) {
      float ramp = (float)smooth((since - 90) / 40.0);
      if (!(ramp <= 0.0F)) {
         for (int i = 0; i < 40; i++) {
            double phi = i * 2.399963 + 0.3;
            double speed = 0.0055 + 0.0095 * frac(i * 0.618034);
            double q = frac(since * speed + frac(i * 0.381966));
            double ang = 0.07 + 0.8 * Math.pow(q, 1.4);
            Vec3 radial = skyE1.scale(Math.cos(phi)).add(skyE2.scale(Math.sin(phi)));
            Vec3 pos = skyDir.scale(Math.cos(ang)).add(radial.scale(Math.sin(ang))).normalize();
            Vec3 outward = skyDir.scale(-Math.sin(ang)).add(radial.scale(Math.cos(ang)));
            float alpha = (float)(Math.sin(Math.PI * q) * 0.8) * ramp * fade;
            int kind = i % 3;
            float r = kind == 0 ? 0.55F : 1.0F;
            float g = kind == 0 ? 1.0F : (kind == 1 ? 0.6F : 0.95F);
            float b = kind == 0 ? 0.95F : (kind == 1 ? 0.85F : 1.0F);
            drawStreak(context, pos, outward, 10.0 + 75.0 * q, 1.8 + 1.6 * q, r, g, b, alpha);
         }
      }
   }

   private static void drawSunrise(WorldRenderContext context, int since) {
      int f = since - waveTicks;
      if (f >= 0) {
         double u = (double)f / fadeTicks;
         float a = (float)(smooth(u / 0.25) * (1.0 - smooth((u - 0.55) / 0.45)));
         if (!(a <= 0.0F)) {
            Vec3 dir = new Vec3(skyDir.x, 0.04, skyDir.z).normalize();
            double d = skyDistance();
            double k = d / 250.0;
            float wide = (float)(520.0 * k / 2.0);
            drawQuad(context, GLOW_TEXTURE, dir.scale(d), -wide, wide, -wide * 0.55F, wide * 0.55F, 0.0F, 1.0F, 0.62F, 0.3F, a * 0.85F, true);
            float core = (float)(220.0 * k / 2.0);
            drawQuad(context, GLOW_TEXTURE, dir.scale(d), -core, core, -core, core, 0.0F, 1.0F, 0.88F, 0.65F, a, true);
         }
      }
   }

   private static void drawStreak(WorldRenderContext context, Vec3 headDir, Vec3 moveDir, double length, double width, float r, float g, float b, float alpha) {
      if (!(alpha <= 0.0F)) {
         Vec3 fx = frameX(headDir);
         Vec3 fy = frameY(headDir).scale(-1.0);
         double ax = moveDir.dot(fx);
         double ay = moveDir.dot(fy);
         if (!(ax * ax + ay * ay < 1.0E-8)) {
            float roll = (float)Math.atan2(-ay, -ax);
            double d = skyDistance();
            double k = d / 250.0;
            drawQuad(context, JET_TEXTURE, headDir.scale(d), 0.0F, (float)(length * k), (float)(-width * k), (float)(width * k), roll, r, g, b, alpha, true);
         }
      }
   }

   private static void drawBillboardTint(
      WorldRenderContext context, ResourceLocation texture, double size, float roll, float r, float g, float b, float alpha, boolean additive
   ) {
      double d = skyDistance();
      double scale = d / 250.0;
      float half = (float)(size * scale / 2.0);
      drawQuad(context, texture, skyDir.scale(d), -half, half, -half, half, roll, r, g, b, alpha, additive);
   }

   private static double frac(double x) {
      return x - Math.floor(x);
   }

   private static void drawDebris(WorldRenderContext context, int since, float fade) {
      float appear = (float)smooth(since / 20.0);
      if (!(appear <= 0.0F)) {
         Vec3 sunDir = skyDir;
         double d = skyDistance();
         double k = d / 200.0;

         for (int i = 0; i < 140; i++) {
            double drift = since / 20.0 * 0.02 * debrisDrift[i];
            Vec3 dir = sunDir.add(debrisOff[i].scale(0.6 + drift)).normalize();
            float shade = debrisDark[i] ? 0.1F : 1.0F;
            float h = (float)(debrisSize[i] * 0.5 * k);
            drawQuad(context, DOT_TEXTURE, dir.scale(d), -h, h, -h, h, 0.0F, shade, shade, shade, 0.85F * appear * fade, false);
         }
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

   private static void drawBillboard(WorldRenderContext context, ResourceLocation texture, double size, float alpha) {
      drawBillboardEx(context, texture, size, 0.0F, alpha, false);
   }

   private static void drawBillboardEx(WorldRenderContext context, ResourceLocation texture, double size, float roll, float alpha, boolean additive) {
      double d = skyDistance();
      double scale = d / 250.0;
      float half = (float)(size * scale / 2.0);
      drawQuad(context, texture, skyDir.scale(d), -half, half, -half, half, roll, 1.0F, 1.0F, 1.0F, alpha, additive);
   }

   private static void drawJet(WorldRenderContext context, double length, double halfWidth, float roll, float alpha) {
      double d = skyDistance();
      double scale = d / 250.0;
      drawQuad(
         context,
         JET_TEXTURE,
         skyDir.scale(d),
         0.0F,
         (float)(length * scale),
         (float)(-halfWidth * scale),
         (float)(halfWidth * scale),
         roll,
         1.0F,
         1.0F,
         1.0F,
         alpha,
         true
      );
   }

   private static float partialFrac() {
      long now = System.nanoTime();
      if (ticks != lastTickSeen) {
         lastTickSeen = ticks;
         lastTickNano = now;
      }

      return Math.min(1.0F, (float)(now - lastTickNano) / 5.0E7F);
   }

   private static void fx(
      WorldRenderContext context,
      ShaderInstance shader,
      ResourceLocation fallback,
      double size,
      float roll,
      float r,
      float g,
      float b,
      float alpha,
      boolean additive,
      float p1,
      float p2,
      float p3
   ) {
      if (SupernovaShaders.ok(shader)) {
         SupernovaShaders.params(shader, p1, p2, p3);
         activeShader = shader;

         try {
            drawBillboardTint(context, WHITE_TEXTURE, size, roll, r, g, b, alpha, additive);
         } finally {
            activeShader = null;
         }
      } else if (fallback != null) {
         drawBillboardTint(context, fallback, size, roll, r, g, b, alpha, additive);
      }
   }

   private static void fxSphere(
      WorldRenderContext context,
      ShaderInstance shader,
      double size,
      float spinTurns,
      float tilt,
      float r,
      float g,
      float b,
      float alpha,
      boolean additive,
      float p2
   ) {
      SupernovaShaders.params(shader, spinTurns * (float) (Math.PI * 2), p2, 0.0F);
      activeShader = shader;

      try {
         drawSphere(context, WHITE_TEXTURE, size, 0.0F, tilt, r, g, b, alpha, additive, 1.0F, 1.0F);
      } finally {
         activeShader = null;
      }
   }

   private static void starFx(WorldRenderContext context, double size, float roll, float r, float g, float b, float alpha) {
      fx(context, SupernovaShaders.STAR, STAR_TEXTURE, size, roll, r, g, b, alpha, true, 1.0F, 0.0F, 0.0F);
   }

   private static void drawAt(WorldRenderContext context, ResourceLocation texture, Vec3 offset, float half, float alpha) {
      drawQuad(context, texture, offset, -half, half, -half, half, 0.0F, 1.0F, 1.0F, 1.0F, alpha, false);
   }

   private static void drawQuad(
      WorldRenderContext context,
      ResourceLocation texture,
      Vec3 offset,
      float x0,
      float x1,
      float y0,
      float y1,
      float roll,
      float r,
      float g,
      float b,
      float alpha,
      boolean additive
   ) {
      if (!(alpha <= 0.0F)) {
         Camera camera = context.camera();
         Matrix4f view = new Matrix4f().rotation(camera.rotation().conjugate(new Quaternionf()));
         Matrix4f matrix = new Matrix4f(view).translate((float)offset.x, (float)offset.y, (float)offset.z).mul(skyBasis(offset)).rotateZ(roll);
         Matrix4fStack modelView = RenderSystem.getModelViewStack();
         modelView.pushMatrix();
         modelView.identity();
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
         BufferBuilder buffer = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         buffer.addVertex(matrix, x0, y0, 0.0F).setUv(0.0F, 1.0F).setColor(r, g, b, alpha);
         buffer.addVertex(matrix, x1, y0, 0.0F).setUv(1.0F, 1.0F).setColor(r, g, b, alpha);
         buffer.addVertex(matrix, x1, y1, 0.0F).setUv(1.0F, 0.0F).setColor(r, g, b, alpha);
         buffer.addVertex(matrix, x0, y1, 0.0F).setUv(0.0F, 0.0F).setColor(r, g, b, alpha);
         BufferUploader.drawWithShader(buffer.buildOrThrow());
         RenderSystem.enableCull();
         RenderSystem.depthMask(true);
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         modelView.popMatrix();
         RenderSystem.applyModelViewMatrix();
      }
   }

   private static double skyDistance() {
      double far = Minecraft.getInstance().options.getEffectiveRenderDistance() * 16.0 * 4.0;
      return Math.max(100.0, far * 0.85);
   }

   private static void ensureSkyDir(WorldRenderContext context) {
      if (!skyDirSet) {
         Vec3 d = new Vec3(cx, cy, cz).subtract(context.camera().getPosition());
         if (d.lengthSqr() < 1.0) {
            d = new Vec3(0.0, 1.0, 0.0);
         }

         d = d.normalize();
         if (d.y < 0.17) {
            d = new Vec3(d.x, 0.17, d.z).normalize();
         }

         skyDir = d;
         Vec3 helper = Math.abs(d.y) < 0.9 ? new Vec3(0.0, 1.0, 0.0) : new Vec3(1.0, 0.0, 0.0);
         skyE1 = d.cross(helper).normalize();
         skyE2 = d.cross(skyE1).normalize();
         skyDirSet = true;
      }
   }

   private static double smooth(double x) {
      double t = Math.max(0.0, Math.min(1.0, x));
      return t * t * (3.0 - 2.0 * t);
   }

   public static float skyTint() {
      if (!active) {
         return 0.0F;
      }

      int since = ticks - countdownTicks;
      if (since < 0) {
         return 0.0F;
      }

      if (since < 40) {
         return since / 40.0F;
      }

      if (since <= waveTicks) {
         return 1.0F;
      }

      float fade = 1.0F - (float)(since - waveTicks) / fadeTicks;
      return Math.max(0.0F, fade);
   }

   private static float flashAlpha() {
      if (!active) {
         return 0.0F;
      }

      int since = ticks - countdownTicks;
      if (since < 0) {
         int left = -since;
         return left <= 10 ? 0.18F * (1.0F - left / 10.0F) : 0.0F;
      }

      if (since < 2) {
         return 1.0F * (since + 1) / 2.0F;
      }

      if (since < 10) {
         return 1.0F;
      }

      int fade = since - 2 - 8;
      return fade >= 100 ? 0.0F : 1.0F * (float)(1.0 - smooth(fade / 100.0));
   }

   private static float shakeIntensity() {
      int since = ticks - countdownTicks;
      if (since < -60) {
         return 0.0F;
      } else if (since < 0) {
         return 0.12F * (float)smooth((since + 60) / 60.0);
      } else {
         return since < 10 ? 1.0F : (float)(1.0 - smooth((since - 10) / 90.0));
      }
   }

   static {
      for (int i = 0; i < 8; i++) {
         RING_ANIM[i] = Ghostsafio.id("textures/effect/supernova_ring_" + i + ".png");
      }

      REMNANT_TEXTURE = Ghostsafio.id("textures/effect/supernova_remnant.png");
      activeShader = null;
      lastTickSeen = -1;
      lastTickNano = 0L;
      WHITE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/misc/white.png");
      active = false;
      ticks = 0;
      countdownTicks = 0;
      waveTicks = 0;
      fadeTicks = 0;
      skyDir = new Vec3(0.0, 1.0, 0.0);
      skyDirSet = false;
      skyE1 = new Vec3(1.0, 0.0, 0.0);
      skyE2 = new Vec3(0.0, 0.0, 1.0);
      SHAKE_RANDOM = new Random();
      shakeYaw = 0.0F;
      shakePitch = 0.0F;
      dotDir = new Vec3[360];
      dotSize = new double[360];
      dotDelay = new double[360];
      dotConsume = new double[360];
      dotColor = new float[360][3];
      debrisOff = new Vec3[140];
      debrisSize = new double[140];
      debrisDark = new boolean[140];
      debrisDrift = new double[140];
   }
}
