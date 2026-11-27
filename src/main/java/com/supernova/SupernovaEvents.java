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
   public static void onJoin(EntityJoinLevelEvent e) {
      if (!e.getLevel().isClientSide() && e.getEntity() instanceof SupernovaEntity n) {
         if (e.getLevel() instanceof ServerLevel sl) {
            try {
               ChunkPos cp = n.chunkPosition();
               sl.setChunkForced(cp.x(), cp.z(), true);
            } catch (Exception var4) {
            }
         }

         SupernovaNet.broadcast(n, false);
      }
   }

   @SubscribeEvent
   public static void onLeave(EntityLeaveLevelEvent e) {
      if (!e.getLevel().isClientSide() && e.getEntity() instanceof SupernovaEntity n) {
         SupernovaNet.broadcast(n, true);
         if (e.getLevel() instanceof ServerLevel sl && sl.getServer().isRunning()) {
            try {
               ChunkPos cp = n.chunkPosition();
               sl.setChunkForced(cp.x(), cp.z(), false);
            } catch (Exception var4) {
            }
         }
      }
   }

   private static void sendAll(ServerPlayer p) {
      for (SupernovaEntity n : p.level().getEntities((EntityTypeTest)SupernovaMod.SUPERNOVA.get(), x -> true)) {
         SupernovaNet.sendTo(p, n);
      }
   }

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
