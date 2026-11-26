package net.ruleta.client.dedsafio;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.FluidState;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public final class AguaDedsafio {
   private static ShaderInstance shader;

   private AguaDedsafio() {
   }

   public static void shader(ShaderInstance s) {
      shader = s;
   }

   public static boolean ocultar(BlockPos pos, FluidState fluido) {
      Minecraft mc = Minecraft.getInstance();
      return mc.level != null
         && pos.getY() <= (int)Math.floor(-1.2000000000000002) - 1
         && fluido.is(FluidTags.WATER)
         && mc.level.dimension().equals(DedsafioCielo.DIMENSION);
   }

   public static void dibujar(Matrix4f vista, float radio, double alturaCamara) {
      ShaderInstance sh = shader;
      if (sh != null) {
         float t = DedsafioCielo.tiempo();
         float[] sol = DedsafioCielo.direccionSol();
         fijar(sh, "Tiempo", t % 3600.0F, 0.0F, 0.0F, 0.0F, 1);
         fijar(sh, "Sol", sol[0], sol[1], sol[2], 1.0F, 4);
         fijar(sh, "Ajuste", 0.0F, 1.0F, 0.4F, 0.0F, 4);
         fijar(sh, "Mundo", (float)(DedsafioCielo.camX % 4096.0), (float)(DedsafioCielo.camZ % 4096.0), 0.0F, 0.0F, 4);
         Matrix4fStack mv = RenderSystem.getModelViewStack();
         mv.pushMatrix();
         mv.identity();
         mv.mul(vista);
         RenderSystem.applyModelViewMatrix();
         RenderSystem.setShader(() -> sh);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.depthMask(false);
         RenderSystem.enableDepthTest();
         RenderSystem.disableCull();

         try {
            float y = (float)(-2.2 - alturaCamara);
            BufferBuilder b = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION);
            b.addVertex(-radio, y, -radio);
            b.addVertex(-radio, y, radio);
            b.addVertex(radio, y, radio);
            b.addVertex(radio, y, -radio);
            BufferUploader.drawWithShader(b.buildOrThrow());
         } finally {
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            mv.popMatrix();
            RenderSystem.applyModelViewMatrix();
         }
      }
   }

   private static void fijar(ShaderInstance sh, String nombre, float a, float b, float c, float d, int n) {
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
