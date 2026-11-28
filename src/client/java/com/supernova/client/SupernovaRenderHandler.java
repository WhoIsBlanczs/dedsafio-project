package com.supernova.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import com.mojang.blaze3d.vertex.VertexSorting;

import com.mojang.logging.LogUtils;
import com.supernova.SupernovaState;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

import net.ruleta.client.compat.RenderLevelStageEvent;
import net.ruleta.client.compat.SubscribeEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;

import org.slf4j.Logger;

public class SupernovaRenderHandler {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static long lastErrLog;
   private static long lastNullLog;
   public static boolean dbgFlip;
   public static boolean dbgNoCull;
   public static boolean dbgNoDepth;
   public static boolean dbgMagenta;
   public static boolean dbgNoChain;
   private static final int ALPHA = 0;
   private static final int ADD = 1;
   private static final int MUL = 2;
   private static float rayNear;
   private static final float JET_GIRO = 0.32F;
   private static final float JET_INCLINA = 42.0F;
   private static final float JET_VAIVEN = 12.0F;
   private static TextureTarget profCopia;
   public static final float BLANCO_FIJO = 120.0F;
   public static final float BLANCO_FUNDE = 210.0F;

   @SubscribeEvent
   public static void onRender(RenderLevelStageEvent e) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static Object use(int blend) {
      // efecto de dibujo pendiente de portar a 26.3
      return null;
   }

   private static void depth(boolean test) {
      if (test && !dbgNoDepth) {
      } else {
      }
   }

   private static void worldTint(Matrix4f proj) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void skyDome(Matrix4f view, float far, float time, double camY) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawSupernova(SupernovaState n, Vec3 cam, Matrix4f view, float far, float now) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawRays(SupernovaState n, Vec3 cam, Matrix4f view, float far, float now, Vector3f cs, float dS, float fade, float charge, float time) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void beamOneSided(Vector3f start, Vector3f axis, float len, float width) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void groundLight(SupernovaState n, Vector3f axisW, float lenW, float widthW, Vec3 cam, Matrix4f view, float far, float strength) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void groundVert(BufferBuilder bb, Matrix4f view, float x, float y, float z, float r, float g, float b, float a, float u, float v) {
      Vector3f q = view.transformPosition(new Vector3f(x, y, z));
      bb.addVertex(q.x(), q.y(), q.z()).setColor(r, g, b, a).setUv(u, v);
   }

   private static void setSphere(Object sh, float mode, float time, float charge, float expl, float fade, Vector3f center, Vec3 viewDirW) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static int copiarProfundidad(Minecraft mc) {
      // efecto de dibujo pendiente de portar a 26.3
      return 0;
   }

   private static void godRays(Matrix4f proj, Matrix4f view) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void overlays(Matrix4f proj, Matrix4f view) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void fullscreen(Matrix4f proj, float r, float g, float b, float a) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void streak(Vector3f c, float halfW, float halfH) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void billboard(Vector3f c, float h) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void beam(Vector3f c, Vector3f axis, float len, float width) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void drawSphere(Matrix4f m, float r, boolean inside) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void vert(BufferBuilder bb, Matrix4f m, float r, float a, float b) {
      float nx = Mth.sin(a) * Mth.cos(b);
      float ny = Mth.cos(a);
      float nz = Mth.sin(a) * Mth.sin(b);
      bb.addVertex(m, nx * r, ny * r, nz * r)
         .setColor(nx * 0.5F + 0.5F, ny * 0.5F + 0.5F, nz * 0.5F + 0.5F, 1.0F)
         .setUv(b / (float) (Math.PI * 2), a / (float) Math.PI);
   }
}
