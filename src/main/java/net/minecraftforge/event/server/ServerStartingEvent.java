package net.minecraftforge.event.server;

import net.minecraft.server.MinecraftServer;

public class ServerStartingEvent extends ServerEvent {
   public ServerStartingEvent(MinecraftServer server) {
      super(server);
   }
}
