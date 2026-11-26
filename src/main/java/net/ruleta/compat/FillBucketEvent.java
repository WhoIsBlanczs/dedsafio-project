package net.ruleta.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public final class FillBucketEvent extends Evento {
   private final Player jugador;
   private final ItemStack cubo;
   private final Level nivel;
   private final HitResult objetivo;

   public FillBucketEvent(Player jugador, ItemStack cubo, Level nivel, HitResult objetivo) {
      this.jugador = jugador;
      this.cubo = cubo;
      this.nivel = nivel;
      this.objetivo = objetivo;
   }

   public Player getEntity() {
      return this.jugador;
   }

   public ItemStack getEmptyBucket() {
      return this.cubo;
   }

   public Level getLevel() {
      return this.nivel;
   }

   public HitResult getTarget() {
      return this.objetivo;
   }
}
