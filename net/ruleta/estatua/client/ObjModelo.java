package net.ruleta.estatua.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ObjModelo {
   private static final Logger LOG = LoggerFactory.getLogger("ruleta");
   private static final String RUTA_MODELO = "models/estatua_nutria/nutria_pedestal.obj";
   private static final String CARPETA_TEXTURAS = "textures/block/estatua_nutria/";
   private final List<ObjModelo.Grupo> grupos;
   private static ResourceManager ultimoGestor;
   private static ObjModelo cache = new ObjModelo(List.of());

   public List<ObjModelo.Grupo> grupos() {
      return this.grupos;
   }

   private ObjModelo(List<ObjModelo.Grupo> grupos) {
      this.grupos = grupos;
   }

   public static synchronized ObjModelo obtener() {
      ResourceManager gestor = Minecraft.getInstance().getResourceManager();
      if (gestor != ultimoGestor) {
         ultimoGestor = gestor;

         try {
            cache = cargar(gestor);
         } catch (Exception var2) {
            LOG.error("No se pudo leer el modelo OBJ de la estatua de nutria", var2);
            cache = new ObjModelo(List.of());
         }
      }

      return cache;
   }

   private static List<String> lineas(ResourceManager gestor, ResourceLocation id) throws Exception {
      Optional<Resource> recurso = gestor.getResource(id);
      if (recurso.isEmpty()) {
         throw new IllegalStateException("Falta " + id);
      } else {
         List<String> salida = new ArrayList<>();

         String linea;
         try (BufferedReader lector = new BufferedReader(new InputStreamReader(recurso.get().open(), StandardCharsets.UTF_8))) {
            while ((linea = lector.readLine()) != null) {
               salida.add(linea.trim());
            }
         }

         return salida;
      }
   }

   private static ObjModelo cargar(ResourceManager gestor) throws Exception {
      ResourceLocation rutaObj = ResourceLocation.fromNamespaceAndPath("ruleta", "models/estatua_nutria/nutria_pedestal.obj");
      List<float[]> pos = new ArrayList<>();
      List<float[]> uv = new ArrayList<>();
      List<float[]> nor = new ArrayList<>();
      Map<String, String> texturaDeMaterial = new HashMap<>();
      Map<String, List<float[]>> porMaterial = new LinkedHashMap<>();
      String actual = null;

      for (String l : lineas(gestor, rutaObj)) {
         if (!l.isEmpty() && !l.startsWith("#")) {
            String[] p = l.split("\\s+");
            String archivo = p[0];
            switch (archivo) {
               case "mtllib":
                  int corte = "models/estatua_nutria/nutria_pedestal.obj".lastIndexOf(47);
                  ResourceLocation rutaMtl = ResourceLocation.fromNamespaceAndPath(
                     "ruleta", "models/estatua_nutria/nutria_pedestal.obj".substring(0, corte + 1) + p[1]
                  );
                  leerMtl(gestor, rutaMtl, texturaDeMaterial);
                  break;
               case "v":
                  pos.add(new float[]{Float.parseFloat(p[1]), Float.parseFloat(p[2]), Float.parseFloat(p[3])});
                  break;
               case "vt":
                  uv.add(new float[]{Float.parseFloat(p[1]), Float.parseFloat(p[2])});
                  break;
               case "vn":
                  nor.add(new float[]{Float.parseFloat(p[1]), Float.parseFloat(p[2]), Float.parseFloat(p[3])});
                  break;
               case "usemtl":
                  actual = p[1];
                  break;
               case "f":
                  int n = p.length - 1;
                  float[][] v = new float[n][];

                  for (int i = 0; i < n; i++) {
                     v[i] = vertice(p[i + 1], pos, uv, nor);
                  }

                  List<float[]> lista = porMaterial.computeIfAbsent(actual, kx -> new ArrayList<>());
                  if (n == 3) {
                     lista.add(cara(v[0], v[1], v[2], v[2]));
                  } else if (n == 4) {
                     lista.add(cara(v[0], v[1], v[2], v[3]));
                  } else {
                     for (int i = 1; i + 1 < n; i++) {
                        lista.add(cara(v[0], v[i], v[i + 1], v[i + 1]));
                     }
                  }
            }
         }
      }

      List<ObjModelo.Grupo> grupos = new ArrayList<>();

      for (Entry<String, List<float[]>> e : porMaterial.entrySet()) {
         String archivo = texturaDeMaterial.get(e.getKey());
         if (archivo != null) {
            float[] datos = new float[e.getValue().size() * 32];
            int k = 0;

            for (float[] cara : e.getValue()) {
               System.arraycopy(cara, 0, datos, k, 32);
               k += 32;
            }

            grupos.add(new ObjModelo.Grupo(ResourceLocation.fromNamespaceAndPath("ruleta", "textures/block/estatua_nutria/" + archivo), datos));
         }
      }

      return new ObjModelo(grupos);
   }

   private static void leerMtl(ResourceManager gestor, ResourceLocation ruta, Map<String, String> texturas) throws Exception {
      String material = null;

      for (String l : lineas(gestor, ruta)) {
         if (!l.isEmpty() && !l.startsWith("#")) {
            String[] p = l.split("\\s+");
            if (p[0].equals("newmtl") && p.length > 1) {
               material = p[1];
            } else if (p[0].equalsIgnoreCase("map_Kd") && material != null && p.length > 1) {
               String archivo = p[p.length - 1].replace('\\', '/');
               texturas.put(material, archivo.substring(archivo.lastIndexOf(47) + 1));
            }
         }
      }
   }

   private static float[] vertice(String ref, List<float[]> pos, List<float[]> uv, List<float[]> nor) {
      String[] t = ref.split("/", -1);
      float[] a = pos.get(indice(t[0], pos.size()));
      float u = 0.0F;
      float v = 0.0F;
      if (t.length > 1 && !t[1].isEmpty()) {
         float[] b = uv.get(indice(t[1], uv.size()));
         u = b[0];
         v = 1.0F - b[1];
      }

      float nx = Float.NaN;
      float ny = 0.0F;
      float nz = 0.0F;
      if (t.length > 2 && !t[2].isEmpty()) {
         float[] c = nor.get(indice(t[2], nor.size()));
         nx = c[0];
         ny = c[1];
         nz = c[2];
      }

      return new float[]{a[0], a[1], a[2], u, v, nx, ny, nz};
   }

   private static int indice(String texto, int total) {
      int i = Integer.parseInt(texto);
      return i > 0 ? i - 1 : total + i;
   }

   private static float[] cara(float[] a, float[] b, float[] c, float[] d) {
      float[] fin = new float[32];
      float[][] vs = new float[][]{a, b, c, d};
      float[] plana = null;

      for (int i = 0; i < 4; i++) {
         float[] v = vs[i];
         float nx = v[5];
         float ny = v[6];
         float nz = v[7];
         if (Float.isNaN(nx)) {
            if (plana == null) {
               plana = normal(a, b, c);
            }

            nx = plana[0];
            ny = plana[1];
            nz = plana[2];
         }

         System.arraycopy(new float[]{v[0], v[1], v[2], v[3], v[4], nx, ny, nz}, 0, fin, i * 8, 8);
      }

      return fin;
   }

   private static float[] normal(float[] a, float[] b, float[] c) {
      float ux = b[0] - a[0];
      float uy = b[1] - a[1];
      float uz = b[2] - a[2];
      float vx = c[0] - a[0];
      float vy = c[1] - a[1];
      float vz = c[2] - a[2];
      float x = uy * vz - uz * vy;
      float y = uz * vx - ux * vz;
      float z = ux * vy - uy * vx;
      float largo = (float)Math.sqrt(x * x + y * y + z * z);
      return largo < 1.0E-6F ? new float[]{0.0F, 1.0F, 0.0F} : new float[]{x / largo, y / largo, z / largo};
   }

   public record Grupo(ResourceLocation textura, float[] datos) {
   }
}
