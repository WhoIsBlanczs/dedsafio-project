package com.supernova;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.ruleta.bunker.Bunkers;

public final class SupernovaRomper {
   public static boolean activo;
   private static final int BLOQUES_POR_TICK = 40000;
   private static final int COLUMNAS_POR_TICK = 600;
   private static final BlockState AIRE = Blocks.AIR.defaultBlockState();
   private static final List<SupernovaRomper.Trabajo> TRABAJOS = new ArrayList<>();

   private SupernovaRomper() {
   }

   static SupernovaRomper.Trabajo empezar(ServerLevel nivel, double x, double z, float radioMax) {
      SupernovaRomper.Trabajo t = new SupernovaRomper.Trabajo(nivel, x, z, radioMax);
      TRABAJOS.add(t);
      return t;
   }

   public static int cancelarTodos() {
      int n = TRABAJOS.size();
      TRABAJOS.clear();
      return n;
   }

   public static void alTick(MinecraftServer servidor) {
      if (!TRABAJOS.isEmpty()) {
         int bloques = 40000;
         int columnas = 600;
         Iterator<SupernovaRomper.Trabajo> it = TRABAJOS.iterator();

         while (it.hasNext() && bloques > 0 && columnas > 0) {
            SupernovaRomper.Trabajo t = it.next();
            ServerLevel nivel = t.nivel;
            int arriba = nivel.getMaxBuildHeight() - 1;
            int abajo = nivel.getMinBuildHeight();
            MutableBlockPos pos = new MutableBlockPos();

            while (!t.terminado() && t.dist[t.siguiente] <= t.limite && bloques > 0 && columnas > 0) {
               int x = t.cx + t.dx[t.siguiente];
               int z = t.cz + t.dz[t.siguiente];

               int y;
               for (y = t.yPendiente == Integer.MIN_VALUE ? arriba : t.yPendiente; y >= abajo && bloques > 0; y--) {
                  pos.set(x, y, z);
                  BlockState estado = nivel.getBlockState(pos);
                  if (!estado.isAir() && !Bunkers.protegido(nivel, pos, estado)) {
                     nivel.setBlock(pos, AIRE, 18);
                     bloques--;
                  }
               }

               if (y < abajo) {
                  t.yPendiente = Integer.MIN_VALUE;
                  t.siguiente++;
                  columnas--;
               } else {
                  t.yPendiente = y;
               }
            }

            if (t.terminado()) {
               it.remove();
            }
         }
      }
   }

   public static void alApagar(MinecraftServer servidor) {
      TRABAJOS.clear();
      activo = false;
   }

   static final class Trabajo {
      final ServerLevel nivel;
      final int cx;
      final int cz;
      final int[] dx;
      final int[] dz;
      final float[] dist;
      int siguiente;
      float limite;
      int yPendiente = Integer.MIN_VALUE;

      Trabajo(ServerLevel nivel, double x, double z, float radioMax) {
         this.nivel = nivel;
         this.cx = (int)Math.floor(x);
         this.cz = (int)Math.floor(z);
         int r = (int)Math.ceil(radioMax);
         int lado = 2 * r + 1;
         long[] tmp = new long[lado * lado];
         int n = 0;

         for (int a = -r; a <= r; a++) {
            for (int b = -r; b <= r; b++) {
               int d2 = a * a + b * b;
               if (d2 <= radioMax * radioMax) {
                  tmp[n++] = (long)d2 << 32 | (long)(a + 32768) << 16 | b + 32768;
               }
            }
         }

         Arrays.sort(tmp, 0, n);
         this.dx = new int[n];
         this.dz = new int[n];
         this.dist = new float[n];

         for (int i = 0; i < n; i++) {
            this.dx[i] = (int)(tmp[i] >> 16 & 65535L) - 32768;
            this.dz[i] = (int)(tmp[i] & 65535L) - 32768;
            this.dist[i] = (float)Math.sqrt(tmp[i] >>> 32);
         }
      }

      boolean terminado() {
         return this.siguiente >= this.dx.length;
      }
   }
}
