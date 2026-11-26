package net.ruleta.tragamonedas;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.ruleta.Ruleta;

public final class ConfigTragamonedas {
   public static final String COMODIN = "*";
   public static final String TIPO_COMBINACION = "combinacion";
   public static final String TIPO_IGUALES = "iguales";
   public static final String MODO_TODAS = "todas";
   public static final String MODO_UNA = "una";
   public static final String LIBRO = "minecraft:enchanted_book";
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final Gson GSON_COMPACTO = new Gson();
   private static ConfigTragamonedas actual;
   public String pago_item = "ruleta:dedita_casino";
   public int pago_cantidad = 1;
   public List<ConfigTragamonedas.Simbolo> simbolos = new ArrayList<>();
   public List<ConfigTragamonedas.Premio> premios = new ArrayList<>();
   public static final String DEDITA = "ruleta:dedita_casino";

   private static ConfigTragamonedas porDefecto() {
      ConfigTragamonedas c = new ConfigTragamonedas();
      c.simbolos.add(new ConfigTragamonedas.Simbolo("minecraft:iron_ingot", 30));
      c.simbolos.add(new ConfigTragamonedas.Simbolo("minecraft:gold_ingot", 24));
      c.simbolos.add(new ConfigTragamonedas.Simbolo("minecraft:emerald", 16));
      c.simbolos.add(new ConfigTragamonedas.Simbolo("minecraft:diamond", 10));
      c.simbolos.add(new ConfigTragamonedas.Simbolo("ruleta:dedita_casino", 8));
      c.premios
         .add(
            new ConfigTragamonedas.Premio(
               "ruleta:dedita_casino", "ruleta:dedita_casino", "ruleta:dedita_casino", new ConfigTragamonedas.Recompensa("ruleta:dedita_casino", 20)
            )
         );
      c.premios
         .add(
            new ConfigTragamonedas.Premio(
               "minecraft:diamond", "minecraft:diamond", "minecraft:diamond", new ConfigTragamonedas.Recompensa("minecraft:diamond", 6)
            )
         );
      c.premios
         .add(
            new ConfigTragamonedas.Premio(
               "minecraft:emerald", "minecraft:emerald", "minecraft:emerald", new ConfigTragamonedas.Recompensa("minecraft:emerald", 8)
            )
         );
      c.premios
         .add(
            new ConfigTragamonedas.Premio(
               "minecraft:gold_ingot", "minecraft:gold_ingot", "minecraft:gold_ingot", new ConfigTragamonedas.Recompensa("minecraft:gold_ingot", 6)
            )
         );
      c.premios
         .add(
            new ConfigTragamonedas.Premio(
               "minecraft:iron_ingot", "minecraft:iron_ingot", "minecraft:iron_ingot", new ConfigTragamonedas.Recompensa("minecraft:iron_ingot", 4)
            )
         );
      c.premios
         .add(new ConfigTragamonedas.Premio("ruleta:dedita_casino", "ruleta:dedita_casino", "*", new ConfigTragamonedas.Recompensa("ruleta:dedita_casino", 2)));
      return c;
   }

   public static List<String> carasPorDefecto() {
      List<String> l = new ArrayList<>();

      for (ConfigTragamonedas.Simbolo s : porDefecto().simbolos) {
         l.add(s.item);
      }

      return l;
   }

   public static ConfigTragamonedas copiaDefecto() {
      return porDefecto();
   }

   public String aJson() {
      return GSON.toJson(this);
   }

   public String aJsonCompacto() {
      return GSON_COMPACTO.toJson(this);
   }

   public static ConfigTragamonedas desdeJson(String json) {
      try {
         ConfigTragamonedas c = (ConfigTragamonedas)GSON.fromJson(json, ConfigTragamonedas.class);
         return c == null ? null : sanear(c);
      } catch (Exception var2) {
         return null;
      }
   }

   public static boolean itemExiste(String id) {
      ResourceLocation rl = id == null ? null : ResourceLocation.tryParse(id);
      return rl != null && BuiltInRegistries.ITEM.containsKey(rl);
   }

