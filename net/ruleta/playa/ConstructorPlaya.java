package net.ruleta.playa;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.LevelChunk;
import net.ruleta.dedsafio.ComandoDedsafio;

public final class ConstructorPlaya {
   public static final int X_MIN = -431;
   public static final int X_MAX = 248;
   public static final int Z_ORILLA = 13;
   private static final int FUNDIDO = 64;
   private static final int AGUA_TOPE = -3;
   private static final int FONDO_ORIGINAL = -5;
   private static final int FONDO_MIN = -44;
   private static final int PLAYA_FONDO = 48;
   private static final int PLANO_HASTA = 150;
   private static final int SUBE_LARGO = 70;
   private static final long SEMILLA = 25412347365L;
   private static final long PRESUPUESTO_NS = 12000000L;
   private static final int CADA_TICKS = 10;
   private static final int MARCA_Y = -61;
   private static ConstructorPlaya.Actual actual;
   private static final Deque<int[]> cola = new ArrayDeque<>();
   private static int contador;
   private static final int VIEJA_Z = 245;

   private ConstructorPlaya() {
   }

   public static void registrar() {
      ServerTickEvents.END_SERVER_TICK.register(ConstructorPlaya::tick);
      ServerLifecycleEvents.SERVER_STOPPED.register((ServerStopped)s -> {
         actual = null;
         cola.clear();
      });
   }

   private static BlockPos marca(int cx, int cz) {
      return new BlockPos(cx * 16, -61, cz * 16);
   }

   private static boolean hecho(LevelChunk trozo) {
      return trozo.getBlockState(marca(trozo.getPos().x, trozo.getPos().z)).is(Blocks.DEEPSLATE);
   }

   private static void tick(MinecraftServer servidor) {
      ServerLevel nivel = servidor.getLevel(ComandoDedsafio.DIMENSION);
      if (nivel != null) {
         if (++contador >= 10) {
            contador = 0;
            buscar(servidor, nivel);
         }

         long limite = System.nanoTime() + 12000000L;

         while (System.nanoTime() < limite) {
            if (actual == null) {
               int[] c = cola.poll();
               if (c == null) {
                  return;
               }

               actual = new ConstructorPlaya.Actual(nivel, c[0], c[1]);
            }

            LevelChunk trozo = nivel.getChunkSource().getChunkNow(actual.cx, actual.cz);
            if (trozo != null && !hecho(trozo)) {
               try {
                  fila(nivel, trozo, actual.fila);
               } catch (RuntimeException var6) {
                  actual = null;
                  continue;
               }

               if (++actual.fila >= 16) {
                  trozo.setBlockState(marca(actual.cx, actual.cz), Blocks.DEEPSLATE.defaultBlockState(), false);
                  actual = null;
               }
            } else {
               actual = null;
            }
         }
      }
   }

   private static void buscar(MinecraftServer servidor, ServerLevel nivel) {
      int radio = Math.max(4, Math.min(24, servidor.getPlayerList().getViewDistance()));
      List<int[]> candidatos = new ArrayList<>();
      Set<Long> vistos = new HashSet<>();

      for (ServerPlayer j : nivel.players()) {
         int px = j.chunkPosition().x;
         int pz = j.chunkPosition().z;

         for (int dx = -radio; dx <= radio; dx++) {
            for (int dz = -radio; dz <= radio; dz++) {
               int cx = px + dx;
               int cz = pz + dz;
               long k = (long)cx << 32 ^ cz & 4294967295L;
               if (vistos.add(k) && (actual == null || actual.cx != cx || actual.cz != cz)) {
                  LevelChunk trozo = nivel.getChunkSource().getChunkNow(cx, cz);
                  if (trozo != null && !hecho(trozo)) {
                     candidatos.add(new int[]{cx, cz, dx * dx + dz * dz});
                  }
               }
            }
         }
      }

      candidatos.sort((a, b) -> Integer.compare(a[2], b[2]));
      cola.clear();

      for (int[] c : candidatos) {
         cola.add(c);
      }
   }

