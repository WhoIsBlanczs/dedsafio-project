package net.ruleta.compat;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;

/** En 26.3 la hora del mundo es el reloj "minecraft:overworld" en vez de setDayTime/getDayTime. */
public final class Reloj {
   private Reloj() {
   }

   private static Holder<WorldClock> reloj(MinecraftServer server) {
      return server.registryAccess().lookupOrThrow(Registries.WORLD_CLOCK).getOrThrow(WorldClocks.OVERWORLD);
   }

   public static long hora(MinecraftServer server) {
      return server.overworld().getOverworldClockTime();
   }

   public static void poner(MinecraftServer server, long hora) {
      server.overworld().clockManager().setTotalTicks(reloj(server), hora);
      server.getPlayerList().broadcastAll(server.overworld().clockManager().createFullSyncPacket());
   }
}
