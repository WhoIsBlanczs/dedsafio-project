package com.watonstudio.creepermetalico.client;

import com.mojang.renderpearl.backend.opengl.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

@Environment(EnvType.CLIENT)
public final class EclipseSkyRenderer {
   private static final float TWO_PI = (float) (Math.PI * 2);
   private static final float CUBE = 100.0F;
   private static final float LADO_PATH = 0.6222F;
   private static final float LADO_Z = 0.7828F;
   private static Object program = null;
   private static boolean warned = false;

   private EclipseSkyRenderer() {
   }

   public static void registerShader() {
      // efecto de dibujo pendiente de portar a 26.3
   }

   public static void render(Matrix4f view, float tickDelta, Camera camera, boolean thickFog) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void warnOnce(String msg) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void set1(String name, float a) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void set2(String name, float a, float b) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void set3(String name, float a, float b, float c) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void set4(String name, float a, float b, float c, float d) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void cube(BufferBuilder b, float s) {
      float[][] p = new float[][]{{-s, -s, -s}, {s, -s, -s}, {s, s, -s}, {-s, s, -s}, {-s, -s, s}, {s, -s, s}, {s, s, s}, {-s, s, s}};
      int[][] faces = new int[][]{{0, 1, 2, 3}, {5, 4, 7, 6}, {4, 0, 3, 7}, {1, 5, 6, 2}, {3, 2, 6, 7}, {4, 5, 1, 0}};

      for (int[] f : faces) {
         vert(b, p[f[0]]);
         vert(b, p[f[1]]);
         vert(b, p[f[2]]);
         vert(b, p[f[0]]);
         vert(b, p[f[2]]);
         vert(b, p[f[3]]);
      }
   }

   private static void vert(BufferBuilder b, float[] v) {
      b.addVertex(v[0], v[1], v[2]);
   }
}
