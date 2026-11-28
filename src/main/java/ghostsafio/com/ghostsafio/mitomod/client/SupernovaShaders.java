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
public final class SupernovaShaders {
   public static ShaderInstance SUN;
   public static ShaderInstance CORONA;
   public static ShaderInstance FIRE;
   public static ShaderInstance NEBULA;
   public static ShaderInstance RING;
   public static ShaderInstance AURORA;
   public static ShaderInstance STAR;
   public static ShaderInstance RAYS;
   public static ShaderInstance SHOCK;
   private static boolean enabled = false;

   private SupernovaShaders() {
   }

   public static void register() {
      try {
         if (Files.exists(FabricLoader.getInstance().getConfigDir().resolve("ghostsafio_sin_shaders.txt"))) {
            Ghostsafio.LOGGER.info("Supernova: shaders apagados (existe config/ghostsafio_sin_shaders.txt)");
            return;
         }
      } catch (RuntimeException e) {
         return;
      }

      enabled = true;
      CoreShaderRegistrationCallback.EVENT.register((CoreShaderRegistrationCallback)context -> {
         context.register(Ghostsafio.id("sn_sun"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> SUN = s);
         context.register(Ghostsafio.id("sn_corona"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> CORONA = s);
         context.register(Ghostsafio.id("sn_fire"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> FIRE = s);
         context.register(Ghostsafio.id("sn_nebula"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> NEBULA = s);
         context.register(Ghostsafio.id("sn_ring"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> RING = s);
         context.register(Ghostsafio.id("sn_aurora"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> AURORA = s);
         context.register(Ghostsafio.id("sn_star"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> STAR = s);
         context.register(Ghostsafio.id("sn_rays"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> RAYS = s);
         context.register(Ghostsafio.id("sn_shock"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> SHOCK = s);
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
