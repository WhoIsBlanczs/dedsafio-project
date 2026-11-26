package net.ruleta.client.escenas;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AnimacionMuerte {
   private static final Logger LOG = LoggerFactory.getLogger("ruleta");
   private static final ResourceLocation SONIDO = ResourceLocation.fromNamespaceAndPath("ruleta", "muerte_animacion.sonido");
   private static final int ROJO = 16722474;
   private static boolean cargada;
   private static boolean lista;
   private static int nCuadros;
   private static int anchoEscena;
   private static int altoEscena;
   private static float fps;
   private static float escala;
   private static float bajada;
   private static float textoY;
   private static float fin;
   private static boolean[] pantallaCompleta;
   private static boolean[] modoRecorte;
   private static int recX;
   private static int recY;
   private static int recAncho;
   private static int recAlto;
   private static float[][] sacudida;
   private static float sacudidaTau;
   private static float[][] zoom;
   private static float zoomTau;
   private static float vinDesde;
   private static float vinHasta;
   private static float vinMax;
   private static float vinEntra;
   private static float vinSale;
   private static float finDesde;
   private static float finHasta;
   private static float sonidoDesde;
   private static Flujo completo;
   private static Flujo recorte;
   private static long inicio = -1L;
   private static boolean sonado;
   private static SoundInstance sonando;
   private static Component mensaje = Component.empty();

   private AnimacionMuerte() {
   }

   public static void precargar() {
      cargar();
   }

   private static void cargar() {
      if (!cargada) {
         cargada = true;
         Optional<Resource> r = Minecraft.getInstance()
            .getResourceManager()
            .getResource(ResourceLocation.fromNamespaceAndPath("ruleta", "textures/muerte_animacion/escena.json"));
         if (r.isEmpty()) {
            LOG.warn("Falta textures/muerte_animacion/escena.json");
         } else {
            try (InputStreamReader in = new InputStreamReader(r.get().open(), StandardCharsets.UTF_8)) {
               JsonObject j = JsonParser.parseReader(in).getAsJsonObject();
               nCuadros = j.get("cuadros").getAsInt();
               fps = j.get("fps").getAsFloat();
               anchoEscena = j.get("ancho").getAsInt();
               altoEscena = j.get("alto").getAsInt();
               escala = j.get("escala").getAsFloat();
               bajada = j.get("bajada").getAsFloat();
               textoY = j.get("texto_y").getAsFloat();
               fin = j.get("fin").getAsFloat();
               pantallaCompleta = new boolean[nCuadros];

               for (JsonElement e : j.getAsJsonArray("pantalla_completa")) {
                  int n = e.getAsInt();
                  if (n >= 0 && n < nCuadros) {
                     pantallaCompleta[n] = true;
                  }
               }

               JsonArray rec = j.getAsJsonArray("recorte");
               recX = rec.get(0).getAsInt();
               recY = rec.get(1).getAsInt();
               recAncho = rec.get(2).getAsInt();
               recAlto = rec.get(3).getAsInt();
               JsonArray modo = j.getAsJsonArray("modo");
               modoRecorte = new boolean[nCuadros];

               for (int i = 0; i < nCuadros && i < modo.size(); i++) {
                  modoRecorte[i] = modo.get(i).getAsInt() == 1;
               }

               sacudida = pares(j.getAsJsonArray("sacudida"));
               sacudidaTau = j.get("sacudida_tau").getAsFloat();
               zoom = pares(j.getAsJsonArray("zoom"));
               zoomTau = j.get("zoom_tau").getAsFloat();
               JsonObject v = j.getAsJsonObject("vineta");
               vinDesde = v.get("desde").getAsFloat();
               vinHasta = v.get("hasta").getAsFloat();
               vinMax = v.get("max").getAsFloat();
               vinEntra = v.get("entrada").getAsFloat();
               vinSale = v.get("salida").getAsFloat();
               JsonObject f = j.getAsJsonObject("final");
               finDesde = f.get("desde").getAsFloat();
               finHasta = f.get("hasta").getAsFloat();
               sonidoDesde = j.get("sonido_desde").getAsFloat();
               completo = new Flujo("muerte_completo", "textures/muerte_animacion/completo", anchoEscena, altoEscena);
               recorte = new Flujo("muerte_recorte", "textures/muerte_animacion/recorte", recAncho, recAlto);
               lista = true;
            } catch (Exception var9) {
               LOG.warn("No se pudo leer la escena de muerte", var9);
               lista = false;
            }
         }
      }
   }

   private static float[][] pares(JsonArray a) {
      float[][] r = new float[a.size()][2];

      for (int i = 0; i < a.size(); i++) {
         r[i][0] = a.get(i).getAsJsonArray().get(0).getAsFloat();
         r[i][1] = a.get(i).getAsJsonArray().get(1).getAsFloat();
      }

      return r;
   }

   public static void empezar(Component texto) {
      cargar();
      if (lista) {
         pararSonido();
         mensaje = (Component)(texto == null ? Component.empty() : texto);
         inicio = System.nanoTime();
         sonado = false;
      }
   }

   public static boolean activa() {
      return inicio >= 0L;
   }

   public static void parar() {
      inicio = -1L;
      pararSonido();
      soltar();
   }

   private static void soltar() {
      if (completo != null) {
         completo.soltar();
      }

      if (recorte != null) {
         recorte.soltar();
      }
   }

   private static void pararSonido() {
      if (sonando != null) {
         Minecraft.getInstance().getSoundManager().stop(sonando);
         sonando = null;
      }
   }

   private static int siguienteDelMismoTipo(int n, boolean esRecorte) {
      for (int i = n + 1; i <= Math.min(n + 3, nCuadros - 1); i++) {
         if (modoRecorte[i] == esRecorte) {
            return i;
         }
      }

      return -1;
   }

   private static float impulsos(float[][] lista, float tau, float t) {
      float s = 0.0F;

      for (float[] p : lista) {
         float dt = t - p[0];
         if (dt >= 0.0F && dt < tau * 8.0F) {
            s += p[1] * (float)Math.exp(-dt / tau);
         }
      }

      return s;
   }

   private static float azar(float a, float b) {
      float x = Mth.sin(a * 12.9898F + b * 78.233F) * 43758.547F;
      return x - Mth.floor(x);
   }

   public static void dibujar(GuiGraphics ctx) {
      if (inicio >= 0L) {
         float t = (float)(System.nanoTime() - inicio) / 1.0E9F;
         if (t >= fin) {
            parar();
         } else {
            Minecraft mc = Minecraft.getInstance();
            if (!sonado && t >= sonidoDesde) {
               sonado = true;
               sonando = new SimpleSoundInstance(SONIDO, SoundSource.MASTER, 1.0F, 1.0F, RandomSource.create(), false, 0, Attenuation.NONE, 0.0, 0.0, 0.0, true);
               mc.getSoundManager().play(sonando);
            }

            int w = ctx.guiWidth();
            int h = ctx.guiHeight();
            int n = Mth.clamp((int)(t * fps), 0, nCuadros - 1);
            float vin = 0.0F;
            if (t >= vinDesde && t <= vinHasta) {
               vin = vinMax * Math.min(Mth.clamp((t - vinDesde) / vinEntra, 0.0F, 1.0F), Mth.clamp((vinHasta - t) / vinSale, 0.0F, 1.0F));
            }

            if (vin > 0.0F) {
               int a = Math.round(vin * 255.0F) << 24;
               int borde = a | 1703936;
               int tb = h / 4;
               ctx.fillGradient(0, 0, w, tb, borde, 0);
               ctx.fillGradient(0, h - tb, w, h, 0, borde);
            }

            float k = h * escala / altoEscena;
            float z = 1.0F + impulsos(zoom, zoomTau, t);
            float amp = impulsos(sacudida, sacudidaTau, t) * k;
            float dx = (azar(t * 60.0F, 1.0F) * 2.0F - 1.0F) * amp;
            float dy = (azar(t * 60.0F, 2.0F) * 2.0F - 1.0F) * amp;
            float alfa = 1.0F;
            if (t >= finDesde) {
               alfa = 1.0F - Mth.clamp((t - finDesde) / Math.max(0.001F, finHasta - finDesde), 0.0F, 1.0F);
            }

            float ancho = anchoEscena * k * z;
            float alto = altoEscena * k * z;
            float ox = (w - ancho) / 2.0F + dx;
            float oy = (h - alto) / 2.0F + bajada * h + dy;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alfa);
            boolean esRecorte = modoRecorte[n];
            Flujo actual = esRecorte ? recorte : completo;
            if (actual.mostrar(n, siguienteDelMismoTipo(n, esRecorte))) {
               int sub = actual.subido();
               if (esRecorte) {
                  ctx.blit(
                     actual.id(),
                     Math.round(ox + recX * k * z),
                     Math.round(oy + recY * k * z),
                     Math.round(recAncho * k * z),
                     Math.round(recAlto * k * z),
                     0.0F,
                     0.0F,
                     recAncho,
                     recAlto,
                     recAncho,
                     recAlto
                  );
               } else if (sub >= 0 && sub < nCuadros && pantallaCompleta[sub]) {
                  float extra = Math.abs(dx) + Math.abs(dy) + 2.0F;
                  ctx.blit(
                     actual.id(),
                     Math.round(-extra),
                     Math.round(-extra),
                     Math.round(w + extra * 2.0F),
                     Math.round(h + extra * 2.0F),
                     0.0F,
                     0.0F,
                     anchoEscena,
                     altoEscena,
                     anchoEscena,
                     altoEscena
                  );
               } else {
                  ctx.blit(
                     actual.id(),
                     Math.round(ox),
                     Math.round(oy),
                     Math.round(ancho),
                     Math.round(alto),
                     0.0F,
                     0.0F,
                     anchoEscena,
                     altoEscena,
                     anchoEscena,
                     altoEscena
                  );
               }
            }

            for (int i = n + 1; i <= Math.min(n + 3, nCuadros - 1); i++) {
               if (modoRecorte[i] != esRecorte) {
                  (modoRecorte[i] ? recorte : completo).mostrar(i, -1);
                  break;
               }
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            if (t >= sonidoDesde && mensaje != null) {
               Font f = mc.font;
               float aparece = Mth.clamp((t - sonidoDesde) / 0.25F, 0.0F, 1.0F) * alfa;
               int a = Math.max(4, Math.round(aparece * 255.0F));
               float escalaTexto = Math.max(1.0F, Math.min(2.0F, w / 320.0F));
               Component texto = Component.empty().append(mensaje).withStyle(s -> s.withColor(16722474));
               int anchoTexto = f.width(texto);
               float ty = Math.min(h - 14.0F * escalaTexto, oy + textoY * k * z);
               ctx.pose().pushPose();
               ctx.pose().translate(w / 2.0F, ty, 0.0F);
               ctx.pose().scale(escalaTexto, escalaTexto, 1.0F);
               ctx.drawString(f, texto, -anchoTexto / 2, 0, a << 24 | 16722474, true);
               ctx.pose().popPose();
            }

            RenderSystem.disableBlend();
         }
      }
   }
}
