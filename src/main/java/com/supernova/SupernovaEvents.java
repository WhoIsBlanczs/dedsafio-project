package com.supernova;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.ruleta.compat.EntityJoinLevelEvent;
import net.ruleta.compat.EntityLeaveLevelEvent;
import net.ruleta.compat.SubscribeEvent;

public class SupernovaEvents {
   @SubscribeEvent
   public static void onJoin(EntityJoinLevelEvent e) { }

   @SubscribeEvent
   public static void onLeave(EntityLeaveLevelEvent e) { }

   private static void sendAll(ServerPlayer p) { }

   public static void onLogin(ServerPlayer p) {
      sendAll(p);
   }

   public static void onDimension(ServerPlayer p) {
      sendAll(p);
   }

   public static void onRespawn(ServerPlayer p, boolean vivo) {
      sendAll(p);
   }
}
