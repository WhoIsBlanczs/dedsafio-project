package net.ruleta.compat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class LivingUseTotemEvent extends LivingEntityEvento {
   private final DamageSource fuente;
   private final ItemStack totem;

   public LivingUseTotemEvent(LivingEntity entidad, DamageSource fuente, ItemStack totem) {
      super(entidad);
      this.fuente = fuente;
      this.totem = totem;
   }

   public DamageSource getSource() {
      return this.fuente;
   }

   public ItemStack getTotem() {
      return this.totem;
   }
}
