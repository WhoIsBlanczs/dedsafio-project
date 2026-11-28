package net.ruleta.compat;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public abstract class ExplosionEvent extends Evento {
   private final Level nivel;
   private final Explosion explosion;

   protected ExplosionEvent(Level nivel, Explosion explosion) {
      this.nivel = nivel;
      this.explosion = explosion;
   }

   public Level getLevel() {
      return this.nivel;
   }

   public Explosion getExplosion() {
      return this.explosion;
   }

   public static final class Detonate extends ExplosionEvent {
      private final List<Entity> entidades;
      private final List<BlockPos> bloques;

      public Detonate(Level nivel, Explosion explosion, List<Entity> entidades, List<BlockPos> bloques) {
         super(nivel, explosion);
         this.entidades = entidades;
         this.bloques = bloques;
      }

      public List<BlockPos> getAffectedBlocks() {
         return this.bloques;
      }

      public List<Entity> getAffectedEntities() {
         return this.entidades;
      }
   }

   public static final class Start extends ExplosionEvent {
      public Start(Level nivel, Explosion explosion) {
         super(nivel, explosion);
      }
   }
}
