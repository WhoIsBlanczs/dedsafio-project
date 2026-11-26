package net.ruleta.client.dedsafio;

import com.mojang.renderpearl.backend.opengl.Uniform;
import com.mojang.blaze3d.vertex.Object;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Object;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

public final class EspumaDedsafio {
   private static Object shader;
   private static final int R = 40;
   private static final int Y = -3;
   private static final double Y_DIBUJO = -2.17;
   private static final float ALCANCE = 2.4F;
   private static int[] quads = new int[0];
   private static float[] valores = new float[0];
   private static int cuantos;
   private static int ultX = Integer.MIN_VALUE;
   private static int ultZ = Integer.MIN_VALUE;
   private static long ultimoEscaneo;

   private EspumaDedsafio() {
   }

   public static void shader(Object s) {
      shader = s;
   }

   public static void limpiar() {
      cuantos = 0;
      ultX = Integer.MIN_VALUE;
   }

   private static void escanear(Level nivel, int cx, int cz) {
      int n = 81;
      boolean[] solido = new boolean[n * n];
      boolean[] agua = new boolean[n * n];
      MutableBlockPos p = new MutableBlockPos();

      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            BlockState e = nivel.getBlockState(p.set(cx - 40 + i, -3, cz - 40 + j));
            if (e.getFluidState().is(FluidTags.WATER)) {
               agua[i * n + j] = true;
            } else if (!e.isAir() && e.canOcclude()) {
               solido[i * n + j] = true;
            }
         }
      }

      int m = n + 1;
      float[] esq = new float[m * m];

      for (int i = 0; i < m; i++) {
         for (int j = 0; j < m; j++) {
            float mejor = 0.0F;

            for (int a = i - 3; a <= i + 2; a++) {
               for (int c = j - 3; c <= j + 2; c++) {
                  if (a >= 0 && c >= 0 && a < n && c < n && solido[a * n + c]) {
                     float dx = Math.max(Math.max(a - i, 0), i - (a + 1));
                     float dz = Math.max(Math.max(c - j, 0), j - (c + 1));
                     float d = (float)Math.sqrt(dx * dx + dz * dz);
                     mejor = Math.max(mejor, 1.0F - d / 2.4F);
                  }
               }
            }

            esq[i * m + j] = Math.max(0.0F, mejor);
         }
      }

      int cap = 256;
      int[] q = new int[cap * 2];
      float[] v = new float[cap * 4];
      int k = 0;

      for (int i = 0; i < n; i++) {
         for (int j = 0; j < n; j++) {
            if (agua[i * n + j]) {
               float a = esq[i * m + j];
               float b = esq[(i + 1) * m + j];
               float c = esq[(i + 1) * m + j + 1];
               float d = esq[i * m + j + 1];
               if (!(a + b + c + d < 0.02F)) {
                  if (k >= cap) {
                     cap *= 2;
                     q = Arrays.copyOf(q, cap * 2);
                     v = Arrays.copyOf(v, cap * 4);
                  }

                  q[k * 2] = cx - 40 + i;
                  q[k * 2 + 1] = cz - 40 + j;
                  v[k * 4] = a;
                  v[k * 4 + 1] = b;
                  v[k * 4 + 2] = c;
                  v[k * 4 + 3] = d;
                  k++;
               }
            }
         }
      }

      quads = q;
      valores = v;
      cuantos = k;
   }

   private static void fijar(Object sh, String nombre, float a, float b, float c, float d, int n) {
      Uniform u = sh.getUniform(nombre);
      if (u != null) {
         if (n == 4) {
            u.set(a, b, c, d);
         } else {
            u.set(a);
         }
      }
   }

   public static void dibujar(Matrix4f vista, double camX, double camY, double camZ) { }
}
