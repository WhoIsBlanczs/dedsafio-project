package com.watonstudio.creepermetalico;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;


import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;


import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class FogataFx {
   static final int T_HIT = 30;
   static final int T_DROP = 170;
   static final int T_STAR = 540;
   static final int T_FINAL = 640;
   static final int T_END = 700;
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
   static final FogataFx.Pal NORM = new FogataFx.Pal(new float[][]{CY, LI, YE, GR, CY}, TE, LI, CY, YE, GR, PU, YE, LI, YE, LI, 1.0F);
   static final FogataFx.Pal RED = new FogataFx.Pal(
      new float[][]{RD, PK, RO, RD, PK},
      DR,
      RD,
      new float[]{1.0F, 0.15F, 0.15F},
      RO,
      RD,
      new float[]{0.8F, 0.2F, 0.6F},
      new float[]{1.0F, 0.35F, 0.4F},
      new float[]{1.0F, 0.2F, 0.2F},
      PK,
      RD,
      2.2F
   );
   static BufferBuilder bb;
   static Matrix4f M;
   static Vec3 CAM;
   static final double[] RT = new double[3];
   static final double[] UPV = new double[3];
   static final double[] X = new double[]{1.0, 0.0, 0.0};
   static final double[] Z = new double[]{0.0, 0.0, 1.0};

   public static void add(BlockPos p, boolean rojo) {
      ClientLevel w = Minecraft.getInstance().level;
      if (w != null) {
         ACTIVE.add(new FogataFx.Active(p, w.getGameTime(), rojo));
      }
   }

   public static void init() {
      // efecto de dibujo pendiente de portar a 26.3
   }

   static void render(Object ctx) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   static void dome(float t) {
      float dn = sm(30.0F, 100.0F, t) * (1.0F - sm(650.0F, 700.0F, t));
      if (!(dn <= 0.0F)) {
         int lat = 10;
         int lon = 24;
         double R = 90.0;

         for (int i = 0; i < lat; i++) {
            double p0 = Math.toRadians(-10.0 + 100.0 * i / lat);
            double p1 = Math.toRadians(-10.0 + 100.0 * (i + 1) / lat);

            for (int j = 0; j < lon; j++) {
               double a0 = (Math.PI * 2) * j / lon;
               double a1 = (Math.PI * 2) * (j + 1) / lon;
               float al = 0.93F * dn;
               v(CAM.x + R * Math.cos(p0) * Math.cos(a0), CAM.y + R * Math.sin(p0), CAM.z + R * Math.cos(p0) * Math.sin(a0), 0.0F, 0.0F, 0.02F, al);
               v(CAM.x + R * Math.cos(p0) * Math.cos(a1), CAM.y + R * Math.sin(p0), CAM.z + R * Math.cos(p0) * Math.sin(a1), 0.0F, 0.0F, 0.02F, al);
               v(CAM.x + R * Math.cos(p1) * Math.cos(a1), CAM.y + R * Math.sin(p1), CAM.z + R * Math.cos(p1) * Math.sin(a1), 0.0F, 0.0F, 0.02F, al);
               v(CAM.x + R * Math.cos(p1) * Math.cos(a0), CAM.y + R * Math.sin(p1), CAM.z + R * Math.cos(p1) * Math.sin(a0), 0.0F, 0.0F, 0.02F, al);
            }
         }
      }
   }

   static double bodyH(float t) {
      if (t < 170.0F) {
         return 0.0;
      }

      if (t <= 540.0F) {
         return 9.0F * sm(170.0F, 540.0F, t);
      }

      if (t < 640.0F) {
         return 9.0;
      }

      double p = Math.min(1.0, (t - 640.0F) / 58.0);
      return 9.0 * (1.0 - p * p);
   }

   static void scene(BlockPos pos, float t, FogataFx.Pal P, boolean red) {
      double fx = pos.getX() + 0.5;
      double fy = pos.getY() + 0.03;
      double fz = pos.getZ() + 0.5;
      float env = sm(0.0F, 20.0F, t) * (1.0F - sm(670.0F, 700.0F, t));
      if (!(env <= 0.0F)) {
         double hh = bodyH(t);
         double[] body = new double[]{fx, fy + 0.97 + hh + 0.9, fz};
         double[] fc = new double[]{fx, fy, fz};

         for (int i = 0; i < 160; i++) {
            double yaw = h(i, 10) * 2.0F * Math.PI;
            double pit = Math.asin(h(i, 11) * 0.97);
            double[] c = new double[]{CAM.x + 88.0 * Math.cos(pit) * Math.cos(yaw), CAM.y + 88.0 * Math.sin(pit), CAM.z + 88.0 * Math.cos(pit) * Math.sin(yaw)};
            float tw = 0.5F + 0.5F * (float)Math.sin(t * 0.15 + h(i, 12) * 40.0F);
            glow(c, 0.3 + 0.6 * h(i, 13), h(i, 14) < 0.3F ? CY : WH, sm(60.0F, 120.0F, t) * env * (0.35F + 0.65F * tw), 4);
         }

         float ra = sm(30.0F, 40.0F, t) * (1.0F - sm(540.0F, 600.0F, t)) * env;
         if (ra > 0.0F) {
            double[] c = new double[]{fx, fy + 22.0, fz};
            float g = sm(30.0F, 170.0F, t);
            double gg = 0.12 + 0.88 * (1.0F - (1.0F - g) * (1.0F - g));
            double th = 0.5;
            double ph = t * 0.02;
            double[] u = new double[]{Math.cos(ph), 0.0, -Math.sin(ph)};
            double[] w = new double[]{Math.cos(th) * Math.sin(ph), Math.sin(th), Math.cos(th) * Math.cos(ph)};
            double pul = 1.0 + 0.1 * Math.exp(-Math.max(0.0F, t - 30.0F) / 8.0F);

            for (int k = 0; k < 5; k++) {
               double r = (3.2 + k * 0.65 + 0.15 * Math.sin(t * 0.2 + k)) * gg * pul;
               soft(c, u, w, r, 0.35 + 0.25 * gg, P.sky[k], P.sky[(k + 1) % 5], 0.85F * ra, 64);
            }

            glow(c, 3.6 * gg, P.sky[3], 0.55F * ra, 16);
            double s = 0.2 + 1.3 * sm(60.0F, 170.0F, t);
            cube(c, s, t * 0.05, t * 0.07, red ? new float[]{1.0F, 0.7F, 0.75F} : new float[]{0.75F, 1.0F, 0.85F}, 0.9F * ra);
            glow(c, s * 1.3, red ? new float[]{1.0F, 0.6F, 0.7F} : new float[]{0.8F, 1.0F, 0.9F}, 0.4F * ra, 10);
         }

         if (t >= 170.0F && t < 240.0F) {
            double p = (t - 170.0F) / 70.0;
            double hl = 2.0 + 10.0 * (1.0 - Math.pow(1.0 - p, 3.0));
            float a = (float)Math.pow(1.0 - p, 0.8) * env;

            for (int i = 0; i < 4; i++) {
               double ang = i * Math.PI / 2.0;
               double ux = Math.cos(ang);
               double uz = Math.sin(ang);
               double wx = -Math.sin(ang);
               double wz = Math.cos(ang);
               double cx0 = fx + ux * hl;
               double cz0 = fz + uz * hl;
               double y = fy + 0.04;
               float[] sq = P.sq;
               v(cx0 - wx * hl - ux * 0.3, y, cz0 - wz * hl - uz * 0.3, sq[0], sq[1], sq[2], a * 0.95F);
               v(cx0 + wx * hl - ux * 0.3, y, cz0 + wz * hl - uz * 0.3, sq[0], sq[1], sq[2], a * 0.95F);
               v(cx0 + wx * hl + ux * 0.3, y, cz0 + wz * hl + uz * 0.3, sq[0], sq[1], sq[2], a * 0.95F);
               v(cx0 - wx * hl + ux * 0.3, y, cz0 - wz * hl + uz * 0.3, sq[0], sq[1], sq[2], a * 0.95F);
               v(cx0 - wx * hl - ux * 1.2, y, cz0 - wz * hl - uz * 1.2, sq[0], sq[1], sq[2], 0.0F);
               v(cx0 + wx * hl - ux * 1.2, y, cz0 + wz * hl - uz * 1.2, sq[0], sq[1], sq[2], 0.0F);
               v(cx0 + wx * hl - ux * 0.3, y, cz0 + wz * hl - uz * 0.3, sq[0], sq[1], sq[2], a * 0.9F);
               v(cx0 - wx * hl - ux * 0.3, y, cz0 - wz * hl - uz * 0.3, sq[0], sq[1], sq[2], a * 0.9F);
            }
         }

         float oa = sm(170.0F, 200.0F, t) * (1.0F - sm(300.0F, 380.0F, t)) * env;
         if (oa > 0.0F) {
            glow(new double[]{fx, fy + 5.0 + hh * 0.2, fz}, 3.2 + 0.4 * Math.sin(t * 0.1), P.orb, 0.7F * oa, 16);
         }

         float aa = sm(160.0F, 260.0F, t) * (1.0F - sm(640.0F, 690.0F, t)) * env;
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

         float ga = sm(170.0F, 230.0F, t) * (1.0F - sm(650.0F, 690.0F, t)) * env;
         if (ga > 0.0F) {
            for (int i = 0; i < 12; i++) {
               double r0 = i * 1.4;
               double r1 = (i + 1) * 1.4;
               strip(
                  fc,
                  X,
                  Z,
                  r0,
                  r1,
                  mix(P.g0, P.g1, (float)Math.pow(r0 / 16.0, 1.2)),
                  ga * P.boost * (0.04F + 0.2F * (float)Math.pow(r0 / 16.8, 1.6)),
                  mix(P.g0, P.g1, (float)Math.pow(r1 / 16.0, 1.2)),
                  ga * P.boost * (0.04F + 0.2F * (float)Math.pow(r1 / 16.8, 1.6)),
                  40,
                  0.0,
                  Math.PI * 2
               );
            }

            strip(fc, X, Z, 16.8, 18.6, P.edge, ga * 0.45F, P.edge, 0.0F, 48, 0.0, Math.PI * 2);
            soft(new double[]{fx, fy + 0.01, fz}, X, Z, 2.2 + 0.15 * Math.sin(t * 0.15), 0.5, P.r0, P.r1, 0.8F * ga, 40);

            for (int j = 0; j < 5; j++) {
               float ph = (Math.max(0.0F, t - 170.0F) * 0.0125F + j / 5.0F) % 1.0F;
               double r = 1.0F + ph * 15.0F;
               double wd = 0.7 + ph * 1.6;
               float a = ga * (float)Math.pow(1.0F - ph, 0.6) * sm(0.0F, 0.12F, ph);
               soft(new double[]{fx, fy + 0.02, fz}, X, Z, r, wd, P.r0, P.r1, 0.95F * a, 56);
               soft(new double[]{fx, fy + 0.03, fz}, X, Z, r * 1.03, 0.12, WH, WH, 0.5F * a, 56);
            }
         }

         float da = sm(200.0F, 250.0F, t) * (1.0F - sm(620.0F, 660.0F, t)) * env;
         if (da > 0.0F) {
            float[][] cs = new float[][]{CY, LI, GR};

            for (int k = 0; k < 3; k++) {
               double yaw = t * 0.07 * (k % 2 == 0 ? 1 : -1) + k * 2.094;
               double R = 3.2 + 0.25 * k;
               double hx = Math.cos(yaw);
               double hz = Math.sin(yaw);
               double sx = -Math.sin(yaw);
               double sz = Math.cos(yaw);
               int sg = 24;

               for (int i = 0; i < sg; i++) {
                  double a0 = Math.PI * i / sg;
                  double a1 = Math.PI * (i + 1) / sg;
                  float dash = 0.35F + 0.65F * Math.max(0.0F, (float)Math.sin(i * 0.8 - t * 0.35 + k));

                  for (int pass = 0; pass < 2; pass++) {
                     double tk = pass == 0 ? 0.09 : 0.28;
                     float al = da * dash * (pass == 0 ? 1.0F : 0.25F);
                     double ax = fx + hx * Math.cos(a0) * R;
                     double ay = fy + 0.2 + Math.sin(a0) * R;
                     double az = fz + hz * Math.cos(a0) * R;
                     double bx = fx + hx * Math.cos(a1) * R;
                     double by = fy + 0.2 + Math.sin(a1) * R;
                     double bz = fz + hz * Math.cos(a1) * R;
                     v(ax + sx * tk, ay, az + sz * tk, cs[k][0], cs[k][1], cs[k][2], al);
                     v(ax - sx * tk, ay, az - sz * tk, cs[k][0], cs[k][1], cs[k][2], al);
                     v(bx - sx * tk, by, bz - sz * tk, cs[k][0], cs[k][1], cs[k][2], al);
                     v(bx + sx * tk, by, bz + sz * tk, cs[k][0], cs[k][1], cs[k][2], al);
                  }
               }
            }
         }

         float ba = sm(400.0F, 480.0F, t) * (1.0F - sm(630.0F, 650.0F, t)) * env;
         if (ba > 0.0F) {
            for (int k = 0; k < 2; k++) {
               double ang = k * Math.PI / 2.0;
               double dx = Math.cos(ang) * 0.6;
               double dz = Math.sin(ang) * 0.6;
               float[] col = mix(P.burst, WH, 0.3F);
               v(fx - dx, fy, fz - dz, col[0], col[1], col[2], 0.3F * ba);
               v(fx + dx, fy, fz + dz, col[0], col[1], col[2], 0.3F * ba);
               v(fx + dx, fy + 40.0, fz + dz, col[0], col[1], col[2], 0.0F);
               v(fx - dx, fy + 40.0, fz - dz, col[0], col[1], col[2], 0.0F);
            }
         }

         float la = sm(300.0F, 420.0F, t) * (1.0F - sm(630.0F, 670.0F, t)) * env;
         if (la > 0.0F) {
            double rad = 1.5 + 4.5 * sm(300.0F, 540.0F, t);
            glow(body, rad, P.burst, 0.42F * la, 20);
            glow(body, rad * 0.35, WH, 0.55F * la, 14);

            for (int i = 0; i < 14; i++) {
               ray(body, i * 2 * Math.PI / 14.0 + t * 0.01, rad * (1.6 + 0.8 * h(i, 3)), 0.3, P.burst, 0.4F * la);
            }
         }

         if (t > 525.0F && t < 575.0F) {
            float f = t < 540.0F ? sm(525.0F, 540.0F, t) : 1.0F - sm(540.0F, 575.0F, t);
            double[] c = new double[]{body[0] + RT[0] * 3.5 + UPV[0] * 2.5, body[1] + RT[1] * 3.5 + UPV[1] * 2.5, body[2] + RT[2] * 3.5 + UPV[2] * 2.5};
            glow(c, 7.0F * f, WH, 0.8F * f, 16);

            for (int i = 0; i < 4; i++) {
               ray(c, i * Math.PI / 2.0, 10.0F * f, 0.65 * f + 0.05, WH, 0.95F * f);
            }

            for (int i = 0; i < 4; i++) {
               ray(c, (Math.PI / 4) + i * Math.PI / 2.0, 5.0F * f, 0.3 * f + 0.03, WH, 0.7F * f);
            }

            glow(body, 12.0F * f, red ? PK : mix(CY, WH, 0.3F), 0.18F * f, 16);
         }

         if (t >= 540.0F && t < 640.0F) {
            double p = (t - 540.0F) / 100.0;
            double e = 1.0 - Math.pow(1.0 - Math.min(1.0, p * 3.0), 3.0);
            glow(body, 3.0 + 4.0 * e, P.burst, 0.3F * (float)(1.0 - p), 16);

            for (int i = 0; i < 26; i++) {
               ray(
                  body,
                  i * 2 * Math.PI / 26.0 + 0.1 + t * 0.004,
                  e * (8.0F + 9.0F * h(i, 5)),
                  0.3 * (1.0 - p) + 0.03,
                  i % 3 == 0 ? WH : P.burst,
                  0.65F * (float)Math.pow(1.0 - p, 0.7)
               );
            }
         }

         if (t >= 640.0F && t < 680.0F) {
            double p = (t - 640.0F) / 40.0;
            glow(body, 4.0 + 6.0 * p, WH, 0.5F * (float)(1.0 - p), 16);

            for (int i = 0; i < 30; i++) {
               ray(body, i * 2 * Math.PI / 30.0, p * (10.0F + 8.0F * h(i, 6)), 0.35 * (1.0 - p) + 0.03, P.burst, 0.9F * (float)(1.0 - p));
            }
         }

         float sa = sm(170.0F, 240.0F, t) * (1.0F - sm(650.0F, 690.0F, t)) * env;
         if (sa > 0.0F) {
            for (int i = 0; i < 90; i++) {
               double ang = h(i, 0) * 2.0F * Math.PI + t * 0.004 * (0.5 + h(i, 1));
               double rad = 2.0F + h(i, 2) * 15.0F;
               double y = fy + (h(i, 3) * 20.0F + t * (0.02 + h(i, 4) * 0.05)) % 22.0;
               float tw = 0.5F + 0.5F * (float)Math.sin(t * 0.2 + h(i, 6) * 20.0F);
               float[] col = h(i, 7) < 0.4F ? YE : (h(i, 7) < 0.7F ? CY : WH);
               glow(new double[]{fx + Math.cos(ang) * rad, y, fz + Math.sin(ang) * rad}, 0.1 + h(i, 5) * 0.16, col, sa * tw * 0.95F, 6);
            }
         }
      }
   }

   static void v(double x, double y, double z, float r, float g, float b, float a) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   static void flush() {
      // efecto de dibujo pendiente de portar a 26.3
   }

   static void pt(double[] c, double[] u, double[] w, double r, double ang, float[] col, float a) {
      double ca = Math.cos(ang) * r;
      double sa = Math.sin(ang) * r;
      v(c[0] + u[0] * ca + w[0] * sa, c[1] + u[1] * ca + w[1] * sa, c[2] + u[2] * ca + w[2] * sa, col[0], col[1], col[2], a);
   }

   static void strip(double[] c, double[] u, double[] w, double r0, double r1, float[] c0, float a0, float[] c1, float a1, int seg, double A0, double A1) {
      for (int i = 0; i < seg; i++) {
         double A = A0 + (A1 - A0) * i / seg;
         double B = A0 + (A1 - A0) * (i + 1) / seg;
         pt(c, u, w, r0, A, c0, a0);
         pt(c, u, w, r0, B, c0, a0);
         pt(c, u, w, r1, B, c1, a1);
         pt(c, u, w, r1, A, c1, a1);
      }
   }

   static void soft(double[] c, double[] u, double[] w, double r, double wd, float[] c0, float[] c1, float a, int seg) {
      float[] m = mix(c0, c1, 0.5F);
      strip(c, u, w, r - wd / 2.0, r, c0, 0.0F, m, a, seg, 0.0, Math.PI * 2);
      strip(c, u, w, r, r + wd / 2.0, m, a, c1, 0.0F, seg, 0.0, Math.PI * 2);
   }

   static void glow(double[] c, double size, float[] col, float a, int seg) {
      strip(c, RT, UPV, 0.0, size, col, a, col, 0.0F, seg, 0.0, Math.PI * 2);
   }

   static void ray(double[] c, double ang, double len, double wid, float[] col, float a) {
      double ca = Math.cos(ang);
      double sa = Math.sin(ang);
      double dx = RT[0] * ca + UPV[0] * sa;
      double dy = RT[1] * ca + UPV[1] * sa;
      double dz = RT[2] * ca + UPV[2] * sa;
      double sx = -RT[0] * sa + UPV[0] * ca;
      double sy = -RT[1] * sa + UPV[1] * ca;
      double sz = -RT[2] * sa + UPV[2] * ca;
      v(c[0] + sx * wid, c[1] + sy * wid, c[2] + sz * wid, col[0], col[1], col[2], a);
      v(c[0] - sx * wid, c[1] - sy * wid, c[2] - sz * wid, col[0], col[1], col[2], a);
      v(c[0] + dx * len, c[1] + dy * len, c[2] + dz * len, col[0], col[1], col[2], 0.0F);
      v(c[0] + dx * len, c[1] + dy * len, c[2] + dz * len, col[0], col[1], col[2], 0.0F);
   }

   static void cube(double[] c, double s, double ay, double ax, float[] col, float a) {
      double[][] p = new double[8][];

      for (int i = 0; i < 8; i++) {
         double x = (i & 1) == 0 ? -s : s;
         double y = (i & 2) == 0 ? -s : s;
         double z = (i & 4) == 0 ? -s : s;
         double x1 = x * Math.cos(ay) + z * Math.sin(ay);
         double z1 = -x * Math.sin(ay) + z * Math.cos(ay);
         double y1 = y * Math.cos(ax) - z1 * Math.sin(ax);
         double z2 = y * Math.sin(ax) + z1 * Math.cos(ax);
         p[i] = new double[]{c[0] + x1, c[1] + y1, c[2] + z2};
      }

      for (int i = 0; i < 8; i++) {
         for (int b = 0; b < 3; b++) {
            int j = i ^ 1 << b;
            if (j > i) {
               line(p[i], p[j], 0.06, col, a);
            }
         }
      }
   }

   static void line(double[] p, double[] q, double th, float[] c, float a) {
      double dx = q[0] - p[0];
      double dy = q[1] - p[1];
      double dz = q[2] - p[2];
      double vx = CAM.x - (p[0] + q[0]) / 2.0;
      double vy = CAM.y - (p[1] + q[1]) / 2.0;
      double vz = CAM.z - (p[2] + q[2]) / 2.0;
      double nx = dy * vz - dz * vy;
      double ny = dz * vx - dx * vz;
      double nz = dx * vy - dy * vx;
      double l = Math.sqrt(nx * nx + ny * ny + nz * nz);
      if (!(l < 1.0E-6)) {
         nx = nx / l * th;
         ny = ny / l * th;
         nz = nz / l * th;
         v(p[0] + nx, p[1] + ny, p[2] + nz, c[0], c[1], c[2], a);
         v(p[0] - nx, p[1] - ny, p[2] - nz, c[0], c[1], c[2], a);
         v(q[0] - nx, q[1] - ny, q[2] - nz, c[0], c[1], c[2], a);
         v(q[0] + nx, q[1] + ny, q[2] + nz, c[0], c[1], c[2], a);
      }
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
   record Active(BlockPos pos, long start, boolean rojo) {
   }

   @Environment(EnvType.CLIENT)
   static class Pal {
      float[][] sky;
      float[] g0;
      float[] g1;
      float[] r0;
      float[] r1;
      float[] au0;
      float[] au1;
      float[] orb;
      float[] sq;
      float[] burst;
      float[] edge;
      float boost;

      Pal(
         float[][] sky,
         float[] g0,
         float[] g1,
         float[] r0,
         float[] r1,
         float[] au0,
         float[] au1,
         float[] orb,
         float[] sq,
         float[] burst,
         float[] edge,
         float boost
      ) {
         this.sky = sky;
         this.g0 = g0;
         this.g1 = g1;
         this.r0 = r0;
         this.r1 = r1;
         this.au0 = au0;
         this.au1 = au1;
         this.orb = orb;
         this.sq = sq;
         this.burst = burst;
         this.edge = edge;
         this.boost = boost;
      }
   }
}
