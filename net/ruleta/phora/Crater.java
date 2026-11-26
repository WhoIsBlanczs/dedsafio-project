package net.ruleta.phora;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.ruleta.bunker.Bunkers;

public final class Crater {
   private static final int POR_TICK = 6000;
   private static final Deque<Crater> COLA = new ArrayDeque<>();
   private final ServerLevel nivel;
   private final List<BlockPos> quitar = new ArrayList<>();
   private final List<BlockPos> forrar = new ArrayList<>();
   private int i;
   private int j;
   private int magma;
   private static final int MAGMA = 6;
   private final RandomSource r;

   private Crater(ServerLevel nivel, BlockPos c, RandomSource r, float tamano, float fondoK) {
      this.nivel = nivel;
      this.r = r;
      float escala = tamano * (0.9F + r.nextFloat() * 0.2F);
      int radio = Math.max(6, Math.round(25.0F * escala));
      int hondo = Math.max(3, Math.round(30.0F * escala * fondoK));
      int encima = Math.round(14.0F * Math.max(0.6F, escala));
      float[] bulto = new float[12];

      for (int k = 0; k < bulto.length; k++) {
         bulto[k] = 0.85F + r.nextFloat() * 0.3F;
      }

      for (int dx = -radio - 3; dx <= radio + 3; dx++) {
         for (int dz = -radio - 3; dz <= radio + 3; dz++) {
            double ang = Math.atan2(dz, dx);
            float f = (float)((ang + Math.PI) / (Math.PI * 2) * bulto.length);
            int k0 = (int)Math.floor(f) % bulto.length;
            int k1 = (k0 + 1) % bulto.length;
            float m = f - (float)Math.floor(f);
            float rr = radio * (bulto[k0] * (1.0F - m) + bulto[k1] * m);
            double h = Math.sqrt(dx * dx + dz * dz) / rr;
            if (!(h > 1.12)) {
               double dist = Math.sqrt(dx * dx + dz * dz);
               double paso = Math.max(1.0, (double)(rr / hondo));
               int fondo = h < 1.0 ? -Math.min(hondo, (int)Math.floor((rr - dist) / paso) + 1) : 0;
               int techo = h < 1.0 ? (int)Math.round(encima * (1.0 - h * 0.6)) : 0;

               for (int dy = fondo; dy <= techo; dy++) {
                  if (h < 1.0) {
                     this.quitar.add(c.offset(dx, dy, dz));
                  }
               }

               for (int dyx = fondo - 2; dyx < fondo; dyx++) {
                  this.forrar.add(c.offset(dx, dyx, dz));
               }
            }
         }
      }

      this.quitar.sort((a, b) -> Double.compare(a.distSqr(c), b.distSqr(c)));
   }

   public static void abrir(ServerLevel nivel, BlockPos centro, RandomSource r, float tamano) {
      COLA.add(new Crater(nivel, centro, r, tamano, 1.0F));
   }

   public static void abrir(ServerLevel nivel, BlockPos centro, RandomSource r, float tamano, float fondoK) {
      COLA.add(new Crater(nivel, centro, r, tamano, fondoK));
   }

   static void limpiar() {
      COLA.clear();
   }

   static void avanzar() {
      int presupuesto = 6000;

      while (presupuesto > 0 && !COLA.isEmpty()) {
         Crater c = COLA.peek();
         presupuesto = c.paso(presupuesto);
         if (c.terminado()) {
            COLA.poll();
         }
      }
   }

   private boolean terminado() {
      return this.i >= this.quitar.size() && this.j >= this.forrar.size();
   }

   private static boolean rompible(BlockState s, ServerLevel n, BlockPos p) {
      return !s.isAir() && s.getDestroySpeed(n, p) >= 0.0F && n.getBlockEntity(p) == null;
   }

   private int paso(int presupuesto) {
      int banderas = 18;

      while (presupuesto > 0 && this.i < this.quitar.size()) {
         BlockPos p = this.quitar.get(this.i++);
         if (this.nivel.isLoaded(p)) {
            BlockState s = this.nivel.getBlockState(p);
            if ((rompible(s, this.nivel, p) || !s.getFluidState().isEmpty()) && !Bunkers.protegido(this.nivel, p, s)) {
               this.nivel.setBlock(p, Blocks.AIR.defaultBlockState(), banderas);
               presupuesto--;
            }
         }
      }

      while (presupuesto > 0 && this.i >= this.quitar.size() && this.j < this.forrar.size()) {
         BlockPos p = this.forrar.get(this.j++);
         if (this.nivel.isLoaded(p)) {
            BlockState s = this.nivel.getBlockState(p);
            if (rompible(s, this.nivel, p)) {
               float x = this.r.nextFloat();
               BlockState nuevo;
               if (this.magma < 6 && this.r.nextFloat() < 6.0F / Math.max(1, this.forrar.size() / 3)) {
                  nuevo = Blocks.MAGMA_BLOCK.defaultBlockState();
                  this.magma++;
               } else if (x < 0.22F) {
                  nuevo = Blocks.BLACKSTONE.defaultBlockState();
               } else {
                  if (!(x < 0.36F)) {
                     continue;
                  }

                  nuevo = Blocks.BASALT.defaultBlockState();
               }

               this.nivel.setBlock(p, nuevo, banderas);
               presupuesto--;
               BlockPos arriba = p.above();
               if (this.r.nextFloat() < 0.03F && this.nivel.isEmptyBlock(arriba) && BaseFireBlock.canBePlacedAt(this.nivel, arriba, Direction.UP)) {
                  this.nivel.setBlock(arriba, BaseFireBlock.getState(this.nivel, arriba), 3);
               }
            }
         }
      }

      return presupuesto;
   }
}
