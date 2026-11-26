package net.ruleta.client.dedsafio;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class RayosDedsafio {
   public static final float fuerza = 0.3F;
   public static float refuerzo = 0.0F;
   private static ShaderInstance shader;
   private static TextureTarget profCopia;

   private RayosDedsafio() {
   }

   public static void shader(ShaderInstance s) {
      shader = s;
   }

   private static int copiarProfundidad(Minecraft mc) {
      RenderTarget main = mc.getMainRenderTarget();
      if (profCopia == null) {
         profCopia = new TextureTarget(main.width, main.height, true, Minecraft.ON_OSX);
      } else if (profCopia.width != main.width || profCopia.height != main.height) {
         profCopia.resize(main.width, main.height, Minecraft.ON_OSX);
      }

      profCopia.copyDepthFrom(main);
      main.bindWrite(true);
      return profCopia.getDepthTextureId();
   }

   public static void liberar() {
      if (profCopia != null) {
         profCopia.destroyBuffers();
         profCopia = null;
      }
   }

   public static void dibujar(Matrix4f proyeccion, Matrix4f vista) {
      ShaderInstance sh = shader;
      float inten = 0.3F + refuerzo;
      if (sh != null && !(inten <= 0.01F)) {
         Minecraft mc = Minecraft.getInstance();
         float[] s = DedsafioCielo.direccionSol();
         Vector3f v = vista.transformDirection(new Vector3f(s[0], s[1], s[2]));
         float len = v.length();
         if (!(len < 1.0E-4F)) {
            float frente = -v.z() / len;
            if (!(frente <= 0.02F)) {
               Vector4f clip = proyeccion.transform(new Vector4f(v.x(), v.y(), v.z(), 1.0F));
               if (!(clip.w() <= 1.0E-4F)) {
                  float uvx = clip.x() / clip.w() * 0.5F + 0.5F;
                  float uvy = clip.y() / clip.w() * 0.5F + 0.5F;
                  float asp = mc.getWindow().getWidth() / Math.max(1.0F, (float)mc.getWindow().getHeight());
                  int prof = copiarProfundidad(mc);
                  float aparece = Math.max(0.0F, Math.min(1.0F, frente / 0.35F));
                  sh.safeGetUniform("Luz").set(uvx, uvy, asp, inten * aparece * 0.9F);
                  sh.safeGetUniform("Tinte").set(1.0F, 0.8F, 0.58F, DedsafioCielo.formacion);
                  sh.setSampler("DepthSampler", prof);
                  RenderSystem.setShader(() -> sh);
                  RenderSystem.enableBlend();
                  RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
                  RenderSystem.disableDepthTest();
                  RenderSystem.disableCull();
                  RenderSystem.depthMask(false);

                  try {
                     BufferBuilder bb = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                     bb.addVertex(-1.0F, -1.0F, 0.0F).setUv(0.0F, 0.0F);
                     bb.addVertex(1.0F, -1.0F, 0.0F).setUv(1.0F, 0.0F);
                     bb.addVertex(1.0F, 1.0F, 0.0F).setUv(1.0F, 1.0F);
                     bb.addVertex(-1.0F, 1.0F, 0.0F).setUv(0.0F, 1.0F);
                     BufferUploader.drawWithShader(bb.buildOrThrow());
                  } finally {
                     RenderSystem.depthMask(true);
                     RenderSystem.enableDepthTest();
                     RenderSystem.enableCull();
                     RenderSystem.disableBlend();
                     RenderSystem.defaultBlendFunc();
                  }
               }
            }
         }
      }
   }
}
