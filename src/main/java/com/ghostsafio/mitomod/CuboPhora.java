package com.ghostsafio.mitomod;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Disconnect;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public final class CuboPhora {
   public static final int DEFAULT_SIZE = 129;
   private static final SoundSource MUSIC_SOURCE = SoundSource.AMBIENT;
   private static final int MUSIC_TICKS = 3560;
   private static BlockState[] states;
   private static CuboPhora.Job job;
   private static BlockPos lastOrigin;
   private static int lastSize;
   private static final Map<UUID, Long> musicStart = new HashMap<>();
   private static final Map<UUID, Integer> outsideChecks = new HashMap<>();

   private CuboPhora() {
   }

   private static BlockState[] states() {
      if (states == null) {
         BlockState[] s = new BlockState[23];
         s[0] = Blocks.AIR.defaultBlockState();
         s[1] = ModPhora.PANEL.defaultBlockState();
         s[2] = ModPhora.TILE.defaultBlockState();
         s[3] = ModPhora.ORANGE.defaultBlockState();
         s[4] = ModPhora.CIRCUIT_ORANGE.defaultBlockState();
         s[5] = ModPhora.YELLOW.defaultBlockState();
         s[6] = ModPhora.TEAL.defaultBlockState();
         s[7] = ModPhora.CIRCUIT_TEAL.defaultBlockState();
         s[8] = ModPhora.RED.defaultBlockState();
         s[17] = ModPhora.IMPULSOR.defaultBlockState();
         s[18] = Blocks.COBBLESTONE.defaultBlockState();
         s[19] = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
         s[20] = Blocks.SPRUCE_PLANKS.defaultBlockState();
         s[21] = Blocks.IRON_CHAIN.defaultBlockState();
         s[22] = ModPhora.VERDE.defaultBlockState();
         BlockState ladder = Blocks.LADDER.defaultBlockState();
         s[9] = (BlockState)ladder.setValue(LadderBlock.FACING, Direction.EAST);
         s[10] = (BlockState)ladder.setValue(LadderBlock.FACING, Direction.WEST);
         s[11] = (BlockState)ladder.setValue(LadderBlock.FACING, Direction.SOUTH);
         s[12] = (BlockState)ladder.setValue(LadderBlock.FACING, Direction.NORTH);
         s[13] = Blocks.DEEPSLATE.defaultBlockState();
         s[14] = Blocks.COBBLED_DEEPSLATE.defaultBlockState();
         s[15] = Blocks.TUFF.defaultBlockState();
         s[16] = Blocks.ANDESITE.defaultBlockState();
         states = s;
      }

      return states;
   }

   public static void register() {
      CommandRegistrationCallback.EVENT
         .register(
            (CommandRegistrationCallback)(dispatcher, registryAccess, environment) -> dispatcher.register(
               (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("dev").requires(s -> com.ghostsafio.mitomod.compat.Compat.permiso(s, 2)))
                  .then(
                     ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("cubo")
                              .then(
                                 ((LiteralArgumentBuilder)Commands.literal("build").executes(ctx -> build((CommandSourceStack)ctx.getSource(), 129)))
                                    .then(
                                       Commands.argument("tamano", IntegerArgumentType.integer(97, 193))
                                          .executes(ctx -> build((CommandSourceStack)ctx.getSource(), IntegerArgumentType.getInteger(ctx, "tamano")))
                                    )
                              ))
                           .then(Commands.literal("ir").executes(ctx -> teleport((CommandSourceStack)ctx.getSource()))))
                        .then(Commands.literal("stop").executes(ctx -> stop((CommandSourceStack)ctx.getSource())))
                  )
            )
         );
      ServerTickEvents.END_SERVER_TICK.register(CuboPhora::tick);
      ServerLifecycleEvents.SERVER_STOPPED.register((ServerStopped)s -> {
         job = null;
         musicStart.clear();
         outsideChecks.clear();
      });
      ServerPlayConnectionEvents.DISCONNECT.register((Disconnect)(handler, s) -> {
         musicStart.remove(handler.getPlayer().getUUID());
         outsideChecks.remove(handler.getPlayer().getUUID());
      });
   }

   private static int build(CommandSourceStack source, int size) {
      if (job != null) {
         source.sendFailure(Component.literal("Ya se esta construyendo un cubo (usa /dev cubo stop para cancelar)"));
         return 0;
      } else {
         ServerLevel level = source.getLevel();
         int s = size | 1;
         BlockPos at = BlockPos.containing(source.getPosition());
         int surface = level.getHeight(Types.MOTION_BLOCKING_NO_LEAVES, at.getX(), at.getZ());
         int oy = surface - 6;
         if (oy >= level.getMinY() + 2 && oy + s <= level.getMaxY()) {
            BlockPos origin = new BlockPos(at.getX() - s / 2, oy, at.getZ() - s / 2);
            long seed = origin.asLong() * 31L + s;
            job = new CuboPhora.Job(level, origin, new CuboPhoraShape(s, seed));
            lastOrigin = origin;
            lastSize = s;
            source.sendSuccess(
               () -> Component.literal("Construyendo el Cubo Phora (" + s + " bloques de lado). Tarda unos segundos; alejate un poco para verlo."), true
            );
            return 1;
         } else {
            source.sendFailure(Component.literal("No cabe en altura aqui: prueba un tamano menor o un lugar mas bajo"));
            return 0;
         }
      }
   }

   private static int stop(CommandSourceStack source) {
      if (job == null) {
         source.sendFailure(Component.literal("No hay ninguna construccion en curso"));
         return 0;
      } else {
         job = null;
         source.sendSuccess(() -> Component.literal("Construccion cancelada (queda lo que ya se hizo)"), true);
         return 1;
      }
   }

   private static int teleport(CommandSourceStack source) {
      if (lastOrigin == null) {
         source.sendFailure(Component.literal("Todavia no has construido ningun cubo desde que arranco el servidor"));
         return 0;
      }

      try {
         ServerPlayer player = source.getPlayerOrException();
         int cx = lastOrigin.getX() + lastSize + 4;
         int cz = lastOrigin.getZ() + lastSize / 2;
         int cy = lastOrigin.getY() + 12 - 1 - 2 + 1;
         com.ghostsafio.mitomod.compat.Compat.teleport(player, source.getLevel(), cx + 0.5, cy, cz + 0.5, player.getYRot(), 20.0F);
         return 1;
      } catch (Exception e) {
         source.sendFailure(Component.literal("Solo un jugador puede usar esto"));
         return 0;
      }
   }

   private static void tick(MinecraftServer server) {
      if (job != null) {
         CuboPhora.Job j = job;
         boolean finished = false;

         try {
            finished = j.step();
         } catch (Exception e) {
            Ghostsafio.LOGGER.error("Error construyendo el Cubo Phora", e);
            job = null;
            server.getPlayerList().broadcastSystemMessage(Component.literal("El Cubo Phora fallo al construirse (mira el log del juego)"), false);
            return;
         }

         if (finished) {
            job = null;
            BlockPos o = j.origin;
            server.getPlayerList()
               .broadcastSystemMessage(
                  Component.literal(
                     "Una grieta se abrio en X: " + (o.getX() + j.S / 2) + " / Z: " + (o.getZ() + j.S / 2) + " y ha dejado entrar al Mundo un Cubo Phora."
                  ),
                  false
               );
         } else if (server.getTickCount() % 40 == 0) {
            int pct = (int)(100L * j.done / j.total);
            server.getPlayerList().broadcastSystemMessage(Component.literal("Cubo Phora: " + pct + "%"), true);
         }
      }

      if (server.getTickCount() % 20 == 0) {
         musicTick(server);
      }
   }

   private static boolean insideCube(ServerPlayer player) {
      return isInside(((net.minecraft.server.level.ServerLevel)player.level()), player.blockPosition());
   }

   public static boolean isInside(ServerLevel level, BlockPos p) {
      int top = level.getHeight(Types.MOTION_BLOCKING, p.getX(), p.getZ());
      if (top - 1 <= p.getY()) {
         return false;
      }

      BlockState s = level.getBlockState(new BlockPos(p.getX(), top - 1, p.getZ()));
      return ModPhora.BLOCKS.contains(s.getBlock());
   }

   private static void musicTick(MinecraftServer server) {
      long now = server.getTickCount();

      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
         UUID id = player.getUUID();
         boolean inside = insideCube(player);
         Long started = musicStart.get(id);
         if (inside) {
            outsideChecks.remove(id);
            if (started == null || now - started >= 3660L) {
               playMusic(player);
               musicStart.put(id, now);
            }
         } else if (started != null) {
            int n = outsideChecks.merge(id, 1, Integer::sum);
            if (n >= 3) {
               player.connection.send(new ClientboundStopSoundPacket(ModPhora.MUSICA_ID, MUSIC_SOURCE));
               musicStart.remove(id);
               outsideChecks.remove(id);
            }
         }
      }

      Iterator<UUID> it = musicStart.keySet().iterator();

      while (it.hasNext()) {
         if (server.getPlayerList().getPlayer(it.next()) == null) {
            it.remove();
         }
      }
   }

   private static void playMusic(ServerPlayer player) {
      Holder<SoundEvent> holder = BuiltInRegistries.SOUND_EVENT.wrapAsHolder(ModPhora.MUSICA);
      player.connection.send(new ClientboundSoundEntityPacket(holder, MUSIC_SOURCE, player, 0.9F, 1.0F, player.level().getRandom().nextLong()));
   }

   private static final class Job {
      final ServerLevel level;
      final BlockPos origin;
      final CuboPhoraShape shape;
      final int S;
      final int W;
      final int M;
      final long total;
      long done;
      int y;
      int a;
      int b;
      final MutableBlockPos pos = new MutableBlockPos();
      final MutableBlockPos pos2 = new MutableBlockPos();

      Job(ServerLevel level, BlockPos origin, CuboPhoraShape shape) {
         this.level = level;
         this.origin = origin;
         this.shape = shape;
         this.S = shape.S;
         this.M = 12;
         this.W = this.S + 2 * this.M;
         this.total = (long)this.W * this.W * this.S;
      }

      boolean step() {
         BlockState[] st = CuboPhora.states();
         int evalBudget = 120000;
         int setBudget = 5000;

         while (evalBudget > 0 && setBudget > 0) {
            if (this.y >= this.S) {
               return true;
            }

            int x = this.a - this.M;
            int z = this.b - this.M;
            int code = this.shape.code(x, this.y, z);
            evalBudget--;
            this.done++;
            if (code != -1) {
               this.pos.set(this.origin.getX() + x, this.origin.getY() + this.y, this.origin.getZ() + z);
               BlockState want = st[code];
               BlockState cur = this.level.getBlockState(this.pos);
               if (cur != want) {
                  this.level.setBlock(this.pos, want, 2);
                  setBudget--;
               }

               boolean solid = code >= 1 && code <= 8 || code >= 13;
               if (this.y == 0 && solid) {
                  BlockState base = code <= 8 ? st[1] : want;
                  setBudget -= this.foundation(base);
               }
            }

            if (++this.b >= this.W) {
               this.b = 0;
               if (++this.a >= this.W) {
                  this.a = 0;
                  this.y++;
               }
            }
         }

         return false;
      }

      private int foundation(BlockState state) {
         int n = 0;
         int minY = this.level.getMinY();

         for (int yy = this.pos.getY() - 1; yy > this.pos.getY() - 60 && yy >= minY; yy--) {
            this.pos2.set(this.pos.getX(), yy, this.pos.getZ());
            BlockState cur = this.level.getBlockState(this.pos2);
            if (cur.isSolid() && !cur.is(BlockTags.LEAVES)) {
               break;
            }

            this.level.setBlock(this.pos2, state, 2);
            n++;
         }

         return n;
      }
   }
}
