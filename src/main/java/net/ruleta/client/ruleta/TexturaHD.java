package net.ruleta.client.ruleta;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

final class TexturaHD extends AbstractTexture {
   private static final int NIVELES = 5;
   private final Identifier archivo;
   private int ancho;
   private int alto;
   private NativeImage[] listas;

   TexturaHD(Identifier archivo) {
      this(archivo, null);
   }

   TexturaHD(Identifier archivo, NativeImage[] listas) {
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

   public void load(ResourceManager recursos) throws IOException { }

   private void subir(NativeImage[] mips) { }

   private static NativeImage reducir(NativeImage src) { return null; }
}
