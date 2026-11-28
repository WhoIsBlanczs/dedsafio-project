package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityEvent;

public class LivingEvent extends EntityEvent {
   public LivingEvent(LivingEntity entity) {
      super(entity);
   }

   public LivingEntity getEntity() {
      return (LivingEntity)super.getEntity();
   }

   public static class LivingTickEvent extends LivingEvent {
      public LivingTickEvent(LivingEntity entity) {
         super(entity);
      }
   }
}
