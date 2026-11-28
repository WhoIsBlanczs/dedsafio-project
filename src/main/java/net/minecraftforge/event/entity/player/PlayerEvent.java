package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.event.entity.living.LivingEvent;

public class PlayerEvent extends LivingEvent {
   public PlayerEvent(Player player) {
      super(player);
   }

   public Player getEntity() {
      return (Player)super.getEntity();
   }

   public Player getPlayer() {
      return this.getEntity();
   }

   public static class Clone extends PlayerEvent {
      private final ServerPlayer original;
      private final boolean wasDeath;

      public Clone(Player player, ServerPlayer original, boolean wasDeath) {
         super(player);
         this.original = original;
         this.wasDeath = wasDeath;
      }

      public ServerPlayer getOriginal() {
         return this.original;
      }

      public boolean isWasDeath() {
         return this.wasDeath;
      }

      public boolean wasDeath() {
         return this.wasDeath;
      }
   }

   public static class PlayerChangedDimensionEvent extends PlayerEvent {
      private final ResourceKey<Level> from;
      private final ResourceKey<Level> to;

      public PlayerChangedDimensionEvent(Player player, ResourceKey<Level> from, ResourceKey<Level> to) {
         super(player);
         this.from = from;
         this.to = to;
      }

      public ResourceKey<Level> getFrom() {
         return this.from;
      }

      public ResourceKey<Level> getTo() {
         return this.to;
      }
   }

   public static class PlayerLoggedInEvent extends PlayerEvent {
      public PlayerLoggedInEvent(Player player) {
         super(player);
      }
   }

   public static class PlayerRespawnEvent extends PlayerEvent {
      private final boolean endConquered;

      public PlayerRespawnEvent(Player player, boolean endConquered) {
         super(player);
         this.endConquered = endConquered;
      }

      public boolean isEndConquered() {
         return this.endConquered;
      }
   }
}
