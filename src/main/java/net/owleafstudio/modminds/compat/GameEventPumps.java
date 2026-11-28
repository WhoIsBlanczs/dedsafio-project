package net.owleafstudio.modminds.compat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.AllowDamage;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.AllowDeath;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.AfterRespawn;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.CopyFrom;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarted;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopping;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.EndTick;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.StartTick;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.Before;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Disconnect;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Join;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameEventPumps {
   private static final Logger LOGGER = LoggerFactory.getLogger("modminds-compat");
   private static volatile MinecraftServer currentServer;
   private static final ThreadLocal<Boolean> REENTRANT = ThreadLocal.withInitial(() -> Boolean.FALSE);
   private static final Map<UUID, ResourceKey<Level>> playerDimensions = new ConcurrentHashMap<>();
   private static boolean serverInstalled;

   private GameEventPumps() {
   }

   public static MinecraftServer currentServer() {
      return currentServer;
   }

   public static void installServer() {
      if (!serverInstalled) {
         serverInstalled = true;
         CommandRegistrationCallback.EVENT
            .register((CommandRegistrationCallback)(dispatcher, registryAccess, environment) -> post(new RegisterCommandsEvent(dispatcher)));
         ServerLifecycleEvents.SERVER_STARTING.register((ServerStarting)server -> {
            currentServer = server;
            CompatAccess.bindRegistries(server.registryAccess(), true);
            post(new ServerStartingEvent(server));
         });
         ServerLifecycleEvents.SERVER_STARTED.register((ServerStarted)server -> post(new ServerStartedEvent(server)));
         ServerLifecycleEvents.SERVER_STOPPING.register((ServerStopping)server -> post(new ServerStoppingEvent(server)));
         ServerLifecycleEvents.SERVER_STOPPED.register((ServerStopped)server -> {
            CompatAccess.bindRegistries(null, true);
            currentServer = null;
         });
         ServerTickEvents.START_SERVER_TICK.register((StartTick)server -> {
            post(new TickEvent.ServerTickEvent(TickEvent.Phase.START));
            playerTick(server, TickEvent.Phase.START);
         });
         ServerTickEvents.END_SERVER_TICK.register((EndTick)server -> {
            playerTick(server, TickEvent.Phase.END);
            post(new TickEvent.ServerTickEvent(TickEvent.Phase.END));
            WorkQueue.advanceTick();
         });
         ServerTickEvents.START_LEVEL_TICK.register(GameEventPumps::tickLivingEntities);
         ServerPlayConnectionEvents.JOIN.register((Join)(handler, sender, server) -> post(new PlayerEvent.PlayerLoggedInEvent(handler.getPlayer())));
         ServerPlayConnectionEvents.DISCONNECT.register((Disconnect)(handler, server) -> playerDimensions.remove(handler.getPlayer().getUUID()));
         ServerPlayerEvents.COPY_FROM.register((CopyFrom)(oldPlayer, newPlayer, alive) -> post(new PlayerEvent.Clone(newPlayer, oldPlayer, !alive)));
         ServerPlayerEvents.AFTER_RESPAWN.register((AfterRespawn)(oldPlayer, newPlayer, alive) -> post(new PlayerEvent.PlayerRespawnEvent(newPlayer, alive)));
         ServerEntityEvents.ENTITY_LOAD.register(GameEventPumps::onEntityLoaded);
         ServerLivingEntityEvents.ALLOW_DAMAGE.register((AllowDamage)(entity, source, amount) -> allowDamage(entity, source, amount));
         ServerLivingEntityEvents.ALLOW_DEATH.register((AllowDeath)(entity, source, damageAmount) -> {
            LivingDeathEvent event = new LivingDeathEvent(entity, source);
            post(event);
            return !event.isCanceled() && event.getResult() != Event.Result.DENY;
         });
         PlayerBlockBreakEvents.BEFORE.register((Before)(level, player, pos, state, blockEntity) -> {
            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, pos, player, state);
            post(event);
            return !event.isCanceled() && event.getResult() != Event.Result.DENY;
         });
         UseEntityCallback.EVENT.register((UseEntityCallback)(player, level, hand, entity, hitResult) -> {
            PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(player, hand, entity);
            post(event);
            return !event.isCanceled() && event.getResult() != Event.Result.DENY ? InteractionResult.PASS : InteractionResult.FAIL;
         });
         UseBlockCallback.EVENT
            .register(
               (UseBlockCallback)(player, level, hand, hitResult) -> {
                  PlayerInteractEvent.RightClickBlock event = new PlayerInteractEvent.RightClickBlock(
                     player, hand, hitResult.getBlockPos(), hitResult.getDirection()
                  );
                  post(event);
                  return !event.isCanceled() && event.getResult() != Event.Result.DENY ? InteractionResult.PASS : InteractionResult.FAIL;
               }
            );
      }
   }

   private static void playerTick(MinecraftServer server, TickEvent.Phase phase) {
      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
         if (phase == TickEvent.Phase.START) {
            ResourceKey<Level> previous = playerDimensions.put(player.getUUID(), player.level().dimension());
            if (previous != null && !previous.equals(player.level().dimension())) {
               post(new PlayerEvent.PlayerChangedDimensionEvent(player, previous, player.level().dimension()));
            }
         }

         post(new TickEvent.PlayerTickEvent(phase, player));
      }
   }

   private static void tickLivingEntities(ServerLevel level) {
      List<LivingEntity> living = new ArrayList<>();

      for (Entity entity : level.getAllEntities()) {
         if (entity instanceof LivingEntity livingEntity && !livingEntity.isRemoved()) {
            living.add(livingEntity);
         }
      }

      for (LivingEntity livingEntity : living) {
         post(new LivingEvent.LivingTickEvent(livingEntity));
         if (livingEntity.isUsingItem() && livingEntity.getUseItemRemainingTicks() == 1) {
            post(new LivingEntityUseItemEvent.Finish(livingEntity, livingEntity.getUseItem()));
         }
      }
   }

   private static void onEntityLoaded(Entity entity, ServerLevel level) {
      EntityJoinLevelEvent join = new EntityJoinLevelEvent(entity, level);
      post(join);
      if (!join.isCanceled() && join.getResult() != Event.Result.DENY) {
         if (entity instanceof ItemEntity itemEntity
            && itemEntity.getOwner() instanceof Player player
            && !player.isRemoved()
            && itemEntity.getAge() == 0) {
            ItemTossEvent toss = new ItemTossEvent(itemEntity, player);
            post(toss);
            if (toss.isCanceled() || toss.getResult() == Event.Result.DENY) {
               itemEntity.discard();
            }
         }
      } else {
         entity.discard();
      }
   }

   private static boolean allowDamage(LivingEntity entity, DamageSource source, float amount) {
      if (REENTRANT.get()) {
         return true;
      } else {
         LivingAttackEvent attack = new LivingAttackEvent(entity, source, amount);
         post(attack);
         if (!attack.isCanceled() && attack.getResult() != Event.Result.DENY) {
            LivingHurtEvent hurt = new LivingHurtEvent(entity, source, amount);
            post(hurt);
            if (hurt.isCanceled() || hurt.getResult() == Event.Result.DENY) {
               return false;
            } else if (Float.compare(hurt.getAmount(), amount) != 0) {
               REENTRANT.set(Boolean.TRUE);

               try {
                  entity.hurt(source, hurt.getAmount());
               } finally {
                  REENTRANT.set(Boolean.FALSE);
               }

               return false;
            } else {
               return true;
            }
         } else {
            return false;
         }
      }
   }

   static void post(Event event) {
      try {
         ForgeEventBus.postToBoth(event);
      } catch (Throwable var2) {
         LOGGER.error("Pumping {} failed", event.getClass().getSimpleName(), var2);
      }
   }
}
