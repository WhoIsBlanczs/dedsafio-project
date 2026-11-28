package net.ruleta.estatua;

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
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.ruleta.Ruleta;
import net.ruleta.tragamonedas.ConfigTragamonedas;

public final class ConfigPedestal {
   public static final int MAX_TRATOS = 40;
   public static final int VERSION = 2;
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final Gson GSON_COMPACTO = new Gson();
   private static ConfigPedestal actual;
   public int version = 0;
   public List<ConfigPedestal.Trato> tratos = new ArrayList<>();

   private static ConfigPedestal porDefecto() {
      ConfigPedestal c = new ConfigPedestal();
      c.version = 2;
      c.tratos.add(new ConfigPedestal.Trato("ruletafanproject:dedita_azul", 2, "ruletafanproject:dedita_casino", 1));
      c.tratos.add(new ConfigPedestal.Trato("ruletafanproject:dedita_azul", 2, "ruletafanproject:dilitio", 1));
      return c;
   }

   public static ConfigPedestal copiaDefecto() {
      return porDefecto();
   }

   public String aJson() {
      return GSON.toJson(this);
   }

   public String aJsonCompacto() {
      return GSON_COMPACTO.toJson(this);
   }

   public static ConfigPedestal desdeJson(String json) {
      try {
         ConfigPedestal c = (ConfigPedestal)GSON.fromJson(json, ConfigPedestal.class);
         return c == null ? null : sanear(c);
      } catch (Exception e) {
         return null;
      }
   }

   public static Item item(String id) {
      Identifier rl = id == null ? null : Identifier.tryParse(id);
      return rl == null ? null : (Item)BuiltInRegistries.ITEM.getOptional(rl).orElse(null);
   }

   public static ConfigPedestal sanear(ConfigPedestal c) {
      List<ConfigPedestal.Trato> ok = new ArrayList<>();
      if (c.tratos != null) {
         for (ConfigPedestal.Trato t : c.tratos) {
            if (t != null && ok.size() < 40 && ConfigTragamonedas.itemExiste(t.da_item) && ConfigTragamonedas.itemExiste(t.recibe_item)) {
               t.da_cantidad = Math.max(1, Math.min(item(t.da_item).getDefaultMaxStackSize(), t.da_cantidad));
               t.recibe_cantidad = Math.max(1, Math.min(item(t.recibe_item).getDefaultMaxStackSize(), t.recibe_cantidad));
               ok.add(t);
            }
         }
      }

      c.tratos = ok;
      c.version = 2;
      return c;
   }

   public static synchronized void guardar(ConfigPedestal c) {
      actual = c;
      Path ruta = FabricLoader.getInstance().getConfigDir().resolve("ruleta_pedestal.json");

      try (Writer w = Files.newBufferedWriter(ruta)) {
         GSON.toJson(c, w);
      } catch (IOException e) {
         Ruleta.LOG.error("No se pudo guardar {}", ruta, e);
      }
   }

   public static synchronized ConfigPedestal get() {
      if (actual == null) {
         actual = cargar();
      }

      return actual;
   }

   private static ConfigPedestal cargar() {
      Path ruta = FabricLoader.getInstance().getConfigDir().resolve("ruleta_pedestal.json");
      ConfigPedestal c = null;
      if (Files.exists(ruta)) {
         try (Reader r = Files.newBufferedReader(ruta)) {
            c = (ConfigPedestal)GSON.fromJson(r, ConfigPedestal.class);
         } catch (Exception e) {
            Ruleta.LOG.error("No se pudo leer {}; se usa la configuración por defecto", ruta, e);
         }
      }

      if (c != null && c.version < 2) {
         if (c.tratos == null) {
            c.tratos = new ArrayList<>();
         }

         boolean yaEsta = false;

         for (ConfigPedestal.Trato t : c.tratos) {
            if (t != null && "ruletafanproject:dilitio".equals(t.recibe_item)) {
               yaEsta = true;
            }
         }

         if (!yaEsta) {
            c.tratos.add(new ConfigPedestal.Trato("ruletafanproject:dedita_azul", 2, "ruletafanproject:dilitio", 1));
         }

         c = sanear(c);

         try (Writer w = Files.newBufferedWriter(ruta)) {
            GSON.toJson(c, w);
         } catch (IOException e) {
            Ruleta.LOG.error("No se pudo actualizar {}", ruta, e);
         }
      }

      if (c == null) {
         c = porDefecto();

         try (Writer w = Files.newBufferedWriter(ruta)) {
            GSON.toJson(c, w);
         } catch (IOException e) {
            Ruleta.LOG.error("No se pudo crear {}", ruta, e);
         }
      }

      return sanear(c);
   }

   public static class Trato {
      public String da_item;
      public int da_cantidad = 1;
      public String recibe_item;
      public int recibe_cantidad = 1;

      public Trato() {
      }

      public Trato(String daItem, int daCantidad, String recibeItem, int recibeCantidad) {
         this.da_item = daItem;
         this.da_cantidad = daCantidad;
         this.recibe_item = recibeItem;
         this.recibe_cantidad = recibeCantidad;
      }
   }
}
