package net.ruleta.client.ruleta;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.renderpearl.backend.opengl.Uniform;
import com.mojang.blaze3d.vertex.Object;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Object;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Object;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.joml.Matrix4f;

public final class Pintor {
   private static Object shader;
   private static final Map<String, Identifier> TEXTURAS = new HashMap<>();
   private static final Map<String, NativeImage[]> LISTAS = new ConcurrentHashMap<>();
   private static final ConcurrentLinkedQueue<String> POR_SUBIR = new ConcurrentLinkedQueue<>();
   private static boolean precargando;

   private Pintor() {
   }

   static void shader(Object s) {
      shader = s;
   }

   static Identifier textura(String nombre) {
      return TEXTURAS.computeIfAbsent(
         nombre,
         n -> {
            Identifier id = Identifier.fromNamespaceAndPath("ruleta", "hd/" + n);
            Minecraft.getInstance()
               .getTextureManager()
               .register(id, new TexturaHD(Identifier.fromNamespaceAndPath("ruleta", "textures/ruleta/" + n + ".png"), LISTAS.remove(n)));
            return id;
         }
      );
   }

   public static void precargar() {
      if (!precargando) {
         precargando = true;
         ResourceManager rm = Minecraft.getInstance().getResourceManager();
         List<Identifier> archivos = new ArrayList<>();

         for (Identifier rl : rm.listResources("textures/ruleta", r -> r.getNamespace().equals("ruleta") && r.getPath().endsWith(".png")).keySet()) {
            archivos.add(rl);
         }

         ExecutorService hilos = Executors.newFixedThreadPool(2, r -> {
            Thread t = new Thread(r, "ruleta-precarga");
            t.setDaemon(true);
            t.setPriority(1);
            return t;
         });

         for (Identifier rl : archivos) {
            String n = rl.getPath().substring("textures/ruleta/".length(), rl.getPath().length() - ".png".length());
            if (!TEXTURAS.containsKey(n)) {
               hilos.execute(() -> {
                  try (InputStream in = rm.getResourceOrThrow(rl).open()) {
                     LISTAS.put(n, TexturaHD.preparar(in));
                     POR_SUBIR.add(n);
                  } catch (Exception var8) {
                  }
               });
            }
         }

         hilos.shutdown();
      }
   }

   public static void subirPendientes() {
      String n = POR_SUBIR.poll();
      if (n != null) {
         if (!TEXTURAS.containsKey(n)) {
            if (LISTAS.containsKey(n)) {
               textura(n);
            }
         } else {
            NativeImage[] sobra = LISTAS.remove(n);
            if (sobra != null) {
               for (NativeImage i : sobra) {
                  i.close();
               }
            }
         }
      }
   }

   static void empezar() { }

   static void terminar() { }

   static void rectTono(Matrix4f m, String textura, float x0, float y0, float x1, float y1, int argb, float giro, float luz, float saturacion) {
      Uniform u = shader == null ? null : shader.getUniform("Recolor");
      if (u == null) {
         rect(m, textura, x0, y0, x1, y1, argb);
      } else {
         u.set(giro / 360.0F, luz, saturacion, 1.0F);

         try {
            rect(m, textura, x0, y0, x1, y1, argb);
         } finally {
            u.set(0.0F, 0.0F, 1.0F, 0.0F);
         }
      }
   }

   static void rectSuma(Matrix4f m, String textura, float x0, float y0, float x1, float y1, int argb) { }

   static void rect(Matrix4f m, String textura, float x0, float y0, float x1, float y1, int argb) { }
}
