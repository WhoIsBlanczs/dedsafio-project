package net.ruleta.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Object;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Object;
import net.minecraft.client.renderer.Object;
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

   public void render(LightningBolt e, float yaw, float pt, PoseStack pose, Object buffers, int luz) { }

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
      Object vc, Matrix4f m, RayoRealista.Tramo t, Vec3 cam, double ex, double ey, double ez, float grosor, float alfa, float cr, float cg, float cb
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