   private static double hash(long x, long z, long extra) {
      long h = x * -7046029254386353131L + z * -4417276706812531889L + extra * 1609587929392839161L + 25412347365L;
      h ^= h >>> 30;
      h *= -4658895280553007687L;
      h ^= h >>> 27;
      h *= -7723592293110705685L;
      h ^= h >>> 31;
      return (h >>> 11) / 9.007199E15F;
   }

   private static double suave(double t) {
      return t * t * (3.0 - 2.0 * t);
   }

   private static double ruido(double x, double z, long extra) {
      long ix = (long)Math.floor(x);
      long iz = (long)Math.floor(z);
      double fx = suave(x - ix);
      double fz = suave(z - iz);
      double a = hash(ix, iz, extra);
      double b = hash(ix + 1L, iz, extra);
      double c = hash(ix, iz + 1L, extra);
      double d = hash(ix + 1L, iz + 1L, extra);
      return a + (b - a) * fx + (c + (d - c) * fx - (a + (b - a) * fx)) * fz;
   }

   private static double atenuacion(int x) {
      double k = 1.0;
      if (x < -431) {
         k = 1.0 - (-431 - x) / 64.0;
      } else if (x > 248) {
         k = 1.0 - (x - 248) / 64.0;
      }

      return k <= 0.0 ? 0.0 : suave(Math.min(1.0, k));
   }

   static int fondo(int x, int z) {
      return Integer.MIN_VALUE;
   }

   private static boolean reparable(BlockState e) {
      return tocable(e) || e.is(Blocks.SEAGRASS) || e.is(Blocks.TALL_SEAGRASS) || e.is(Blocks.KELP) || e.is(Blocks.KELP_PLANT);
   }

   private static BlockState original(int y) {
      if (y >= -4) {
         return Blocks.WATER.defaultBlockState();
      } else if (y >= -9) {
         return Blocks.SAND.defaultBlockState();
      } else {
         return y >= -13 ? Blocks.SANDSTONE.defaultBlockState() : Blocks.STONE.defaultBlockState();
      }
   }

   private static void reparar(ServerLevel nivel, LevelChunk trozo, MutableBlockPos p, int x, int z) {
      if (z > 13 && z <= 245 && x >= -495 && x <= 312) {
         for (int y = -3; y >= -47; y--) {
            BlockState ahora = trozo.getBlockState(p.set(x, y, z));
            BlockState debe = original(y);
            if (ahora != debe
               && reparable(ahora)
               && (!ahora.is(Blocks.SEAGRASS) && !ahora.is(Blocks.TALL_SEAGRASS) || y < -4 || !trozo.getBlockState(p.set(x, -5, z)).is(Blocks.SAND))) {
               nivel.setBlock(p.immutable(), debe, 18);
            }
         }
      }
   }

   private static boolean tocable(BlockState e) {
      return e.isAir() || e.is(Blocks.WATER) || e.is(Blocks.SAND) || e.is(Blocks.SANDSTONE) || e.is(Blocks.STONE);
   }

   private static void poner(ServerLevel nivel, LevelChunk trozo, BlockPos pos, BlockState nuevo) {
      BlockState ahora = trozo.getBlockState(pos);
      if (ahora != nuevo && tocable(ahora)) {
         nivel.setBlock(pos.immutable(), nuevo, 18);
      }
   }

   private static void fila(ServerLevel nivel, LevelChunk trozo, int dz) {
      int cx = trozo.getPos().x;
      int cz = trozo.getPos().z;
      MutableBlockPos p = new MutableBlockPos();

      for (int dx = 0; dx < 16; dx++) {
         int x = cx * 16 + dx;
         int z = cz * 16 + dz;
         reparar(nivel, trozo, p, x, z);
         if (!MapaPlaya.cubre(x, z)) {
            algas(nivel, trozo, p, x, z);
            if (z <= 13 && z >= -35 && x >= -431 && x <= 248) {
               decoracion(nivel, trozo, p, x, z);
            }
         }
      }
   }

