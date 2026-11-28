package com.ghostsafio.mitomod.client;

import com.ghostsafio.mitomod.Ghostsafio;
import com.ghostsafio.mitomod.SupernovaPayload;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;


import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect;


import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class SupernovaClient {
   private static final Identifier SUN_TEXTURE = Ghostsafio.id("textures/effect/supernova_sun.png");
   private static final Identifier STAR_TEXTURE = Ghostsafio.id("textures/effect/supernova_star.png");
   private static final Identifier RING_TEXTURE = Ghostsafio.id("textures/effect/supernova_ring.png");
   private static final Identifier GLOW_TEXTURE = Ghostsafio.id("textures/effect/supernova_glow.png");
   private static final Identifier DOT_TEXTURE = Ghostsafio.id("textures/effect/supernova_dot.png");
   private static final Identifier NEBULA_TEXTURE = Ghostsafio.id("textures/effect/supernova_nebula.png");
   private static final Identifier JET_TEXTURE = Ghostsafio.id("textures/effect/supernova_jet.png");
   private static final Identifier BLAST_TEXTURE = Ghostsafio.id("textures/effect/supernova_blast.png");
   private static final Identifier SHOCK_TEXTURE = Ghostsafio.id("textures/effect/supernova_shock.png");
   private static final Identifier RAYS_TEXTURE = Ghostsafio.id("textures/effect/supernova_rays.png");
   private static final Identifier SUN_SURFACE = Ghostsafio.id("textures/effect/supernova_sun_surface.png");
   private static final Identifier CORONA_TEXTURE = Ghostsafio.id("textures/effect/supernova_corona.png");
   private static final Identifier FIRE_SURFACE = Ghostsafio.id("textures/effect/supernova_fire_surface.png");
   private static final int RING_FRAMES = 8;
   private static final Identifier[] RING_ANIM = new Identifier[8];
   private static final Identifier REMNANT_TEXTURE;
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

   private static int lastTickSeen;
   private static long lastTickNano;
   private static final Identifier WHITE_TEXTURE;
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
      net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(com.ghostsafio.mitomod.SupernovaPayload.TYPE, (payload, context) -> {
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
      net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> active = false);
      net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
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
      net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry.addLast(com.ghostsafio.mitomod.Ghostsafio.id("supernova_flash"), (graphics, tickCounter) -> {
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

   private static void renderWorld(Object context) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static double consumedFraction(double t) {
      double total = 0.0;

      for (int i = 0; i < 360; i++) {
         total += smooth((t - dotConsume[i]) / 0.12);
      }

      return total / 360.0;
   }

   private static void drawSun(Object context, double t) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawDots(Object context, double t) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static BufferBuilder spriteBegin(Identifier texture, boolean additive) {
      // efecto de dibujo pendiente de portar a 26.3
      return null;
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
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawSphere(
      Object context,
      Identifier texture,
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
      // efecto de dibujo pendiente de portar a 26.3
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
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawWave(Object context) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawCharge(Object context, double c) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawExplosion(Object context, int since, float fade) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawAurora(Object context, float alpha, float time) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void auroraVertex(BufferBuilder buffer, Matrix4f matrix, double az, double el, double dist, float[] c, float a, float u, float v) {
      float x = (float)(Math.cos(el) * Math.cos(az) * dist);
      float y = (float)(Math.sin(el) * dist);
      float z = (float)(Math.cos(el) * Math.sin(az) * dist);
      buffer.addVertex(matrix, x, y, z).setUv(u, v).setColor(c[0], c[1], c[2], Math.max(0.0F, Math.min(1.0F, a)));
   }

   private static void drawRingAnimated(Object context, double size, int since, double p, float alpha) {
      // efecto de dibujo pendiente de portar a 26.3
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

   private static void drawPulseWave(Object context, int since, float fade) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawSparks(Object context, int since, float fade) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawSunrise(Object context, int since) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawStreak(Object context, Vec3 headDir, Vec3 moveDir, double length, double width, float r, float g, float b, float alpha) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawBillboardTint(
      Object context, Identifier texture, double size, float roll, float r, float g, float b, float alpha, boolean additive
   ) {
      double d = skyDistance();
      double scale = d / 250.0;
      float half = (float)(size * scale / 2.0);
      drawQuad(context, texture, skyDir.scale(d), -half, half, -half, half, roll, r, g, b, alpha, additive);
   }

   private static double frac(double x) {
      return x - Math.floor(x);
   }

   private static void drawDebris(Object context, int since, float fade) {
      // efecto de dibujo pendiente de portar a 26.3
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

   private static void drawBillboard(Object context, Identifier texture, double size, float alpha) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawBillboardEx(Object context, Identifier texture, double size, float roll, float alpha, boolean additive) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawJet(Object context, double length, double halfWidth, float roll, float alpha) {
      // efecto de dibujo pendiente de portar a 26.3
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
      Object context,
      Object shader,
      Identifier fallback,
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
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void fxSphere(
      Object context,
      Object shader,
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
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void starFx(Object context, double size, float roll, float r, float g, float b, float alpha) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawAt(Object context, Identifier texture, Vec3 offset, float half, float alpha) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawQuad(
      Object context,
      Identifier texture,
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
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static double skyDistance() {
      double far = Minecraft.getInstance().options.getEffectiveRenderDistance() * 16.0 * 4.0;
      return Math.max(100.0, far * 0.85);
   }

   private static void ensureSkyDir(Object context) {
      // efecto de dibujo pendiente de portar a 26.3
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
      lastTickSeen = -1;
      lastTickNano = 0L;
      WHITE_TEXTURE = Identifier.withDefaultNamespace("textures/misc/white.png");
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
