package net.owleafstudio.modminds.compat;

import javax.annotation.Nullable;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarted;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopping;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class NetworkCompat {
   private static volatile MinecraftServer currentServer;
   private static boolean commonInstalled;
   private static boolean clientInstalled;

   private NetworkCompat() {
   }

   public static synchronized void installCommon() {
      if (!commonInstalled) {
         commonInstalled = true;
         ServerLifecycleEvents.SERVER_STARTED.register((ServerStarted)server -> currentServer = server);
         ServerLifecycleEvents.SERVER_STOPPING.register((ServerStopping)server -> currentServer = null);

         for (SimpleChannel channel : NetworkRegistry.getChannels()) {
            channel.installCommon();
         }
      }
   }

   public static synchronized void installClient() {
      if (!clientInstalled) {
         clientInstalled = true;

         for (SimpleChannel channel : NetworkRegistry.getChannels()) {
            channel.installClient();
         }
      }
   }

   @Nullable
   public static MinecraftServer getServer() {
      return currentServer;
   }
}
