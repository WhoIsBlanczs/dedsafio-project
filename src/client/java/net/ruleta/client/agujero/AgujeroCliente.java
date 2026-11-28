package net.ruleta.client.agujero;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import com.mojang.blaze3d.vertex.VertexSorting;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;

import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.ruleta.agujero.AgujeroNegro;
import net.ruleta.agujero.AgujeroPayload;


import net.ruleta.client.compat.ClientPlayerNetworkEvent;
import net.ruleta.client.compat.EventPriority;
import net.ruleta.client.compat.RenderLevelStageEvent;
import net.ruleta.client.compat.SubscribeEvent;
import net.ruleta.client.compat.ViewportEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;

public final class AgujeroCliente {
   private static final Identifier TINTE = Identifier.fromNamespaceAndPath("ruletafanproject", "shaders/post/agujero_tinte.json");

   private static final float FUNDIDO = 2.0F;


   private static boolean activo;
   private static final List<AgujeroPayload.Agujero> AGUJEROS = new ArrayList<>();
   private static float nivel;
   private static long ultimo = -1L;
   private static final double SOMBRA = 2.598;
   private static final double MEDIO_CUADRADO = 9.0;
   private static TextureTarget copia;
   private static boolean profundidadGuardada;

   private AgujeroCliente() {
   }

   public static void shaderObjeto(Object s) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   public static void shader(Object s) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   public static void recibir(AgujeroPayload p) {
      activo = p.activo();
      AGUJEROS.clear();
      AGUJEROS.addAll(p.agujeros());
   }

   public static float nivel() {
      return nivel;
   }

   public static boolean tapaCielo() {
      Minecraft mc = Minecraft.getInstance();
      return nivel >= 0.999F && mc.level != null && mc.level.dimension() == Level.OVERWORLD;
   }

   private static void avanzar(boolean listo) {
      long ahora = System.nanoTime();
      float dt = ultimo < 0L ? 0.0F : Math.min(0.25F, (float)(ahora - ultimo) / 1.0E9F);
      ultimo = ahora;
      float paso = activo ? (listo ? dt : 0.0F) : -dt;
      nivel = Mth.clamp(nivel + paso / 2.0F, 0.0F, 1.0F);
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public static void alDibujarMundo(RenderLevelStageEvent e) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void dibujarAgujeros(RenderLevelStageEvent e, Minecraft mc) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void asegurarCopia(Minecraft mc) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static int copiarEscena(Minecraft mc) {
      // efecto de dibujo pendiente de portar a 26.3
      return 0;
   }

   private static void esferaAgujero(BufferBuilder b, Matrix4f vista, float r) {
      int lat = 24;
      int lon = 48;

      for (int i = 0; i < 24; i++) {
         float a0 = (float) Math.PI * i / 24.0F;
         float a1 = (float) Math.PI * (i + 1) / 24.0F;

         for (int j = 0; j < 48; j++) {
            float b0 = (float) (Math.PI * 2) * j / 48.0F;
            float b1 = (float) (Math.PI * 2) * (j + 1) / 48.0F;
            puntoEsfera(b, vista, r, a0, b0);
            puntoEsfera(b, vista, r, a1, b0);
            puntoEsfera(b, vista, r, a1, b1);
            puntoEsfera(b, vista, r, a0, b1);
         }
      }
   }

   private static void puntoEsfera(BufferBuilder b, Matrix4f vista, float r, float a, float c) {
      float x = r * Mth.sin(a) * Mth.cos(c);
      float y = r * Mth.cos(a);
      float z = r * Mth.sin(a) * Mth.sin(c);
      Vector3f v = vista.transformPosition(new Vector3f(x, y, z));
      b.addVertex(v.x(), v.y(), v.z()).setUv(0.0F, 0.0F);
   }

   private static void cuboAgujero(BufferBuilder b, Matrix4f vista, float s) {
      float[][] caras = new float[][]{
         {-s, s, -s, s, s, -s, s, s, s, -s, s, s},
         {-s, -s, -s, -s, -s, s, s, -s, s, s, -s, -s},
         {-s, -s, -s, s, -s, -s, s, s, -s, -s, s, -s},
         {s, -s, s, -s, -s, s, -s, s, s, s, s, s},
         {-s, -s, s, -s, -s, -s, -s, s, -s, -s, s, s},
         {s, -s, -s, s, -s, s, s, s, s, s, s, -s}
      };

      for (float[] c : caras) {
         for (int k = 0; k < 12; k += 3) {
            Vector3f v = vista.transformPosition(new Vector3f(c[k], c[k + 1], c[k + 2]));
            b.addVertex(v.x(), v.y(), v.z()).setUv(0.0F, 0.0F);
         }
      }
   }

   private static void cielo(RenderLevelStageEvent e, Minecraft mc) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static float faseRoja() {
      float tiempo = (float)(System.nanoTime() / 1.0E9 % 3600.0);
      float s = 0.5F + 0.5F * Mth.sin(tiempo * (float) (Math.PI * 2) / 90.0F - 1.2F);
      float x = Mth.clamp((s - 0.55F) / 0.3F, 0.0F, 1.0F);
      return x * x * (3.0F - 2.0F * x);
   }

   private static float[] colorNeblina() {
      float r = faseRoja();
      return new float[]{0.2F, 0.21F, 0.25F};
   }

   @SubscribeEvent
   public static void alColorearNiebla(ViewportEvent.ComputeFogColor e) {
      Minecraft mc = Minecraft.getInstance();
      if (!(nivel <= 0.0F) && mc.level != null && mc.level.dimension() == Level.OVERWORLD) {
         float[] c = colorNeblina();
         e.setRed(Mth.lerp(nivel, e.getRed(), c[0] * 0.55F));
         e.setGreen(Mth.lerp(nivel, e.getGreen(), c[1] * 0.55F));
         e.setBlue(Mth.lerp(nivel, e.getBlue(), c[2] * 0.55F));
      }
   }

   @SubscribeEvent
   public static void alSalir(ClientPlayerNetworkEvent.LoggingOut e) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   private static void cubo(float s) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   public static final class Registro {
      private Registro() {
      }

      public static void registrar() {
         EntityRendererRegistry.register(AgujeroNegro.TIPO.get(), NoopRenderer::new);
      }
   }
}
