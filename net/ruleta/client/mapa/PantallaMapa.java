package net.ruleta.client.mapa;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Axis;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.ruleta.bunker.Bunkers;
import net.ruleta.client.bunker.BunkerMarcador;
import org.joml.Matrix4f;

public class PantallaMapa extends Screen {
   private static final float ZOOM_TEXTURAS = 3.0F;
   private static float zoom = 2.0F;
   private static final int LOSETA_CHUNKS = 8;
   private static final int LOSETA = 128;
   private static final int LOSETAS_MAX = 400;
   private final Map<Long, PantallaMapa.Loseta> losetas = new HashMap<>();
   private final Set<Long> vacias = new HashSet<>();
   private double centroX;
   private double centroZ;

   public PantallaMapa() {
      super(Component.literal("Mapa"));
   }

   protected void init() {
      if (this.minecraft != null && this.minecraft.player != null && this.losetas.isEmpty() && this.vacias.isEmpty()) {
         this.centroX = this.minecraft.player.getX();
         this.centroZ = this.minecraft.player.getZ();
         Player p = this.minecraft.player;
         int radio = this.minecraft.options.getEffectiveRenderDistance() + 1;
         int pcx = p.getBlockX() >> 4;
         int pcz = p.getBlockZ() >> 4;

         for (int dz = -radio; dz <= radio; dz++) {
            for (int dx = -radio; dx <= radio; dx++) {
               long c = ChunkPos.asLong(pcx + dx, pcz + dz);
               if (!MapaDatos.CACHE.containsKey(c) && this.minecraft.level.getChunkSource().getChunk(pcx + dx, pcz + dz, ChunkStatus.FULL, false) != null) {
                  MapaDatos.pedir(c);
               }
            }
         }
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   private static long region(int cx, int cz) {
      return ChunkPos.asLong(Math.floorDiv(cx, 8), Math.floorDiv(cz, 8));
   }

   private PantallaMapa.Loseta crear(long reg) {
      int rx = ChunkPos.getX(reg);
      int rz = ChunkPos.getZ(reg);
      boolean hay = false;

      for (int z = 0; z < 8 && !hay; z++) {
         for (int x = 0; x < 8; x++) {
            if (MapaDatos.CACHE.containsKey(ChunkPos.asLong(rx * 8 + x, rz * 8 + z))) {
               hay = true;
               break;
            }
         }
      }

      if (!hay) {
         this.vacias.add(reg);
         return null;
      } else {
         DynamicTexture tex = new DynamicTexture(128, 128, true);
         ResourceLocation id = this.minecraft.getTextureManager().register("ruleta_mapa", tex);
         PantallaMapa.Loseta l = new PantallaMapa.Loseta(tex, id);
         NativeImage img = tex.getPixels();
         if (img != null) {
            for (int z = 0; z < 8; z++) {
               for (int xx = 0; xx < 8; xx++) {
                  int cx = rx * 8 + xx;
                  int cz = rz * 8 + z;
                  MapaDatos.Trozo t = MapaDatos.CACHE.get(ChunkPos.asLong(cx, cz));
                  if (t != null) {
                     pintar(img, xx * 16, z * 16, t);
                  }
               }
            }
         }

         tex.upload();
         this.losetas.put(reg, l);
         return l;
      }
   }

   public void tick() {
      if (!MapaDatos.CAMBIADOS.isEmpty()) {
         Set<PantallaMapa.Loseta> tocadas = new HashSet<>();

         for (long c : MapaDatos.CAMBIADOS) {
            int cx = ChunkPos.getX(c);
            int cz = ChunkPos.getZ(c);
            long reg = region(cx, cz);
            this.vacias.remove(reg);
            PantallaMapa.Loseta l = this.losetas.get(reg);
            MapaDatos.Trozo t = MapaDatos.CACHE.get(c);
            if (l != null && t != null && l.textura.getPixels() != null) {
               pintar(l.textura.getPixels(), Math.floorMod(cx, 8) * 16, Math.floorMod(cz, 8) * 16, t);
               tocadas.add(l);
            }
         }

         for (PantallaMapa.Loseta l : tocadas) {
            l.textura.upload();
         }

         MapaDatos.CAMBIADOS.clear();
      }
   }

   private static void pintar(NativeImage img, int ox, int oz, MapaDatos.Trozo t) {
      for (int z = 0; z < 16; z++) {
         for (int x = 0; x < 16; x++) {
            img.setPixelRGBA(ox + x, oz + z, t.abgr[z * 16 + x]);
         }
      }
   }

   public void removed() {
      if (this.minecraft != null) {
         for (PantallaMapa.Loseta l : this.losetas.values()) {
            this.minecraft.getTextureManager().release(l.id);
         }
      }

      this.losetas.clear();
      this.vacias.clear();
      super.removed();
   }

   public void renderBackground(GuiGraphics g, int ratonX, int ratonY, float parcial) {
   }

   public void render(GuiGraphics g, int ratonX, int ratonY, float parcial) {
      g.fill(0, 0, this.width, this.height, -16777216);
      float cx = this.width / 2.0F;
      float cy = this.height / 2.0F;
      this.losetas(g, cx, cy);
      if (zoom >= 3.0F) {
         this.texturas(g, cx, cy);
      }

      this.vineta(g);
      this.otrosJugadores(g, parcial, cx, cy);
      this.bunkers(g, cx, cy);
      this.jugador(g, parcial, cx, cy);
      this.brujula(g);
      super.render(g, ratonX, ratonY, parcial);
   }

   private void losetas(GuiGraphics g, float cx, float cy) {
      double bx0 = this.centroX - cx / zoom;
      double bx1 = this.centroX + cx / zoom;
      double bz0 = this.centroZ - cy / zoom;
      double bz1 = this.centroZ + cy / zoom;
      int r0x = Math.floorDiv(Mth.floor(bx0), 128);
      int r1x = Math.floorDiv(Mth.floor(bx1), 128);
      int r0z = Math.floorDiv(Mth.floor(bz0), 128);
      int r1z = Math.floorDiv(Mth.floor(bz1), 128);

      for (PantallaMapa.Loseta l : this.losetas.values()) {
         l.vista = false;
      }

      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      int nuevas = 0;

      for (int rz = r0z; rz <= r1z; rz++) {
         for (int rx = r0x; rx <= r1x; rx++) {
            long reg = ChunkPos.asLong(rx, rz);
            PantallaMapa.Loseta l = this.losetas.get(reg);
            if (l == null) {
               if (this.vacias.contains(reg) || nuevas >= 6) {
                  continue;
               }

               l = this.crear(reg);
               nuevas++;
               if (l == null) {
                  continue;
               }
            }

            l.vista = true;
            float x0 = cx + (float)((rx * 128 - this.centroX) * zoom);
            float y0 = cy + (float)((rz * 128 - this.centroZ) * zoom);
            g.pose().pushPose();
            g.pose().translate(x0, y0, 0.0F);
            g.pose().scale(zoom, zoom, 1.0F);
            g.blit(l.id, 0, 0, 0.0F, 0.0F, 128, 128, 128, 128);
            g.pose().popPose();
         }
      }

      if (this.losetas.size() > 400) {
         Iterator<Entry<Long, PantallaMapa.Loseta>> it = this.losetas.entrySet().iterator();

         while (it.hasNext()) {
            PantallaMapa.Loseta l = it.next().getValue();
            if (!l.vista) {
               this.minecraft.getTextureManager().release(l.id);
               it.remove();
            }
         }
      }
   }

   private void texturas(GuiGraphics g, float cx, float cy) {
      int bx0 = Mth.floor(this.centroX - cx / zoom);
      int bx1 = Mth.ceil(this.centroX + cx / zoom);
      int bz0 = Mth.floor(this.centroZ - cy / zoom);
      int bz1 = Mth.ceil(this.centroZ + cy / zoom);
      Matrix4f m = g.pose().last().pose();
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      RenderSystem.setShaderTexture(0, MapaDatos.atlasEstatico(this.minecraft));
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      BufferBuilder b = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

      for (int ccz = bz0 >> 4; ccz <= bz1 >> 4; ccz++) {
         for (int ccx = bx0 >> 4; ccx <= bx1 >> 4; ccx++) {
            MapaDatos.Trozo t = MapaDatos.CACHE.get(ChunkPos.asLong(ccx, ccz));
            if (t != null) {
               for (int z = 0; z < 16; z++) {
                  int wz = (ccz << 4) + z;
                  if (wz >= bz0 && wz <= bz1) {
                     float y0 = cy + (float)((wz - this.centroZ) * zoom);
                     float y1 = y0 + zoom;

                     for (int x = 0; x < 16; x++) {
                        int wx = (ccx << 4) + x;
                        if (wx >= bx0 && wx <= bx1) {
                           TextureAtlasSprite sp = t.sprite[z * 16 + x];
                           if (sp != null) {
                              int c = t.mult[z * 16 + x];
                              float r = (c >> 16 & 0xFF) / 255.0F;
                              float v = (c >> 8 & 0xFF) / 255.0F;
                              float a = (c & 0xFF) / 255.0F;
                              float x0 = cx + (float)((wx - this.centroX) * zoom);
                              float x1 = x0 + zoom;
                              b.addVertex(m, x0, y0, 0.0F).setColor(r, v, a, 1.0F).setUv(sp.getU0(), sp.getV0());
                              b.addVertex(m, x0, y1, 0.0F).setColor(r, v, a, 1.0F).setUv(sp.getU0(), sp.getV1());
                              b.addVertex(m, x1, y1, 0.0F).setColor(r, v, a, 1.0F).setUv(sp.getU1(), sp.getV1());
                              b.addVertex(m, x1, y0, 0.0F).setColor(r, v, a, 1.0F).setUv(sp.getU1(), sp.getV0());
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      BufferUploader.drawWithShader(b.buildOrThrow());
   }

   private void vineta(GuiGraphics g) {
      int w = this.width;
      int h = this.height;
      int borde = Math.max(24, Math.min(w, h) / 7);

      for (int i = 0; i < borde; i++) {
         float k = 1.0F - (float)i / borde;
         int a = (int)(150.0F * k * k);
         int col = a << 24;
         g.fill(0, i, w, i + 1, col);
         g.fill(0, h - i - 1, w, h - i, col);
         g.fill(i, 0, i + 1, h, col);
         g.fill(w - i - 1, 0, w - i, h, col);
      }

      g.fill(0, 0, w, 1, 1728053247);
      g.fill(0, 0, 1, h, 1157627903);
      g.fill(0, h - 1, w, h, -1728053248);
      g.fill(w - 1, 0, w, h, 1996488704);
   }

   private void bunkers(GuiGraphics g, float cx, float cy) {
      for (Bunkers.Bunker b : BunkerMarcador.deAqui()) {
         float sx = cx + (float)((b.mx() - this.centroX) * zoom);
         float sy = cy + (float)((b.mz() - this.centroZ) * zoom);
         BunkerMarcador.dibujarPin(g, sx, sy, b);
      }
   }

   private void otrosJugadores(GuiGraphics g, float parcial, float cx, float cy) {
      if (this.minecraft != null && this.minecraft.level != null) {
         for (Player otro : this.minecraft.level.players()) {
            if (otro != this.minecraft.player && !otro.isSpectator()) {
               double px = Mth.lerp(parcial, otro.xo, otro.getX());
               double pz = Mth.lerp(parcial, otro.zo, otro.getZ());
               int sx = Math.round(cx + (float)((px - this.centroX) * zoom));
               int sy = Math.round(cy + (float)((pz - this.centroZ) * zoom));
               g.fill(sx - 3, sy - 3, sx + 3, sy + 3, -14671840);
               g.fill(sx - 2, sy - 2, sx + 2, sy + 2, -1);
            }
         }
      }
   }

   private void jugador(GuiGraphics g, float parcial, float cx, float cy) {
      Player p = this.minecraft == null ? null : this.minecraft.player;
      if (p != null) {
         double px = Mth.lerp(parcial, p.xo, p.getX());
         double pz = Mth.lerp(parcial, p.zo, p.getZ());
         float sx = cx + (float)((px - this.centroX) * zoom);
         float sy = cy + (float)((pz - this.centroZ) * zoom);
         float yaw = p.getViewYRot(parcial);
         g.pose().pushPose();
         g.pose().translate(sx, sy, 0.0F);
         g.pose().mulPose(Axis.ZP.rotationDegrees(yaw + 180.0F));
         Matrix4f m = g.pose().last().pose();
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         RenderSystem.enableBlend();
         RenderSystem.disableCull();
         BufferBuilder b = Tesselator.getInstance().begin(Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
         flecha(b, m, 0.62F, 0.35F, 0.0F, 0.0F);
         flecha(b, m, 0.45F, 0.95F, 0.12F, 0.12F);
         BufferUploader.drawWithShader(b.buildOrThrow());
         RenderSystem.enableCull();
         g.pose().popPose();
      }
   }

   private static void flecha(BufferBuilder b, Matrix4f m, float s, float r, float v, float a) {
      float[] punta = new float[]{0.0F, -9.0F * s};
      float[] izq = new float[]{-6.5F * s, 7.0F * s};
      float[] muesca = new float[]{0.0F, 3.0F * s};
      float[] der = new float[]{6.5F * s, 7.0F * s};
      vertice(b, m, punta, r, v, a);
      vertice(b, m, izq, r, v, a);
      vertice(b, m, muesca, r, v, a);
      vertice(b, m, punta, r, v, a);
      vertice(b, m, muesca, r, v, a);
      vertice(b, m, der, r, v, a);
   }

   private static void vertice(BufferBuilder b, Matrix4f m, float[] p, float r, float v, float a) {
      b.addVertex(m, p[0], p[1], 0.0F).setColor(r, v, a, 1.0F);
   }

   private void brujula(GuiGraphics g) {
      float cx = this.width - 34.0F;
      float cy = 30.0F;
      float largo = 20.0F;
      g.pose().pushPose();
      g.pose().translate(cx, cy, 0.0F);
      Matrix4f m = g.pose().last().pose();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      RenderSystem.enableBlend();
      RenderSystem.disableCull();
      BufferBuilder b = Tesselator.getInstance().begin(Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
      linea(b, m, -largo, -largo, largo, largo, 3.0F, 0.0F);
      linea(b, m, -largo, largo, largo, -largo, 3.0F, 0.0F);
      linea(b, m, -largo, -largo, largo, largo, 1.2F, 1.0F);
      linea(b, m, -largo, largo, largo, -largo, 1.2F, 1.0F);
      BufferUploader.drawWithShader(b.buildOrThrow());
      RenderSystem.enableCull();
      g.pose().popPose();
      int ix = Math.round(cx);
      int iy = Math.round(cy);
      int d = 11;
      this.letra(g, "N", ix, iy - d);
      this.letra(g, "S", ix, iy + d);
      this.letra(g, "W", ix - d, iy);
      this.letra(g, "E", ix + d, iy);
   }

   private void letra(GuiGraphics g, String t, int x, int y) {
      int w = this.font.width(t);
      int lx = x - w / 2;
      int ly = y - 4;

      for (int ox = -1; ox <= 1; ox++) {
         for (int oy = -1; oy <= 1; oy++) {
            if (ox != 0 || oy != 0) {
               g.drawString(this.font, t, lx + ox, ly + oy, 0, false);
            }
         }
      }

      g.drawString(this.font, t, lx, ly, 16777215, false);
   }

   private static void linea(BufferBuilder b, Matrix4f m, float x1, float y1, float x2, float y2, float grosor, float tono) {
      float dx = x2 - x1;
      float dy = y2 - y1;
      float l = Mth.sqrt(dx * dx + dy * dy);
      float nx = -dy / l * grosor * 0.5F;
      float ny = dx / l * grosor * 0.5F;
      float[][] q = new float[][]{{x1 + nx, y1 + ny}, {x1 - nx, y1 - ny}, {x2 - nx, y2 - ny}, {x2 + nx, y2 + ny}};
      int[] orden = new int[]{0, 1, 2, 0, 2, 3};

      for (int i : orden) {
         b.addVertex(m, q[i][0], q[i][1], 0.0F).setColor(tono, tono, tono, 1.0F);
      }
   }

   public boolean mouseScrolled(double ratonX, double ratonY, double deltaX, double delta) {
      double bx = this.centroX + (ratonX - this.width / 2.0) / zoom;
      double bz = this.centroZ + (ratonY - this.height / 2.0) / zoom;
      zoom = Mth.clamp(zoom * (delta > 0.0 ? 1.25F : 0.8F), 0.25F, 24.0F);
      this.centroX = bx - (ratonX - this.width / 2.0) / zoom;
      this.centroZ = bz - (ratonY - this.height / 2.0) / zoom;
      return true;
   }

   public boolean mouseDragged(double ratonX, double ratonY, int boton, double dx, double dy) {
      this.centroX = this.centroX - dx / zoom;
      this.centroZ = this.centroZ - dy / zoom;
      return true;
   }

   public boolean keyPressed(int tecla, int codigo, int mods) {
      if (MapaCliente.TECLA.matches(tecla, codigo)) {
         this.onClose();
         return true;
      } else if (tecla == 32 && this.minecraft != null && this.minecraft.player != null) {
         this.centroX = this.minecraft.player.getX();
         this.centroZ = this.minecraft.player.getZ();
         return true;
      } else {
         return super.keyPressed(tecla, codigo, mods);
      }
   }

   private static final class Loseta {
      final DynamicTexture textura;
      final ResourceLocation id;
      boolean vista;

      Loseta(DynamicTexture t, ResourceLocation id) {
         this.textura = t;
         this.id = id;
      }
   }
}
