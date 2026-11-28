package com.watonstudio.creepermetalico;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.EndTick;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class CreeperQteManager {
   public static final int KEY_COUNT = 6;
   public static final int STEP_TIMEOUT_TICKS = 60;
   private static final double QTE_RADIUS = 8.0;
   private static final Random RANDOM = new Random();
   private static final Map<UUID, CreeperQteManager.Session> BY_ENTITY = new HashMap<>();
   private static final Map<UUID, CreeperQteManager.Session> BY_PLAYER = new HashMap<>();
   private static final Set<UUID> ALLOW_ONCE = new HashSet<>();
   private static final Map<UUID, UUID> PROTECTED_PLAYER = new HashMap<>();
   private static final ThreadLocal<Boolean> FLORAL_EXPLOSION_ACTIVE = ThreadLocal.withInitial(() -> false);

   private CreeperQteManager() {
   }

   public static void register() {
      ServerTickEvents.END_SERVER_TICK.register((EndTick)server -> {
         long now = System.currentTimeMillis();

         for (CreeperQteManager.Session session : new ArrayList<>(BY_ENTITY.values())) {
            if (session.player.isRemoved() || !session.player.isAlive()) {
               removeSession(session, true);
            } else if (session.explosionAction != null && session.entity.isRemoved()) {
               removeSession(session, true);
            } else {
               if (session.explosionAction == null) {
                  session.player.setDeltaMovement(0.0, 0.0, 0.0);
               }

               if (now >= session.deadline) {
                  restartSequence(session);
               }
            }
         }
      });
      ServerPlayNetworking.registerGlobalReceiver(CreeperQteInputPayload.ID, (payload, context) -> handleInput(context.player(), payload));
   }

   public static void beginFloralExplosionDamage() {
      FLORAL_EXPLOSION_ACTIVE.set(true);
   }

   public static void endFloralExplosionDamage() {
      FLORAL_EXPLOSION_ACTIVE.remove();
   }

   public static boolean isFloralExplosionDamageActive() {
      return Boolean.TRUE.equals(FLORAL_EXPLOSION_ACTIVE.get());
   }

   public static boolean interceptExplosion(Entity entity, Runnable explosionAction) {
      UUID entityId = entity.getUUID();
      if (ALLOW_ONCE.remove(entityId)) {
         return false;
      }

      if (BY_ENTITY.containsKey(entityId)) {
         return true;
      }

      if (entity.level() instanceof ServerLevel world) {
         ServerPlayer player = world.players()
            .stream()
            .filter(p -> p.isAlive() && !p.isCreative() && !p.isSpectator() && p.distanceToSqr(entity) <= 64.0)
            .min(Comparator.comparingDouble(p -> p.distanceToSqr(entity)))
            .orElse(null);
         if (player == null) {
            return false;
         }

         if (BY_PLAYER.containsKey(player.getUUID())) {
            return true;
         }

         CreeperQteManager.Session session = new CreeperQteManager.Session(entity, player, explosionAction);
         BY_ENTITY.put(entityId, session);
         BY_PLAYER.put(player.getUUID(), session);
         restartSequence(session);
         return true;
      } else {
         return false;
      }
   }

   public static void startAfterExplosion(Entity entity) {
      if (entity.level() instanceof ServerLevel world) {
         ServerPlayer player = world.players()
            .stream()
            .filter(p -> p.isAlive() && !p.isCreative() && !p.isSpectator() && p.distanceToSqr(entity) <= 64.0)
            .min(Comparator.comparingDouble(p -> p.distanceToSqr(entity)))
            .orElse(null);
         if (player != null && !BY_PLAYER.containsKey(player.getUUID()) && !BY_ENTITY.containsKey(entity.getUUID())) {
            CreeperQteManager.Session session = new CreeperQteManager.Session(entity, player, null);
            BY_ENTITY.put(entity.getUUID(), session);
            BY_PLAYER.put(player.getUUID(), session);
            restartSequence(session);
         }
      }
   }

   private static void handleInput(ServerPlayer player, CreeperQteInputPayload payload) {
      CreeperQteManager.Session session = BY_PLAYER.get(player.getUUID());
      if (session != null && payload.step() == session.step) {
         if (System.currentTimeMillis() >= session.deadline) {
            restartSequence(session);
         } else if (payload.key() != session.sequence[session.step]) {
            restartSequence(session);
         } else {
            session.step++;
            if (session.step >= 6) {
               finishSequence(session);
            } else {
               session.deadline = System.currentTimeMillis() + 3000L;
               sendState(session);
            }
         }
      }
   }

   private static void restartSequence(CreeperQteManager.Session session) {
      for (int i = 0; i < 6; i++) {
         session.sequence[i] = RANDOM.nextInt(4);
      }

      session.step = 0;
      session.deadline = System.currentTimeMillis() + 3000L;
      sendState(session);
   }

   private static void sendState(CreeperQteManager.Session session) {
      ServerPlayNetworking.send(session.player, new CreeperQteStatePayload(session.entity.getId(), session.sequence[session.step], session.step, 6, 60));
      broadcastVisualState(session, true);
   }

   private static void finishSequence(CreeperQteManager.Session session) {
      removeSession(session, true);
      if (session.explosionAction != null) {
         UUID playerId = session.player.getUUID();
         UUID entityId = session.entity.getUUID();
         PROTECTED_PLAYER.put(playerId, entityId);
         ALLOW_ONCE.add(entityId);

         try {
            session.explosionAction.run();
            if (!session.entity.isRemoved()) {
               session.entity.discard();
            }
         } finally {
            ALLOW_ONCE.remove(entityId);
            PROTECTED_PLAYER.remove(playerId);
         }
      }
   }

   private static void removeSession(CreeperQteManager.Session session, boolean hideOverlay) {
      broadcastVisualState(session, false);
      BY_ENTITY.remove(session.entity.getUUID(), session);
      BY_PLAYER.remove(session.player.getUUID(), session);
      if (hideOverlay && !session.player.isRemoved()) {
         ServerPlayNetworking.send(session.player, new CreeperQteStatePayload(session.entity.getId(), -1, -1, 6, 0));
      }
   }

   public static boolean isProtectedFromExplosion(Entity player, Entity explodingEntity) {
      if (player != null && explodingEntity != null) {
         UUID protectedEntity = PROTECTED_PLAYER.get(player.getUUID());
         return protectedEntity != null && protectedEntity.equals(explodingEntity.getUUID());
      } else {
         return false;
      }
   }

   private static void broadcastVisualState(CreeperQteManager.Session session, boolean active) {
      if (session.player.level() instanceof ServerLevel world) {
         for (ServerPlayer observer : world.players()) {
            if (observer.distanceToSqr(session.player) <= 4096.0) {
               ServerPlayNetworking.send(observer, new CreeperQteVisualPayload(session.player.getId(), active ? Math.min(session.step, 5) : 0, active));
            }
         }
      }
   }

   private static final class Session {
      private final Entity entity;
      private final ServerPlayer player;
      private final Runnable explosionAction;
      private final int[] sequence = new int[6];
      private int step;
      private long deadline;

      private Session(Entity entity, ServerPlayer player, Runnable explosionAction) {
         this.entity = entity;
         this.player = player;
         this.explosionAction = explosionAction;
      }
   }
}
