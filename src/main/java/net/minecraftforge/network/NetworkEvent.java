package net.minecraftforge.network;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.owleafstudio.modminds.compat.WorkQueue;

public final class NetworkEvent {
   private NetworkEvent() {
   }

   public static class Context {
      @Nullable
      private final ServerPlayer sender;
      private final NetworkEvent.Direction direction;
      private boolean packetHandled;

      public Context(@Nullable ServerPlayer sender, NetworkEvent.Direction direction) {
         this.sender = sender;
         this.direction = direction;
      }

      public void enqueueWork(Runnable work) {
         if (this.direction == NetworkEvent.Direction.PLAY_TO_CLIENT) {
            work.run();
         } else {
            WorkQueue.queue(0, work);
         }
      }

      public void setPacketHandled(boolean handled) {
         this.packetHandled = handled;
      }

      public boolean isPacketHandled() {
         return this.packetHandled;
      }

      @Nullable
      public ServerPlayer getSender() {
         return this.sender;
      }

      public NetworkEvent.Direction getDirection() {
         return this.direction;
      }
   }

   public static enum Direction {
      PLAY_TO_CLIENT(NetworkEvent.ReceptionSide.CLIENT),
      PLAY_TO_SERVER(NetworkEvent.ReceptionSide.SERVER);

      private final NetworkEvent.ReceptionSide receptionSide;

      private Direction(NetworkEvent.ReceptionSide receptionSide) {
         this.receptionSide = receptionSide;
      }

      public NetworkEvent.ReceptionSide getReceptionSide() {
         return this.receptionSide;
      }
   }

   public static enum ReceptionSide {
      CLIENT,
      SERVER,
      UNKNOWN;

      public boolean isServer() {
         return this == SERVER;
      }

      public boolean isClient() {
         return this == CLIENT;
      }
   }
}
