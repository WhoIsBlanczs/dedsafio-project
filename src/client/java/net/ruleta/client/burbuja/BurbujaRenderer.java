package net.ruleta.client.burbuja;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.ruleta.burbuja.Burbuja;

public class BurbujaRenderer extends EntityRenderer<Burbuja, net.ruleta.compat.Compat.EstadoEntidad> {
   private static final Identifier BLANCO = Identifier.fromNamespaceAndPath("ruletafanproject", "textures/entity/cosmeticos/blanco.png");
   private static final int LAT = 14;
   private static final int LON = 26;
   private static final float HUNDIDO_MAX = 0.34F;

   public BurbujaRenderer(Context ctx) {
      super(ctx);
      this.shadowRadius = 0.0F;
   }

   public Identifier getTextureLocation(Burbuja e) {
      return BLANCO;
   }

   public net.ruleta.compat.Compat.EstadoEntidad createRenderState() {
      return new net.ruleta.compat.Compat.EstadoEntidad();
   }

   public void extractRenderState(Burbuja entidad$, net.ruleta.compat.Compat.EstadoEntidad estado$, float pt$) {
      super.extractRenderState(entidad$, estado$, pt$);
      estado$.entidad = entidad$;
      estado$.pt = pt$;
   }

   public void submit(net.ruleta.compat.Compat.EstadoEntidad estado$, PoseStack ps, net.minecraft.client.renderer.SubmitNodeCollector colector$, net.minecraft.client.renderer.state.level.CameraRenderState camara$) {
      Burbuja e = (Burbuja)estado$.entidad;
      float pt = estado$.pt;
      float yaw = e.getYRot();
      int luzEntidad = estado$.lightCoords;
      float R = e.getDiametro() / 2.0F;
      int n = 390;
      if (e.abolladura == null || e.abolladura.length != n) {
         e.abolladura = new float[n];
         e.abolladuraVel = new float[n];
      }

      double cx = Mth.lerp(pt, e.xOld, e.getX());
      double cy = Mth.lerp(pt, e.yOld, e.getY()) + R;
      double cz = Mth.lerp(pt, e.zOld, e.getZ());
      float[][] dir = new float[n][3];

      for (int i = 0; i <= 14; i++) {
         double th = Math.PI * i / 14.0;

         for (int j = 0; j < 26; j++) {
            double ph = (Math.PI * 2) * j / 26.0;
            int k = i * 26 + j;
            dir[k][0] = (float)(Math.sin(th) * Math.cos(ph));
            dir[k][1] = (float)Math.cos(th);
            dir[k][2] = (float)(Math.sin(th) * Math.sin(ph));
         }
      }

      this.actualizarHundido(e, cx, cy, cz, R, dir);
      float[][] pos = new float[n][3];

      for (int k = 0; k < n; k++) {
         float r = R - e.abolladura[k];
         pos[k][0] = dir[k][0] * r;
         pos[k][1] = R + dir[k][1] * r;
         pos[k][2] = dir[k][2] * r;
      }

      Vec3 cam = Minecraft.getInstance().gameRenderer.mainCamera().position();
      float camX = (float)(cam.x - cx);
      float camY = (float)(cam.y - (cy - R));
      float camZ = (float)(cam.z - cz);
      float t = e.tickCount + pt;
      float[][] col = new float[n][4];

      for (int k = 0; k < n; k++) {
         float vx = camX - pos[k][0];
         float vy = camY - pos[k][1];
         float vz = camZ - pos[k][2];
         float lv = Mth.sqrt(vx * vx + vy * vy + vz * vz);
         if (lv < 1.0E-4F) {
            lv = 1.0F;
         }

         float nv = Math.abs(dir[k][0] * vx / lv + dir[k][1] * vy / lv + dir[k][2] * vz / lv);
         float fres = (float)Math.pow(1.0F - nv, 1.5);
         float h = (
               0.3F
                  + (1.0F - nv) * 1.25F
                  + 0.22F * (float)Math.sin(dir[k][1] * 3.4F + t * 0.021F)
                  + 0.18F * (float)Math.sin(dir[k][0] * 4.1F + dir[k][2] * 3.3F - t * 0.017F)
            )
            % 1.0F;
         if (h < 0.0F) {
            h++;
         }

         int rgb = Mth.hsvToRgb(h, 0.62F, 1.0F);
         float r = (rgb >> 16 & 0xFF) / 255.0F;
         float g = (rgb >> 8 & 0xFF) / 255.0F;
         float b = (rgb & 0xFF) / 255.0F;
         float s1 = (float)Math.pow(Math.max(0.0F, dir[k][0] * -0.4F + dir[k][1] * 0.8F + dir[k][2] * 0.45F), 22.0);
         float s2 = (float)Math.pow(Math.max(0.0F, dir[k][0] * 0.55F + dir[k][1] * 0.3F + dir[k][2] * -0.78F), 40.0);
         float sp = Math.min(1.0F, s1 + s2 * 0.7F);
         col[k][0] = Mth.lerp(sp, r, 1.0F);
         col[k][1] = Mth.lerp(sp, g, 1.0F);
         col[k][2] = Mth.lerp(sp, b, 1.0F);
         col[k][3] = Mth.clamp(0.16F + 0.62F * fres + 0.55F * sp, 0.0F, 0.9F);
      }

      int luz = 15728880;
      net.minecraft.client.renderer.rendertype.RenderType vc$tipo = (net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucentEmissive(BLANCO));
      colector$.submitCustomGeometry(ps, vc$tipo, (pose, vc) -> {
         for (int pasada = 0; pasada < 2; pasada++) {
            for (int i = 0; i < 14; i++) {
               for (int j = 0; j < 26; j++) {
                  int j2 = (j + 1) % 26;
                  int a = i * 26 + j;
                  int b = i * 26 + j2;
                  int c = (i + 1) * 26 + j2;
                  int d = (i + 1) * 26 + j;
                  float mx = (pos[a][0] + pos[b][0] + pos[c][0] + pos[d][0]) / 4.0F;
                  float my = (pos[a][1] + pos[b][1] + pos[c][1] + pos[d][1]) / 4.0F;
                  float mz = (pos[a][2] + pos[b][2] + pos[c][2] + pos[d][2]) / 4.0F;
                  float nx = dir[a][0] + dir[b][0] + dir[c][0] + dir[d][0];
                  float ny = dir[a][1] + dir[b][1] + dir[c][1] + dir[d][1];
                  float nz = dir[a][2] + dir[b][2] + dir[c][2] + dir[d][2];
                  boolean delante = nx * (camX - mx) + ny * (camY - my) + nz * (camZ - mz) >= 0.0F;
                  if (delante == (pasada == 1)) {
                     vertice(vc, pose, luz, pos[a], dir[a], col[a]);
                     vertice(vc, pose, luz, pos[b], dir[b], col[b]);
                     vertice(vc, pose, luz, pos[c], dir[c], col[c]);
                     vertice(vc, pose, luz, pos[d], dir[d], col[d]);
                  }
               }
            }
         }
      });
   }

