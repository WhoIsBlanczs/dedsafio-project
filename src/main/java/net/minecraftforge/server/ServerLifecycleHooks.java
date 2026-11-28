package net.minecraftforge.server;

import net.minecraft.server.MinecraftServer;
import net.owleafstudio.modminds.compat.GameEventPumps;

public final class ServerLifecycleHooks {
   private ServerLifecycleHooks() {
   }

   public static MinecraftServer getCurrentServer() {
      return GameEventPumps.currentServer();
   }

   public static MinecraftServer getServer() {
      return getCurrentServer();
   }

   public static boolean isServerRunning() {
      MinecraftServer server = getCurrentServer();
      return server != null && server.isRunning();
   }
}
