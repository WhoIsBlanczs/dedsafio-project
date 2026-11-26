package net.ruleta.compat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class EntityLeaveLevelEvent extends Evento {
   private final Entity entidad;
   private final Level nivel;

   public EntityLeaveLevelEvent(Entity entidad, Level nivel) {
      this.entidad = entidad;
      this.nivel = nivel;
   }

   public Entity getEntity() {
      return this.entidad;
   }

   public Level getLevel() {
      return this.nivel;
   }
}
