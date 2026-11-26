package net.ruleta.arboles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class GeneradorArboles {
   public static final int COSTA = 0;
   public static final int PALMERA = 1;

   private GeneradorArboles() {
   }

   private static BlockState tronco(int tipo, Axis eje) {
      return (BlockState)(tipo == 0 ? Blocks.DARK_OAK_LOG : Blocks.JUNGLE_LOG).defaultBlockState().setValue(RotatedPillarBlock.AXIS, eje);
   }

   private static BlockState hojas(int tipo) {
      return (BlockState)(tipo == 0 ? Blocks.DARK_OAK_LEAVES : Blocks.JUNGLE_LEAVES).defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
   }

   private static boolean libre(LevelAccessor nivel, BlockPos p) {
      BlockState s = nivel.getBlockState(p);
      return s.isAir() || s.canBeReplaced() && s.getFluidState().isEmpty() || s.is(BlockTags.LEAVES) || s.is(BlockTags.SAPLINGS);
   }

   private static void tronco(LevelAccessor nivel, int x, int y, int z, int tipo, Axis eje) {
      BlockPos p = new BlockPos(x, y, z);
      if (libre(nivel, p)) {
         nivel.setBlock(p, tronco(tipo, eje), 2);
      }
   }

   private static void hoja(LevelAccessor nivel, int x, int y, int z, int tipo) {
      BlockPos p = new BlockPos(x, y, z);
      BlockState s = nivel.getBlockState(p);
      if (s.isAir() || s.canBeReplaced() && s.getFluidState().isEmpty() || s.is(BlockTags.SAPLINGS)) {
         nivel.setBlock(p, hojas(tipo), 2);
      }
   }

   private static void copa(LevelAccessor nivel, RandomSource r, int cx, int cy, int cz, float rh, float rv, int tipo) {
      int ih = (int)Math.ceil(rh);
      int iv = (int)Math.ceil(rv);

      for (int dx = -ih; dx <= ih; dx++) {
         for (int dy = -iv; dy <= iv; dy++) {
            for (int dz = -ih; dz <= ih; dz++) {
               float d = (dx * dx + dz * dz) / (rh * rh) + dy * dy / (rv * rv);
               if (!(d > 1.0F) && (!(d > 0.72F) || r.nextInt(4) != 0)) {
                  hoja(nivel, cx + dx, cy + dy, cz + dz, tipo);
               }
            }
         }
      }
   }

   public static void crecer(LevelAccessor nivel, BlockPos base, RandomSource r, int tipo) {
      if (tipo == 1) {
         palmera(nivel, base, r);
      } else {
         costa(nivel, base, r);
      }
   }

   private static void costa(LevelAccessor nivel, BlockPos base, RandomSource r) {
      int alto = 10 + r.nextInt(7);
      double ang = r.nextDouble() * Math.PI * 2.0;
      double inclina = 3.0 + r.nextDouble() * 3.0;
      double dx = Math.cos(ang);
      double dz = Math.sin(ang);
      boolean grueso = alto >= 12 && r.nextBoolean();
      int[] tx = new int[alto];
      int[] tz = new int[alto];

      for (int i = 0; i < alto; i++) {
         double t = (double)i / (alto - 1);
         tx[i] = base.getX() + (int)Math.round(dx * inclina * t);
         tz[i] = base.getZ() + (int)Math.round(dz * inclina * t);
         int y = base.getY() + i;
         tronco(nivel, tx[i], y, tz[i], 0, Axis.Y);
         if (grueso && i < alto * 0.55F) {
            int ex = Math.abs(dx) >= Math.abs(dz) ? (dx >= 0.0 ? 1 : -1) : 0;
            int ez = ex == 0 ? (dz >= 0.0 ? 1 : -1) : 0;
            tronco(nivel, tx[i] + ex, y, tz[i] + ez, 0, Axis.Y);
         }
      }

      int cx = tx[alto - 1];
      int cz = tz[alto - 1];
      int cy = base.getY() + alto - 1;
      int ramas = 3 + r.nextInt(3);

      for (int k = 0; k < ramas; k++) {
         int desde = (int)(alto * (0.55 + r.nextDouble() * 0.35));
         double a = r.nextDouble() * Math.PI * 2.0;
         int largo = 2 + r.nextInt(3);
         int bx = tx[desde];
         int bz = tz[desde];
         int by = base.getY() + desde;
         int fx = bx;
         int fy = by;
         int fz = bz;

         for (int s = 1; s <= largo; s++) {
            fx = bx + (int)Math.round(Math.cos(a) * s);
            fz = bz + (int)Math.round(Math.sin(a) * s);
            fy = by + s / 2;
            Axis eje = Math.abs(Math.cos(a)) >= Math.abs(Math.sin(a)) ? Axis.X : Axis.Z;
            tronco(nivel, fx, fy, fz, 0, eje);
         }

         copa(nivel, r, fx, fy + 1, fz, 2.6F, 1.8F, 0);
      }

      int ox = (int)Math.round(dx * 1.2);
      int oz = (int)Math.round(dz * 1.2);
      copa(nivel, r, cx + ox, cy + 1, cz + oz, 4.2F, 2.7F, 0);
      copa(nivel, r, cx - ox, cy - 1, cz - oz, 3.4F, 2.0F, 0);
      tronco(nivel, cx, cy + 1, cz, 0, Axis.Y);
   }

   private static void palmera(LevelAccessor nivel, BlockPos base, RandomSource r) {
      int alto = 7 + r.nextInt(6);
      double ang = r.nextDouble() * Math.PI * 2.0;
      double curva = 2.0 + r.nextDouble() * 2.5;
      double dx = Math.cos(ang);
      double dz = Math.sin(ang);
      int px = base.getX();
      int pz = base.getZ();

      for (int i = 0; i < alto; i++) {
         double t = (double)i / (alto - 1);
         px = base.getX() + (int)Math.round(dx * curva * t * t);
         pz = base.getZ() + (int)Math.round(dz * curva * t * t);
         tronco(nivel, px, base.getY() + i, pz, 1, Axis.Y);
      }

      int py = base.getY() + alto - 1;
      hoja(nivel, px, py + 1, pz, 1);
      hoja(nivel, px, py + 2, pz, 1);
      int[][] cruz = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

      for (int[] c : cruz) {
         hoja(nivel, px + c[0], py + 1, pz + c[1], 1);
      }

      int hojasN = 8;
      double giro = r.nextDouble() * Math.PI * 2.0;

      for (int k = 0; k < hojasN; k++) {
         double a = giro + k * ((Math.PI * 2) / hojasN) + (r.nextDouble() - 0.5) * 0.3;
         int largo = 4 + r.nextInt(2);
         double cx = Math.cos(a);
         double cz = Math.sin(a);

         for (int s = 1; s <= largo; s++) {
            int fx = px + (int)Math.round(cx * s);
            int fz = pz + (int)Math.round(cz * s);
            int fy = py + (s <= 2 ? 1 : 3 - s);
            hoja(nivel, fx, fy, fz, 1);
            if (s >= 2 && s < largo) {
               int sx = (int)Math.round(-cz);
               int sz = (int)Math.round(cx);
               hoja(nivel, fx + sx, fy - (s >= 3 ? 1 : 0), fz + sz, 1);
               hoja(nivel, fx - sx, fy - (s >= 3 ? 1 : 0), fz - sz, 1);
            }
         }
      }
   }
}
