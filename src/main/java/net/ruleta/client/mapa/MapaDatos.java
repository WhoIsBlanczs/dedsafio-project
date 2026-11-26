package net.ruleta.client.mapa;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Object;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.FluidState;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.TickEvent;

public final class MapaDatos {
   private static final Map<ResourceKey<Level>, Map<Long, MapaDatos.Trozo>> MUNDOS = new HashMap<>();
   private static ResourceKey<Level> dimension;
   static Map<Long, MapaDatos.Trozo> CACHE = new HashMap<>();
   static final Set<Long> CAMBIADOS = new HashSet<>();
   private static final LinkedHashSet<Long> COLA = new LinkedHashSet<>();
   private static final int POR_TICK = 6;
   private static final int POR_TICK_ABIERTO = 40;
   private static final Map<TextureAtlasSprite, Integer> MEDIOS = new HashMap<>();
   private static final Map<BlockState, Object[]> CARAS = new HashMap<>();
   private static final RandomSource AZAR = RandomSource.create(42L);
   private static NativeImage atlas;
   private static int atlasId = -1;
   private static Object mundo;
   private static int ticks;
   private static int sinUsarAtlas;
   private static DynamicTexture fotoAtlas;
   private static Identifier fotoId;
   private static int fotoDe = -2;
   private static final int SIN_DATO = Integer.MIN_VALUE;

   private MapaDatos() {
   }

   static void pedir(long chunk) {
      COLA.add(chunk);
   }

   public static void alCargarChunk(ClientLevel nivelCargado, LevelChunk chunk) {
      COLA.add(chunk.getPos().toLong());
   }

   @SubscribeEvent
   public static void alTick(TickEvent.ClientTickEvent e) {
      if (e.phase == TickEvent.Phase.END) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.level == null) {
            if (mundo != null) {
               MUNDOS.clear();
               vaciar();
            }
         } else {
            if (mundo != mc.level) {
               COLA.clear();
               CAMBIADOS.clear();
               mundo = mc.level;
               if (!mc.level.dimension().equals(dimension)) {
                  dimension = mc.level.dimension();
                  CACHE = MUNDOS.computeIfAbsent(dimension, k -> new HashMap<>());
               }
            }

            int id = mc.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).getId();
            if (id != atlasId) {
               atlasId = id;
               MEDIOS.clear();
               CARAS.clear();
               if (atlas != null) {
                  atlas.close();
                  atlas = null;
               }

               COLA.addAll(CACHE.keySet());
            }

            if (++ticks % 40 == 0 && mc.player != null) {
               int pcx = mc.player.getBlockX() >> 4;
               int pcz = mc.player.getBlockZ() >> 4;

               for (int dz = -2; dz <= 2; dz++) {
                  for (int dx = -2; dx <= 2; dx++) {
                     COLA.add(ChunkPos.asLong(pcx + dx, pcz + dz));
                  }
               }
            }

            boolean abierto = mc.screen instanceof PantallaMapa;
            if (!abierto) {
               CAMBIADOS.clear();
            }

            int presupuesto = abierto ? 40 : 6;
            Iterator<Long> it = COLA.iterator();

            while (presupuesto > 0 && it.hasNext()) {
               long c = it.next();
               it.remove();
               LevelChunk ch = mc.level.getChunkSource().getChunk(ChunkPos.getX(c), ChunkPos.getZ(c), ChunkStatus.FULL, false);
               if (ch != null) {
                  CACHE.put(c, convertir(mc, ch, ChunkPos.getX(c), ChunkPos.getZ(c)));
                  if (abierto) {
                     CAMBIADOS.add(c);
                  }

                  presupuesto--;
               }
            }

            if (!COLA.isEmpty() || abierto) {
               sinUsarAtlas = 0;
            } else if (atlas != null && ++sinUsarAtlas > 1200) {
               atlas.close();
               atlas = null;
               sinUsarAtlas = 0;
            }
         }
      }
   }

   private static void vaciar() {
      CACHE.clear();
      COLA.clear();
      CAMBIADOS.clear();
      mundo = null;
      dimension = null;
      CACHE = new HashMap<>();
   }

   private static Object[] caraArriba(Minecraft mc, BlockState st) { return null; }

   private static int colorMedio(Minecraft mc, TextureAtlasSprite sp) { return 0; }

   static Identifier atlasEstatico(Minecraft mc) { return null; }

   private static int multiplicar(int rgb, int tinte, float luz) {
      int r = Mth.clamp((int)((rgb >> 16 & 0xFF) * (tinte >> 16 & 0xFF) / 255 * luz), 0, 255);
      int g = Mth.clamp((int)((rgb >> 8 & 0xFF) * (tinte >> 8 & 0xFF) / 255 * luz), 0, 255);
      int b = Mth.clamp((int)((rgb & 0xFF) * (tinte & 0xFF) / 255 * luz), 0, 255);
      return r << 16 | g << 8 | b;
   }

   private static int alturaEn(int cx, int cz, MapaDatos.Trozo propio, int x, int z) {
      int ccx = cx + Math.floorDiv(x, 16);
      int ccz = cz + Math.floorDiv(z, 16);
      int lx = Math.floorMod(x, 16);
      int lz = Math.floorMod(z, 16);
      if (ccx == cx && ccz == cz) {
         return propio.alto[lz * 16 + lx];
      }

      MapaDatos.Trozo v = CACHE.get(ChunkPos.asLong(ccx, ccz));
      return v == null ? Integer.MIN_VALUE : v.alto[lz * 16 + lx];
   }

   private static MapaDatos.Trozo convertir(Minecraft mc, LevelChunk ch, int cx, int cz) { return null; }

   public static final class Trozo {
      final int[] abgr = new int[256];
      final TextureAtlasSprite[] sprite = new TextureAtlasSprite[256];
      final int[] mult = new int[256];
      final int[] alto = new int[256];
   }
}
