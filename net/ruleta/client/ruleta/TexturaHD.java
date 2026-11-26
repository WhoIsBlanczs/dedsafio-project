package net.ruleta.client.ruleta;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

final class TexturaHD extends AbstractTexture {
   private static final int NIVELES = 5;
   private final ResourceLocation archivo;
   private int ancho;
   private int alto;
   private NativeImage[] listas;

   TexturaHD(ResourceLocation archivo) {
      this(archivo, null);
   }

   TexturaHD(ResourceLocation archivo, NativeImage[] listas) {
      this.archivo = archivo;
      this.listas = listas;
   }

   int ancho() {
      return this.ancho;
   }

   int alto() {
      return this.alto;
   }

   static NativeImage[] preparar(InputStream in) throws IOException {
      NativeImage base = NativeImage.read(in);
      int ancho = base.getWidth();
      int alto = base.getHeight();
      int niveles = 1;

      while (niveles < 5 && ancho >> niveles >= 1 && alto >> niveles >= 1) {
         niveles++;
      }

      NativeImage[] mips = new NativeImage[niveles];
      mips[0] = base;

      for (int k = 1; k < niveles; k++) {
         mips[k] = reducir(mips[k - 1]);
      }

      return mips;
   }

   public void load(ResourceManager recursos) throws IOException {
      NativeImage[] mips = this.listas;
      this.listas = null;
      if (mips == null) {
         try (InputStream in = recursos.getResourceOrThrow(this.archivo).open()) {
            mips = preparar(in);
         }
      }

      this.ancho = mips[0].getWidth();
      this.alto = mips[0].getHeight();
      NativeImage[] subir = mips;
      if (RenderSystem.isOnRenderThreadOrInit()) {
         this.subir(subir);
      } else {
         RenderSystem.recordRenderCall(() -> this.subir(subir));
      }
   }

   private void subir(NativeImage[] mips) {
      TextureUtil.prepareImage(this.getId(), mips.length - 1, mips[0].getWidth(), mips[0].getHeight());

      for (int k = 0; k < mips.length; k++) {
         mips[k].upload(k, 0, 0, 0, 0, mips[k].getWidth(), mips[k].getHeight(), true, true, true, true);
      }

      this.setFilter(true, true);
   }

   private static NativeImage reducir(NativeImage src) {
      int w = Math.max(1, src.getWidth() / 2);
      int h = Math.max(1, src.getHeight() / 2);
      NativeImage out = new NativeImage(w, h, false);

      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            long sa = 0L;
            long sr = 0L;
            long sg = 0L;
            long sb = 0L;

            for (int dy = 0; dy < 2; dy++) {
               for (int dx = 0; dx < 2; dx++) {
                  int c = src.getPixelRGBA(Math.min(src.getWidth() - 1, 2 * x + dx), Math.min(src.getHeight() - 1, 2 * y + dy));
                  int a = c >>> 24;
                  sa += a;
                  sr += (long)(c & 0xFF) * a;
                  sg += (long)(c >> 8 & 0xFF) * a;
                  sb += (long)(c >> 16 & 0xFF) * a;
               }
            }

            int a = (int)((sa + 2L) / 4L);
            int r = sa == 0L ? 0 : (int)(sr / sa);
            int g = sa == 0L ? 0 : (int)(sg / sa);
            int b = sa == 0L ? 0 : (int)(sb / sa);
            out.setPixelRGBA(x, y, a << 24 | b << 16 | g << 8 | r);
         }
      }

      return out;
   }
}
