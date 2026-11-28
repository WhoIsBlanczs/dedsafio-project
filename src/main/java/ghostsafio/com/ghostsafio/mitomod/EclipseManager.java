package com.ghostsafio.mitomod;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Join;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class EclipseManager {
   public static final int DEFAULT_SECONDS = 90;
   private static final ResourceLocation SOUND_ID = ResourceLocation.fromNamespaceAndPath("ghostsafio_eclipse", "eclipse");
   public static final SoundEvent ECLIPSE_SOUND = (SoundEvent)Registry.register(
      BuiltInRegistries.SOUND_EVENT, SOUND_ID, SoundEvent.createVariableRangeEvent(SOUND_ID)
   );
   private static final int SOUND_PEAK_TICKS = 900;
   private static MinecraftServer server;
   private static int soundTick;
   private static boolean active;
   private static int duration;
   private static int ticks;
   private static boolean announcedTotal;

   public static void register() {
      CommandRegistrationCallback.EVENT
         .register(
            (CommandRegistrationCallback)(dispatcher, registryAccess, environment) -> dispatcher.register(
               (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("dev").requires(source -> source.hasPermission(2)))
                  .then(
                     ((LiteralArgumentBuilder)Commands.literal("eclipse")
                           .then(
                              ((LiteralArgumentBuilder)Commands.literal("start").executes(ctx -> start((CommandSourceStack)ctx.getSource(), 90)))
                                 .then(
                                    Commands.argument("segundos", IntegerArgumentType.integer(15, 900))
                                       .executes(ctx -> start((CommandSourceStack)ctx.getSource(), IntegerArgumentType.getInteger(ctx, "segundos")))
                                 )
                           ))
                        .then(Commands.literal("stop").executes(ctx -> stop()))
                  )
            )
         );
      ServerTickEvents.END_SERVER_TICK.register(EclipseManager::tick);
      ServerLifecycleEvents.SERVER_STOPPED.register((ServerStopped)s -> {
         active = false;
         server = null;
      });
      ServerPlayConnectionEvents.JOIN.register((Join)(handler, sender, s) -> {
         if (active && ServerPlayNetworking.canSend(handler.getPlayer(), EclipsePayload.TYPE)) {
            ServerPlayNetworking.send(handler.getPlayer(), payload());
         }
      });
   }

   private static int start(CommandSourceStack source, int seconds) {
      if (active) {
         source.sendFailure(Component.literal("Ya hay un eclipse en curso (usa /dev eclipse stop)"));
         return 0;
      } else {
         float timeOfDay = source.getLevel().getTimeOfDay(1.0F);
         double sunHeight = Math.cos(timeOfDay * 2.0 * Math.PI);
         if (sunHeight < 0.15) {
            source.sendFailure(Component.literal("El sol no esta en el cielo. Pon de dia primero, por ejemplo: /time set 3000"));
            return 0;
         } else {
            server = source.getServer();
            active = true;
            duration = seconds * 20;
            ticks = 0;
            announcedTotal = false;
            soundTick = Math.max(1, duration / 2 - 900);
            server.getPlayerList().broadcastSystemMessage(Component.literal("¡Eclipse solar! Mira hacia el sol."), false);
            sendToAll(payload());
            return 1;
         }
      }
   }

   private static void tick(MinecraftServer s) {
      if (active) {
         ticks++;
         if (ticks % 20 == 0) {
            sendToAll(payload());
         }

         if (ticks == soundTick) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
               player.playNotifySound(ECLIPSE_SOUND, SoundSource.MASTER, 1.0F, 1.0F);
            }
         }

         if (!announcedTotal && ticks >= duration * 0.45) {
            announcedTotal = true;
            server.getPlayerList().broadcastSystemMessage(Component.literal("Eclipse total: no te alejes, casi no se ve nada"), false);
         }

         if (ticks >= duration) {
            stop();
         }
      }
   }

   private static int stop() {
      if (!active) {
         return 0;
      }

      active = false;
      sendToAll(new EclipsePayload(false, 0, 0));

      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
         player.connection.send(new ClientboundStopSoundPacket(SOUND_ID, SoundSource.MASTER));
      }

      return 1;
   }

   private static EclipsePayload payload() {
      return new EclipsePayload(true, duration, ticks);
   }

   private static void sendToAll(EclipsePayload payload) {
      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
         if (ServerPlayNetworking.canSend(player, EclipsePayload.TYPE)) {
            ServerPlayNetworking.send(player, payload);
         }
      }
   }
}
