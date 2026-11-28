package com.ghostsafio.mitomod.client;

import com.ghostsafio.mitomod.Ghostsafio;
import com.mojang.renderpearl.backend.opengl.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.nio.file.Files;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.fabricmc.loader.api.FabricLoader;


@Environment(EnvType.CLIENT)
public final class EclipseShaders {





   private static boolean enabled = false;

   private EclipseShaders() {
   }

   public static void register() {
      // efecto de dibujo pendiente de portar a 26.3
   }

   public static boolean ok(Object shader) {
      // efecto de dibujo pendiente de portar a 26.3
      return false;
   }

   public static float time() {
      return (float)(System.nanoTime() / 1.0E9 % 4000.0);
   }

   public static void params(Object shader, float y, float z, float w) {
      // efecto de dibujo pendiente de portar a 26.3
   }
}
