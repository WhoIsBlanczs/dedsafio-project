package com.watonstudio.creepermetalico.client;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class FogataFx {
   static final int T_HIT = 20;
   static final int T_RISER = 115;
   static final int T_DROP = 158;
   static final int T_ACC = 424;
   static final int T_STAR = 494;
   static final int T_RUM = 522;
   static final int T_FINAL = 595;
   static final int T_END = 660;
   static final List<FogataFx.Active> ACTIVE = new ArrayList<>();
   static final float[] CY = new float[]{0.1F, 0.95F, 0.8F};
   static final float[] LI = new float[]{0.7F, 1.0F, 0.2F};
   static final float[] GR = new float[]{0.2F, 1.0F, 0.4F};
   static final float[] YE = new float[]{1.0F, 0.95F, 0.3F};
   static final float[] WH = new float[]{1.0F, 0.97F, 0.85F};
   static final float[] TE = new float[]{0.1F, 0.7F, 0.7F};
   static final float[] PU = new float[]{0.55F, 0.3F, 1.0F};
   static final float[] OR = new float[]{1.0F, 0.5F, 0.1F};
   static final float[] RD = new float[]{0.9F, 0.08F, 0.12F};
   static final float[] PK = new float[]{1.0F, 0.4F, 0.5F};
   static final float[] DR = new float[]{0.35F, 0.0F, 0.05F};
   static final float[] RO = new float[]{1.0F, 0.5F, 0.2F};
   static final FogataFx.Pal NORM = new FogataFx.Pal(
      new float[][]{CY, LI, YE, GR, CY},
      new float[]{0.55F, 1.0F, 0.3F},
      CY,
      YE,
      GR,
      PU,
      YE,
      LI,
      YE,
      new float[][]{YE, CY, WH, GR},
      new float[]{0.75F, 1.0F, 0.85F}
   );
   static final FogataFx.Pal REDP = new FogataFx.Pal(
      new float[][]{{0.55F, 0.0F, 0.0F}, {0.8F, 0.0F, 0.0F}, {1.0F, 0.0F, 0.0F}, {0.75F, 0.0F, 0.0F}, {0.9F, 0.0F, 0.0F}},
      new float[]{1.0F, 0.0F, 0.0F},
      new float[]{1.0F, 0.0F, 0.0F},
      new float[]{0.6F, 0.0F, 0.0F},
      new float[]{0.7F, 0.0F, 0.0F},
      new float[]{1.0F, 0.0F, 0.0F},
      new float[]{1.0F, 0.0F, 0.0F},
      new float[]{0.8F, 0.0F, 0.0F},
      new float[]{1.0F, 0.0F, 0.0F},
      new float[][]{{0.7F, 0.0F, 0.0F}, {1.0F, 0.0F, 0.0F}, {0.8F, 0.0F, 0.0F}, {0.55F, 0.0F, 0.0F}},
      new float[]{1.0F, 0.0F, 0.0F}
   );
   static final Set<String> REDABLE = Set.of(
      "crescent",
      "ring_a",
      "ring_b",
      "ring_c",
      "ring_multi",
      "ring_wavy",
      "square",
      "orb",
      "glow_pixel",
      "glow_soft",
      "disc",
      "beam",
      "star4",
      "star8",
      "pixel"
   );
   static BufferBuilder bb;
   static Matrix4f M;
   static Vec3 CAM;
   static boolean REDNOW;
   static final double[] RT = new double[3];
   static final double[] UPV = new double[3];
   static final double[] X = new double[]{1.0, 0.0, 0.0};
   static final double[] Z = new double[]{0.0, 0.0, 1.0};
   static final List<FogataFx.TQ> TQS = new ArrayList<>();
   static final List<FogataFx.TQ> SMOKE = new ArrayList<>();
   static final Map<String, ResourceLocation> TEXC = new HashMap<>();
   static final double FX = 0.0;
   static final double FZ = 0.0;
   static final double OUT_Z = 0.0;
   static final int[][] FS = new int[][]{
      {-1, 1, 1, 1, 1, 1, 1, -1, 1, -1, -1, 1},
      {1, 1, -1, -1, 1, -1, -1, -1, -1, 1, -1, -1},
      {1, 1, 1, 1, 1, -1, 1, -1, -1, 1, -1, 1},
      {-1, 1, -1, -1, 1, 1, -1, -1, 1, -1, -1, -1},
      {-1, 1, -1, 1, 1, -1, 1, 1, 1, -1, 1, 1},
      {-1, -1, 1, 1, -1, 1, 1, -1, -1, -1, -1, -1}
   };
   static final float[][] FNO = new float[][]{
      {0.0F, 0.0F, 1.0F}, {0.0F, 0.0F, -1.0F}, {1.0F, 0.0F, 0.0F}, {-1.0F, 0.0F, 0.0F}, {0.0F, 1.0F, 0.0F}, {0.0F, -1.0F, 0.0F}
   };

   public static void add(BlockPos p, boolean rojo, String name) {
      ClientLevel w = Minecraft.getInstance().level;
      if (w != null) {
         ACTIVE.add(new FogataFx.Active(p, w.getGameTime(), rojo, name));
      }
   }

   public static void init() {
      WorldRenderEvents.AFTER_TRANSLUCENT.register(FogataFx::render);
   }

   static void render(WorldRenderContext ctx) {
      Minecraft mc = Minecraft.getInstance();
      ClientLevel w = mc.level;
      if (w != null && !ACTIVE.isEmpty()) {
         ACTIVE.removeIf(ax -> w.getGameTime() - ax.start() > 670L);
         float td = mc.getTimer().getGameTimeDeltaPartialTick(false);
         PoseStack ms = ctx.matrixStack();
         Camera cam = ctx.camera();
         CAM = cam.getPosition();
         Quaternionf q = cam.rotation();
         Vector3f r = new Vector3f(1.0F, 0.0F, 0.0F).rotate(q);
         Vector3f u = new Vector3f(0.0F, 1.0F, 0.0F).rotate(q);
         RT[0] = r.x;
         RT[1] = r.y;
         RT[2] = r.z;
         UPV[0] = u.x;
         UPV[1] = u.y;
         UPV[2] = u.z;
         ms.pushPose();
         ms.translate(-CAM.x, -CAM.y, -CAM.z);
         M = ms.last().pose();
         RenderSystem.enableBlend();
         RenderSystem.disableCull();

         for (FogataFx.Active a : ACTIVE) {
            float t = (float)(w.getGameTime() - a.start()) + td;
            if (!(t < 0.0F)) {
               REDNOW = a.rojo();
               RenderSystem.defaultBlendFunc();
               RenderSystem.disableDepthTest();
               RenderSystem.depthMask(false);
               darken(a.pos(), t);
               flush();
               RenderSystem.enableDepthTest();
               RenderSystem.depthMask(true);
               bodyPass(a.pos(), t, a.name());
               RenderSystem.depthMask(false);
               RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
               scene(a.pos(), t, a.rojo() ? REDP : NORM);
               stars(a.pos(), t, 0.0F);
               flush();
               drawTextured();
               RenderSystem.defaultBlendFunc();
               drawSmoke();
            }
         }

         RenderSystem.enableDepthTest();
         RenderSystem.depthMask(true);
         RenderSystem.enableCull();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableBlend();
         ms.popPose();
      }
   }

   static void darken(BlockPos pos, float t) {
      float bub = sm(20.0F, 40.0F, t) * (1.0F - sm(610.0F, 650.0F, t));
      if (!(bub <= 0.0F)) {
         double fx = pos.getX() + 0.5 + 0.0;
         double fy = pos.getY() + 5;
         double fz = pos.getZ() + 0.5 + 0.0;
         double R = 32.0 + 3.0 * Math.sin(t * 0.05);
         int lat = 16;
         int lon = 32;

         for (int i = 0; i < lat; i++) {
            double p0 = Math.PI * i / lat - (Math.PI / 2);
            double p1 = Math.PI * (i + 1) / lat - (Math.PI / 2);

            for (int j = 0; j < lon; j++) {
               double a0 = (Math.PI * 2) * j / lon;
               double a1 = (Math.PI * 2) * (j + 1) / lon;
               float al = 0.985F * bub;
               v(fx + R * Math.cos(p0) * Math.cos(a0), fy + R * Math.sin(p0), fz + R * Math.cos(p0) * Math.sin(a0), 0.0F, 0.0F, 0.01F, al);
               v(fx + R * Math.cos(p0) * Math.cos(a1), fy + R * Math.sin(p0), fz + R * Math.cos(p0) * Math.sin(a1), 0.0F, 0.0F, 0.01F, al);
               v(fx + R * Math.cos(p1) * Math.cos(a1), fy + R * Math.sin(p1), fz + R * Math.cos(p1) * Math.sin(a1), 0.0F, 0.0F, 0.01F, al);
               v(fx + R * Math.cos(p1) * Math.cos(a0), fy + R * Math.sin(p1), fz + R * Math.cos(p1) * Math.sin(a0), 0.0F, 0.0F, 0.01F, al);
            }
         }
      }
   }

   static void stars(BlockPos pos, float t, float env) {
      double fx = pos.getX() + 0.5 + 0.0;
      double fy = pos.getY() + 5;
      double fz = pos.getZ() + 0.5 + 0.0;
      float bub = sm(20.0F, 40.0F, t) * (1.0F - sm(610.0F, 650.0F, t));
      if (!(bub <= 0.0F)) {
         double R = 31.0;

         for (int i = 0; i < 220; i++) {
            double yaw = h(i, 40) * 2.0F * Math.PI;
            double pit = Math.asin(h(i, 41) * 1.8 - 0.9);
            double rr = R * (0.3 + h(i, 42) * 0.7);
            double[] c = new double[]{fx + Math.cos(pit) * Math.cos(yaw) * rr, fy + Math.sin(pit) * rr, fz + Math.cos(pit) * Math.sin(yaw) * rr};
            float tw = 0.5F + 0.5F * (float)Math.sin(t * 0.2 + h(i, 43) * 30.0F);
            float[] starTint = REDNOW ? new float[]{1.0F, 0.0F, 0.0F} : (h(i, 45) < 0.3F ? CY : WH);
            BB("glow_pixel", c, 0.3 + h(i, 44) * 0.6, starTint, bub * tw * 0.9F, 0.0);
         }
      }
   }

   static double bodyH(float t) {
      if (t < 158.0F) {
         return 0.0;
      }

      if (t <= 494.0F) {
         return 9.0F * sm(158.0F, 494.0F, t);
      }

      if (t < 595.0F) {
         return 9.0;
      }

      double p = Math.min(1.0, (t - 595.0F) / 58.0);
      return 9.0 * (1.0 - p * p);
   }

   static double[] bodyPos(double fx, double fy, double fz, float t) {
      double out = 0.0 * sm(600.0F, 645.0F, t);
      return new double[]{fx + 0.0, fy + 0.2 + bodyH(t) + 0.12 * Math.sin(t * 0.07) * sm(158.0F, 218.0F, t), fz + 0.0 - out};
   }

   static ResourceLocation skinOf(String name) {
      try {
         ClientPacketListener nh = Minecraft.getInstance().getConnection();
         if (nh != null && name != null) {
            PlayerInfo e = nh.getPlayerInfo(name);
            if (e != null) {
               return e.getSkin().texture();
            }
         }
      } catch (Exception var3) {
      }

      return FogataTex.get("steve");
   }

   static void bodyPass(BlockPos pos, float t, String name) {
      float vis = sm(138.0F, 178.0F, t) * (1.0F - sm(649.0F, 653.0F, t));
      if (!(vis <= 0.0F)) {
         double fx = pos.getX() + 0.5;
         double fy = pos.getY() + 0.03;
         double fz = pos.getZ() + 0.5;
         double[] c = bodyPos(fx, fy, fz, t);
         double ang = 0.0;
         float tilt = 0.45F * sm(158.0F, 218.0F, t) * (1.0F - sm(595.0F, 625.0F, t)) + 1.5F * sm(605.0F, 653.0F, t) + 0.1F * (float)Math.sin(t * 0.05);
         Matrix3f R = new Matrix3f().rotateY((float)(ang + (Math.PI / 2)) + t * 0.02F).rotateX(tilt);
         float sc = 0.5F + 0.5F * vis;
         ResourceLocation skin = skinOf(name);
         BufferBuilder sb = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         skinBox(sb, c, R, 0.0F, 0.0F, 0.0F, 8, 12, 4, 16, 16, sc);
         skinBox(sb, c, R, 0.0F, 0.625F * sc, 0.0F, 8, 8, 8, 0, 0, sc);

         for (int sd = -1; sd <= 1; sd += 2) {
            float a2 = sd * (0.5F + 0.2F * (float)Math.sin(t * 0.07));
            Matrix3f Ra = new Matrix3f(R).rotateZ(a2);
            Vector3f d = new Matrix3f().rotateZ(a2).transform(new Vector3f(0.0F, -0.375F * sc, 0.0F));
            Vector3f cc = new Vector3f(sd * 0.375F * sc + d.x, 0.375F * sc + d.y, d.z);
            R.transform(cc);
            skinBox(sb, new double[]{c[0] + cc.x, c[1] + cc.y, c[2] + cc.z}, Ra, 0.0F, 0.0F, 0.0F, 4, 12, 4, sd < 0 ? 40 : 32, sd < 0 ? 16 : 48, sc);
            skinBox(sb, c, R, sd * 0.125F * sc, -0.75F * sc, 0.0F, 4, 12, 4, sd < 0 ? 0 : 16, sd < 0 ? 16 : 48, sc);
         }

         RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
         RenderSystem.setShaderTexture(0, skin);
         BufferUploader.drawWithShader(sb.buildOrThrow());
      }
   }

   static void skinBox(BufferBuilder b, double[] c, Matrix3f R, float ox, float oy, float oz, int w, int h, int d, int u, int v, float sc) {
      int[][] rect = new int[][]{
         {u + d, v + d, w, h}, {u + 2 * d + w, v + d, w, h}, {u + d + w, v + d, d, h}, {u, v + d, d, h}, {u + d, v, w, d}, {u + d + w, v, w, d}
      };
      float k16 = sc / 16.0F;

      for (int f = 0; f < 6; f++) {
         Vector3f n = R.transform(new Vector3f(FNO[f][0], FNO[f][1], FNO[f][2]));
         float k = 0.6F + 0.4F * Math.max(0.0F, n.x * 0.3F + n.y * 0.9F + n.z * 0.3F);
         float[] uu = new float[]{rect[f][0] / 64.0F, (rect[f][0] + rect[f][2]) / 64.0F, (rect[f][0] + rect[f][2]) / 64.0F, rect[f][0] / 64.0F};
         float[] vv = new float[]{rect[f][1] / 64.0F, rect[f][1] / 64.0F, (rect[f][1] + rect[f][3]) / 64.0F, (rect[f][1] + rect[f][3]) / 64.0F};

         for (int q = 0; q < 4; q++) {
            Vector3f p = R.transform(
               new Vector3f(ox + FS[f][q * 3] * w * k16 / 2.0F, oy + FS[f][q * 3 + 1] * h * k16 / 2.0F, oz + FS[f][q * 3 + 2] * d * k16 / 2.0F)
            );
            b.addVertex(M, (float)(c[0] + p.x), (float)(c[1] + p.y), (float)(c[2] + p.z)).setUv(uu[q], vv[q]).setColor(k, k, k, 1.0F);
         }
      }
   }

   static void scene(BlockPos pos, float t, FogataFx.Pal P) {
      boolean red = REDNOW;
      double fxf = pos.getX() + 0.5;
      double fy = pos.getY() + 0.03;
      double fzf = pos.getZ() + 0.5;
      double fx = fxf + 0.0;
      double fz = fzf + 0.0;
      float env = sm(0.0F, 20.0F, t) * (1.0F - sm(630.0F, 660.0F, t));
      if (!(env <= 0.0F)) {
         double hh = bodyH(t);
         double[] body = new double[]{fx, fy + 1.6 + hh, fz};

         for (int i = 0; i < 140; i++) {
            double yaw = h(i, 10) * 2.0F * Math.PI;
            double pit = Math.asin(h(i, 11) * 0.97);
            double[] c = new double[]{CAM.x + 88.0 * Math.cos(pit) * Math.cos(yaw), CAM.y + 88.0 * Math.sin(pit), CAM.z + 88.0 * Math.cos(pit) * Math.sin(yaw)};
            float tw = 0.5F + 0.5F * (float)Math.sin(t * 0.15 + h(i, 12) * 40.0F);
            float[] starTint = REDNOW ? new float[]{1.0F, 0.0F, 0.0F} : (h(i, 14) < 0.3F ? CY : WH);
            BB("glow_pixel", c, 0.7 + 1.2 * h(i, 13), starTint, sm(30.0F, 90.0F, t) * env * (0.35F + 0.65F * tw), 0.0);
         }

         float ra = sm(20.0F, 30.0F, t) * (1.0F - sm(500.0F, 560.0F, t)) * env;
         if (ra > 0.0F) {
            double[] c = new double[]{fx, fy + 22.0, fz};
            float g = sm(20.0F, 158.0F, t);
            double gg = 0.12 + 0.88 * (1.0F - (1.0F - g) * (1.0F - g));
            double th = 0.5;
            double ph = t * 0.02;
            double[] u = new double[]{Math.cos(ph), 0.0, -Math.sin(ph)};
            double[] w = new double[]{Math.cos(th) * Math.sin(ph), Math.sin(th), Math.cos(th) * Math.cos(ph)};
            double pul = 1.0 + 0.1 * Math.exp(-Math.max(0.0F, t - 20.0F) / 8.0F) + (t >= 158.0F ? 0.1 * Math.exp(-(t - 158.0F) / 8.0F) : 0.0);
            float rise = sm(115.0F, 158.0F, t);

            for (int k = 0; k < 5; k++) {
               double rot = t * (0.012 + 0.006 * k) * (k % 2 == 0 ? 1 : -1);
               double[] ax = new double[]{
                  u[0] * Math.cos(rot) + w[0] * Math.sin(rot), u[1] * Math.cos(rot) + w[1] * Math.sin(rot), u[2] * Math.cos(rot) + w[2] * Math.sin(rot)
               };
               double[] ay = new double[]{
                  -u[0] * Math.sin(rot) + w[0] * Math.cos(rot), -u[1] * Math.sin(rot) + w[1] * Math.cos(rot), -u[2] * Math.sin(rot) + w[2] * Math.cos(rot)
               };
               double r = (3.4 + k * 0.7) * gg * pul * (1.0 + 0.04 * Math.sin(t * 0.1 + k));
               T("ring_wavy", c, ax, ay, r, r, P.sky[k], Math.min(1.0F, 0.85F * ra * (1.0F + 0.3F * rise)));
            }

            BB("glow_soft", c, 9.0 * gg + 12.0F * rise, P.sky[3], (0.5F + 0.4F * rise) * ra, 0.0);
            double s = 0.2 + 1.3 * sm(40.0F, 158.0F, t);
            float a1 = t * 0.05F;
            float a2 = t * 0.07F;
            Matrix3f Rc = new Matrix3f().rotateY(a1).rotateX(a2);
            Vector3f[] E = new Vector3f[]{
               Rc.transform(new Vector3f(1.0F, 0.0F, 0.0F)), Rc.transform(new Vector3f(0.0F, 1.0F, 0.0F)), Rc.transform(new Vector3f(0.0F, 0.0F, 1.0F))
            };

            for (int ai = 0; ai < 3; ai++) {
               for (int sg = -1; sg <= 1; sg += 2) {
                  double[] cc = new double[]{c[0] + E[ai].x * s * sg, c[1] + E[ai].y * s * sg, c[2] + E[ai].z * s * sg};
                  Vector3f o1 = E[(ai + 1) % 3];
                  Vector3f o2 = E[(ai + 2) % 3];
                  T("square", cc, new double[]{o1.x, o1.y, o1.z}, new double[]{o2.x, o2.y, o2.z}, s, s, P.cube, 0.8F * ra);
               }
            }

            BB("orb", c, s * 2.6, WH, 0.6F * ra, 0.0);
         }

         if (t >= 158.0F && t < 228.0F) {
            double p = (t - 158.0F) / 70.0;
            double hl = 2.0 + 10.0 * (1.0 - Math.pow(1.0 - p, 3.0));
            float a = (float)Math.pow(1.0 - p, 0.8) * env;
            T("square", new double[]{fx, fy + 0.05, fz}, X, Z, hl, hl, P.sq, a * 0.95F);
         }

         float aa = sm(148.0F, 248.0F, t) * (1.0F - sm(610.0F, 650.0F, t)) * env;
         if (aa > 0.0F) {
            for (int c = 0; c < 4; c++) {
               double c0 = c * Math.PI / 2.0 + 0.4;
               double span = 1.15;
               int sg = 18;
               int lv = 7;
               int[] di = new int[]{0, 1, 1, 0};
               int[] dj = new int[]{0, 0, 1, 1};

               for (int i = 0; i < sg; i++) {
                  for (int j = 0; j < lv; j++) {
                     double[][] p = new double[4][];
                     float[] al = new float[4];
                     float[][] co = new float[4][];

                     for (int n = 0; n < 4; n++) {
                        double ang = c0 - span / 2.0 + span * (i + di[n]) / sg;
                        double jj = j + dj[n];
                        double R = 30.0 + 2.5 * Math.sin(ang * 7.0 + t * 0.04 + c);
                        p[n] = new double[]{fx + R * Math.cos(ang), fy + jj / lv * 34.0 + 1.5 * Math.sin(ang * 5.0 + t * 0.05), fz + R * Math.sin(ang)};
                        double taper = Math.pow(Math.sin(Math.PI * Math.min(1.0, Math.max(0.0, (double)(i + di[n]) / sg))), 0.8);
                        al[n] = aa * 0.4F * (float)(Math.pow(1.0 - jj / lv, 0.8) * (0.7 + 0.3 * Math.sin(ang * 11.0 + t * 0.06)) * taper);
                        co[n] = mix(P.au0, P.au1, (float)(jj / lv * (c % 2 == 0 ? 1.0 : 0.4)));
                     }

                     for (int n = 0; n < 4; n++) {
                        v(p[n][0], p[n][1], p[n][2], co[n][0], co[n][1], co[n][2], al[n]);
                     }
                  }
               }
            }
         }

         float ga = sm(158.0F, 218.0F, t) * (1.0F - sm(620.0F, 650.0F, t)) * env;
         if (ga > 0.0F) {
            float g = sm(158.0F, 218.0F, t);
            double sz = 18.0 * (0.25 + 0.75 * g);

            for (int k = 0; k < 3; k++) {
               float hue = REDNOW ? 0.0F + 0.04F * k + 0.03F * (float)Math.sin(t * 0.03 + k) : 0.14F + 0.17F * k + 0.05F * (float)Math.sin(t * 0.03 + k);
               double sk = sz * (1.0 - 0.02 * k);
               float[] discTint = REDNOW ? (k == 0 ? new float[]{1.0F, 0.0F, 0.0F} : new float[]{0.65F, 0.0F, 0.0F}) : hsv(hue, 0.85F, 1.0F);
               T("disc", new double[]{fx, fy + 0.05 + k * 0.004, fz}, X, Z, sk, sk, discTint, ga * 0.32F);
            }

            for (int j = 0; j < 5; j++) {
               float ph = (Math.max(0.0F, t - 158.0F) * 0.0125F + j / 5.0F) % 1.0F;
               double r = 1.0F + ph * 16.0F;
               float a = ga * (float)Math.pow(1.0F - ph, 0.6) * sm(0.0F, 0.12F, ph);
               T("ring_multi", new double[]{fx, fy + 0.02, fz}, X, Z, r, r, mix(P.r0, P.r1, ph), 0.95F * a);
            }

            for (int j = 0; j < 4; j++) {
               double rot = t * (0.012 + 0.006 * j) * (j % 2 == 0 ? 1 : -1) + j * 1.7;
               double s2 = 5 + j * 4;
               double[] ax = new double[]{Math.cos(rot), 0.0, Math.sin(rot)};
               double[] ay = new double[]{-Math.sin(rot), 0.0, Math.cos(rot)};
               float acc = t >= 424.0F ? (float)Math.exp(-(t - 424.0F) / 12.0) : 0.0F;
               T(
                  "crescent",
                  new double[]{fx, fy + 0.03 + j * 0.003, fz},
                  ax,
                  ay,
                  s2,
                  s2,
                  WH,
                  Math.min(1.0F, ga * (0.8F + 0.2F * (float)Math.sin(t * 0.06 + j) + 0.6F * acc))
               );
            }
         }

         if (t >= 424.0F && t < 454.0F) {
            double pa = (t - 424.0F) / 30.0;
            double rr = 2.0 + pa * 18.0;
            T("ring_multi", new double[]{fx, fy + 0.04, fz}, X, Z, rr, rr, WH, (float)(1.0 - pa) * 0.9F * env);
            BB("glow_soft", new double[]{fx, fy + 1.5, fz}, 10.0 * (1.0 - pa) + 4.0, P.burst, 0.35F * (float)(1.0 - pa) * env, 0.0);
         }

         float da = sm(188.0F, 238.0F, t) * (1.0F - sm(585.0F, 620.0F, t)) * env;
         if (da > 0.0F) {
            String[] nm = new String[]{"ring_a", "ring_b", "ring_c", "ring_multi", "ring_wavy"};

            for (int k = 0; k < nm.length; k++) {
               double yaw = t * 0.04 * (k % 2 == 0 ? 1 : -1) + k * 1.257;
               double R = (2.6 + 0.3 * k) * (1.0 + 0.05 * Math.sin(t * 0.1 + k));
               T(nm[k], new double[]{fx, fy + 0.1, fz}, new double[]{Math.cos(yaw), 0.0, Math.sin(yaw)}, new double[]{0.0, 1.0, 0.0}, R, R, WH, da);
            }
         }

         float ba = sm(484.0F, 522.0F, t) * (1.0F - sm(580.0F, 600.0F, t)) * env;
         if (ba > 0.0F) {
            for (int k = 0; k < 2; k++) {
               double ang = k * Math.PI / 2.0;
               T(
                  "beam",
                  new double[]{fx, fy + 25.0, fz},
                  new double[]{Math.cos(ang), 0.0, Math.sin(ang)},
                  new double[]{0.0, 1.0, 0.0},
                  2.2,
                  25.0,
                  mix(P.burst, WH, 0.3F),
                  0.33F * ba
               );
            }
         }

         float la = sm(148.0F, 188.0F, t) * (1.0F - sm(585.0F, 625.0F, t)) * env;
         if (la > 0.0F) {
            double rad = 1.2 + 3.3 * sm(158.0F, 494.0F, t);
            BB("glow_soft", body, rad * 2.6, P.burst, 0.6F * la, 0.0);
            BB("orb", body, rad * 0.9, WH, 0.8F * la, 0.0);
            BB("star8", body, rad * 3.0, P.burst, 0.25F * la, t * 0.01);
         }

         float sm2 = sm(158.0F, 178.0F, t) * (1.0F - sm(585.0F, 635.0F, t)) * env;
         if (sm2 > 0.0F) {
            for (int i = 0; i < 26; i++) {
               double ph = (h(i, 60) + t * (0.01 + h(i, 61) * 0.012)) % 1.6;
               double puffY = fy + ph * 34.0;
               double wob = Math.sin(t * 0.05 + i) * (1.0 + ph * 2.0);
               double px = fx + wob * 0.4;
               double pz = fz + Math.cos(t * 0.04 + i) * (1.0 + ph * 2.0) * 0.4;
               float fade = (float)Math.min(1.0, 2.2 - ph) * (float)Math.min(1.0, ph * 4.0);
               float gray = 0.72F + 0.1F * h(i, 62);
               BB(
                  "glow_soft",
                  new double[]{px, puffY, pz},
                  1.6 + ph * 3.2 + h(i, 63) * 1.4,
                  new float[]{gray, gray, gray * 1.02F},
                  sm2 * fade * 0.8F,
                  h(i, 64) * 6.28,
                  true
               );
            }
         }

         if (t > 488.0F && t < 524.0F) {
            float f = t < 494.0F ? sm(488.0F, 494.0F, t) : 1.0F - sm(494.0F, 524.0F, t);
            double[] c = new double[]{body[0] + RT[0] * 3.5 + UPV[0] * 2.5, body[1] + RT[1] * 3.5 + UPV[1] * 2.5, body[2] + RT[2] * 3.5 + UPV[2] * 2.5};
            BB("glow_soft", c, 14.0F * f, WH, 0.8F * f, 0.0);
            BB("star4", c, 22.0F * f, WH, 0.95F * f, 0.0);
            BB("glow_soft", body, 30.0F * f, red ? PK : mix(CY, WH, 0.3F), 0.22F * f, 0.0);
         }

         if (t >= 522.0F && t < 595.0F) {
            double p = (t - 522.0F) / 73.0;
            double e = 1.0 - Math.pow(1.0 - Math.min(1.0, p * 3.0), 3.0);
            float q = (float)(1.0 - p);
            BB("glow_soft", body, 6.0 + 10.0 * e, P.burst, 0.4F * q, 0.0);
            BB("star8", body, 4.0 + 26.0 * e, P.burst, 0.8F * (float)Math.pow(q, 0.7), t * 0.01);
            BB("star8", body, 3.0 + 18.0 * e, WH, 0.45F * (float)Math.pow(q, 0.7), -t * 0.008);
         }

         if (t >= 595.0F && t < 635.0F) {
            double p = (t - 595.0F) / 40.0;
            float q = (float)(1.0 - p);
            BB("glow_soft", body, 8.0 + 14.0 * p, WH, 0.5F * q, 0.0);
            BB("star8", body, 4.0 + 24.0 * p, P.burst, 0.8F * q, t * 0.02);
         }

         float oa2 = sm(60.0F, 188.0F, t) * (1.0F - sm(620.0F, 650.0F, t)) * env;
         if (oa2 > 0.0F) {
            for (int i = 0; i < 60; i++) {
               double ang = h(i, 20) * 2.0F * Math.PI + t * (0.0015 + h(i, 24) * 0.003);
               double rad = 4.0F + h(i, 21) * 38.0F;
               double y = fy + (h(i, 22) * 30.0F + t * (0.012 + h(i, 25) * 0.04)) % 34.0 - 2.0;
               float tw = 0.55F + 0.45F * (float)Math.sin(t * 0.05 + h(i, 26) * 30.0F);
               BB(
                  "orb",
                  new double[]{fx + Math.cos(ang) * rad, y, fz + Math.sin(ang) * rad},
                  0.5 + h(i, 23) * 1.7,
                  P.orbs[(int)(h(i, 27) * 4.0F) % 4],
                  oa2 * tw * 0.95F,
                  0.0
               );
            }
         }

         float sa = sm(158.0F, 228.0F, t) * (1.0F - sm(620.0F, 650.0F, t)) * env;
         if (sa > 0.0F) {
            for (int i = 0; i < 110; i++) {
               double ang = h(i, 0) * 2.0F * Math.PI + t * 0.004 * (0.5 + h(i, 1));
               double rad = 2.0F + h(i, 2) * 15.0F;
               double y = fy + (h(i, 3) * 20.0F + t * (0.02 + h(i, 4) * 0.05)) % 22.0;
               float tw = 0.5F + 0.5F * (float)Math.sin(t * 0.2 + h(i, 6) * 20.0F);
               float[] col = red ? (h(i, 7) < 0.5F ? RO : PK) : (h(i, 7) < 0.4F ? YE : (h(i, 7) < 0.7F ? CY : WH));
               BB("pixel", new double[]{fx + Math.cos(ang) * rad, y, fz + Math.sin(ang) * rad}, 0.12 + h(i, 5) * 0.22, col, sa * tw, 0.0);
            }
         }
      }
   }

   static void v(double x, double y, double z, float r, float g, float b, float a) {
      if (bb == null) {
         bb = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      }

      bb.addVertex(M, (float)x, (float)y, (float)z).setColor(r, g, b, Math.max(0.0F, Math.min(1.0F, a)));
   }

   static void flush() {
      if (bb != null) {
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         BufferUploader.drawWithShader(bb.buildOrThrow());
         bb = null;
      }
   }

   static ResourceLocation tex(String n) {
      return FogataTex.get(n);
   }

   static void T(String name, double[] c, double[] ax, double[] ay, double hw, double hh, float[] tint, float a) {
      T(name, c, ax, ay, hw, hh, tint, a, false);
   }

   static void T(String name, double[] c, double[] ax, double[] ay, double hw, double hh, float[] tint, float a, boolean smoke) {
      if (!(a <= 0.004F)) {
         FogataFx.TQ q = new FogataFx.TQ();
         q.tex = tex(name + (REDNOW && REDABLE.contains(name) ? "_red" : ""));

         for (int i = 0; i < 3; i++) {
            q.p[i] = c[i] - ax[i] * hw + ay[i] * hh;
            q.p[3 + i] = c[i] + ax[i] * hw + ay[i] * hh;
            q.p[6 + i] = c[i] + ax[i] * hw - ay[i] * hh;
            q.p[9 + i] = c[i] - ax[i] * hw - ay[i] * hh;
         }

         q.r = tint[0];
         q.g = tint[1];
         q.b = tint[2];
         q.a = Math.min(1.0F, a);
         (smoke ? SMOKE : TQS).add(q);
      }
   }

   static void BB(String name, double[] c, double size, float[] tint, float a, double rot) {
      BB(name, c, size, tint, a, rot, false);
   }

   static void BB(String name, double[] c, double size, float[] tint, float a, double rot, boolean smoke) {
      double ca = Math.cos(rot);
      double sa = Math.sin(rot);
      double[] ax = new double[]{RT[0] * ca + UPV[0] * sa, RT[1] * ca + UPV[1] * sa, RT[2] * ca + UPV[2] * sa};
      double[] ay = new double[]{-RT[0] * sa + UPV[0] * ca, -RT[1] * sa + UPV[1] * ca, -RT[2] * sa + UPV[2] * ca};
      T(name, c, ax, ay, size / 2.0, size / 2.0, tint, a, smoke);
   }

   static void drawTextured() {
      drawList(TQS);
   }

   static void drawSmoke() {
      drawList(SMOKE);
   }

   static void drawList(List<FogataFx.TQ> list) {
      if (!list.isEmpty()) {
         Map<ResourceLocation, List<FogataFx.TQ>> g = new LinkedHashMap<>();

         for (FogataFx.TQ q : list) {
            g.computeIfAbsent(q.tex, kx -> new ArrayList<>()).add(q);
         }

         list.clear();
         float[] us = new float[]{0.0F, 1.0F, 1.0F, 0.0F};
         float[] vs = new float[]{0.0F, 0.0F, 1.0F, 1.0F};

         for (Entry<ResourceLocation, List<FogataFx.TQ>> e : g.entrySet()) {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, e.getKey());
            BufferBuilder b = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

            for (FogataFx.TQ q : e.getValue()) {
               for (int k = 0; k < 4; k++) {
                  b.addVertex(M, (float)q.p[k * 3], (float)q.p[k * 3 + 1], (float)q.p[k * 3 + 2]).setUv(us[k], vs[k]).setColor(q.r, q.g, q.b, q.a);
               }
            }

            BufferUploader.drawWithShader(b.buildOrThrow());
         }
      }
   }

   static float[] hsv(float h, float s, float v) {
      h -= (float)Math.floor(h);
      float c = v * s;
      float x = c * (1.0F - Math.abs(h * 6.0F % 2.0F - 1.0F));
      float m = v - c;
      int i = (int)(h * 6.0F);
      float r;
      float g;
      float b;
      switch (i) {
         case 0:
            r = c;
            g = x;
            b = 0.0F;
            break;
         case 1:
            r = x;
            g = c;
            b = 0.0F;
            break;
         case 2:
            r = 0.0F;
            g = c;
            b = x;
            break;
         case 3:
            r = 0.0F;
            g = x;
            b = c;
            break;
         case 4:
            r = x;
            g = 0.0F;
            b = c;
            break;
         default:
            r = c;
            g = 0.0F;
            b = x;
      }

      return new float[]{r + m, g + m, b + m};
   }

   static float[] mix(float[] a, float[] b, float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      return new float[]{a[0] + (b[0] - a[0]) * t, a[1] + (b[1] - a[1]) * t, a[2] + (b[2] - a[2]) * t};
   }

   static float sm(float a, float b, float x) {
      float t = Math.max(0.0F, Math.min(1.0F, (x - a) / (b - a)));
      return t * t * (3.0F - 2.0F * t);
   }

   static float h(int i, int k) {
      int x = i * 374761393 + k * 668265263;
      x = (x ^ x >>> 13) * 1274126177;
      return ((x ^ x >>> 16) & 65535) / 65535.0F;
   }

   @Environment(EnvType.CLIENT)
   record Active(BlockPos pos, long start, boolean rojo, String name) {
   }

   @Environment(EnvType.CLIENT)
   static class Pal {
      float[][] sky;
      float[][] orbs;
      float[] disc;
      float[] r0;
      float[] r1;
      float[] au0;
      float[] au1;
      float[] orb;
      float[] sq;
      float[] burst;
      float[] cube;

      Pal(float[][] sky, float[] disc, float[] r0, float[] r1, float[] au0, float[] au1, float[] orb, float[] sq, float[] burst, float[][] orbs, float[] cube) {
         this.sky = sky;
         this.disc = disc;
         this.r0 = r0;
         this.r1 = r1;
         this.au0 = au0;
         this.au1 = au1;
         this.orb = orb;
         this.sq = sq;
         this.burst = burst;
         this.orbs = orbs;
         this.cube = cube;
      }
   }

   @Environment(EnvType.CLIENT)
   static class TQ {
      ResourceLocation tex;
      double[] p = new double[12];
      float r;
      float g;
      float b;
      float a;
   }
}
