package com.watonstudio.creepermetalico;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRules;

public final class EclipseManager {
   private static final long FIXED_TIME = 4000L;
   private static boolean active = false;
   private static int startTick = 0;
   private static boolean previousDaylightCycle = true;

   private EclipseManager() {
   }

   public static boolean isActive() {
      return active;
   }

   public static void reset() {
      active = false;
      startTick = 0;
      previousDaylightCycle = true;
   }

   public static EclipsePayload currentPayload(MinecraftServer server) {
      int age = active ? Math.max(0, server.getTickCount() - startTick) : 0;
      return new EclipsePayload(active, age);
   }

   public static boolean start(MinecraftServer server) {
      if (active) {
         return false;
      }

      active = true;
      startTick = server.getTickCount();
      previousDaylightCycle = server.getGameRules().get(net.minecraft.world.level.gamerules.GameRules.ADVANCE_TIME);
      server.getGameRules().set(net.minecraft.world.level.gamerules.GameRules.ADVANCE_TIME, false, server);
      ServerLevel overworld = server.overworld();
      if (overworld != null) {
         long t = com.watonstudio.creepermetalico.compat.Reloj.hora(overworld.getServer());
         long d = Math.floorMod(t, 24000L);
         if (d < 2000L || d > 10000L) {
            long base = t - d;
            long target = d > 10000L ? base + 24000L + 4000L : base + 4000L;
            com.watonstudio.creepermetalico.compat.Reloj.poner(overworld.getServer(), target);
         }

         net.minecraft.world.level.saveddata.WeatherData clima = overworld.getWeatherData();
         clima.setClearWeatherTime(24000);
         clima.setRainTime(0);
         clima.setThunderTime(0);
         clima.setRaining(false);
         clima.setThundering(false);
      }

      broadcast(server, new EclipsePayload(true, 0));
      return true;
   }

   public static boolean stop(MinecraftServer server) {
      if (!active) {
         return false;
      }

      active = false;
      restoreDaylight(server);
      broadcast(server, new EclipsePayload(false, 0));
      return true;
   }

   public static void onServerStopping(MinecraftServer server) {
      if (active) {
         active = false;
         restoreDaylight(server);
      }
   }

   private static void restoreDaylight(MinecraftServer server) {
      server.getGameRules().set(net.minecraft.world.level.gamerules.GameRules.ADVANCE_TIME, previousDaylightCycle, server);
   }

   private static void broadcast(MinecraftServer server, EclipsePayload payload) {
      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
         if (ServerPlayNetworking.canSend(player, EclipsePayload.ID)) {
            ServerPlayNetworking.send(player, payload);
         }
      }
   }
}
