package net.ruleta.playa;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.ruleta.Ruleta;
import net.ruleta.dedsafio.ComandoDedsafio;

public final class MapaPlaya {
   public static final int ORIGEN_X = -440;
   public static final int ORIGEN_Y = -4;
   public static final int ORIGEN_Z = -185;
   public static final int TAM_X = 690;
   public static final int TAM_Y = 22;
   public static final int TAM_Z = 199;
   private static final String RECURSO = "structures/playa_dulceazar.nbt";
   private static final long PRESUPUESTO_NS = 12000000L;
   private static final int CADA_TICKS = 20;
   private static final int[][] PAREDES = new int[][]{{-139, -2, 13, -77, 17, 13}};
   private static final int MARCA_Y = -63;
   private static final int FLAGS = 50;
   private static volatile short[] datos;
   private static BlockState[] paleta;
   private static int sx;
   private static int sy;
   private static int sz;
   private static boolean fallo;
   private static boolean listo;
   private static boolean armada;
   private static boolean comprobado;
   private static volatile boolean cargando;
   private static volatile boolean cargaFallida;
   private static final int RADIO_ATERRIZAJE = 5;
   private static final long PRESUPUESTO_ESPERA_NS = 40000000L;
   private static final int ESPERA_MAXIMA = 1200;
   private static final Set<UUID> libres = new HashSet<>();
   private static final Map<UUID, Integer> esperas = new HashMap<>();
   private static final List<int[]> ATERRIZAJE = new ArrayList<>();
   private static final Deque<int[]> cola = new ArrayDeque<>();

   private MapaPlaya() {
   }

   public static boolean cubre(int x, int z) {
      return x >= -440 && x < -440 + (sx > 0 ? sx : 690) && z >= -185 && z < -185 + (sz > 0 ? sz : 199);
   }

   public static void registrar() {
      ServerTickEvents.END_SERVER_TICK.register(MapaPlaya::tick);
      ServerLifecycleEvents.SERVER_STOPPED.register((ServerStopped)s -> reiniciar());
   }

   public static void reiniciar() {
      cola.clear();
      armada = false;
      listo = false;
      comprobado = false;
      cargando = false;
      cargaFallida = false;
      libres.clear();
      esperas.clear();
      datos = null;
      paleta = null;
   }

   private static BlockPos marca(int cx, int cz) {
      return new BlockPos(cx * 16, -63, cz * 16);
   }

   private static BlockPos marcaFin() {
      return new BlockPos(1, -63, 1);
   }

   private static boolean hecho(LevelChunk trozo) { return false; }

