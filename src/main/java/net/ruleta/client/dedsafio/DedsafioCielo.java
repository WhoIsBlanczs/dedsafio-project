package net.ruleta.client.dedsafio;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexBuffer.Usage;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public final class DedsafioCielo {
   public static final ResourceKey<Level> DIMENSION = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("ruleta", "dedsafio"));
   public static final ResourceLocation FIGURAS = ResourceLocation.fromNamespaceAndPath("ruleta", "textures/dedsafio/figuras.png");
   public static final ResourceLocation NUBES = ResourceLocation.fromNamespaceAndPath("ruleta", "textures/dedsafio/nubes.png");
   public static final float[] HORIZONTE = new float[]{0.98F, 0.72F, 0.64F};
   public static final double NIVEL_AGUA = -2.2;
   public static final float FORMA = 9.0F;
   public static final float QUEDA = 16.0F;
   public static final float DESHACE = 8.0F;
   public static final float DESCANSO = 6.0F;
   public static final int NUM_FIGURAS = 6;
   private static ShaderInstance shader;
   private static long inicio = -1L;
   public static float formacion = 0.0F;
   public static int figuraFija = -1;
   public static double camX;
   public static double camZ;
   public static final float rumbo = 0.0F;
   public static final float altura = 2.8F;
   public static final float nubes = 0.0F;
   public static final float tamano = 1.0F;
   public static final float brilloSol = 0.4F;
   public static final float oscuridad = 0.34F;
   private static float figuraVista = -1.0F;
   private static final float[] FIG_OFFX = new float[]{0.0765F, -0.0169F, 0.0104F, -0.022F, -0.0065F, 0.0195F, 0.0123F};
   private static final float[] FIG_OFFY = new float[]{0.2516F, 0.3035F, 0.2464F, 0.2672F, 0.249F, 0.2309F, 0.2509F};
   private static VertexBuffer cuboVbo;
   private static float cuboLado = -1.0F;

   private DedsafioCielo() {
   }

   public static void shader(ShaderInstance s) {
      shader = s;
   }

   public static boolean visible() {
      Minecraft mc = Minecraft.getInstance();
      return mc.level != null && mc.level.dimension().equals(DIMENSION);
   }

   public static boolean enDimension() {
      Minecraft mc = Minecraft.getInstance();
      return mc.level != null && mc.level.dimension().equals(DIMENSION);
   }

   public static void tick() {
      if (!visible()) {
         inicio = -1L;
      } else if (inicio < 0L) {
         inicio = System.nanoTime();
      }
   }

   public static void limpiar() {
      inicio = -1L;
      formacion = 0.0F;
   }

   public static void liberar() {
      formacion = 0.0F;
      if (cuboVbo != null) {
         cuboVbo.close();
         cuboVbo = null;
         cuboLado = -1.0F;
      }
   }

   private static float suave(float x) {
      x = Math.max(0.0F, Math.min(1.0F, x));
      return x * x * (3.0F - 2.0F * x);
   }

   private static float[] figuraAhora(float t) {
      float[] escena = EscenaDedsafio.figura();
      if (escena != null && figuraFija == -2) {
         return escena;
      }

      if (figuraFija >= 0) {
         return new float[]{figuraFija, 1.0F};
      }

      if (figuraFija == -1) {
         return new float[]{-1.0F, 0.0F};
      }

      float ciclo = 39.0F;
      int n = (int)(t / ciclo);
      float s = t - n * ciclo;
      float f;
      if (s < 9.0F) {
         f = suave(s / 9.0F);
      } else if (s < 25.0F) {
         f = 1.0F;
      } else if (s < 33.0F) {
         f = 1.0F - suave((s - 9.0F - 16.0F) / 8.0F);
      } else {
         f = 0.0F;
      }

      return new float[]{n % 6, f};
   }

   public static void mirarFigura() {
      LocalPlayer p = Minecraft.getInstance().player;
      if (p != null && visible() && !(figuraVista < -0.5F) && !(formacion < 0.05F)) {
         int fi = Math.max(0, Math.min(FIG_OFFX.length - 1, Math.round(figuraVista)));
         float yawObj = -(0.0F - (float)Math.toDegrees(FIG_OFFX[fi] * 1.0F));
         float pitchObj = -(2.8F + (float)Math.toDegrees(FIG_OFFY[fi] * 1.0F));
         float f = Math.min(1.0F, formacion * 2.0F) * 0.12F;
         float dy = Mth.wrapDegrees(yawObj - p.getYRot());
         float dp = pitchObj - p.getXRot();
         p.setYRot(p.getYRot() + dy * f);
         p.setXRot(p.getXRot() + dp * f);
      }
   }

   public static float tiempo() {
      return inicio < 0L ? 0.0F : (float)(System.nanoTime() - inicio) / 1.0E9F;
   }

   public static float[] direccionSol() {
      double az = Math.toRadians(0.0);
      double el = Math.toRadians(2.8F);
      return new float[]{(float)(Math.sin(az) * Math.cos(el)), (float)Math.sin(el), (float)(Math.cos(az) * Math.cos(el))};
   }

   public static void dibujar(Matrix4f vista, float lejos, double alturaCamara) {
      ShaderInstance sh = shader;
      if (sh != null && inicio >= 0L) {
         float t = (float)(System.nanoTime() - inicio) / 1.0E9F;
         float[] fig = figuraAhora(t);
         formacion = fig[0] > -0.5F ? fig[1] : 0.0F;
         figuraVista = fig[0];
         double az = Math.toRadians(0.0);
         double el = Math.toRadians(2.8F);
         float sx = (float)(Math.sin(az) * Math.cos(el));
         float sy = (float)Math.sin(el);
         float sz = (float)(Math.cos(az) * Math.cos(el));
         fijar(sh, "Tiempo", t % 3600.0F);
         fijar(sh, "Sol", sx, sy, sz, 1.0F);
         fijar(sh, "Escena", fig[0], fig[1], 0.34F, (float)(alturaCamara - -2.2));
         fijar(sh, "Ajuste", 0.0F, 1.0F, 0.4F, 0.0F);
         fijar(sh, "Mundo", (float)(camX % 4096.0), (float)(camZ % 4096.0), 0.0F, 0.0F);
         AbstractTexture texFig = Minecraft.getInstance().getTextureManager().getTexture(FIGURAS);
         texFig.setFilter(true, false);
         sh.setSampler("Figuras", texFig);
         Matrix4fStack mv = RenderSystem.getModelViewStack();
         mv.pushMatrix();
         mv.identity();
         mv.mul(vista);
         RenderSystem.applyModelViewMatrix();
         RenderSystem.setShader(() -> sh);
         RenderSystem.disableBlend();
         RenderSystem.depthMask(false);
         RenderSystem.enableDepthTest();
         RenderSystem.depthFunc(515);
         RenderSystem.disableCull();

         try {
            cubo(lejos * 0.3F, sh);
         } finally {
            RenderSystem.enableCull();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            mv.popMatrix();
            RenderSystem.applyModelViewMatrix();
         }
      }
   }

   private static void cubo(float s, ShaderInstance sh) {
      if (cuboVbo != null && !(Math.abs(cuboLado - s) > 0.001F)) {
         cuboVbo.bind();
      } else {
         float[][] caras = new float[][]{
            {-s, s, -s, s, s, -s, s, s, s, -s, s, s},
            {-s, -s, -s, -s, -s, s, s, -s, s, s, -s, -s},
            {-s, -s, -s, s, -s, -s, s, s, -s, -s, s, -s},
            {s, -s, s, -s, -s, s, -s, s, s, s, s, s},
            {-s, -s, s, -s, -s, -s, -s, s, -s, -s, s, s},
            {s, -s, -s, s, -s, s, s, s, s, s, s, -s}
         };
         BufferBuilder b = Tesselator.getInstance().begin(Mode.QUADS, DefaultVertexFormat.POSITION);

         for (float[] c : caras) {
            for (int k = 0; k < 12; k += 3) {
               b.addVertex(c[k], c[k + 1], c[k + 2]);
            }
         }

         if (cuboVbo == null) {
            cuboVbo = new VertexBuffer(Usage.STATIC);
         }

         cuboVbo.bind();
         cuboVbo.upload(b.buildOrThrow());
         cuboLado = s;
      }

      cuboVbo.drawWithShader(new Matrix4f(RenderSystem.getModelViewMatrix()), new Matrix4f(RenderSystem.getProjectionMatrix()), sh);
      VertexBuffer.unbind();
   }

   private static void fijar(ShaderInstance sh, String nombre, float... v) {
      Uniform u = sh.getUniform(nombre);
      if (u != null) {
         if (v.length == 4) {
            u.set(v[0], v[1], v[2], v[3]);
         } else {
            u.set(v[0]);
         }
      }
   }
}
