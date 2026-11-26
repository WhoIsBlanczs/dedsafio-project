package net.ruleta.compat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class EntityJoinLevelEvent extends Evento {
   private final Entity entidad;
   private final Level nivel;
   private final boolean cargadaDelDisco;

   public EntityJoinLevelEvent(Entity entidad, Level nivel, boolean cargadaDelDisco) {
      this.entidad = entidad;
      this.nivel = nivel;
      this.cargadaDelDisco = cargadaDelDisco;
   }

   public Entity getEntity() {
      return this.entidad;
   }

   public Level getLevel() {
      return this.nivel;
   }

   public boolean loadedFromDisk() {
      return this.cargadaDelDisco;
   }
}