   private static void vertice(VertexConsumer vc, Pose pose, int luz, float[] p, float[] n, float[] c) {
      vc.addVertex(pose.pose(), p[0], p[1], p[2])
         .setColor(c[0], c[1], c[2], c[3])
         .setUv(0.5F, 0.5F)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(luz)
         .setNormal(pose, n[0], n[1], n[2]);
   }

   private void actualizarHundido(Burbuja e, double cx, double cy, double cz, float R, float[][] dir) {
      Level nivel = e.level();
      int n = dir.length;
      float[] objetivo = new float[n];
      AABB zona = new AABB(cx - R - 0.5, cy - R - 0.5, cz - R - 0.5, cx + R + 0.5, cy + R + 0.5, cz + R + 0.5);
      List<Entity> cerca = nivel.getEntities(
         e, zona, x -> x.isAlive() && !x.isSpectator() && x.getVehicle() != e && !(x instanceof Burbuja) && !(x instanceof ItemEntity)
      );

      for (int k = 0; k < n; k++) {
         float prof = 0.0F;
         boolean tocando = false;

         for (int s = 0; s <= 6; s++) {
            float d = s * 0.06F;
            float r = R - d;
            double px = cx + dir[k][0] * r;
            double py = cy + dir[k][1] * r;
            double pz = cz + dir[k][2] * r;
            if (!solido(nivel, cerca, px, py, pz)) {
               break;
            }

            tocando = true;
            prof = d + 0.06F;
         }

         objetivo[k] = tocando ? Math.min(0.34F, prof) : 0.0F;
      }

      for (int pasada = 0; pasada < 3; pasada++) {
         float[] tmp = new float[n];

         for (int i = 0; i <= 14; i++) {
            for (int j = 0; j < 26; j++) {
               int k = i * 26 + j;
               float suma = objetivo[k] * 2.0F;
               suma += objetivo[Math.max(0, i - 1) * 26 + j] + objetivo[Math.min(14, i + 1) * 26 + j];
               suma += objetivo[i * 26 + (j + 26 - 1) % 26] + objetivo[i * 26 + (j + 1) % 26];
               tmp[k] = suma / 6.0F;
            }
         }

         objetivo = tmp;
      }

      for (int k = 0; k < n; k++) {
         float acel = (objetivo[k] - e.abolladura[k]) * 0.32F;
         e.abolladuraVel[k] = e.abolladuraVel[k] * 0.66F + acel;
         e.abolladura[k] = Mth.clamp(e.abolladura[k] + e.abolladuraVel[k], -0.04F, 0.34F);
      }

      for (int i : new int[]{0, 14}) {
         float v = e.abolladura[i * 26];

         for (int j = 1; j < 26; j++) {
            e.abolladura[i * 26 + j] = v;
         }
      }
   }

   private static boolean solido(Level nivel, List<Entity> entidades, double x, double y, double z) {
      BlockPos bp = BlockPos.containing(x, y, z);
      BlockState estado = nivel.getBlockState(bp);
      if (!estado.isAir()) {
         VoxelShape forma = estado.getCollisionShape(nivel, bp);
         if (!forma.isEmpty() && forma.bounds().move(bp).contains(x, y, z)) {
            return true;
         }
      }

      for (Entity o : entidades) {
         if (o.getBoundingBox().contains(x, y, z)) {
            return true;
         }
      }

      return false;
   }
}
