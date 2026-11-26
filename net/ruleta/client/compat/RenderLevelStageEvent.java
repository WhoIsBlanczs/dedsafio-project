package net.ruleta.client.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;

public final class RenderLevelStageEvent extends Evento {
   private final RenderLevelStageEvent.Stage etapa;
   private final PoseStack pila;
   private final Matrix4f proyeccion;
   private final Matrix4f vista;
   private final Camera camara;
   private final float parcial;

   public RenderLevelStageEvent(RenderLevelStageEvent.Stage etapa, PoseStack pila, Matrix4f vista, Matrix4f proyeccion, Camera camara, float parcial) {
      this.etapa = etapa;
      this.pila = pila;
      this.vista = vista;
      this.proyeccion = proyeccion;
      this.camara = camara;
      this.parcial = parcial;
   }

   public RenderLevelStageEvent.Stage getStage() {
      return this.etapa;
   }

   public PoseStack getPoseStack() {
      return this.pila;
   }

   public Matrix4f getModelViewMatrix() {
      return this.vista;
   }

   public Matrix4f getProjectionMatrix() {
      return this.proyeccion;
   }

   public Camera getCamera() {
      return this.camara;
   }

   public float getPartialTick() {
      return this.parcial;
   }

   public static enum Stage {
      AFTER_TRANSLUCENT_BLOCKS,
      AFTER_PARTICLES,
      AFTER_LEVEL;
   }
}
