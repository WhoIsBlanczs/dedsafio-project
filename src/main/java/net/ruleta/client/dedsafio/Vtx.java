package net.ruleta.client.dedsafio;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

final class Vtx {
   private static Matrix4fStack pila;

   private Vtx() {
   }

   static Identifier rl(String espacio, String ruta) {
      return Identifier.fromNamespaceAndPath(espacio, ruta);
   }

   static BufferBuilder quads() {
      return Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
   }

   static void v(BufferBuilder b, float x, float y, float z, float u, float w) {
      b.addVertex(x, y, z).setUv(u, w);
   }

   static void dibujar(BufferBuilder b) {
      BufferUploader.drawWithShader(b.buildOrThrow());
   }

   static void empujarVista(Matrix4f vista) {
      pila = RenderSystem.getModelViewStack();
      pila.pushMatrix();
      pila.identity();
      pila.mul(vista);
      RenderSystem.applyModelViewMatrix();
   }

   static void sacarVista() {
      if (pila != null) {
         pila.popMatrix();
         pila = null;
         RenderSystem.applyModelViewMatrix();
      }
   }
}
