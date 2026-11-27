package net.ruleta.client.escenas;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.NativeImage.Format;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Flujo {
   private static final Logger LOG = LoggerFactory.getLogger("ruleta");
   private static final ExecutorService LECTOR = Executors.newFixedThreadPool(2, r -> {
      Thread t = new Thread(r, "ruleta-escenas");
      t.setDaemon(true);
      return t;
   });
   private final String carpeta;
   private final Identifier id;
   public final int ancho;
   public final int alto;
   private DynamicTexture textura;
   private int subido = -1;
   private int pedido = -1;
   private CompletableFuture<NativeImage> lectura;

   public Flujo(String nombre, String carpeta, int ancho, int alto) {
      this.carpeta = carpeta;
      this.id = Identifier.fromNamespaceAndPath("ruleta", "dinamica/" + nombre);
      this.ancho = ancho;
      this.alto = alto;
   }

   public Identifier id() {
      return this.id;
   }

   public boolean mostrar(int n, int proximo) {
      if (n < 0) {
         return false;
      }

      if (this.lectura != null && this.lectura.isDone()) {
         NativeImage img = this.lectura.getNow(null);
         int leido = this.pedido;
         this.lectura = null;
         if (img != null) {
            if (leido == n || this.subido != n) {
               this.subir(img);
               this.subido = leido;
            }

            img.close();
         }
      }

      if (this.lectura == null) {
         if (this.subido != n) {
            this.pedir(n);
         } else if (proximo >= 0 && proximo != n) {
            this.pedir(proximo);
         }
      }

      return this.textura != null && this.subido >= 0;
   }

   public int subido() {
      return this.subido;
   }

   private void subir(NativeImage img) { }

   private void pedir(int n) {
      this.pedido = n;
      Identifier ruta = Identifier.fromNamespaceAndPath("ruleta", String.format("%s/%03d.png", this.carpeta, n));
      this.lectura = CompletableFuture.supplyAsync(() -> leer(ruta), LECTOR);
   }

   private static NativeImage leer(Identifier ruta) { return null; }

   public void soltar() {
      if (this.lectura != null) {
         this.lectura.thenAccept(img -> {
            if (img != null) {
               img.close();
            }
         });
         this.lectura = null;
      }

      if (this.textura != null) {
         Minecraft.getInstance().getTextureManager().release(this.id);
         this.textura = null;
      }

      this.subido = -1;
      this.pedido = -1;
   }
}
