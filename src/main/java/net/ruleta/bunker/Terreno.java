package net.ruleta.bunker;

import java.util.BitSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;

final class Terreno {
   private static final int EXPLANADA = 6;
   private static final int LADERA_MAX = 80;
   private static final int ALTO_LIBRE = 16;
   private static final int FLAGS = 18;

   private Terreno() {
   }

   private static int sueloDe(ServerLevel nivel, int x, int z, int desde, MutableBlockPos p) {
      int min = nivel.getMinBuildHeight();

      for (int y = Math.min(desde, nivel.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1); y > min; y--) {
         p.set(x, y, z);
         BlockState s = nivel.getBlockState(p);
         if (!s.isAir() && s.getFluidState().isEmpty() && !s.is(BlockTags.LEAVES) && !s.canBeReplaced() && s.isSolid()) {
            return y;
         }
      }

      return min;
   }

   private static BlockState material(ServerLevel nivel, int hondo) {
      if (nivel.dimension() == Level.NETHER) {
         return Blocks.NETHERRACK.defaultBlockState();
      } else if (nivel.dimension() == Level.END) {
         return Blocks.END_STONE.defaultBlockState();
      } else if (hondo == 0) {
         return Blocks.GRASS_BLOCK.defaultBlockState();
      } else {
         return hondo <= 3 ? Blocks.DIRT.defaultBlockState() : Blocks.STONE.defaultBlockState();
      }
   }

   static final class Obra {
      private static final int CAPAS_POR_TICK = 2;
      private static final int COLUMNAS_POR_TICK = 1200;
      private final ServerLevel nivel;
      private final int ex;
      private final int ey;
      private final int ez;
      private final BitSet ocupado;
      private final int alcance = 86;
      private final int ancho;
      private final int total;
      private int capa;
      private int columna;
      private final MutableBlockPos p = new MutableBlockPos();

      Obra(ServerLevel nivel, BlockPos esquina, BitSet ocupado) {
         this.nivel = nivel;
         this.ex = esquina.getX();
         this.ey = esquina.getY();
         this.ez = esquina.getZ();
         this.ocupado = ocupado;
         this.ancho = 96 + 2 * 86;
         this.total = this.ancho * (99 + 2 * 86);
      }

      ServerLevel nivel() {
         return this.nivel;
      }

      boolean paso() {
         BlockState aire = Blocks.AIR.defaultBlockState();
         int sx = 96;
         int sz = 99;
         if (this.capa < 34) {
            for (int n = 0; n < 2 && this.capa < 34; this.capa++) {
               int y = this.capa;

               for (int z = 0; z < sz; z++) {
                  for (int x = 0; x < sx; x++) {
                     if (!this.ocupado.get((y * sz + z) * sx + x)) {
                        this.p.set(this.ex + x, this.ey + y, this.ez + z);
                        if (!this.nivel.getBlockState(this.p).isAir()) {
                           this.nivel.setBlock(this.p, aire, 18);
                        }
                     }
                  }
               }

               n++;
            }

            return false;
         } else {
            for (int hasta = Math.min(this.total, this.columna + 1200); this.columna < hasta; this.columna++) {
               this.columna(this.columna % this.ancho - 86, this.columna / this.ancho - 86, aire);
            }

            return this.columna >= this.total;
         }
      }

      private void columna(int x, int z, BlockState aire) {
         int sx = 96;
         int sz = 99;
         int arriba = this.ey - 1;
         int fx = x < 0 ? -x : (x >= sx ? x - sx + 1 : 0);
         int fz = z < 0 ? -z : (z >= sz ? z - sz + 1 : 0);
         float d = Mth.sqrt(fx * fx + fz * fz);
         if (!(d > 86.0F)) {
            int wx = this.ex + x;
            int wz = this.ez + z;
            int tope;
            if (d <= 6.0F) {
               tope = arriba;
               if (d > 0.0F) {
                  for (int y = this.ey; y < this.ey + 16; y++) {
                     this.p.set(wx, y, wz);
                     if (!this.nivel.getBlockState(this.p).isAir()) {
                        this.nivel.setBlock(this.p, aire, 18);
                     }
                  }
               }
            } else {
               float ruido = (float)(Math.sin(wx * 0.21) * 1.3 + Math.cos(wz * 0.17) * 1.3);
               tope = arriba - Math.round((d - 6.0F) * 0.95F + ruido);
            }

            int suelo = Terreno.sueloDe(this.nivel, wx, wz, tope, this.p);
            if (suelo < tope || !(d > 6.0F)) {
               for (int y = suelo + 1; y <= tope; y++) {
                  this.p.set(wx, y, wz);
                  this.nivel.setBlock(this.p, Terreno.material(this.nivel, tope - y), 18);
               }
            }
         }
      }
   }
}
