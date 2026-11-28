package net.ruleta.compat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public final class LivingDamageEvent extends LivingEntityEvento {
   private final DamageSource fuente;
   private float cantidad;

   public LivingDamageEvent(LivingEntity entidad, DamageSource fuente, float cantidad) {
      super(entidad);
      this.fuente = fuente;
      this.cantidad = cantidad;
   }

   public DamageSource getSource() {
      return this.fuente;
   }

   public float getAmount() {
      return this.cantidad;
   }

   public void setAmount(float cantidad) {
      this.cantidad = cantidad;
   }
}
