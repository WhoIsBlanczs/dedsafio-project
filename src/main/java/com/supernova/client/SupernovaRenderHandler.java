package com.supernova.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.vertex.Object;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Object;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.logging.LogUtils;
import com.supernova.SupernovaState;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Object;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.ruleta.client.OpcionesCliente;
import net.ruleta.client.compat.RenderLevelStageEvent;
import net.ruleta.client.compat.SubscribeEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
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
   public static void onRender(RenderLevelStageEvent e) { }

   private static Object use(int blend) { return null; }

   private static void depth(boolean test) { }

   private static void worldTint(Matrix4f proj) { }

   private static void skyDome(Matrix4f view, float far, float time, double camY) { }

   private static void drawSupernova(SupernovaState n, Vec3 cam, Matrix4f view, float far, float now) { }

   private static void drawRays(SupernovaState n, Vec3 cam, Matrix4f view, float far, float now, Vector3f cs, float dS, float fade, float charge, float time) { }

   private static void beamOneSided(Vector3f start, Vector3f axis, float len, float width) { }

   private static void groundLight(SupernovaState n, Vector3f axisW, float lenW, float widthW, Vec3 cam, Matrix4f view, float far, float strength) { }

   private static void groundVert(Object bb, Matrix4f view, float x, float y, float z, float r, float g, float b, float a, float u, float v) {
      Vector3f q = view.transformPosition(new Vector3f(x, y, z));
      bb.addVertex(q.x(), q.y(), q.z()).setColor(r, g, b, a).setUv(u, v);
   }

   private static void setSphere(Object sh, float mode, float time, float charge, float expl, float fade, Vector3f center, Vec3 viewDirW) {
      sh.safeGetUniform("Mode").set(mode);
      sh.safeGetUniform("Time").set(time);
      sh.safeGetUniform("Charge").set(charge);
      sh.safeGetUniform("Expl").set(expl);
      sh.safeGetUniform("Fade").set(fade);
      sh.safeGetUniform("SphereCenter").set(center.x(), center.y(), center.z());
      sh.safeGetUniform("ViewDirW").set((float)viewDirW.x, (float)viewDirW.y, (float)viewDirW.z);
   }

   private static int copiarProfundidad(Minecraft mc) { return 0; }

   private static void godRays(Matrix4f proj, Matrix4f view) { }

   private static void overlays(Matrix4f proj, Matrix4f view) { }

   private static void fullscreen(Matrix4f proj, float r, float g, float b, float a) { }

   private static void streak(Vector3f c, float halfW, float halfH) { }

   private static void billboard(Vector3f c, float h) { }

   private static void beam(Vector3f c, Vector3f axis, float len, float width) { }

   private static void drawSphere(Matrix4f m, float r, boolean inside) { }

   private static void vert(Object bb, Matrix4f m, float r, float a, float b) {
      float nx = Mth.sin(a) * Mth.cos(b);
      float ny = Mth.cos(a);
      float nz = Mth.sin(a) * Mth.sin(b);
      bb.addVertex(m, nx * r, ny * r, nz * r)
         .setColor(nx * 0.5F + 0.5F, ny * 0.5F + 0.5F, nz * 0.5F + 0.5F, 1.0F)
         .setUv(b / (float) (Math.PI * 2), a / (float) Math.PI);
   }
}
