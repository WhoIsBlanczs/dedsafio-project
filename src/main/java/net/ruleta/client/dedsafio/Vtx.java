package net.ruleta.client.dedsafio;

import com.mojang.blaze3d.vertex.Object;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Object;
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

   static Object quads() { return null; }

   static void v(Object b, float x, float y, float z, float u, float w) {
      b.addVertex(x, y, z).setUv(u, w);
   }

   static void dibujar(Object b) { }

   static void empujarVista(Matrix4f vista) { }

   static void sacarVista() { }
}
