package net.ruleta.client.agujero;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;


import net.minecraft.client.Minecraft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CieloBake {
   private static final Logger LOG = LoggerFactory.getLogger("ruletafanproject");
   private static final int RES_NEBULOSA = 768;
   private static final int RES_CONSTELACIONES = 1024;
   private static final int CARAS = 12;

   private static RenderTarget nebulosa;
   private static RenderTarget constelaciones;
   private static int hechas;
   private static boolean fallo;

   private CieloBake() {
   }

   public static void shader(Object s) {
      // efecto de dibujo pendiente de portar a 26.3
   }

   public static boolean listo() {
      return hechas >= 12 && nebulosa != null && constelaciones != null;
   }

   public static boolean fallo() {
      return fallo;
   }

   public static RenderTarget nebulosa() {
      return nebulosa;
   }

   public static RenderTarget constelaciones() {
      return constelaciones;
   }

   public static boolean avanzar() {
      // efecto de dibujo pendiente de portar a 26.3
      return false;
   }

   public static void liberar() {
      if (nebulosa != null) {
         nebulosa.destroyBuffers();
         nebulosa = null;
      }

      if (constelaciones != null) {
         constelaciones.destroyBuffers();
         constelaciones = null;
      }

      hechas = 0;
   }

   private static RenderTarget crear(int ancho, int alto) {
      // efecto de dibujo pendiente de portar a 26.3
      return null;
   }

   private static void dibujarCara(RenderTarget destino, int res, int cara, float modo) {
      // efecto de dibujo pendiente de portar a 26.3
   }
}
