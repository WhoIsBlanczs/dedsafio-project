package net.ruleta.client.dedsafio;

import com.mojang.renderpearl.backend.opengl.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

public final class DedsafioCielo {
   public static final ResourceKey<Level> DIMENSION = ResourceKey.create(Registries.DIMENSION, Identifier.fromNamespaceAndPath("ruleta", "dedsafio"));
   public static final Identifier FIGURAS = Identifier.fromNamespaceAndPath("ruleta", "textures/dedsafio/figuras.png");
   public static final Identifier NUBES = Identifier.fromNamespaceAndPath("ruleta", "textures/dedsafio/nubes.png");
   public static final float[] HORIZONTE = new float[]{0.98F, 0.72F, 0.64F};
   public static final double NIVEL_AGUA = -2.2;
   public static final float FORMA = 9.0F;
   public static final float QUEDA = 16.0F;
   public static final float DESHACE = 8.0F;
   public static final float DESCANSO = 6.0F;
   public static final int NUM_FIGURAS = 6;
   private static Object shader;
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
   private static Object cuboVbo;
   private static float cuboLado = -1.0F;

   private DedsafioCielo() {
   }

   public static void shader(Object s) {
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

   public static void liberar() { }

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

   public static void dibujar(Matrix4f vista, float lejos, double alturaCamara) { }

   private static void cubo(float s, Object sh) { }

   private static void fijar(Object sh, String nombre, float... v) { }
}
