package net.ruleta.client.dedsafio;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.vertex.Object;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Object;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Object;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class RayosDedsafio {
   public static final float fuerza = 0.3F;
   public static float refuerzo = 0.0F;
   private static Object shader;
   private static TextureTarget profCopia;

   private RayosDedsafio() {
   }

   public static void shader(Object s) {
      shader = s;
   }

   private static int copiarProfundidad(Minecraft mc) { return 0; }

   public static void liberar() {
      if (profCopia != null) {
         profCopia.destroyBuffers();
         profCopia = null;
      }
   }

   public static void dibujar(Matrix4f proyeccion, Matrix4f vista) { }
}
