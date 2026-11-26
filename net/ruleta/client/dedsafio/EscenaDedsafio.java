package net.ruleta.client.dedsafio;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public final class EscenaDedsafio {
   private static final ResourceLocation ESCENA = ResourceLocation.fromNamespaceAndPath("ruleta", "dedsafio/escena.json");
   private static final ResourceLocation LETRA = ResourceLocation.fromNamespaceAndPath("ruleta", "dedsafio/letra.txt");
   private static boolean corriendo;
   private static long inicio;
   private static double inicioVideo = 139.0;
   private static double duracion = 303.12;
   private static float volumenCancion = 1.0F;
   private static final List<float[]> figuras = new ArrayList<>();
   private static final List<float[]> subs = new ArrayList<>();
   private static final List<String> textos = new ArrayList<>();
   private static final List<String> letra = new ArrayList<>();
   private static long pendiente = -1L;
   private static Boolean estabaDentro;

   private EscenaDedsafio() {
   }

   public static boolean corriendo() {
      return corriendo;
   }

   public static double segundoVideo() {
      return inicioVideo + (System.nanoTime() - inicio) / 1.0E9;
   }

   private static float suave(float x) {
      x = Math.max(0.0F, Math.min(1.0F, x));
      return x * x * (3.0F - 2.0F * x);
   }

   public static float[] figura() {
      if (!corriendo) {
         return null;
      } else {
         double v = segundoVideo();
         float mejor = 0.0F;
         float cual = -1.0F;

         for (float[] f : figuras) {
            if (!(v < f[1]) && !(v > f[4])) {
               float k;
               if (v < f[2]) {
                  k = suave((float)((v - f[1]) / (f[2] - f[1])));
               } else if (v < f[3]) {
                  k = 1.0F;
               } else {
                  k = 1.0F - suave((float)((v - f[3]) / (f[4] - f[3])));
               }

               if (k > mejor) {
                  mejor = k;
                  cual = f[0];
               }
            }
         }

         return new float[]{cual, mejor};
      }
   }

   public static float subtituloRestante() {
      if (!corriendo) {
         return 99.0F;
      } else {
         double v = segundoVideo();

         for (float[] s : subs) {
            if (v >= s[0] && v < s[1]) {
               return (float)(s[1] - v);
            }
         }

         return 99.0F;
      }
   }

   public static String subtitulo() {
      if (!corriendo) {
         return null;
      } else {
         double v = segundoVideo();

         for (int i = 0; i < subs.size(); i++) {
            float[] s = subs.get(i);
            if (v >= s[0] && v < s[1]) {
               String literal = i < textos.size() ? textos.get(i) : null;
               if (literal != null) {
                  return literal.isBlank() ? null : literal;
               }

               int idx = (int)s[2] - 1;
               if (idx >= 0 && idx < letra.size()) {
                  String t = parte(letra.get(idx), (int)s[3]);
                  return t != null && !t.isBlank() ? t : null;
               }

               return null;
            }
         }

         return null;
      }
   }

   private static String parte(String t, int cual) {
      if (cual != 0 && t != null) {
         int n = t.length();
         int mid = n / 2;
         int corte = -1;
         int mejor = Integer.MAX_VALUE;

         for (int i = 0; i < n; i++) {
            if (t.charAt(i) == ',' && i >= n * 0.35 && i <= n * 0.65 && Math.abs(i - mid) < mejor) {
               mejor = Math.abs(i - mid);
               corte = i + 1;
            }
         }

         if (corte < 0) {
            mejor = Integer.MAX_VALUE;

            for (int ix = 1; ix < n - 1; ix++) {
               if (t.charAt(ix) == ' ' && Math.abs(ix - mid) < mejor) {
                  mejor = Math.abs(ix - mid);
                  corte = ix;
               }
            }
         }

         if (corte < 0) {
            return t;
         } else {
            String a = t.substring(0, corte).strip();
            String b = t.substring(corte).strip();
            return cual == 1 ? a : b;
         }
      } else {
         return t;
      }
   }

   public static void cerrarOjo() {
      HudDedsafio.cerrar(() -> HudDedsafio.abrirSiNoCambia(8.0F));
   }

   public static void iniciarRemoto(float transcurrido) {
      AudioDedsafio.pararCanto();
      pendiente = System.nanoTime() - (long)(transcurrido * 1.0E9F);
   }

   private static void arrancarRemota(Minecraft mc, long inicioAbsoluto) {
      try {
         cargar(mc);
      } catch (Exception var5) {
         return;
      }

      double pasado = (System.nanoTime() - inicioAbsoluto) / 1.0E9;
      if (!(pasado > duracion + 1.0)) {
         parar();
         DedsafioCielo.figuraFija = -2;
         inicio = inicioAbsoluto;
         corriendo = true;
         if (pasado < 2.5) {
            AudioDedsafio.pararCanto();
            AudioDedsafio.iniciarCancion(volumenCancion);
         }
      }
   }

   public static void parar() {
      pendiente = -1L;
      corriendo = false;
      DedsafioCielo.figuraFija = -1;
      AudioDedsafio.pararCancion();
   }

   private static void cargar(Minecraft mc) throws Exception {
      figuras.clear();
      subs.clear();
      textos.clear();
      letra.clear();
      ResourceManager rm = mc.getResourceManager();

      try (BufferedReader r = new BufferedReader(new InputStreamReader(rm.getResourceOrThrow(ESCENA).open(), StandardCharsets.UTF_8))) {
         JsonObject o = JsonParser.parseReader(r).getAsJsonObject();
         inicioVideo = o.get("cancion_inicio_video").getAsDouble();
         duracion = o.get("duracion_cancion").getAsDouble();
         volumenCancion = o.has("volumen_cancion") ? o.get("volumen_cancion").getAsFloat() : 1.0F;

         for (JsonElement e : o.getAsJsonArray("figuras")) {
            JsonObject f = e.getAsJsonObject();
            figuras.add(
               new float[]{
                  f.get("figura").getAsFloat(), f.get("ini").getAsFloat(), f.get("plena").getAsFloat(), f.get("sale").getAsFloat(), f.get("fin").getAsFloat()
               }
            );
         }

         for (JsonElement e : o.getAsJsonArray("subtitulos")) {
            JsonObject s = e.getAsJsonObject();
            subs.add(
               new float[]{
                  s.get("ini").getAsFloat(),
                  s.get("fin").getAsFloat(),
                  s.has("linea") ? s.get("linea").getAsFloat() : subs.size() + 1,
                  s.has("parte") ? s.get("parte").getAsFloat() : 0.0F
               }
            );
            textos.add(s.has("texto") ? s.get("texto").getAsString() : null);
         }
      }

      String linea;
      try (BufferedReader r = new BufferedReader(new InputStreamReader(rm.getResourceOrThrow(LETRA).open(), StandardCharsets.UTF_8))) {
         while ((linea = r.readLine()) != null) {
            if (!linea.startsWith("#") && !linea.isBlank() && !linea.matches("^\\[\\d+].*")) {
               letra.add(linea.replace("|", "\n").strip());
            }
         }
      } catch (Exception var11) {
      }
   }

   public static void tick() {
      Minecraft mc = Minecraft.getInstance();
      boolean dentro = mc.level != null && DedsafioCielo.enDimension();
      if (estabaDentro != null && estabaDentro != dentro) {
         HudDedsafio.llegada();
      }

      if (Boolean.TRUE.equals(estabaDentro) && !dentro) {
         parar();
      }

      Boolean antes = estabaDentro;
      estabaDentro = mc.level == null ? null : dentro;
      if (pendiente >= 0L && dentro) {
         long p = pendiente;
         pendiente = -1L;
         arrancarRemota(mc, p);
      }

      if (dentro && !Boolean.TRUE.equals(antes) && !corriendo && pendiente < 0L) {
         AudioDedsafio.iniciarCanto();
      }

      if (!dentro) {
         AudioDedsafio.pararCanto();
      }

      if (!DedsafioCielo.visible()) {
         RayosDedsafio.liberar();
         DedsafioCielo.liberar();
      }

      if (corriendo) {
         boolean fin = segundoVideo() > inicioVideo + duracion + 1.0;
         if (fin || !dentro) {
            parar();
         }
      }
   }

   public static void limpiar() {
      parar();
      pendiente = -1L;
      RayosDedsafio.liberar();
      DedsafioCielo.liberar();
      estabaDentro = null;
   }
}
