package net.ruleta;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

final class ConfigArchivo {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

   private ConfigArchivo() {
   }

   private static Path rutaConfig() {
      return FabricLoader.getInstance().getConfigDir().resolve("ruleta.json");
   }

   static Path ruta() {
      MinecraftServer server = Servidor.actual();
      return server == null ? rutaConfig() : server.getWorldPath(LevelResource.ROOT).resolve("ruleta.json");
   }

   private static JsonObject leer(Path path) {
      try {
         return !Files.exists(path) ? null : JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
      } catch (Exception var2) {
         return null;
      }
   }

   static JsonObject seccion(String clave) throws IOException {
      JsonObject mundo = leer(ruta());
      if (mundo != null && mundo.has(clave) && mundo.get(clave).isJsonObject()) {
         return mundo.getAsJsonObject(clave);
      } else {
         JsonObject antiguo = leer(rutaConfig());
         return antiguo != null && antiguo.has(clave) && antiguo.get(clave).isJsonObject() ? antiguo.getAsJsonObject(clave) : null;
      }
   }

   static void guardar(String clave, JsonObject seccion) throws IOException {
      Path path = ruta();
      JsonObject root = leer(path);
      if (root == null) {
         root = new JsonObject();
      }

      root.add(clave, seccion);
      Files.createDirectories(path.getParent());
      Files.writeString(path, GSON.toJson(root), StandardCharsets.UTF_8);
   }
}
