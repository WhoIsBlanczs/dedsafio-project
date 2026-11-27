package net.ruleta.client.reviil;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CabezaReviil {
   private static final Logger LOG = LoggerFactory.getLogger("ruleta");
   private static final Identifier MODELO = Identifier.fromNamespaceAndPath("ruleta", "models/momento_reviil/cabeza.bin");
   private static final Identifier TEXTURA = Identifier.fromNamespaceAndPath("ruleta", "textures/momento_reviil/cabeza.png");
   private static final Identifier OJO = Identifier.fromNamespaceAndPath("ruleta", "textures/momento_reviil/ojo_espiral.png");
   private static final float[][] OJOS = new float[][]{{-0.2459F, 0.18F, 0.3905F, 0.1015F}, {0.2459F, 0.18F, 0.3905F, 0.1015F}};
   private static final float DISTANCIA = 40.0F;
   private static final float MEDIO_ALTO = 0.34F;
   private static final float PROFUNDIDAD = 1.6F;
   private static final float LUZ_AMBIENTE = 0.42F;
   private static final float LUZ_DIFUSA = 0.85F;
   private static final float BORDE_ROJO = 0.35F;
   private static final float GIRO_GRADOS = 0.0F;
   private static final float Z_OJO = 0.318F;
   private static final float OJO_RADIO = 0.8F;
   private static final float ANCHO = 1.15F;
   private static final float GIRO_OJOS = 2.4F;
   private static boolean cargado;
   private static float[] vertices;
   private static int[] caras;
   private static float[] normales;
   private static int[] colR;
   private static int[] colG;
   private static int[] colB;
   private static Object vbo;
   private static int luzSubida = Integer.MIN_VALUE;
   private static final float PASO_LUZ = 2.0F;
   public static float retroceso;
   public static float cabeceoExtra;

   private CabezaReviil() {
   }

   public static void precargar() {
      cargar();
   }

   private static void cargar() {
      if (!cargado) {
         cargado = true;
         Optional<Resource> r = Minecraft.getInstance().getResourceManager().getResource(MODELO);
         if (r.isEmpty()) {
            LOG.warn("Falta el modelo de la cabeza de Reviil ({})", MODELO);
         } else {
            try (
               InputStream in = r.get().open();
               DataInputStream d = new DataInputStream(new BufferedInputStream(in));
            ) {
               byte[] magia = new byte[4];
               d.readFully(magia);
               int nv = d.readInt();
               int nt = d.readInt();
               float[] v = new float[nv * 6];

               for (int i = 0; i < v.length; i++) {
                  v[i] = d.readFloat();
               }

               int[] c = new int[nt * 3];

               for (int i = 0; i < c.length; i++) {
                  c[i] = d.readInt();
               }

               vertices = v;
               caras = c;
               normales = calcularNormales(v, c);
               colR = new int[nv];
               colG = new int[nv];
               colB = new int[nv];
            } catch (Exception ex) {
               LOG.warn("No se pudo leer el modelo de la cabeza de Reviil", ex);
            }
         }
      }
   }

   private static float[] calcularNormales(float[] v, int[] c) {
      int nv = v.length / 6;
      float[] n = new float[nv * 3];

      for (int t = 0; t < c.length; t += 3) {
         int a = c[t] * 6;
         int b = c[t + 1] * 6;
         int d = c[t + 2] * 6;
         float e1x = v[b] - v[a];
         float e1y = v[b + 1] - v[a + 1];
         float e1z = v[b + 2] - v[a + 2];
         float e2x = v[d] - v[a];
         float e2y = v[d + 1] - v[a + 1];
         float e2z = v[d + 2] - v[a + 2];
         float nx = e1y * e2z - e1z * e2y;
         float ny = e1z * e2x - e1x * e2z;
         float nz = e1x * e2y - e1y * e2x;
         float cx = (v[a] + v[b] + v[d]) / 3.0F;
         float cy = (v[a + 1] + v[b + 1] + v[d + 1]) / 3.0F - 0.07F;
         float cz = (v[a + 2] + v[b + 2] + v[d + 2]) / 3.0F;
         if (nx * cx + ny * cy + nz * cz < 0.0F) {
            nx = -nx;
            ny = -ny;
            nz = -nz;
         }

         for (int k = 0; k < 3; k++) {
            int i = c[t + k] * 3;
            n[i] += nx;
            n[i + 1] = n[i + 1] + ny;
            n[i + 2] = n[i + 2] + nz;
         }
      }

      for (int i = 0; i < n.length; i += 3) {
         float l = (float)Math.sqrt(n[i] * n[i] + n[i + 1] * n[i + 1] + n[i + 2] * n[i + 2]);
         if (l > 1.0E-6F) {
            n[i] /= l;
            n[i + 1] = n[i + 1] / l;
            n[i + 2] = n[i + 2] / l;
         } else {
            n[i + 2] = 1.0F;
         }
      }

      return n;
   }

   private static void iluminar(float cabeceo, float giro) {
      float cs = (float)Math.cos(cabeceo);
      float sn = (float)Math.sin(cabeceo);
      float cg = (float)Math.cos(giro);
      float sg = (float)Math.sin(giro);
      float lx = -0.45F;
      float ly = 0.55F;
      float lz = 0.7F;
      float ll = (float)Math.sqrt(lx * lx + ly * ly + lz * lz);
      lx /= ll;
      ly /= ll;
      lz /= ll;
      float[] v = vertices;

      for (int i = 0; i < colR.length; i++) {
         float mx = normales[i * 3];
         float ny = normales[i * 3 + 1];
         float mz = normales[i * 3 + 2];
         float nx = mx * cg + mz * sg;
         float nz = -mx * sg + mz * cg;
         float ty = ny * cs - nz * sn;
         float tz = ny * sn + nz * cs;
         float ndl = Math.max(0.0F, nx * lx + ty * ly + tz * lz);
         float luzModelo = v[i * 6 + 5];
         float sombra = Math.min(1.0F, 0.42F + 0.85F * ndl) * (0.8F + 0.2F * luzModelo);
         float borde = 1.0F - Math.max(0.0F, Math.min(1.0F, tz));
         float tinte = 1.0F - 0.35F * borde * borde;
         colR[i] = Math.round(sombra * 255.0F);
         colG[i] = Math.round(sombra * tinte * 255.0F);
         colB[i] = Math.round(sombra * tinte * 255.0F);
      }
   }

   public static void dibujar(Matrix4f vista, float angulo, float centroY, float visible, float tiempo, float distancia) { }

   private static int[] mezclar(int x, int y, int z) {
      x = x * 1664525 + 1013904223;
      y = y * 1664525 + 1013904223;
      z = z * 1664525 + 1013904223;
      x += y * z;
      y += z * x;
      z += x * y;
      x ^= x >>> 16;
      y ^= y >>> 16;
      z ^= z >>> 16;
      x += y * z;
      y += z * x;
      z += x * y;
      return new int[]{x, y, z};
   }

   private static float azar(float x) {
      int v = mezclar((int)Math.floor(x), (int)Math.floor(x * 0.37F + 91.0F), 7)[0];
      return (float)(Integer.toUnsignedLong(v) / 4.294967295E9);
   }

   private static float ondula(float x) {
      float i = (float)Math.floor(x);
      float f = x - i;
      f = f * f * (3.0F - 2.0F * f);
      float a = azar(i * 7.0F + 3.0F);
      return a + (azar(i * 7.0F + 10.0F) - a) * f;
   }

   public static float centroGrieta(float s) {
      return 0.07F * (ondula(s) - 0.5F) + 0.025F * (ondula(s * 2.0F) - 0.5F) + 0.018F * (ondula(s * 3.0F) - 0.5F) + 0.008F * (ondula(s * 5.0F) - 0.5F);
   }
}
