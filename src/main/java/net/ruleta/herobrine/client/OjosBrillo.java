package net.ruleta.herobrine.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;

public final class OjosBrillo {
   private static final List<OjosBrillo.Ojo> OJOS = new ArrayList<>();
   private static OjosBrillo.RectOjo[] rectas;

   private OjosBrillo() {
   }

   static OjosBrillo.RectOjo[] rectasDeLosOjos() {
      if (rectas != null) {
         return rectas;
      }

      List<OjosBrillo.RectOjo> r = new ArrayList<>();

      try (InputStream in = Minecraft.getInstance().getResourceManager().open(HerobrineRenderer.OJOS)) {
         NativeImage img = NativeImage.read(in);

         try {
            for (int mitad = 0; mitad < 2; mitad++) {
               int x0 = mitad == 0 ? 8 : 12;
               int x1 = mitad == 0 ? 11 : 15;
               int minX = 99;
               int minY = 99;
               int maxX = -1;
               int maxY = -1;

               for (int y = 8; y <= 15; y++) {
                  for (int x = x0; x <= x1; x++) {
                     if ((img.getPixel(x, y) >>> 24 & 0xFF) > 16) {
                        minX = Math.min(minX, x);
                        maxX = Math.max(maxX, x);
                        minY = Math.min(minY, y);
                        maxY = Math.max(maxY, y);
                     }
                  }
               }

               if (maxX >= 0) {
                  float cx = (minX + maxX + 1) / 2.0F - 12.0F;
                  float cy = (minY + maxY + 1) / 2.0F - 16.0F;
                  r.add(new OjosBrillo.RectOjo(cx, cy, (maxX - minX + 1) / 2.0F, (maxY - minY + 1) / 2.0F));
               }
            }
         } catch (Throwable var14) {
            if (img != null) {
               try {
                  img.close();
               } catch (Throwable var13) {
                  var14.addSuppressed(var13);
               }
            }

            throw var14;
         }

         if (img != null) {
            img.close();
         }
      } catch (Exception ex) {
         r.clear();
      }

      if (r.isEmpty()) {
         r.add(new OjosBrillo.RectOjo(-2.0F, -3.5F, 1.0F, 0.5F));
         r.add(new OjosBrillo.RectOjo(2.0F, -3.5F, 1.0F, 0.5F));
      }

      rectas = r.toArray(new OjosBrillo.RectOjo[0]);
      return rectas;
   }

   static void registrar(OjosBrillo.Ojo o) {
      long ahora = System.nanoTime();
      synchronized (OJOS) {
         OJOS.removeIf(x -> x.clave() == o.clave() || ahora - x.nanos() > 40000000L);
         OJOS.add(o);
      }
   }

   static void resplandor(Object vc, OjosBrillo.Ojo o) {
      float[][] in = new float[][]{{-o.hw(), -o.hh()}, {o.hw(), -o.hh()}, {o.hw(), o.hh()}, {-o.hw(), o.hh()}};
      quad(vc, o, in[0], in[1], in[2], in[3], 0.85F, 0.85F, 0.85F, 0.85F);
      aro(vc, o, 0.4F, 0.3F);
   }

   private static void aro(Object vc, OjosBrillo.Ojo o, float e, float a) {
      float hw = o.hw();
      float hh = o.hh();
      float[][] in = new float[][]{{-hw, -hh}, {hw, -hh}, {hw, hh}, {-hw, hh}};
      float[][] out = new float[][]{{-hw - e, -hh - e}, {hw + e, -hh - e}, {hw + e, hh + e}, {-hw - e, hh + e}};

      for (int i = 0; i < 4; i++) {
         int j = (i + 1) % 4;
         quad(vc, o, in[i], in[j], out[j], out[i], a, a, 0.0F, 0.0F);
      }
   }

   private static void rect(Object vc, OjosBrillo.Ojo o, float e, float a) {
      float hw = o.hw();
      float hh = o.hh();
      float[][] in = new float[][]{{-hw, -hh}, {hw, -hh}, {hw, hh}, {-hw, hh}};
      float[][] out = new float[][]{{-hw - e, -hh - e}, {hw + e, -hh - e}, {hw + e, hh + e}, {-hw - e, hh + e}};

      for (int i = 0; i < 4; i++) {
         int j = (i + 1) % 4;
         quad(vc, o, in[i], in[j], out[j], out[i], a, a, 0.0F, 0.0F);
      }

      quad(vc, o, in[0], in[1], in[2], in[3], 1.0F, 1.0F, 1.0F, 1.0F);
   }

   private static void quad(Object vc, OjosBrillo.Ojo o, float[] p0, float[] p1, float[] p2, float[] p3, float a0, float a1, float a2, float a3) {
      float[][] p = new float[][]{p0, p1, p2, p3};
      float[] al = new float[]{a0, a1, a2, a3};

      for (int k = 0; k < 8; k++) {
         int i = k < 4 ? k : 7 - k;
         Vector3f c = o.centro();
         float x = c.x + o.px().x * p[i][0] + o.py().x * p[i][1] + o.normal().x * 0.012F;
         float y = c.y + o.px().y * p[i][0] + o.py().y * p[i][1] + o.normal().y * 0.012F;
         float z = c.z + o.px().z * p[i][0] + o.py().z * p[i][1] + o.normal().z * 0.012F;
         vc.addVertex(x, y, z).setColor(1.0F, 1.0F, 1.0F, al[i]);
      }
   }

   public static void dibujar(Camera camara, Matrix4f proyeccion, Matrix4f vista, float pt) { }

   record Ojo(Vector3f centro, Vector3f px, Vector3f py, Vector3f normal, float hw, float hh, long nanos, int clave) {
   }

   record RectOjo(float x, float y, float hw, float hh) {
   }
}
