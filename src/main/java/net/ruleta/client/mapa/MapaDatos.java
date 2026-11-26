package net.ruleta.client.mapa;

import com.mojang.renderpearl.backend.opengl.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
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

   private static Object[] caraArriba(Minecraft mc, BlockState st) {
      return CARAS.computeIfAbsent(st, s -> {
         BakedModel modelo = mc.getBlockRenderer().getBlockModel(s);
         List<BakedQuad> qs = modelo.getQuads(s, Direction.UP, AZAR);
         if (qs.isEmpty()) {
            qs = modelo.getQuads(s, null, AZAR);
         }

         if (!qs.isEmpty()) {
            BakedQuad q = qs.get(0);
            return new Object[]{q.getSprite(), q.isTinted() ? q.getTintIndex() : -1};
         } else {
            return new Object[]{modelo.getParticleIcon(), -1};
         }
      });
   }

   private static int colorMedio(Minecraft mc, TextureAtlasSprite sp) {
      Integer c = MEDIOS.get(sp);
      if (c != null) {
         return c;
      }

      if (atlas == null) {
         AbstractTexture t = mc.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
         RenderSystem.bindTexture(t.getId());
         int w = GlStateManager._getTexLevelParameter(3553, 0, 4096);
         int h = GlStateManager._getTexLevelParameter(3553, 0, 4097);
         atlas = new NativeImage(w, h, false);
         atlas.downloadTexture(0, false);
      }

      long r = 0L;
      long g = 0L;
      long b = 0L;
      long n = 0L;
      int x0 = sp.getX();
      int y0 = sp.getY();
      int w = sp.contents().width();
      int h = sp.contents().height();

      for (int y = y0; y < y0 + h && y < atlas.getHeight(); y++) {
         for (int x = x0; x < x0 + w && x < atlas.getWidth(); x++) {
            int abgr = atlas.getPixelRGBA(x, y);
            if ((abgr >>> 24 & 0xFF) >= 16) {
               r += abgr & 0xFF;
               g += abgr >> 8 & 0xFF;
               b += abgr >> 16 & 0xFF;
               n++;
            }
         }
      }

      int rgb = n == 0L ? 8421504 : (int)(r / n) << 16 | (int)(g / n) << 8 | (int)(b / n);
      MEDIOS.put(sp, rgb);
      return rgb;
   }

   static Identifier atlasEstatico(Minecraft mc) {
      if (fotoId != null && fotoDe == atlasId) {
         return fotoId;
      }

      if (fotoId != null) {
         mc.getTextureManager().release(fotoId);
         fotoId = null;
         fotoAtlas = null;
      }

      if (atlas == null) {
         AbstractTexture t = mc.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
         RenderSystem.bindTexture(t.getId());
         int w = GlStateManager._getTexLevelParameter(3553, 0, 4096);
         int h = GlStateManager._getTexLevelParameter(3553, 0, 4097);
         atlas = new NativeImage(w, h, false);
         atlas.downloadTexture(0, false);
      }

      NativeImage copia = new NativeImage(atlas.getWidth(), atlas.getHeight(), false);
      copia.copyFrom(atlas);
      fotoAtlas = new DynamicTexture(copia);
      fotoAtlas.setFilter(false, false);
      fotoId = mc.getTextureManager().register("ruleta_mapa_atlas", fotoAtlas);
      fotoDe = atlasId;
      return fotoId;
   }

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

   private static MapaDatos.Trozo convertir(Minecraft mc, LevelChunk ch, int cx, int cz) {
      ClientLevel nivel = mc.level;
      MapaDatos.Trozo t = new MapaDatos.Trozo();
      int suelo = nivel.getMinBuildHeight();
      MutableBlockPos pos = new MutableBlockPos();
      BlockState[] estados = new BlockState[256];

      for (int z = 0; z < 16; z++) {
         for (int x = 0; x < 16; x++) {
            int y = ch.getHeight(Types.WORLD_SURFACE, x, z);
            pos.set((cx << 4) + x, y, (cz << 4) + z);
            BlockState st = ch.getBlockState(pos);

            for (int bajado = 0;
               y > suelo && bajado < 64 && (st.isAir() || st.getRenderShape() == RenderShape.INVISIBLE && st.getFluidState().isEmpty());
               st = ch.getBlockState(pos)
            ) {
               y--;
               bajado++;
               pos.setY(y);
            }

            int i0 = z * 16 + x;
            t.alto[i0] = !st.isAir() && (st.getRenderShape() != RenderShape.INVISIBLE || !st.getFluidState().isEmpty()) ? y : suelo;
            estados[i0] = st;
         }
      }

      for (int z = 0; z < 16; z++) {
         for (int x = 0; x < 16; x++) {
            int i = z * 16 + x;
            int y = t.alto[i];
            BlockState st = estados[i];
            pos.set((cx << 4) + x, y, (cz << 4) + z);
            if (!st.isAir() && (st.getRenderShape() != RenderShape.INVISIBLE || !st.getFluidState().isEmpty())) {
               FluidState fl = st.getFluidState();
               int tinte;
               TextureAtlasSprite sp;
               float luz;
               if (!fl.isEmpty() && st.getBlock() instanceof LiquidBlock) {
                  FluidRenderHandler ext = FluidRenderHandlerRegistry.INSTANCE.get(fl.getType());
                  TextureAtlasSprite[] sprites = ext == null ? null : ext.getFluidSprites(nivel, pos, fl);
                  sp = sprites != null && sprites[0] != null
                     ? sprites[0]
                     : (TextureAtlasSprite)mc.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(Identifier.withDefaultNamespace("block/water_still"));
                  tinte = ext == null ? 16777215 : ext.getFluidColor(nivel, pos, fl) & 16777215;
                  int hondo = 0;

                  for (MutableBlockPos q = pos.mutable(); hondo < 16 && q.getY() > suelo && !ch.getBlockState(q).getFluidState().isEmpty(); hondo++) {
                     q.move(0, -1, 0);
                  }

                  luz = Mth.clamp(1.0F - 0.032F * hondo, 0.5F, 1.0F);
               } else {
                  Object[] cara = caraArriba(mc, st);
                  sp = (TextureAtlasSprite)cara[0];
                  int indice = (Integer)cara[1];
                  tinte = indice < 0 ? 16777215 : mc.getBlockColors().getColor(st, nivel, pos, indice);
                  if (tinte == -1) {
                     tinte = 16777215;
                  }

                  int hE = alturaEn(cx, cz, t, x + 1, z);
                  int hO = alturaEn(cx, cz, t, x - 1, z);
                  int hS = alturaEn(cx, cz, t, x, z + 1);
                  int hN = alturaEn(cx, cz, t, x, z - 1);
                  hE = hE == Integer.MIN_VALUE ? y : hE;
                  hO = hO == Integer.MIN_VALUE ? y : hO;
                  hS = hS == Integer.MIN_VALUE ? y : hS;
                  hN = hN == Integer.MIN_VALUE ? y : hN;
                  float pendiente = Mth.clamp((hE - hO) * 0.5F + (hS - hN) * 0.5F, -4.0F, 4.0F);
                  luz = 1.0F + 0.15F * pendiente;
                  float media = (hE + hO + hS + hN) * 0.25F;
                  if (media > y) {
                     luz *= 1.0F - 0.05F * Math.min(4.0F, media - y);
                  }

                  for (int k = 1; k <= 10; k++) {
                     int hk = alturaEn(cx, cz, t, x - k, z - k);
                     if (hk != Integer.MIN_VALUE && hk > y + k * 0.8F) {
                        luz *= 0.7F + 0.018F * k;
                        break;
                     }
                  }

                  luz = Mth.clamp(luz, 0.45F, 1.3F);
               }

               t.sprite[i] = sp;
               t.mult[i] = multiplicar(16777215, tinte, luz);
               int rgb = multiplicar(colorMedio(mc, sp), tinte, luz);
               t.abgr[i] = 0xFF000000 | (rgb & 0xFF) << 16 | (rgb >> 8 & 0xFF) << 8 | rgb >> 16 & 0xFF;
            } else {
               t.sprite[i] = null;
               t.mult[i] = 0;
               t.abgr[i] = -16777216;
            }
         }
      }

      return t;
   }

   public static final class Trozo {
      final int[] abgr = new int[256];
      final TextureAtlasSprite[] sprite = new TextureAtlasSprite[256];
      final int[] mult = new int[256];
      final int[] alto = new int[256];
   }
}
