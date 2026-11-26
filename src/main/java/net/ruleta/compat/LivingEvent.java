package net.ruleta.compat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class LivingEvent {
   private LivingEvent() {
   }

   public static final class LivingTickEvent extends LivingEntityEvento {
      public LivingTickEvent(LivingEntity entidad) {
         super(entidad);
      }
   }

   public static final class LivingVisibilityEvent extends LivingEntityEvento {
      private final Entity observador;
      private double visibilidad;

      public LivingVisibilityEvent(LivingEntity entidad, Entity observador, double visibilidad) {
         super(entidad);
         this.observador = observador;
         this.visibilidad = visibilidad;
      }

      public Entity getLookingEntity() {
         return this.observador;
      }

      public double getVisibilityModifier() {
         return this.visibilidad;
      }

      public void modifyVisibility(double factor) {
         this.visibilidad *= factor;
      }
   }
}
