package net.minecraftforge.event.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;

public class EntityEvent extends Event {
   private final Entity entity;

   public EntityEvent(Entity entity) {
      this.entity = entity;
   }

   public Entity getEntity() {
      return this.entity;
   }

   protected Entity getEntityMutable() {
      return this.entity;
   }

   public Level getLevel() {
      return this.entity == null ? null : this.entity.level();
   }
}
