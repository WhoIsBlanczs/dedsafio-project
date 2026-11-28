package com.watonstudio.creepermetalico.client;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
   private static ShaderInstance program = null;
   private static boolean warned = false;

   private EclipseSkyRenderer() {
   }

   public static void registerShader() {
      CoreShaderRegistrationCallback.EVENT
         .register(
            (CoreShaderRegistrationCallback)context -> context.register(
               ResourceLocation.fromNamespaceAndPath("watonstudio", "eclipse"), DefaultVertexFormat.POSITION, p -> program = p
            )
         );
   }

   public static void render(Matrix4f view, float tickDelta, Camera camera, boolean thickFog) {
      if (ClientEclipse.isRunning() && ClientEclipse.inOverworld()) {
         if (!thickFog && camera.getFluidInCamera() == FogType.NONE) {
            if (program == null) {
               warnOnce("[Eclipse] El shader no cargo: revisa assets/eclipse/shaders/core/ (eclipse.json, .vsh y .fsh) y el log.");
            } else {
               ClientLevel world = Minecraft.getInstance().level;
               if (world != null) {
                  ClientEclipse.Look look = ClientEclipse.look(tickDelta);
                  if (!(look.skyA() <= 0.001F)) {
                     float time = (float)world.getGameTime() + tickDelta;
                     float theta = world.getTimeOfDay(tickDelta) * (float) (Math.PI * 2);
                     float sin = (float)Math.sin(theta);
                     float cos = (float)Math.cos(theta);
                     float sx = -sin;
                     float sy = cos;
                     float sz = 0.0F;
                     float lx = -cos * 0.6222F;
                     float ly = -sin * 0.6222F;
                     float lz = 0.7828F;
                     Vec3 sky = world.getSkyColor(camera.getPosition(), tickDelta);
                     float cr = (float)sky.x;
                     float cg = (float)sky.y;
                     float cb = (float)sky.z;
                     float hr = cr + (0.78F - cr) * 0.55F;
                     float hg = cg + (0.86F - cg) * 0.55F;
                     float hb = cb + (1.0F - cb) * 0.55F;
                     set1("Tiempo", time);
                     set3("SolDir", sx, sy, sz);
                     set3("Lado", lx, ly, lz);
                     set3("Cielo", cr, cg, cb);
                     set3("Horizonte", hr, hg, hb);
                     set4("Fase", look.s(), look.e(), look.flare(), look.skyA());
                     set2("Brillo", look.glow(), look.c());
                     Matrix4fStack stack = RenderSystem.getModelViewStack();
                     stack.pushMatrix();
                     stack.set(view);
                     RenderSystem.applyModelViewMatrix();
                     RenderSystem.setShader(() -> program);
                     RenderSystem.disableCull();
                     RenderSystem.depthMask(false);

                     try {
                        BufferBuilder b = Tesselator.getInstance().begin(Mode.TRIANGLES, DefaultVertexFormat.POSITION);
                        cube(b, 100.0F);
                        BufferUploader.drawWithShader(b.buildOrThrow());
                     } finally {
                        stack.popMatrix();
                        RenderSystem.applyModelViewMatrix();
                        RenderSystem.enableCull();
                        RenderSystem.depthMask(true);
                     }
                  }
               }
            }
         }
      }
   }

   private static void warnOnce(String msg) {
      if (!warned) {
         warned = true;
         Minecraft.getInstance().gui.getChat().addMessage(Component.literal(msg));
      }
   }

   private static void set1(String name, float a) {
      Uniform u = program.getUniform(name);
      if (u != null) {
         u.set(a);
      }
   }

   private static void set2(String name, float a, float b) {
      Uniform u = program.getUniform(name);
      if (u != null) {
         u.set(a, b);
      }
   }

   private static void set3(String name, float a, float b, float c) {
      Uniform u = program.getUniform(name);
      if (u != null) {
         u.set(a, b, c);
      }
   }

   private static void set4(String name, float a, float b, float c, float d) {
      Uniform u = program.getUniform(name);
      if (u != null) {
         u.setMat2x2(a, b, c, d);
      }
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