   public static ConfigTragamonedas sanear(ConfigTragamonedas c) {
      List<ConfigTragamonedas.Simbolo> sim = new ArrayList<>();
      if (c.simbolos != null) {
         for (ConfigTragamonedas.Simbolo s : c.simbolos) {
            if (s != null && itemExiste(s.item) && sim.size() < 8) {
               s.peso = Math.max(1, Math.min(1000, s.peso));
               sim.add(s);
            }
         }
      }

      if (sim.isEmpty()) {
         sim = porDefecto().simbolos;
      }

      c.simbolos = sim;
      List<ConfigTragamonedas.Premio> prem = new ArrayList<>();
      if (c.premios != null) {
         for (ConfigTragamonedas.Premio p : c.premios) {
            if (p != null && prem.size() < 40) {
               p.tipo = "iguales".equals(p.tipo) ? "iguales" : "combinacion";
               p.modo = "una".equals(p.modo) ? "una" : "todas";
               p.iguales = p.iguales >= 3 ? 3 : 2;
               List<String> comb = new ArrayList<>();

               for (int i = 0; i < 3; i++) {
                  String x = p.combinacion != null && i < p.combinacion.size() ? p.combinacion.get(i) : "*";
                  comb.add(!"*".equals(x) && !itemExiste(x) ? "*" : x);
               }

               p.combinacion = comb;
               List<String> validos = new ArrayList<>();
               if (p.items != null) {
                  for (String it : p.items) {
                     if (itemExiste(it) && !validos.contains(it) && validos.size() < 8) {
                        validos.add(it);
                     }
                  }
               }

               p.items = validos;
               List<ConfigTragamonedas.Recompensa> rec = new ArrayList<>();
               if (p.recompensas != null) {
                  for (ConfigTragamonedas.Recompensa r : p.recompensas) {
                     if (r != null && itemExiste(r.item) && r.cantidad > 0 && rec.size() < 6) {
                        int tope = r.esLibro() ? 64 : 2304;
                        r.cantidad = Math.min(tope, r.cantidad);
                        r.maximo = Math.max(r.cantidad, Math.min(tope, r.maximo));
                        r.peso = Math.max(1, Math.min(1000, r.peso));
                        List<ConfigTragamonedas.Encantamiento> enc = new ArrayList<>();
                        if (r.esLibro() && r.encantamientos != null) {
                           for (ConfigTragamonedas.Encantamiento e : r.encantamientos) {
                              if (e != null && e.id != null && ResourceLocation.tryParse(e.id) != null && enc.size() < 80) {
                                 e.nivelMin = Math.max(1, Math.min(255, e.nivelMin));
                                 e.nivelMax = Math.max(e.nivelMin, Math.min(255, e.nivelMax));
                                 enc.add(e);
                              }
                           }
                        }

                        r.encantamientos = enc;
                        rec.add(r);
                     }
                  }
               }

               if (!rec.isEmpty()) {
                  p.recompensas = rec;
                  prem.add(p);
               }
            }
         }
      }

      c.premios = prem;
      if (!itemExiste(c.pago_item)) {
         c.pago_item = "ruleta:dedita_casino";
      }

      c.pago_cantidad = Math.max(1, Math.min(64, c.pago_cantidad));
      return c;
   }

   public static synchronized void guardar(ConfigTragamonedas c) {
      actual = c;
      Path ruta = FabricLoader.getInstance().getConfigDir().resolve("ruleta_tragamonedas.json");

      try (Writer w = Files.newBufferedWriter(ruta)) {
         GSON.toJson(c, w);
      } catch (IOException var7) {
         Ruleta.LOG.error("No se pudo guardar {}", ruta, var7);
      }
   }

   public static synchronized ConfigTragamonedas get() {
      if (actual == null) {
         actual = cargar();
      }

      return actual;
   }

   public static synchronized void recargar() {
      actual = cargar();
   }

   private static ConfigTragamonedas cargar() {
      Path ruta = FabricLoader.getInstance().getConfigDir().resolve("ruleta_tragamonedas.json");
      ConfigTragamonedas c = null;
      if (Files.exists(ruta)) {
         try (Reader r = Files.newBufferedReader(ruta)) {
            c = (ConfigTragamonedas)GSON.fromJson(r, ConfigTragamonedas.class);
         } catch (Exception var11) {
            Ruleta.LOG.error("No se pudo leer {}; se usa la configuración por defecto", ruta, var11);
         }
      }

      boolean nueva = c == null;
      if (nueva) {
         c = porDefecto();

         try (Writer w = Files.newBufferedWriter(ruta)) {
            GSON.toJson(c, w);
         } catch (IOException var9) {
            Ruleta.LOG.error("No se pudo crear {}", ruta, var9);
         }
      }

      if (c.simbolos == null || c.simbolos.isEmpty()) {
         c.simbolos = porDefecto().simbolos;
      }

      if (c.simbolos.size() > 8) {
         c.simbolos = new ArrayList<>(c.simbolos.subList(0, 8));
      }

      if (c.premios == null) {
         c.premios = new ArrayList<>();
      }

      return sanear(c);
   }

   public static class Encantamiento {
      public String id;
      public int nivelMin = 1;
      public int nivelMax = 1;

      public Encantamiento() {
      }

      public Encantamiento(String id, int min, int max) {
         this.id = id;
         this.nivelMin = min;
         this.nivelMax = max;
      }
   }

   public static class Premio {
      public String tipo = "combinacion";
      public List<String> combinacion = new ArrayList<>();
      public int iguales = 2;
      public List<String> items = new ArrayList<>();
      public String modo = "todas";
      public List<ConfigTragamonedas.Recompensa> recompensas = new ArrayList<>();

      public Premio() {
      }

      public Premio(String a, String b, String c, ConfigTragamonedas.Recompensa... r) {
         this.combinacion.add(a);
         this.combinacion.add(b);
         this.combinacion.add(c);
         this.recompensas.addAll(List.of(r));
      }
   }

   public static class Recompensa {
      public String item;
      public int cantidad = 1;
      public int maximo = 1;
      public int peso = 1;
      public List<ConfigTragamonedas.Encantamiento> encantamientos = new ArrayList<>();

      public Recompensa() {
      }

      public Recompensa(String item, int cantidad) {
         this.item = item;
         this.cantidad = cantidad;
         this.maximo = cantidad;
      }

      public boolean esLibro() {
         return "minecraft:enchanted_book".equals(this.item);
      }
   }

   public static class Simbolo {
      public String item;
      public int peso = 1;

      public Simbolo() {
      }

      public Simbolo(String item, int peso) {
         this.item = item;
         this.peso = peso;
      }
   }
}
