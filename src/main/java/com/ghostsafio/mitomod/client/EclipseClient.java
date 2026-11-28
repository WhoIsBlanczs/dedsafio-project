package com.ghostsafio.mitomod.client;

import com.ghostsafio.mitomod.EclipsePayload;
import com.ghostsafio.mitomod.Ghostsafio;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import com.mojang.blaze3d.vertex.VertexSorting;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class EclipseClient {
   private static final Identifier T_WHITE = Ghostsafio.id("textures/effect/eclipse_white.png");
   private static final Identifier T_DOT = Ghostsafio.id("textures/effect/eclipse_dot.png");
   private static final Identifier T_SUN = Ghostsafio.id("textures/effect/eclipse_sun.png");
   private static final Identifier T_GLOW = Ghostsafio.id("textures/effect/eclipse_glow.png");
   private static final Identifier T_MOON = Ghostsafio.id("textures/effect/eclipse_moon.png");
   private static final Identifier T_RIM = Ghostsafio.id("textures/effect/eclipse_rim.png");
   private static final Identifier T_FLARE = Ghostsafio.id("textures/effect/eclipse_flare.png");
   private static final Identifier T_CORONA = Ghostsafio.id("textures/effect/eclipse_corona.png");
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


   public static void register() {
      net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(com.ghostsafio.mitomod.EclipsePayload.TYPE, (payload, context) -> {
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
      net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> active = false);
      net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
         if (active && !client.isPaused()) {
            ticks++;
            if (ticks > duration) {
               active = false;
            }
         }
      });
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

   private static void render(Object context) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void fx(
      Object context,
      Object shader,
      Identifier fallback,
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
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawTwilight(Object context, float strength, Vec3 sunDir) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static double sparkPhase(int i) {
      return i * 2.399963 % 6.2831853;
   }

   private static void drawScreenTint(float r, float g, float b, float a) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawSkyDome(float r, float g, float b, float a) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawStars(Object context, float k, float t) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void quad(
      Object context, Identifier texture, Vec3 dir, double halfTan, float r, float g, float b, float alpha, boolean additive
   ) {
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

   private static double skyDistance() {
      double far = Minecraft.getInstance().options.getEffectiveRenderDistance() * 16.0 * 4.0;
      return Math.max(100.0, far * 0.85);
   }
}
