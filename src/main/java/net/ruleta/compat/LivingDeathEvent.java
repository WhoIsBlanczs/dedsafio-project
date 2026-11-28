package net.ruleta.compat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public final class LivingDeathEvent extends LivingEntityEvento {
   private final DamageSource fuente;

   public LivingDeathEvent(LivingEntity entidad, DamageSource fuente) {
      super(entidad);
      this.fuente = fuente;
   }

   public DamageSource getSource() {
      return this.fuente;
   }
}
