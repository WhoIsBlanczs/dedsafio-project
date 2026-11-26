package net.ruleta.client.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.Object;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

public final class RenderNameTagEvent extends Evento {
   private final Entity entidad;
   private final EntityRenderer<?> dibujante;
   private final PoseStack pila;
   private final Object buffer;
   private final int luz;
   private final float parcial;

   public RenderNameTagEvent(Entity entidad, EntityRenderer<?> dibujante, PoseStack pila, Object buffer, int luz, float parcial) {
      this.entidad = entidad;
      this.dibujante = dibujante;
      this.pila = pila;
      this.buffer = buffer;
      this.luz = luz;
      this.parcial = parcial;
   }

   public Entity getEntity() {
      return this.entidad;
   }

   public EntityRenderer<?> getEntityRenderer() {
      return this.dibujante;
   }

   public PoseStack getPoseStack() {
      return this.pila;
   }

   public Object getMultiBufferSource() {
      return this.buffer;
   }

   public int getPackedLight() {
      return this.luz;
   }

   public float getPartialTick() {
      return this.parcial;
   }
}
