package com.ghostsafio.mitomod.client;

import com.ghostsafio.mitomod.Ghostsafio;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.nio.file.Files;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.ShaderInstance;

@Environment(EnvType.CLIENT)
public final class EclipseShaders {
   public static ShaderInstance SUN;
   public static ShaderInstance MOON;
   public static ShaderInstance CORONA;
   public static ShaderInstance RIM;
   public static ShaderInstance FLARE;
   private static boolean enabled = false;

   private EclipseShaders() {
   }

   public static void register() {
      try {
         if (Files.exists(FabricLoader.getInstance().getConfigDir().resolve("ghostsafio_sin_shaders.txt"))) {
            return;
         }
      } catch (RuntimeException e) {
         return;
      }

      enabled = true;
      CoreShaderRegistrationCallback.EVENT.register((CoreShaderRegistrationCallback)context -> {
         context.register(Ghostsafio.id("ec_sun"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> SUN = s);
         context.register(Ghostsafio.id("ec_moon"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> MOON = s);
         context.register(Ghostsafio.id("ec_corona"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> CORONA = s);
         context.register(Ghostsafio.id("ec_rim"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> RIM = s);
         context.register(Ghostsafio.id("ec_flare"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> FLARE = s);
      });
   }

   public static boolean ok(ShaderInstance shader) {
      return enabled && shader != null;
   }

   public static float time() {
      return (float)(System.nanoTime() / 1.0E9 % 4000.0);
   }

   public static void params(ShaderInstance shader, float y, float z, float w) {
      Uniform u = shader.getUniform("Params");
      if (u != null) {
         u.set(time(), y, z, w);
      }
   }
}
