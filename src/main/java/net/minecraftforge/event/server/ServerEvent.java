package net.minecraftforge.event.server;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.Event;

public class ServerEvent extends Event {
   private final MinecraftServer server;

   public ServerEvent(MinecraftServer server) {
      this.server = server;
   }

   public MinecraftServer getServer() {
      return this.server;
   }
}
