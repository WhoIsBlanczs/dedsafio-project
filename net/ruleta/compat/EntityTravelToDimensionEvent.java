package net.ruleta.compat;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class EntityTravelToDimensionEvent extends Evento {
   private final Entity entidad;
   private final ResourceKey<Level> dimension;

   public EntityTravelToDimensionEvent(Entity entidad, ResourceKey<Level> dimension) {
      this.entidad = entidad;
      this.dimension = dimension;
   }

   public Entity getEntity() {
      return this.entidad;
   }

   public ResourceKey<Level> getDimension() {
      return this.dimension;
   }
}
