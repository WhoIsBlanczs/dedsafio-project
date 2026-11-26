package com.supernova.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.supernova.SupernovaMod;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.ResourceLocation;

public class ClientSetup {
   private static ShaderInstance sphere;
   private static ShaderInstance additive;
   private static ShaderInstance multiply;

   public static ShaderInstance getSphere() {
      return sphere;
   }

   public static ShaderInstance getAdditive() {
      return additive;
   }

   public static ShaderInstance getMultiply() {
      return multiply;
   }

   public static void registrar() {
      CoreShaderRegistrationCallback.EVENT.register((CoreShaderRegistrationCallback)ctx -> {
         ctx.register(ResourceLocation.fromNamespaceAndPath("supernova", "supernova"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> sphere = s);
         ctx.register(ResourceLocation.fromNamespaceAndPath("supernova", "supernova_add"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> additive = s);
         ctx.register(ResourceLocation.fromNamespaceAndPath("supernova", "supernova_mul"), DefaultVertexFormat.POSITION_TEX_COLOR, s -> multiply = s);
      });
      EntityRendererRegistry.register(SupernovaMod.SUPERNOVA.get(), NoopRenderer::new);
   }
}
