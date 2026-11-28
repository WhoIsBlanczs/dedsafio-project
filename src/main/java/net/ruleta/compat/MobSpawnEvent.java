package net.ruleta.compat;

import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.ServerLevelAccessor;

public final class MobSpawnEvent {
   private MobSpawnEvent() {
   }

   public static final class FinalizeSpawn extends Evento {
      private final Mob mob;
      private final ServerLevelAccessor nivel;
      private final DifficultyInstance dificultad;
      private final EntitySpawnReason tipo;

      public FinalizeSpawn(Mob mob, ServerLevelAccessor nivel, DifficultyInstance dificultad, EntitySpawnReason tipo) {
         this.mob = mob;
         this.nivel = nivel;
         this.dificultad = dificultad;
         this.tipo = tipo;
      }

      public Mob getEntity() {
         return this.mob;
      }

      public ServerLevelAccessor getLevel() {
         return this.nivel;
      }

      public double getX() {
         return this.mob.getX();
      }

      public double getY() {
         return this.mob.getY();
      }

      public double getZ() {
         return this.mob.getZ();
      }

      public DifficultyInstance getDifficulty() {
         return this.dificultad;
      }

      public EntitySpawnReason getSpawnType() {
         return this.tipo;
      }

      public boolean isSpawnCancelled() {
         return this.isCanceled();
      }

      public void setSpawnCancelled(boolean cancelada) {
         this.setCanceled(cancelada);
      }
   }
}
