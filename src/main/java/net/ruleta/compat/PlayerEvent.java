package net.ruleta.compat;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public final class PlayerEvent {
   private PlayerEvent() {
   }

   public static final class NameFormat extends Evento {
      private final Player jugador;
      private final Component usuario;
      private Component nombre;

      public NameFormat(Player jugador, Component usuario, Component nombre) {
         this.jugador = jugador;
         this.usuario = usuario;
         this.nombre = nombre;
      }

      public Player getEntity() {
         return this.jugador;
      }

      public Component getUsername() {
         return this.usuario;
      }

      public Component getDisplayname() {
         return this.nombre;
      }

      public void setDisplayname(Component nombre) {
         this.nombre = nombre;
      }
   }

   public static final class StartTracking extends Evento {
      private final Player jugador;
      private final Entity objetivo;

      public StartTracking(Player jugador, Entity objetivo) {
         this.jugador = jugador;
         this.objetivo = objetivo;
      }

      public Player getEntity() {
         return this.jugador;
      }

      public Entity getTarget() {
         return this.objetivo;
      }
   }
}
