package net.ruleta.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public final class RayoRealista extends EntityRenderer<LightningBolt> {
   public RayoRealista(Context ctx) {
      super(ctx);
   }

   public Identifier getTextureLocation(LightningBolt e) {
      return Identifier.parse("textures/entity/lightning.png");
   }

   public void render(LightningBolt e, float yaw, float pt, PoseStack pose, MultiBufferSource buffers, int luz) {
      float edad = e.tickCount + pt;
      float k;
      if (edad < 2.0F) {
         k = 1.0F;
      } else if (edad < 3.5F) {
         k = 0.25F;
      } else if (edad < 5.5F) {
         k = 0.85F;
      } else if (edad < 7.0F) {
         k = 0.2F;
      } else {
         k = Math.max(0.0F, 0.55F - (edad - 7.0F) * 0.14F);
      }

      if (!(k <= 0.01F)) {
         RandomSource r = RandomSource.create(e.seed);
         List<RayoRealista.Tramo> tramos = new ArrayList<>();
         double alto = 260.0;
         Vec3 arriba = new Vec3((r.nextFloat() - 0.5F) * 50.0, alto, (r.nextFloat() - 0.5F) * 50.0);
         List<Vec3> tronco = quebrar(r, arriba, Vec3.ZERO, 7);

         for (int i = 0; i + 1 < tronco.size(); i++) {
            float t0 = (float)i / (tronco.size() - 1);
            float t1 = (float)(i + 1) / (tronco.size() - 1);
            tramos.add(new RayoRealista.Tramo(tronco.get(i), tronco.get(i + 1), 0.1F + 0.14F * (1.0F - t0), 0.1F + 0.14F * (1.0F - t1)));
         }

         for (int i = 3; i < tronco.size() - 2; i++) {
            if (r.nextFloat() < 0.16F) {
               Vec3 o = tronco.get(i);
               double largo = 12.0 + r.nextFloat() * 36.0;
               double ang = r.nextFloat() * Math.PI * 2.0;
               Vec3 fin = o.add(Math.cos(ang) * largo, -largo * (0.5 + r.nextFloat() * 0.9), Math.sin(ang) * largo);
               if (fin.y < 0.0) {
                  fin = new Vec3(fin.x, 0.0, fin.z);
               }

               List<Vec3> rama = quebrar(r, o, fin, 5);

               for (int j = 0; j + 1 < rama.size(); j++) {
                  float f0 = 1.0F - (float)j / (rama.size() - 1);
                  float f1 = 1.0F - (float)(j + 1) / (rama.size() - 1);
                  tramos.add(new RayoRealista.Tramo(rama.get(j), rama.get(j + 1), 0.025F + 0.05F * f0, 0.025F + 0.05F * f1));
                  if (j > 1 && j < rama.size() - 2 && r.nextFloat() < 0.18F) {
                     Vec3 p = rama.get(j);
                     double l2 = 5.0 + r.nextFloat() * 14.0;
                     double a2 = r.nextFloat() * Math.PI * 2.0;
                     Vec3 f2 = p.add(Math.cos(a2) * l2, -l2 * (0.3 + r.nextFloat() * 0.6), Math.sin(a2) * l2);
                     List<Vec3> sub = quebrar(r, p, f2, 4);

                     for (int m = 0; m + 1 < sub.size(); m++) {
                        float g0 = 1.0F - (float)m / (sub.size() - 1);
                        float g1 = 1.0F - (float)(m + 1) / (sub.size() - 1);
                        tramos.add(new RayoRealista.Tramo(sub.get(m), sub.get(m + 1), 0.012F + 0.02F * g0, 0.012F + 0.02F * g1));
                     }
                  }
               }
            }
         }

         VertexConsumer vc = buffers.getBuffer(RenderType.lightning());
         Matrix4f m = pose.last().pose();
         Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
         double ex = Mth.lerp(pt, e.xo, e.getX());
         double ey = Mth.lerp(pt, e.yo, e.getY());
         double ez = Mth.lerp(pt, e.zo, e.getZ());
         double dist = Math.sqrt((ex - cam.x) * (ex - cam.x) + (ez - cam.z) * (ez - cam.z));
         float grosor = (float)Mth.clamp(0.7 + dist / 90.0, 0.7, 4.0);
         float[][] capas = new float[][]{{7.0F, 0.12F, 0.45F, 0.55F, 1.0F}, {2.6F, 0.35F, 0.72F, 0.75F, 1.0F}, {0.9F, 1.0F, 1.0F, 1.0F, 1.0F}};

         for (float[] c : capas) {
            for (RayoRealista.Tramo t : tramos) {
               tira(vc, m, t, cam, ex, ey, ez, grosor * c[0], k * c[1], c[2], c[3], c[4]);
            }
         }
      }
   }

   private static List<Vec3> quebrar(RandomSource r, Vec3 a, Vec3 b, int niveles) {
      List<Vec3> pts = new ArrayList<>();
      pts.add(a);
      pts.add(b);
      double desv = a.distanceTo(b) * 0.13;

      for (int n = 0; n < niveles; n++) {
         List<Vec3> nuevos = new ArrayList<>();

         for (int i = 0; i + 1 < pts.size(); i++) {
            Vec3 p = pts.get(i);
            Vec3 q = pts.get(i + 1);
            nuevos.add(p);
            Vec3 medio = p.add(q).scale(0.5);
            nuevos.add(medio.add((r.nextDouble() - 0.5) * 2.0 * desv, (r.nextDouble() - 0.5) * desv * 0.5, (r.nextDouble() - 0.5) * 2.0 * desv));
         }

         nuevos.add(pts.get(pts.size() - 1));
         pts = nuevos;
         desv *= 0.52;
      }

      return pts;
   }

   private static void tira(
      VertexConsumer vc, Matrix4f m, RayoRealista.Tramo t, Vec3 cam, double ex, double ey, double ez, float grosor, float alfa, float cr, float cg, float cb
   ) {
      Vec3 dir = t.b.subtract(t.a);
      Vec3 medio = t.a.add(t.b).scale(0.5).add(ex - cam.x, ey - cam.y, ez - cam.z);
      Vec3 lado = dir.cross(medio);
      if (!(lado.lengthSqr() < 1.0E-8)) {
         lado = lado.normalize();
         float wa = t.wa * grosor;
         float wb = t.wb * grosor;
         float ax = (float)(t.a.x + lado.x * wa);
         float ay = (float)(t.a.y + lado.y * wa);
         float az = (float)(t.a.z + lado.z * wa);
         float bx = (float)(t.a.x - lado.x * wa);
         float by = (float)(t.a.y - lado.y * wa);
         float bz = (float)(t.a.z - lado.z * wa);
         float cx = (float)(t.b.x - lado.x * wb);
         float cy = (float)(t.b.y - lado.y * wb);
         float cz = (float)(t.b.z - lado.z * wb);
         float dx = (float)(t.b.x + lado.x * wb);
         float dy = (float)(t.b.y + lado.y * wb);
         float dz = (float)(t.b.z + lado.z * wb);
         vc.addVertex(m, ax, ay, az).setColor(cr, cg, cb, alfa);
         vc.addVertex(m, bx, by, bz).setColor(cr, cg, cb, alfa);
         vc.addVertex(m, cx, cy, cz).setColor(cr, cg, cb, alfa);
         vc.addVertex(m, dx, dy, dz).setColor(cr, cg, cb, alfa);
      }
   }

   public boolean shouldRender(LightningBolt e, Frustum f, double x, double y, double z) {
      return true;
   }

   private record Tramo(Vec3 a, Vec3 b, float wa, float wb) {
   }
}
