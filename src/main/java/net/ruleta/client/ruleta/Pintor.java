package net.ruleta.client.ruleta;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
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
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.joml.Matrix4f;

public final class Pintor {
   private static ShaderInstance shader;
   private static final Map<String, ResourceLocation> TEXTURAS = new HashMap<>();
   private static final Map<String, NativeImage[]> LISTAS = new ConcurrentHashMap<>();
   private static final ConcurrentLinkedQueue<String> POR_SUBIR = new ConcurrentLinkedQueue<>();
   private static boolean precargando;

   private Pintor() {
   }

   static void shader(ShaderInstance s) {
      shader = s;
   }

   static ResourceLocation textura(String nombre) {
      return TEXTURAS.computeIfAbsent(
         nombre,
         n -> {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("ruletafanproject", "hd/" + n);
            Minecraft.getInstance()
               .getTextureManager()
               .register(id, new TexturaHD(ResourceLocation.fromNamespaceAndPath("ruletafanproject", "textures/ruleta/" + n + ".png"), LISTAS.remove(n)));
            return id;
         }
      );
   }

   public static void precargar() {
      if (!precargando) {
         precargando = true;
         ResourceManager rm = Minecraft.getInstance().getResourceManager();
         List<ResourceLocation> archivos = new ArrayList<>();

         for (ResourceLocation rl : rm.listResources("textures/ruleta", r -> r.getNamespace().equals("ruletafanproject") && r.getPath().endsWith(".png"))
            .keySet()) {
            archivos.add(rl);
         }

         ExecutorService hilos = Executors.newFixedThreadPool(2, r -> {
            Thread t = new Thread(r, "ruleta-precarga");
            t.setDaemon(true);
            t.setPriority(1);
            return t;
         });

         for (ResourceLocation rl : archivos) {
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

   static void empezar() {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.setShader(() -> shader != null ? shader : GameRenderer.getPositionTexColorShader());
   }

   static void terminar() {
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

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

   static void rectSuma(Matrix4f m, String textura, float x0, float y0, float x1, float y1, int argb) {
      RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);

      try {
         rect(m, textura, x0, y0, x1, y1, argb);
      } finally {
         RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
      }
   }

   static void rect(Matrix4f m, String textura, float x0, float y0, float x1, float y1, int argb) {
      if (argb >>> 24 != 0) {
         RenderSystem.setShaderTexture(0, textura(textura));
         float a = (argb >>> 24) / 255.0F;
         float r = (argb >> 16 & 0xFF) / 255.0F;
         float g = (argb >> 8 & 0xFF) / 255.0F;
         float b = (argb & 0xFF) / 255.0F;
         BufferBuilder bb = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         bb.addVertex(m, x0, y0, 0.0F).setUv(0.0F, 0.0F).setColor(r, g, b, a);
         bb.addVertex(m, x0, y1, 0.0F).setUv(0.0F, 1.0F).setColor(r, g, b, a);
         bb.addVertex(m, x1, y1, 0.0F).setUv(1.0F, 1.0F).setColor(r, g, b, a);
         bb.addVertex(m, x1, y0, 0.0F).setUv(1.0F, 0.0F).setColor(r, g, b, a);
         BufferUploader.drawWithShader(bb.buildOrThrow());
      }
   }
}