   public static void tick(MinecraftServer servidor) {
      if (!listo && !fallo) {
         if (datos == null && !cargando && !cargaFallida && !servidor.getPlayerList().getPlayers().isEmpty()) {
            precargar(servidor);
         }

         ServerLevel nivel = servidor.getLevel(ComandoDedsafio.DIMENSION);
         if (nivel != null && !nivel.players().isEmpty()) {
            if (!comprobado) {
               comprobado = true;
               Ruleta.LOG
                  .info(
                     "[mapa] primer jugador en la dimensión; marca de mapa completo: {}", nivel.getChunk(0, 0).getBlockState(marcaFin()).is(Blocks.DEEPSLATE)
                  );
               if (nivel.getChunk(0, 0).getBlockState(marcaFin()).is(Blocks.DEEPSLATE)) {
                  listo = true;
                  datos = null;
                  paleta = null;
                  return;
               }
            }

            if (cargaFallida) {
               fallo = true;
            } else {
               libres.removeIf(u -> nivel.getPlayerByUUID(u) == null);
               esperas.keySet().removeIf(u -> nivel.getPlayerByUUID(u) == null);
               long limiteEspera = System.nanoTime() + 40000000L;

               for (ServerPlayer j : nivel.players()) {
                  if (!libres.contains(j.getUUID())) {
                     int n = esperas.merge(j.getUUID(), 1, Integer::sum);
                     if (n > 1200 || datos != null && aterrizaje(nivel, j, limiteEspera)) {
                        libres.add(j.getUUID());
                        Ruleta.LOG
                           .info(
                              "[mapa] jugador liberado tras {} ticks (timeout={}, mapa en memoria={}) en {}",
                              new Object[]{n, n > 1200, datos != null, j.blockPosition()}
                           );
                        j.removeEffect(MobEffects.BLINDNESS);
                        j.sendOverlayMessage(Component.empty());
                     } else {
                        j.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, false, false));
                        j.setDeltaMovement(Vec3.ZERO);
                        j.hurtMarked = true;
                        j.fallDistance = 0.0F;
                        j.sendOverlayMessage(Component.literal("Cargando la playa..."));
                     }
                  }
               }

               if (datos != null) {
                  if (!armada) {
                     armar(nivel);
                     armada = true;
                     Ruleta.LOG
                        .info(
                           "[mapa] colocación iniciada: {} trozos, origen ({}, {}, {}), tamaño {}x{}x{}", new Object[]{cola.size(), -440, -4, -185, sx, sy, sz}
                        );
                  }

                  long limite = System.nanoTime() + 12000000L;

                  while (System.nanoTime() < limite) {
                     int[] c = cola.poll();
                     if (c == null) {
                        Ruleta.LOG.info("[mapa] colocación terminada");
                        nivel.getChunk(0, 0).setBlockState(marcaFin(), Blocks.DEEPSLATE.defaultBlockState(), false);
                        listo = true;
                        datos = null;
                        paleta = null;
                        return;
                     }

                     LevelChunk trozo = nivel.getChunk(c[0], c[1]);
                     if (!hecho(trozo)) {
                        colocar(nivel, trozo);
                        trozo.setBlockState(marca(c[0], c[1]), Blocks.DEEPSLATE.defaultBlockState(), false);
                     }
                  }
               }
            }
         } else {
            libres.clear();
            esperas.clear();
         }
      }
   }

   private static void precargar(MinecraftServer servidor) {
      cargando = true;
      Thread hilo = new Thread(() -> {
         if (!cargar(servidor)) {
            cargaFallida = true;
         }

         cargando = false;
      }, "ruleta-mapa-playa");
      hilo.setDaemon(true);
      hilo.start();
   }

   private static boolean aterrizaje(ServerLevel nivel, ServerPlayer j, long limite) {
      int px = j.chunkPosition().x();
      int pz = j.chunkPosition().z();

      for (int[] o : ATERRIZAJE) {
         int cx = px + o[0];
         int cz = pz + o[1];
         if (cx * 16 + 15 >= -440 && cx * 16 < -440 + (sx > 0 ? sx : 690) && cz * 16 + 15 >= -185 && cz * 16 < -185 + (sz > 0 ? sz : 199)) {
            LevelChunk trozo = nivel.getChunk(cx, cz);
            if (!hecho(trozo)) {
               if (System.nanoTime() >= limite) {
                  return false;
               }

               colocar(nivel, trozo);
               trozo.setBlockState(marca(cx, cz), Blocks.DEEPSLATE.defaultBlockState(), false);
            }
         }
      }

      return true;
   }

   private static void armar(ServerLevel nivel) {
      ServerPlayer j = (ServerPlayer)nivel.players().get(0);
      int px = j.chunkPosition().x();
      int pz = j.chunkPosition().z();
      List<int[]> todos = new ArrayList<>();

      for (int cx = Math.floorDiv(-440, 16); cx <= Math.floorDiv(-440 + sx - 1, 16); cx++) {
         for (int cz = Math.floorDiv(-185, 16); cz <= Math.floorDiv(-185 + sz - 1, 16); cz++) {
            todos.add(new int[]{cx, cz, (cx - px) * (cx - px) + (cz - pz) * (cz - pz)});
         }
      }

      todos.sort((a, b) -> Integer.compare(a[2], b[2]));
      cola.clear();
      cola.addAll(todos);
   }

   private static void colocar(ServerLevel nivel, LevelChunk trozo) {
      int x0 = trozo.getPos().x() * 16;
      int z0 = trozo.getPos().z() * 16;
      MutableBlockPos p = new MutableBlockPos();

      for (int dz = 0; dz < 16; dz++) {
         int z = z0 + dz;
         int lz = z - -185;
         if (lz >= 0 && lz < sz) {
            for (int dx = 0; dx < 16; dx++) {
               int x = x0 + dx;
               int lx = x - -440;
               if (lx >= 0 && lx < sx) {
                  for (int ly = 0; ly < sy; ly++) {
                     int v = datos[(ly * sz + lz) * sx + lx];
                     BlockState e = v == 0 ? Blocks.AIR.defaultBlockState() : paleta[v - 1];
                     p.set(x, -4 + ly, z);
                     if (e.isAir()) {
                        if (trozo.getBlockState(p).is(Blocks.BARRIER)) {
                           nivel.setBlock(p, e, 50);
                        }
                     } else {
                        nivel.setBlock(p, e, 50);
                     }
                  }
               }
            }
         }
      }

      for (int[] c : PAREDES) {
         int xa = Math.max(Math.min(c[0], c[3]), x0);
         int xb = Math.min(Math.max(c[0], c[3]), x0 + 15);
         int za = Math.max(Math.min(c[2], c[5]), z0);
         int zb = Math.min(Math.max(c[2], c[5]), z0 + 15);

         for (int x = xa; x <= xb; x++) {
            for (int z = za; z <= zb; z++) {
               for (int y = Math.min(c[1], c[4]); y <= Math.max(c[1], c[4]); y++) {
                  p.set(x, y, z);
                  if (!trozo.getBlockState(p).is(Blocks.BARRIER)) {
                     nivel.setBlock(p, Blocks.BARRIER.defaultBlockState(), 50);
                  }
               }
            }
         }
      }
   }

   private static boolean cargar(MinecraftServer servidor) {
      Identifier id = Identifier.fromNamespaceAndPath("ruleta", "structures/playa_dulceazar.nbt");

      try (
         InputStream bruto = ((Resource)servidor.getResourceManager().getResource(id).orElseThrow()).open();
         DataInputStream in = new DataInputStream(new BufferedInputStream(new GZIPInputStream(bruto), 65536));
      ) {
         if (in.readByte() != 10) {
            throw new IOException("no es un NBT");
         }

         in.readUTF();
         List<BlockState> pal = new ArrayList<>();
         short[] bx = null;
         short[] by = null;
         short[] bz = null;
         short[] bs = null;

         for (int t = in.readByte(); t != 0; t = in.readByte()) {
            String nombre = in.readUTF();
            if (t == 9 && nombre.equals("size")) {
               in.readByte();
               in.readInt();
               sx = in.readInt();
               sy = in.readInt();
               sz = in.readInt();
            } else if (t == 9 && nombre.equals("palette")) {
               in.readByte();
               int n = in.readInt();

               for (int i = 0; i < n; i++) {
                  pal.add(leerEstado(in));
               }
            } else if (t == 9 && nombre.equals("blocks")) {
               in.readByte();
               int n = in.readInt();
               bx = new short[n];
               by = new short[n];
               bz = new short[n];
               bs = new short[n];

               for (int i = 0; i < n; i++) {
                  int x = 0;
                  int y = 0;
                  int z = 0;
                  int estado = 0;

                  for (int tt = in.readByte(); tt != 0; tt = in.readByte()) {
                     String nm = in.readUTF();
                     if (tt == 9 && nm.equals("pos")) {
                        in.readByte();
                        in.readInt();
                        x = in.readInt();
                        y = in.readInt();
                        z = in.readInt();
                     } else if (tt == 3 && nm.equals("state")) {
                        estado = in.readInt();
                     } else {
                        saltar(in, tt);
                     }
                  }

                  bx[i] = (short)x;
                  by[i] = (short)y;
                  bz[i] = (short)z;
                  bs[i] = (short)estado;
               }
            } else {
               saltar(in, t);
            }
         }

         if (bx == null || sx <= 0 || pal.isEmpty()) {
            throw new IOException("faltan size, palette o blocks en el NBT");
         }

         short[] d = new short[sx * sy * sz];

         for (int i = 0; i < bx.length; i++) {
            d[(by[i] * sz + bz[i]) * sx + bx[i]] = (short)(bs[i] + 1);
         }

         paleta = pal.toArray(new BlockState[0]);
         datos = d;
         Ruleta.LOG.info("Mapa de la playa de DEDsafío cargado ({}x{}x{}).", new Object[]{sx, sy, sz});
         return true;
      } catch (Exception e) {
         Ruleta.LOG.error("No se pudo cargar el mapa de la playa de DEDsafío ({}): {}", "structures/playa_dulceazar.nbt", e.toString());
         return false;
      }
   }

   private static BlockState leerEstado(DataInputStream in) throws IOException {
      String nombre = "minecraft:air";
      Map<String, String> props = new HashMap<>();

      for (int t = in.readByte(); t != 0; t = in.readByte()) {
         String nm = in.readUTF();
         if (t == 8 && nm.equals("Name")) {
            nombre = in.readUTF();
         } else if (t == 10 && nm.equals("Properties")) {
            for (int tt = in.readByte(); tt != 0; tt = in.readByte()) {
               String k = in.readUTF();
               if (tt == 8) {
                  props.put(k, in.readUTF());
               } else {
                  saltar(in, tt);
               }
            }
         } else {
            saltar(in, t);
         }
      }

      Block bloque = (Block)BuiltInRegistries.BLOCK.get(Identifier.parse(nombre));
      BlockState e = bloque.defaultBlockState();

      for (Entry<String, String> par : props.entrySet()) {
         Property<?> prop = bloque.getStateDefinition().getProperty(par.getKey());
         if (prop != null) {
            e = con(e, prop, par.getValue());
         }
      }

      return e;
   }

   private static <T extends Comparable<T>> BlockState con(BlockState e, Property<T> prop, String valor) {
      return prop.getValue(valor).map(v -> (BlockState)e.setValue(prop, v)).orElse(e);
   }

   private static void saltar(DataInputStream in, int t) throws IOException {
      switch (t) {
         case 1:
            in.skipBytes(1);
            break;
         case 2:
            in.skipBytes(2);
            break;
         case 3:
         case 5:
            in.skipBytes(4);
            break;
         case 4:
         case 6:
            in.skipBytes(8);
            break;
         case 7:
            in.skipBytes(in.readInt());
            break;
         case 8:
            in.readUTF();
            break;
         case 9:
            int tipo = in.readByte();
            int n = in.readInt();

            for (int i = 0; i < n; i++) {
               saltar(in, tipo);
            }
            break;
         case 10:
            for (int tt = in.readByte(); tt != 0; tt = in.readByte()) {
               in.readUTF();
               saltar(in, tt);
            }
            break;
         case 11:
            in.skipBytes(in.readInt() * 4);
            break;
         case 12:
            in.skipBytes(in.readInt() * 8);
            break;
         default:
            throw new IOException("tipo NBT desconocido " + t);
      }
   }

   static {
      for (int dx = -5; dx <= 5; dx++) {
         for (int dz = -5; dz <= 5; dz++) {
            ATERRIZAJE.add(new int[]{dx, dz});
         }
      }

      ATERRIZAJE.sort((a, b) -> Integer.compare(a[0] * a[0] + a[1] * a[1], b[0] * b[0] + b[1] * b[1]));
   }
}
