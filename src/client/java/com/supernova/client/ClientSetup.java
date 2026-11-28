package com.supernova.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.supernova.SupernovaMod;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.Identifier;

public class ClientSetup {




   public static Object getSphere() {
      // efecto de dibujo pendiente de portar a 26.3
      return null;
   }

   public static Object getAdditive() {
      // efecto de dibujo pendiente de portar a 26.3
      return null;
   }

   public static Object getMultiply() {
      // efecto de dibujo pendiente de portar a 26.3
      return null;
   }

   public static void registrar() {
      net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(SupernovaMod.SUPERNOVA.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
   }
}
