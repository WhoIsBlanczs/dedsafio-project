package com.supernova.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.supernova.SupernovaMod;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.Identifier;

public class ClientSetup {
   private static Object sphere;
   private static Object additive;
   private static Object multiply;

   public static Object getSphere() {
      return sphere;
   }

   public static Object getAdditive() {
      return additive;
   }

   public static Object getMultiply() {
      return multiply;
   }

   public static void registrar() { }
}
