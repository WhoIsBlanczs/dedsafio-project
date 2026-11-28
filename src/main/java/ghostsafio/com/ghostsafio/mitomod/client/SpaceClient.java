package com.ghostsafio.mitomod.client;

import com.ghostsafio.mitomod.Ghostsafio;
import com.ghostsafio.mitomod.ModNave;
import com.ghostsafio.mitomod.NaveEntity;
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
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class SpaceClient {
   private static final ResourceLocation WHITE = Ghostsafio.id("textures/effect/space_white.png");
   private static final ResourceLocation DOT = Ghostsafio.id("textures/effect/supernova_dot.png");
   private static final ResourceLocation GLOW = Ghostsafio.id("textures/effect/supernova_glow.png");
   private static final ResourceLocation RING = Ghostsafio.id("textures/effect/supernova_shock.png");
   private static final ResourceLocation EARTH = Ghostsafio.id("textures/environment/earth.png");
   private static final ResourceLocation RED = Ghostsafio.id("textures/environment/planet_gleba.png");
   private static final ResourceLocation SUN = ResourceLocation.withDefaultNamespace("textures/environment/sun.png");
   public static final double PLANET_RADIUS = 3000.0;
   private static final Vec3 SUN_DIR = new Vec3(0.55, 0.45, -0.7).normalize();
   private static final Vec3 OTHER_DIR = new Vec3(-0.8, 0.3, -0.5).normalize();
   private static final int STAR_COUNT = 1400;
   private static Vec3[] starDir;
   private static float[] starSize;
   private static float[] starPhase;
   private static final int LAT = 96;
   private static final int LON = 192;
   private static float[][] sphereN;
   private static int clientTicks = 0;
   private static boolean wasSpace = false;
   private static int spaceEnterTick = -1000;

   public static void clientTick(Minecraft mc) {
      clientTicks++;
      boolean space = isSpaceDimension();
      if (space && !wasSpace) {
         spaceEnterTick = clientTicks;
      }

      wasSpace = space;
   }

   public static int ticksInSpace() {
      return isSpaceDimension() ? clientTicks - spaceEnterTick : -1;
   }

   private static NaveEntity ship() {
      Minecraft mc = Minecraft.getInstance();
      return mc.player != null && mc.player.getVehicle() instanceof NaveEntity n ? n : null;
   }

   public static boolean isSpaceDimension() {
      ClientLevel level = Minecraft.getInstance().level;
      return level != null && level.dimension() == ModNave.SPACE;
   }

   public static boolean isRedPlanet() {
      ClientLevel level = Minecraft.getInstance().level;
      return level != null && level.dimension() == ModNave.RED_PLANET;
   }

   private static boolean isOverworld() {
      ClientLevel level = Minecraft.getInstance().level;
      return level != null && level.dimension() == Level.OVERWORLD;
   }

   public static float atmosphereDarkness() {
      NaveEntity nave = ship();
      return nave != null && !isSpaceDimension() ? (float)smooth((Minecraft.getInstance().player.getY() - 420.0) / 190.0) : 0.0F;
   }

   public static float cloudHaze() {
      NaveEntity nave = ship();
      if (nave != null && isOverworld()) {
         double y = Minecraft.getInstance().player.getY();
         return (float)(smooth((y - 300.0) / 150.0) * (1.0 - smooth((y - 520.0) / 100.0)));
      } else {
         return 0.0F;
      }
   }

   public static float transitionFlash() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player == null) {
         return 0.0F;
      } else if (isSpaceDimension()) {
         float t = (clientTicks - spaceEnterTick) / 50.0F;
         return t >= 1.0F ? 0.0F : 0.9F * (1.0F - t);
      } else {
         return ship() != null && isOverworld() ? 0.9F * (float)smooth((mc.player.getY() - 640.0) / 60.0) : 0.0F;
      }
   }

   public static void renderAtmosphere(WorldRenderContext context) {
      float k = atmosphereDarkness();
      if (!(k <= 0.01F)) {
         begin();
         drawScreenQuad(0.0F, 0.0F, 0.02F, 0.97F * k);
         drawStars(context.camera(), k);
         end();
      }
   }

   public static void renderSpaceSky(WorldRenderContext context) {
      Camera camera = context.camera();
      Vec3 cam = camera.getPosition();
      double far = farDistance();
      NaveEntity nave = ship();
      int body = nave != null ? nave.getBody() : 0;
      float warp = nave != null ? nave.getWarp() : 0.0F;
      float time = (float)(System.nanoTime() / 1.0E9 % 100000.0);
      begin();
      drawScreenQuad(0.0F, 0.0F, 0.0F, 1.0F);
      drawStars(camera, 1.0F);
      SpaceClient.Frame frame = new SpaceClient.Frame(camera);
      drawSun(frame, (float)(far * 0.6));
      float intro = OrbitFx.introIntensity();
      if (intro > 0.01F) {
         drawSunFlare(frame, (float)(far * 0.6), intro);
      }

      float otherDist = (float)(far * 0.7);
      drawSphere(frame, OTHER_DIR.scale(otherDist), otherDist * 0.1F, body == 0 ? RED : EARTH, 4.0E-4F * clientTicks, false);
      Vec3 rel = new Vec3(-cam.x, -3000.0 - cam.y, -cam.z);
      double dist = rel.length();
      double s = far * 0.5 / dist;
      Vec3 center = rel.scale(s);
      float radius = (float)(3000.0 * s);
      float spin = 1.2E-4F * clientTicks;
      drawSphere(frame, center, radius, body == 0 ? EARTH : RED, spin, false);
      drawSphere(frame, center, radius * 1.04F, WHITE, spin, true);
      drawWarp(warp, time);
      end();
   }

   public static void renderRedSky(WorldRenderContext context) {
      Camera camera = context.camera();
      begin();
      drawScreenQuad(0.5F, 0.11F, 0.15F, 1.0F);
      drawStars(camera, 0.3F);
      SpaceClient.Frame frame = new SpaceClient.Frame(camera);
      drawSun(frame, (float)(farDistance() * 0.6));
      end();
   }

   private static double farDistance() {
      return Minecraft.getInstance().options.getEffectiveRenderDistance() * 16.0 * 4.0;
   }

   private static void begin() {
      Matrix4fStack modelView = RenderSystem.getModelViewStack();
      modelView.pushMatrix();
      modelView.identity();
      RenderSystem.applyModelViewMatrix();
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.disableCull();
   }

   private static void end() {
      RenderSystem.enableCull();
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
      RenderSystem.getModelViewStack().popMatrix();
      RenderSystem.applyModelViewMatrix();
   }

   private static BufferBuilder beginQuads() {
      return Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
   }

   private static void flush(BufferBuilder bb) {
      MeshData mesh = bb.build();
      if (mesh != null) {
         BufferUploader.drawWithShader(mesh);
      }
   }

   private static void additive() {
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
   }

   private static void drawScreenQuad(float r, float g, float b, float a) {
      if (!(a <= 0.0F)) {
         RenderSystem.setShaderTexture(0, WHITE);
         BufferBuilder bb = beginQuads();
         float h = 400.0F;
         float z = -20.0F;
         bb.addVertex(-h, -h, z).setUv(0.0F, 0.0F).setColor(r, g, b, a);
         bb.addVertex(h, -h, z).setUv(1.0F, 0.0F).setColor(r, g, b, a);
         bb.addVertex(h, h, z).setUv(1.0F, 1.0F).setColor(r, g, b, a);
         bb.addVertex(-h, h, z).setUv(0.0F, 1.0F).setColor(r, g, b, a);
         flush(bb);
      }
   }

   private static void emitBillboard(BufferBuilder bb, SpaceClient.Frame f, float cx, float cy, float cz, float half, float r, float g, float b, float a) {
      float rx = f.right.x * half;
      float ry = f.right.y * half;
      float rz = f.right.z * half;
      float ux = f.up.x * half;
      float uy = f.up.y * half;
      float uz = f.up.z * half;
      bb.addVertex(f.view, cx - rx - ux, cy - ry - uy, cz - rz - uz).setUv(0.0F, 1.0F).setColor(r, g, b, a);
      bb.addVertex(f.view, cx + rx - ux, cy + ry - uy, cz + rz - uz).setUv(1.0F, 1.0F).setColor(r, g, b, a);
      bb.addVertex(f.view, cx + rx + ux, cy + ry + uy, cz + rz + uz).setUv(1.0F, 0.0F).setColor(r, g, b, a);
      bb.addVertex(f.view, cx - rx + ux, cy - ry + uy, cz - rz + uz).setUv(0.0F, 0.0F).setColor(r, g, b, a);
   }

   private static void initStars() {
      if (starDir == null) {
         Random random = new Random(777L);
         starDir = new Vec3[1400];
         starSize = new float[1400];
         starPhase = new float[1400];

         for (int i = 0; i < 1400; i++) {
            double y = random.nextDouble() * 2.0 - 1.0;
            double phi = random.nextDouble() * Math.PI * 2.0;
            double r = Math.sqrt(1.0 - y * y);
            starDir[i] = new Vec3(r * Math.cos(phi), y, r * Math.sin(phi));
            starSize[i] = 0.7F + random.nextFloat() * random.nextFloat() * 1.6F;
            starPhase[i] = random.nextFloat();
         }
      }
   }

   private static void drawStars(Camera camera, float alpha) {
      initStars();
      SpaceClient.Frame frame = new SpaceClient.Frame(camera);
      float d = (float)(farDistance() * 0.6);
      float time = (float)(System.nanoTime() / 1.0E9 % 100000.0);
      RenderSystem.setShaderTexture(0, DOT);
      additive();
      BufferBuilder bb = beginQuads();

      for (int i = 0; i < 1400; i++) {
         Vec3 dir = starDir[i];
         float twinkle = 0.7F + 0.3F * (float)Math.sin(time * (1.0F + starPhase[i] * 0.4F) + starPhase[i] * 20.0F);
         float a = Math.min(1.0F, alpha * twinkle * (0.45F + 0.55F * starSize[i] / 2.3F));
         float half = d * 0.0017F * starSize[i];
         emitBillboard(bb, frame, (float)(dir.x * d), (float)(dir.y * d), (float)(dir.z * d), half, 1.0F, 0.97F, 0.9F, a);
      }

      flush(bb);
      RenderSystem.defaultBlendFunc();
   }

   private static void drawSun(SpaceClient.Frame frame, float d) {
      float cx = (float)(SUN_DIR.x * d);
      float cy = (float)(SUN_DIR.y * d);
      float cz = (float)(SUN_DIR.z * d);
      additive();
      RenderSystem.setShaderTexture(0, GLOW);
      BufferBuilder halo = beginQuads();
      emitBillboard(halo, frame, cx, cy, cz, d * 0.45F, 1.0F, 0.78F, 0.45F, 0.28F);
      emitBillboard(halo, frame, cx, cy, cz, d * 0.16F, 1.0F, 0.9F, 0.65F, 0.55F);
      flush(halo);
      RenderSystem.setShaderTexture(0, SUN);
      BufferBuilder sprite = beginQuads();
      emitBillboard(sprite, frame, cx, cy, cz, d * 0.07F, 1.0F, 1.0F, 1.0F, 1.0F);
      flush(sprite);
      RenderSystem.defaultBlendFunc();
   }

   private static void drawSunFlare(SpaceClient.Frame frame, float d, float k) {
      float cx = (float)(SUN_DIR.x * d);
      float cy = (float)(SUN_DIR.y * d);
      float cz = (float)(SUN_DIR.z * d);
      additive();
      RenderSystem.setShaderTexture(0, GLOW);
      BufferBuilder bb = beginQuads();
      emitBillboard(bb, frame, cx, cy, cz, d * (0.8F + 0.6F * k), 1.0F, 0.8F, 0.5F, 0.3F * k);
      emitBillboard(bb, frame, cx, cy, cz, d * (0.3F + 0.25F * k), 1.0F, 0.92F, 0.7F, 0.55F * k);
      emitBillboard(bb, frame, cx, cy, cz, d * 0.1F, 1.0F, 1.0F, 1.0F, 0.85F * k);
      flush(bb);
      RenderSystem.defaultBlendFunc();
   }

   private static void drawWarp(float warp, float time) {
      if (!(warp <= 0.01F)) {
         additive();
         RenderSystem.setShaderTexture(0, WHITE);
         BufferBuilder bb = beginQuads();
         float z = -20.0F;

         for (int i = 0; i < 260; i++) {
            float h1 = hash(i * 3 + 1);
            float h2 = hash(i * 3 + 2);
            float h3 = hash(i * 3 + 3);
            float ang = h1 * (float) (Math.PI * 2);
            float q = frac(time * (0.35F + h2 * 0.9F) + h3);
            float r0 = 1.2F + q * q * 30.0F;
            float len = (0.8F + 10.0F * warp) * (0.3F + q);
            float cs = (float)Math.cos(ang);
            float sn = (float)Math.sin(ang);
            float w = 0.05F + 0.1F * q;
            float a = warp * Math.min(1.0F, q * 3.0F) * (0.4F + 0.6F * h2);
            float red = i % 5 == 0 ? 1.0F : 0.72F;
            float green = i % 5 == 0 ? 0.75F : 0.86F;
            float x0 = cs * r0;
            float y0 = sn * r0;
            float x1 = cs * (r0 + len);
            float y1 = sn * (r0 + len);
            float nx = -sn * w;
            float ny = cs * w;
            bb.addVertex(x0 - nx, y0 - ny, z).setUv(0.0F, 0.0F).setColor(red, green, 1.0F, a);
            bb.addVertex(x0 + nx, y0 + ny, z).setUv(1.0F, 0.0F).setColor(red, green, 1.0F, a);
            bb.addVertex(x1 + nx, y1 + ny, z).setUv(1.0F, 1.0F).setColor(red, green, 1.0F, a * 0.2F);
            bb.addVertex(x1 - nx, y1 - ny, z).setUv(0.0F, 1.0F).setColor(red, green, 1.0F, a * 0.2F);
         }

         flush(bb);
         RenderSystem.setShaderTexture(0, RING);
         BufferBuilder rings = beginQuads();

         for (int k = 0; k < 6; k++) {
            float radius = 4.0F + k * 2.4F + 2.5F * frac(time * 0.35F + k * 0.17F);
            boolean blue = k % 2 == 0;
            float r = blue ? 0.35F : 0.7F;
            float g = blue ? 0.65F : 0.45F;
            float a = warp * 0.85F * (1.0F - k * 0.12F);
            float half = radius / 0.9F;
            rings.addVertex(-half, -half, z).setUv(0.0F, 0.0F).setColor(r, g, 1.0F, a);
            rings.addVertex(half, -half, z).setUv(1.0F, 0.0F).setColor(r, g, 1.0F, a);
            rings.addVertex(half, half, z).setUv(1.0F, 1.0F).setColor(r, g, 1.0F, a);
            rings.addVertex(-half, half, z).setUv(0.0F, 1.0F).setColor(r, g, 1.0F, a);
         }

         flush(rings);
         RenderSystem.defaultBlendFunc();
      }
   }

   private static void initSphere() {
      if (sphereN == null) {
         sphereN = new float[18721][3];

         for (int j = 0; j <= 96; j++) {
            double lat = (Math.PI / 2) - Math.PI * j / 96.0;

            for (int i = 0; i <= 192; i++) {
               double lon = (Math.PI * 2) * i / 192.0;
               float[] n = sphereN[j * 193 + i];
               n[0] = (float)(Math.cos(lat) * Math.cos(lon));
               n[1] = (float)Math.sin(lat);
               n[2] = (float)(Math.cos(lat) * Math.sin(lon));
            }
         }
      }
   }

   private static void drawSphere(SpaceClient.Frame frame, Vec3 c, float radius, ResourceLocation texture, float spin, boolean glow) {
      initSphere();
      int count = 18721;
      float[] px = new float[count];
      float[] py = new float[count];
      float[] pz = new float[count];
      float[] lum = new float[count];
      float[] facing = new float[count];
      float cs = (float)Math.cos(spin);
      float sn = (float)Math.sin(spin);

      for (int k = 0; k < count; k++) {
         float[] n = sphereN[k];
         float nx = n[0] * cs + n[2] * sn;
         float ny = n[1];
         float nz = -n[0] * sn + n[2] * cs;
         float x = (float)c.x + nx * radius;
         float y = (float)c.y + ny * radius;
         float z = (float)c.z + nz * radius;
         px[k] = x;
         py[k] = y;
         pz[k] = z;
         float len = (float)Math.sqrt(x * x + y * y + z * z);
         float cosView = len > 0.0F ? -(nx * x + ny * y + nz * z) / len : 0.0F;
         facing[k] = cosView;
         float sun = nx * (float)SUN_DIR.x + ny * (float)SUN_DIR.y + nz * (float)SUN_DIR.z;
         lum[k] = 0.3F + 0.7F * (float)smooth((sun + 0.15) / 0.75);
      }

      RenderSystem.setShaderTexture(0, texture);
      if (glow) {
         additive();
      }

      BufferBuilder bb = beginQuads();
      int stride = 193;

      for (int j = 0; j < 96; j++) {
         for (int i = 0; i < 192; i++) {
            int a = j * stride + i;
            int b = a + 1;
            int cc = a + stride + 1;
            int d = a + stride;
            if (!(facing[a] + facing[cc] < -0.15F)) {
               int[] ids = new int[]{a, b, cc, d};
               float[] us = new float[]{i / 192.0F, (i + 1) / 192.0F, (i + 1) / 192.0F, i / 192.0F};
               float[] vs = new float[]{j / 96.0F, j / 96.0F, (j + 1) / 96.0F, (j + 1) / 96.0F};

               for (int m = 0; m < 4; m++) {
                  int id = ids[m];
                  if (glow) {
                     float rim = (float)Math.pow(1.0 - Math.max(0.0F, Math.min(1.0F, facing[id])), 2.6);
                     bb.addVertex(frame.view, px[id], py[id], pz[id]).setUv(us[m], vs[m]).setColor(0.45F, 0.7F, 1.0F, rim * 0.9F);
                  } else {
                     float l = lum[id];
                     bb.addVertex(frame.view, px[id], py[id], pz[id]).setUv(us[m], vs[m]).setColor(l, l, l * 0.98F, 1.0F);
                  }
               }
            }
         }
      }

      flush(bb);
      if (glow) {
         RenderSystem.defaultBlendFunc();
      }
   }

   private static float hash(int n) {
      int x = n * 374761393 + 668265263;
      x = (x ^ x >>> 13) * 1274126177;
      x ^= x >>> 16;
      return (x & 16777215) / 1.6777216E7F;
   }

   private static float frac(float x) {
      return x - (float)Math.floor(x);
   }

   private static double smooth(double x) {
      double t = Math.max(0.0, Math.min(1.0, x));
      return t * t * (3.0 - 2.0 * t);
   }

   @Environment(EnvType.CLIENT)
   private static class Frame {
      final Matrix4f view;
      final Vector3f right;
      final Vector3f up;

      Frame(Camera camera) {
         Quaternionf q = camera.rotation();
         this.view = new Matrix4f().rotation(q.conjugate(new Quaternionf()));
         this.right = new Vector3f(1.0F, 0.0F, 0.0F).rotate(q);
         this.up = new Vector3f(0.0F, 1.0F, 0.0F).rotate(q);
      }
   }
}
