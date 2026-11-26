package net.ruleta.compat;

import net.minecraft.world.entity.LivingEntity;

public abstract class LivingEntityEvento extends Evento {
   private final LivingEntity entidad;

   protected LivingEntityEvento(LivingEntity entidad) {
      this.entidad = entidad;
   }

   public LivingEntity getEntity() {
      return this.entidad;
   }
}
