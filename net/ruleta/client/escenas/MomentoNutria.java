package net.ruleta.client.escenas;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.ruleta.client.FiltroPantalla;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MomentoNutria {
   private static final Logger LOG = LoggerFactory.getLogger("ruleta");
   private static final ResourceLocation SONIDO = ResourceLocation.fromNamespaceAndPath("ruleta", "momento_nutria.musica");
   private static boolean cargada;
   private static float[] tiempos;
   private static int[] cuadros;
   private static int[] colores;
   private static float fin;
   private static final float DURACION_MUSICA = 17.97F;
   private static final float MUSICA_ANTERIOR = 16.37F;
   private static final float ESCALA = 1.0977397F;
   private static Flujo flujo;
   private static final int BAILE = 80;
   private static final int VIDEO_ANCHO = 640;
   private static final int TEX_ANCHO = 960;
   private static final int TEX_ALTO = 540;
   private static final int VIDEO_ALTO = 360;
   private static Flujo lado;
   private static float[][] estrellas;
   private static final ResourceLocation ESTRELLA = ResourceLocation.fromNamespaceAndPath("ruleta", "textures/momento_nutria/estrella.png");
   private static final float TAM_ESTRELLA = 2.6F;
   private static long inicio = -1L;
   private static SoundInstance sonando;
   private static float r;
   private static float g;
   private static float b;
   private static int ultimoColor = -1;
   private static long cambio;
   private static final FiltroPantalla FILTRO = new FiltroPantalla(ResourceLocation.fromNamespaceAndPath("ruleta", "shaders/post/momento_nutria.json"));
   private static final float ARCO_HASTA = 17.49797F;
   private static final float VUELTAS_POR_SEGUNDO = 6.1F;
   private static ShaderInstance shaderArco;

   private MomentoNutria() {
   }

   public static void precargar() {
      cargar();
   }

   private static void cargar() {
      if (!cargada) {
         cargada = true;
         Optional<Resource> res = Minecraft.getInstance()
            .getResourceManager()
            .getResource(ResourceLocation.fromNamespaceAndPath("ruleta", "textures/momento_nutria/escena.json"));
         if (res.isEmpty()) {
            LOG.warn("Falta textures/momento_nutria/escena.json");
         } else {
            try (InputStreamReader in = new InputStreamReader(res.get().open(), StandardCharsets.UTF_8)) {
               JsonObject j = JsonParser.parseReader(in).getAsJsonObject();
               JsonArray c = j.getAsJsonArray("cuadros");
               tiempos = new float[c.size()];
               cuadros = new int[c.size()];
               colores = new int[c.size()];

               for (int i = 0; i < c.size(); i++) {
                  JsonArray e = c.get(i).getAsJsonArray();
                  tiempos[i] = e.get(0).getAsFloat();
                  cuadros[i] = e.get(1).getAsInt();
                  colores[i] = e.get(2).getAsInt();
               }

               for (int i = 0; i < tiempos.length; i++) {
                  tiempos[i] = tiempos[i] * 1.0977397F;
               }

               fin = 17.97F;
               JsonArray tam = j.getAsJsonArray("tam");
               flujo = new Flujo("momento_nutria", "textures/momento_nutria", 960, 540);
               lado = new Flujo("momento_nutria_lado", "textures/momento_nutria_lado", 960, 540);
               cargarEstrellas();
            } catch (Exception var8) {
               LOG.warn("No se pudo leer la escena del Momento Nutria", var8);
               tiempos = null;
            }
         }
      }
   }

   private static void cargarEstrellas() {
      Optional<Resource> res = Minecraft.getInstance()
         .getResourceManager()
         .getResource(ResourceLocation.fromNamespaceAndPath("ruleta", "textures/momento_nutria/estrellas.json"));
      if (!res.isEmpty()) {
         try (InputStreamReader in = new InputStreamReader(res.get().open(), StandardCharsets.UTF_8)) {
            JsonObject j = JsonParser.parseReader(in).getAsJsonObject();
            int total = j.get("total").getAsInt();
            estrellas = new float[total][];
            JsonObject porCuadro = j.getAsJsonObject("cuadros");

            for (String k : porCuadro.keySet()) {
               int f = Integer.parseInt(k);
               JsonArray lista = porCuadro.getAsJsonArray(k);
               if (f >= 0 && f < total) {
                  float[] e = new float[lista.size()];

                  for (int i = 0; i < e.length; i++) {
                     e[i] = lista.get(i).getAsFloat();
                  }

                  estrellas[f] = e;
               }
            }
         } catch (Exception var13) {
            LOG.warn("No se pudieron leer las estrellitas del Momento Nutria", var13);
            estrellas = null;
         }
      }
   }

   public static void empezar() {
      cargar();
      if (tiempos != null) {
         parar();
         inicio = System.nanoTime();
         ultimoColor = -1;
         sonando = new SimpleSoundInstance(SONIDO, SoundSource.MASTER, 1.0F, 1.0F, RandomSource.create(), false, 0, Attenuation.NONE, 0.0, 0.0, 0.0, true);
         Minecraft.getInstance().getSoundManager().play(sonando);
      }
   }

   public static boolean activa() {
      return inicio >= 0L;
   }

   public static void parar() {
      inicio = -1L;
      if (sonando != null) {
         Minecraft.getInstance().getSoundManager().stop(sonando);
         sonando = null;
      }

      if (flujo != null) {
         flujo.soltar();
      }

      if (lado != null) {
         lado.soltar();
      }
   }

   private static int cuadro(float t) {
      if (t < tiempos[0]) {
         return -1;
      } else {
         int lo = 0;
         int hi = tiempos.length - 1;

         while (lo < hi) {
            int m = lo + hi + 1 >>> 1;
            if (tiempos[m] <= t) {
               lo = m;
            } else {
               hi = m - 1;
            }
         }

         return lo;
      }
   }

   public static void dibujar(GuiGraphics ctx) {
      if (inicio >= 0L) {
         long ahora = System.nanoTime();
         float t = (float)(ahora - inicio) / 1.0E9F;
         if (t >= fin) {
            parar();
         } else {
            int n = cuadro(t);
            int col = n >= 0 ? colores[n] : 0;
            if (col != 0) {
               if (ultimoColor < 0 || distancia(col, ultimoColor) > 0.35F) {
                  cambio = ahora;
               }

               ultimoColor = col;
            }

            pintar(ctx, t, n);
         }
      }
   }

   private static void pintar(GuiGraphics ctx, float t, int n) {
      int w = ctx.guiWidth();
      int h = ctx.guiHeight();
      if (n >= 0 && cuadros[n] >= 0) {
         int f = cuadros[n];
         boolean enLado = f >= 80;
         Flujo actual = enLado ? lado : flujo;
         int prox = -1;

         for (int i = n + 1; i < cuadros.length && i < n + 30; i++) {
            if (cuadros[i] >= 0 && cuadros[i] != f) {
               prox = cuadros[i];
               break;
            }
         }

         if (prox >= 0 && prox >= 80 != enLado) {
            (enLado ? flujo : lado).mostrar(prox, -1);
            prox = -1;
         }

         if (actual.mostrar(f, prox)) {
            float escala = Math.max(w / 640.0F, h / 360.0F);
            float fw = 640.0F * escala;
            float fh = 360.0F * escala;
            float x0 = (w - fw) / 2.0F;
            float y0 = (h - fh) / 2.0F;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            float arco = arco(t);
            if (arco > 0.0F && shaderArco != null) {
               shaderArco.safeGetUniform("Giro").set(t * 6.1F * (float) (Math.PI * 2));
               shaderArco.safeGetUniform("Arco").set(arco);
               RenderSystem.setShader(() -> shaderArco);
               RenderSystem.setShaderTexture(0, actual.id());
               Matrix4f m = ctx.pose().last().pose();
               BufferBuilder b = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
               b.addVertex(m, x0, y0, 0.0F).setUv(0.0F, 0.0F);
               b.addVertex(m, x0, y0 + fh, 0.0F).setUv(0.0F, 1.0F);
               b.addVertex(m, x0 + fw, y0 + fh, 0.0F).setUv(1.0F, 1.0F);
               b.addVertex(m, x0 + fw, y0, 0.0F).setUv(1.0F, 0.0F);
               BufferUploader.drawWithShader(b.buildOrThrow());
            } else {
               ctx.blit(
                  actual.id(), Math.round(x0), Math.round(y0), Math.round(fw), Math.round(fh), 0.0F, 0.0F, actual.ancho, actual.alto, actual.ancho, actual.alto
               );
            }

            estrellas(ctx, actual.subido(), x0, y0, escala, t);
         }
      } else if (n < 0 && flujo != null) {
         for (int c : cuadros) {
            if (c >= 0) {
               (c >= 80 ? lado : flujo).mostrar(c, -1);
               break;
            }
         }
      }

      RenderSystem.defaultBlendFunc();
      RenderSystem.disableBlend();
   }

   private static void estrellas(GuiGraphics ctx, int f, float x0, float y0, float escala, float t) {
      if (estrellas != null && f >= 0 && f < estrellas.length && estrellas[f] != null && estrellas[f].length != 0) {
         float[] e = estrellas[f];
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, ESTRELLA);
         Matrix4f m = ctx.pose().last().pose();
         BufferBuilder b = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

         for (int i = 0; i + 2 < e.length; i += 3) {
            float ex = e[i];
            float ey = e[i + 1];
            float semilla = ex * 0.137F + ey * 0.271F;
            float titila = 0.8F + 0.35F * (float)Math.sin(t * 11.0F + semilla * 7.0F);
            float r = e[i + 2] * 2.6F * titila * escala;
            float ang = (float)Math.sin(t * 2.0F + semilla) * 0.35F;
            float cx = x0 + ex * escala;
            float cy = y0 + ey * escala;
            float c = (float)Math.cos(ang) * r;
            float s = (float)Math.sin(ang) * r;
            b.addVertex(m, cx - c + s, cy - s - c, 0.0F).setUv(0.0F, 0.0F);
            b.addVertex(m, cx - c - s, cy - s + c, 0.0F).setUv(0.0F, 1.0F);
            b.addVertex(m, cx + c - s, cy + s + c, 0.0F).setUv(1.0F, 1.0F);
            b.addVertex(m, cx + c + s, cy + s - c, 0.0F).setUv(1.0F, 0.0F);
         }

         BufferUploader.drawWithShader(b.buildOrThrow());
         RenderSystem.defaultBlendFunc();
      }
   }

   public static void shader(ShaderInstance s) {
      shaderArco = s;
   }

   private static float arco(float t) {
      return Math.min(Mth.clamp(t / 0.08F, 0.0F, 1.0F), Mth.clamp((17.49797F - t) / 0.08F, 0.0F, 1.0F));
   }

   public static void filtro(float parcial) {
      if (inicio < 0L) {
         FILTRO.cerrar();
      } else {
         long ahora = System.nanoTime();
         float t = (float)(ahora - inicio) / 1.0E9F;
         if (!(t >= fin)) {
            float fuerza = Math.min(Mth.clamp((t - 0.6F) / 0.5F, 0.0F, 1.0F), Mth.clamp((fin - t) / 0.8F, 0.0F, 1.0F));
            float arco = arco(t);
            if ((!(fuerza <= 0.0F) || !(arco <= 0.0F)) && FILTRO.preparar()) {
               float golpe = ultimoColor < 0 ? 0.0F : (float)Math.exp(-((float)(ahora - cambio) / 1.0E9F) / 0.16F);
               FILTRO.fijar("Arco", arco);
               FILTRO.fijar("Tiempo", t);
               FILTRO.fijar("Fuerza", fuerza);
               FILTRO.fijar("Golpe", golpe);
               FILTRO.pasar(parcial);
            }
         }
      }
   }

   private static float distancia(int a, int b) {
      float dr = ((a >> 16 & 0xFF) - (b >> 16 & 0xFF)) / 255.0F;
      float dg = ((a >> 8 & 0xFF) - (b >> 8 & 0xFF)) / 255.0F;
      float db = ((a & 0xFF) - (b & 0xFF)) / 255.0F;
      return (float)Math.sqrt(dr * dr + dg * dg + db * db);
   }
}
