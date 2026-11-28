package net.minecraftforge.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

public class TickEvent extends Event {
   public final TickEvent.Phase phase;

   protected TickEvent(TickEvent.Phase phase) {
      this.phase = phase;
   }

   public static class ClientTickEvent extends TickEvent {
      public ClientTickEvent(TickEvent.Phase phase) {
         super(phase);
      }
   }

   public static enum Phase {
      START,
      END;
   }

   public static class PlayerTickEvent extends TickEvent {
      public final Player player;

      public PlayerTickEvent(TickEvent.Phase phase, Player player) {
         super(phase);
         this.player = player;
      }

      public Player getPlayer() {
         return this.player;
      }
   }

   public static class ServerTickEvent extends TickEvent {
      public ServerTickEvent(TickEvent.Phase phase) {
         super(phase);
      }
   }
}
