package net.ruleta.client.escenas;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.resources.Identifier;
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
   private static final Identifier SONIDO = Identifier.fromNamespaceAndPath("ruleta", "momento_nutria.musica");
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
   private static final Identifier ESTRELLA = Identifier.fromNamespaceAndPath("ruleta", "textures/momento_nutria/estrella.png");
   private static final float TAM_ESTRELLA = 2.6F;
   private static long inicio = -1L;
   private static SoundInstance sonando;
   private static float r;
   private static float g;
   private static float b;
   private static int ultimoColor = -1;
   private static long cambio;
   private static final FiltroPantalla FILTRO = new FiltroPantalla(Identifier.fromNamespaceAndPath("ruleta", "shaders/post/momento_nutria.json"));
   private static final float ARCO_HASTA = 17.49797F;
   private static final float VUELTAS_POR_SEGUNDO = 6.1F;
   private static Object shaderArco;

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
            .getResource(Identifier.fromNamespaceAndPath("ruleta", "textures/momento_nutria/escena.json"));
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
            } catch (Exception ex) {
               LOG.warn("No se pudo leer la escena del Momento Nutria", ex);
               tiempos = null;
            }
         }
      }
   }

   private static void cargarEstrellas() {
      Optional<Resource> res = Minecraft.getInstance()
         .getResourceManager()
         .getResource(Identifier.fromNamespaceAndPath("ruleta", "textures/momento_nutria/estrellas.json"));
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
         } catch (Exception ex) {
            LOG.warn("No se pudieron leer las estrellitas del Momento Nutria", ex);
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
      }

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

   public static void dibujar(GuiGraphicsExtractor ctx) {
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

   private static void pintar(GuiGraphicsExtractor ctx, float t, int n) { }

   private static void estrellas(GuiGraphicsExtractor ctx, int f, float x0, float y0, float escala, float t) { }

   public static void shader(Object s) {
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
