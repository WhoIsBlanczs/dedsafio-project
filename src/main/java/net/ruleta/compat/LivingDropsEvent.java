package net.ruleta.compat;

import java.util.Collection;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;

public final class LivingDropsEvent extends LivingEntityEvento {
   private final DamageSource fuente;
   private final Collection<ItemEntity> drops;

   public LivingDropsEvent(LivingEntity entidad, DamageSource fuente, Collection<ItemEntity> drops) {
      super(entidad);
      this.fuente = fuente;
      this.drops = drops;
   }

   public DamageSource getSource() {
      return this.fuente;
   }

   public Collection<ItemEntity> getDrops() {
      return this.drops;
   }
}
