package net.ruleta.client.dedsafio;

import com.mojang.renderpearl.backend.opengl.Uniform;
import com.mojang.blaze3d.vertex.Object;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Object;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Object;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.FluidState;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public final class AguaDedsafio {
   private static Object shader;

   private AguaDedsafio() {
   }

   public static void shader(Object s) {
      shader = s;
   }

   public static boolean ocultar(BlockPos pos, FluidState fluido) {
      Minecraft mc = Minecraft.getInstance();
      return mc.level != null
         && pos.getY() <= (int)Math.floor(-1.2000000000000002) - 1
         && fluido.is(FluidTags.WATER)
         && mc.level.dimension().equals(DedsafioCielo.DIMENSION);
   }

   public static void dibujar(Matrix4f vista, float radio, double alturaCamara) { }

   private static void fijar(Object sh, String nombre, float a, float b, float c, float d, int n) {
      Uniform u = sh.getUniform(nombre);
      if (u != null) {
         if (n == 4) {
            u.set(a, b, c, d);
         } else {
            u.set(a);
         }
      }
   }
}
