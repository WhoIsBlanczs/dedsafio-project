package net.ruleta.client.compat;

import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.fog.FogRenderer.FogMode;
import net.minecraft.world.level.material.FogType;

public final class ViewportEvent {
   private ViewportEvent() {
   }

   public abstract static class Base extends Evento {
      private final Camera camara;
      private final float parcial;

      protected Base(Camera camara, float parcial) {
         this.camara = camara;
         this.parcial = parcial;
      }

      public Camera getCamera() {
         return this.camara;
      }

      public float getPartialTick() {
         return this.parcial;
      }
   }

   public static final class ComputeCameraAngles extends ViewportEvent.Base {
      private float guinada;
      private float cabeceo;
      private float ladeo;

      public ComputeCameraAngles(Camera camara, float parcial, float guinada, float cabeceo, float ladeo) {
         super(camara, parcial);
         this.guinada = guinada;
         this.cabeceo = cabeceo;
         this.ladeo = ladeo;
      }

      public float getYaw() {
         return this.guinada;
      }

      public float getPitch() {
         return this.cabeceo;
      }

      public float getRoll() {
         return this.ladeo;
      }

      public void setYaw(float v) {
         this.guinada = v;
      }

      public void setPitch(float v) {
         this.cabeceo = v;
      }

      public void setRoll(float v) {
         this.ladeo = v;
      }
   }

   public static final class ComputeFogColor extends ViewportEvent.Base {
      private float rojo;
      private float verde;
      private float azul;

      public ComputeFogColor(Camera camara, float parcial, float rojo, float verde, float azul) {
         super(camara, parcial);
         this.rojo = rojo;
         this.verde = verde;
         this.azul = azul;
      }

      public float getRed() {
         return this.rojo;
      }

      public float getGreen() {
         return this.verde;
      }

      public float getBlue() {
         return this.azul;
      }

      public void setRed(float v) {
         this.rojo = v;
      }

      public void setGreen(float v) {
         this.verde = v;
      }

      public void setBlue(float v) {
         this.azul = v;
      }
   }

   public static final class RenderFog extends ViewportEvent.Base {
      private final FogMode modo;
      private final FogType tipo;
      private float cerca;
      private float lejos;
      private FogShape forma;

      public RenderFog(FogMode modo, FogType tipo, Camera camara, float parcial, float cerca, float lejos, FogShape forma) {
         super(camara, parcial);
         this.modo = modo;
         this.tipo = tipo;
         this.cerca = cerca;
         this.lejos = lejos;
         this.forma = forma;
      }

      public FogMode getMode() {
         return this.modo;
      }

      public FogType getType() {
         return this.tipo;
      }

      public float getNearPlaneDistance() {
         return this.cerca;
      }

      public float getFarPlaneDistance() {
         return this.lejos;
      }

      public FogShape getFogShape() {
         return this.forma;
      }

      public void setNearPlaneDistance(float v) {
         this.cerca = v;
      }

      public void setFarPlaneDistance(float v) {
         this.lejos = v;
      }

      public void setFogShape(FogShape v) {
         this.forma = v;
      }
   }
}