   private static void algas(ServerLevel nivel, LevelChunk trozo, MutableBlockPos p, int x, int z) {
      double parche = ruido(x * 0.055, z * 0.055, 21L) * 0.7 + ruido(x * 0.19, z * 0.19, 22L) * 0.3;
      if (!(parche < 0.5)) {
         double lleno = (parche - 0.5) / 0.5;
         if (trozo.getBlockState(p.set(x, -3, z)).is(Blocks.WATER)) {
            int y = -3;

            while (y > -60 && trozo.getBlockState(p.set(x, y - 1, z)).is(Blocks.WATER)) {
               y--;
            }

            int fondo = y - 1;
            if (fondo >= -30 && trozo.getBlockState(p.set(x, fondo, z)).is(Blocks.SAND)) {
               double tipo = hash(x, z, 32L);
               int agua = -3 - fondo;
               double prob = agua <= 5 ? 0.25 + 0.6 * lleno : 0.04 + 0.22 * lleno;
               if (!(hash(x, z, 31L) > prob)) {
                  if (tipo < 0.55 || agua < 3) {
                     nivel.setBlock(p.set(x, fondo + 1, z).immutable(), Blocks.SEAGRASS.defaultBlockState(), 18);
                  } else if (tipo < 0.78) {
                     BlockState abajo = (BlockState)Blocks.TALL_SEAGRASS
                        .defaultBlockState()
                        .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER);
                     BlockState arriba = (BlockState)Blocks.TALL_SEAGRASS
                        .defaultBlockState()
                        .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER);
                     nivel.setBlock(p.set(x, fondo + 1, z).immutable(), abajo, 18);
                     nivel.setBlock(p.set(x, fondo + 2, z).immutable(), arriba, 18);
                  } else if (agua >= 4) {
                     int alto = Math.min(agua - 2, 2 + (int)(hash(x, z, 33L) * 8.0));

                     for (int i = 1; i < alto; i++) {
                        nivel.setBlock(p.set(x, fondo + i, z).immutable(), Blocks.KELP_PLANT.defaultBlockState(), 18);
                     }

                     nivel.setBlock(
                        p.set(x, fondo + alto, z).immutable(), (BlockState)Blocks.KELP.defaultBlockState().setValue(BlockStateProperties.AGE_25, 25), 18
                     );
                  }
               }
            }
         }
      }
   }

   private static void decoracion(ServerLevel nivel, LevelChunk trozo, MutableBlockPos p, int x, int z) {
      int y = 40;

      while (y > -8 && trozo.getBlockState(p.set(x, y, z)).isAir()) {
         y--;
      }

      BlockState suelo = trozo.getBlockState(p.set(x, y, z));
      if (suelo.is(Blocks.SAND) || suelo.is(Blocks.SANDSTONE) || suelo.is(Blocks.RED_SAND)) {
         if (trozo.getBlockState(p.set(x, y + 1, z)).isAir()) {
            int aguaCerca = 13 - z;
            double junto = Math.exp(-aguaCerca / 7.0);
            double grupo = 0.35 + 2.0 * Math.pow(ruido(x * 0.08, z * 0.08, 41L), 2.0);
            double r = hash(x, z, 42L);
            BlockState nuevo = null;
            double base = 0.018 * grupo * (0.3 + 1.7 * junto);
            if (r < base) {
               nuevo = Playa.PIEDRITAS.get().defaultBlockState();
            } else if (r < base + 0.01 * grupo * (0.3 + 2.2 * junto)) {
               nuevo = Playa.CONCHA.get().defaultBlockState();
            } else if (r < base + 0.01 * grupo * (0.3 + 2.2 * junto) + 0.0035 * (0.5 + junto)) {
               nuevo = Playa.CARACOLA.get().defaultBlockState();
            } else if (aguaCerca <= 8 && r < base + 0.01 * grupo * (0.3 + 2.2 * junto) + 0.0035 * (0.5 + junto) + 0.004 * grupo) {
               nuevo = Playa.ESTRELLA_MAR.get().defaultBlockState();
            }

            if (nuevo != null) {
               nivel.setBlock(p.set(x, y + 1, z).immutable(), nuevo, 18);
            }
         }
      }
   }

   private static final class Actual {
      final ServerLevel nivel;
      final int cx;
      final int cz;
      int fila;

      Actual(ServerLevel nivel, int cx, int cz) {
         this.nivel = nivel;
         this.cx = cx;
         this.cz = cz;
      }
   }
}
