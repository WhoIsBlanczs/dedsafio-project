package net.ruleta.client.burbuja;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Object;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.util.List;
import net.minecraft.client.Minecraft;
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

public class BurbujaRenderer extends EntityRenderer<Burbuja> {
   private static final Identifier BLANCO = Identifier.fromNamespaceAndPath("ruleta", "textures/entity/cosmeticos/blanco.png");
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

   public void render(Burbuja e, float yaw, float pt, PoseStack ps, Object buf, int luzEntidad) { }

   private static void vertice(Object vc, Pose pose, int luz, float[] p, float[] n, float[] c) {
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
