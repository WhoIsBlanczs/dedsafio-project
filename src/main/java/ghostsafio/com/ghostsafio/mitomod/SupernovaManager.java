package com.ghostsafio.mitomod;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Join;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SupernovaManager {
   private static final int DEFAULT_COUNTDOWN_SECONDS = 30;
   private static final int DEFAULT_WAVE_SECONDS = 15;
   private static final double DEFAULT_RADIUS = 128.0;
   private static final int FADE_SECONDS = 40;
   private static final int SOUND_LEAD_TICKS = 159;
   private static final List<AABB> BUNKERS = new ArrayList<>();
   private static boolean active = false;
   private static MinecraftServer server;
   private static ServerLevel level;
   private static Vec3 center;
   private static double radius;
   private static int countdownTicks;
   private static int waveTicks;
   private static int ticks;
   private static long savedDayTime;
   private static long darkDelta;

   public static void register() {
      loadBunkers();
      CommandRegistrationCallback.EVENT
         .register(
            (CommandRegistrationCallback)(dispatcher, registryAccess, environment) -> dispatcher.register(
               (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("dev").requires(source -> source.hasPermission(2)))
                  .then(
                     ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("supernova").then(startNode()))
                           .then(Commands.literal("stop").executes(ctx -> stop())))
                        .then(bunkerNode())
                  )
            )
         );
      ServerTickEvents.END_SERVER_TICK.register(SupernovaManager::tick);
      ServerLifecycleEvents.SERVER_STOPPED.register((ServerStopped)stoppedServer -> {
         active = false;
         server = null;
         level = null;
         center = null;
      });
      ServerPlayConnectionEvents.JOIN.register((Join)(handler, sender, joinedServer) -> {
         if (active && ServerPlayNetworking.canSend(handler.getPlayer(), SupernovaPayload.TYPE)) {
            ServerPlayNetworking.send(handler.getPlayer(), syncPayload());
         }
      });
   }

   private static LiteralArgumentBuilder<CommandSourceStack> startNode() {
      return (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal("start")
         .then(
            ((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3())
                  .executes(ctx -> start((CommandSourceStack)ctx.getSource(), Vec3Argument.getVec3(ctx, "pos"), 128.0, 30, 15)))
               .then(
                  ((RequiredArgumentBuilder)Commands.argument("radius", DoubleArgumentType.doubleArg(8.0, 1000.0))
                        .executes(
                           ctx -> start(
                              (CommandSourceStack)ctx.getSource(), Vec3Argument.getVec3(ctx, "pos"), DoubleArgumentType.getDouble(ctx, "radius"), 30, 15
                           )
                        ))
                     .then(
                        ((RequiredArgumentBuilder)Commands.argument("countdown", IntegerArgumentType.integer(1, 3600))
                              .executes(
                                 ctx -> start(
                                    (CommandSourceStack)ctx.getSource(),
                                    Vec3Argument.getVec3(ctx, "pos"),
                                    DoubleArgumentType.getDouble(ctx, "radius"),
                                    IntegerArgumentType.getInteger(ctx, "countdown"),
                                    15
                                 )
                              ))
                           .then(
                              Commands.argument("wave", IntegerArgumentType.integer(5, 600))
                                 .executes(
                                    ctx -> start(
                                       (CommandSourceStack)ctx.getSource(),
                                       Vec3Argument.getVec3(ctx, "pos"),
                                       DoubleArgumentType.getDouble(ctx, "radius"),
                                       IntegerArgumentType.getInteger(ctx, "countdown"),
                                       IntegerArgumentType.getInteger(ctx, "wave")
                                    )
                                 )
                           )
                     )
               )
         );
   }

   private static LiteralArgumentBuilder<CommandSourceStack> bunkerNode() {
      return (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("bunker")
               .then(
                  Commands.literal("add")
                     .then(
                        Commands.argument("from", BlockPosArgument.blockPos())
                           .then(
                              Commands.argument("to", BlockPosArgument.blockPos())
                                 .executes(
                                    ctx -> addBunker(
                                       (CommandSourceStack)ctx.getSource(), BlockPosArgument.getBlockPos(ctx, "from"), BlockPosArgument.getBlockPos(ctx, "to")
                                    )
                                 )
                           )
                     )
               ))
            .then(Commands.literal("list").executes(ctx -> listBunkers((CommandSourceStack)ctx.getSource()))))
         .then(Commands.literal("clear").executes(ctx -> clearBunkers((CommandSourceStack)ctx.getSource())));
   }

   private static int addBunker(CommandSourceStack source, BlockPos a, BlockPos b) {
      AABB box = new AABB(
         Math.min(a.getX(), b.getX()),
         Math.min(a.getY(), b.getY()),
         Math.min(a.getZ(), b.getZ()),
         Math.max(a.getX(), b.getX()) + 1,
         Math.max(a.getY(), b.getY()) + 1,
         Math.max(a.getZ(), b.getZ()) + 1
      );
      BUNKERS.add(box);
      saveBunkers();
      source.sendSuccess(() -> Component.literal("Búnker agregado (" + BUNKERS.size() + " en total). Los jugadores adentro no reciben daño."), true);
      return 1;
   }

   private static int listBunkers(CommandSourceStack source) {
      if (BUNKERS.isEmpty()) {
         source.sendSuccess(() -> Component.literal("No hay búnkeres."), false);
         return 0;
      }

      for (int i = 0; i < BUNKERS.size(); i++) {
         AABB b = BUNKERS.get(i);
         String text = String.format(
            Locale.ROOT, "Búnker %d: %.0f %.0f %.0f  a  %.0f %.0f %.0f", i + 1, b.minX, b.minY, b.minZ, b.maxX - 1.0, b.maxY - 1.0, b.maxZ - 1.0
         );
         source.sendSuccess(() -> Component.literal(text), false);
      }

      return BUNKERS.size();
   }

   private static int clearBunkers(CommandSourceStack source) {
      BUNKERS.clear();
      saveBunkers();
      source.sendSuccess(() -> Component.literal("Se borraron todos los búnkeres."), true);
      return 1;
   }

   private static boolean inBunker(Vec3 pos) {
      for (AABB box : BUNKERS) {
         if (box.contains(pos)) {
            return true;
         }
      }

      return false;
   }

   private static Path bunkerFile() {
      return FabricLoader.getInstance().getConfigDir().resolve("ghostsafio_bunkers.txt");
   }

   private static void loadBunkers() {
      BUNKERS.clear();
      Path file = bunkerFile();
      if (Files.exists(file)) {
         try {
            for (String line : Files.readAllLines(file)) {
               String[] p = line.trim().split(" ");
               if (p.length == 6) {
                  BUNKERS.add(
                     new AABB(
                        Double.parseDouble(p[0]),
                        Double.parseDouble(p[1]),
                        Double.parseDouble(p[2]),
                        Double.parseDouble(p[3]),
                        Double.parseDouble(p[4]),
                        Double.parseDouble(p[5])
                     )
                  );
               }
            }
         } catch (IOException | NumberFormatException e) {
            Ghostsafio.LOGGER.warn("No se pudieron leer los búnkeres", e);
         }
      }
   }

   private static void saveBunkers() {
      List<String> lines = new ArrayList<>();

      for (AABB b : BUNKERS) {
         lines.add(String.format(Locale.ROOT, "%s %s %s %s %s %s", b.minX, b.minY, b.minZ, b.maxX, b.maxY, b.maxZ));
      }

      try {
         Files.write(bunkerFile(), lines);
      } catch (IOException e) {
         Ghostsafio.LOGGER.warn("No se pudieron guardar los búnkeres", e);
      }
   }

   private static int start(CommandSourceStack source, Vec3 pos, double r, int countdownSeconds, int waveSeconds) {
      if (active) {
         source.sendFailure(Component.literal("Ya hay una supernova activa."));
         return 0;
      }

      active = true;
      server = source.getServer();
      level = source.getLevel();
      center = pos;
      radius = r;
      int minCountdownTicks = 160;
      countdownTicks = Math.max(countdownSeconds * 20, minCountdownTicks);
      int shownSeconds = countdownTicks / 20;
      waveTicks = waveSeconds * 20;
      ticks = 0;
      savedDayTime = level.getDayTime();
      long timeOfDay = savedDayTime % 24000L;
      if (timeOfDay >= 13000L && timeOfDay <= 23000L) {
         darkDelta = 0L;
      } else if (timeOfDay < 13000L) {
         darkDelta = 18000L - timeOfDay;
      } else {
         darkDelta = 42000L - timeOfDay;
      }

      source.sendSuccess(() -> Component.literal(String.format(Locale.ROOT, "Supernova iniciada en %.1f %.1f %.1f", pos.x, pos.y, pos.z)), true);
      if (shownSeconds != countdownSeconds) {
         source.sendSuccess(() -> Component.literal("La cuenta atrás se alargó a " + shownSeconds + " s para que el sonido coincida con la explosión."), false);
      }

      broadcast("¡La estrella colapsará en " + shownSeconds + " segundos! ¡Aléjate todo lo que puedas!");
      sendToAll(syncPayload());
      return 1;
   }

   private static void tick(MinecraftServer minecraftServer) {
      if (active) {
         ticks++;
         if (ticks % 20 == 0) {
            sendToAll(syncPayload());
         }

         updateTime();
         if (ticks == Math.max(1, countdownTicks - 159)) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
               player.playNotifySound(ModSounds.SUPERNOVA, SoundSource.MASTER, 1.0F, 1.0F);
            }
         }

         if (ticks <= countdownTicks && (ticks - 1) % 20 == 0) {
            int secondsLeft = (countdownTicks - ticks) / 20 + 1;

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
               player.displayClientMessage(
                  Component.literal("Supernova en " + secondsLeft + "...").withStyle(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.BOLD}), true
               );
            }
         }

         if (ticks == countdownTicks) {
            broadcast("¡Supernova! La onda expansiva se acerca.");
         }

         if (ticks > countdownTicks) {
            double waveRadius = Math.min(radius, radius * (ticks - countdownTicks) / waveTicks);

            for (ServerPlayer player : new ArrayList(level.players())) {
               if (player.isAlive()
                  && !player.isSpectator()
                  && !player.isCreative()
                  && !inBunker(player.position())
                  && player.position().distanceTo(center) <= waveRadius) {
                  player.hurt(level.damageSources().genericKill(), Float.MAX_VALUE);
               }
            }
         }

         if (ticks >= countdownTicks + waveTicks + 800) {
            stop();
         }
      }
   }

   private static void updateTime() {
      if (darkDelta != 0L) {
         long dark = savedDayTime + darkDelta;
         int endOfWave = countdownTicks + waveTicks;
         if (ticks <= countdownTicks) {
            double progress = (double)ticks / Math.max(1, countdownTicks);
            level.setDayTime(savedDayTime + (long)(darkDelta * smooth(progress)));
         } else if (ticks <= endOfWave) {
            level.setDayTime(dark);
         } else {
            int fadeTicks = 800;
            long back = savedDayTime + endOfWave + fadeTicks;

            while (back < dark) {
               back += 24000L;
            }

            double progress = Math.min(1.0, (double)(ticks - endOfWave) / fadeTicks);
            level.setDayTime(dark + (long)((back - dark) * smooth(progress)));
         }
      }
   }

   private static double smooth(double t) {
      return t * t * (3.0 - 2.0 * t);
   }

   private static int stop() {
      if (!active) {
         return 0;
      }

      active = false;
      level.setDayTime(savedDayTime + ticks);
      sendToAll(new SupernovaPayload(false, center.x, center.y, center.z, radius, 0, 0, 0, 0));
      broadcast("Supernova detenida");
      return 1;
   }

   private static SupernovaPayload syncPayload() {
      return new SupernovaPayload(true, center.x, center.y, center.z, radius, countdownTicks, waveTicks, 800, ticks);
   }

   private static void sendToAll(SupernovaPayload payload) {
      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
         if (ServerPlayNetworking.canSend(player, SupernovaPayload.TYPE)) {
            ServerPlayNetworking.send(player, payload);
         }
      }
   }

   private static void broadcast(String text) {
      server.getPlayerList().broadcastSystemMessage(Component.literal(text), false);
   }
}
