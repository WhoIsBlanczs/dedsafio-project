package com.watonstudio.creepermetalico;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.watonstudio.creepermetalico.net.OpenMenuS2C;
import com.watonstudio.creepermetalico.net.ReviveC2S;
import com.watonstudio.creepermetalico.net.StartFxS2C;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Join;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class FogataMod implements ModInitializer {
   public static final String ID = "fogata";
   public static final String UNBAN_CMD = "pardon %s";
   public static final SoundEvent REVIVIR = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("fogata", "revivir"));
   public static final TagKey<Item> CUCHILLOS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("fogata", "cuchillos"));
   public static final TagKey<Item> CUBIERTOS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("fogata", "cubiertos"));
   public static final Map<String, FogataMod.Pending> PENDING = new HashMap<>();

   public void onInitialize() {
      System.out.println("[FOGATA] cargado");
      Registry.register(BuiltInRegistries.SOUND_EVENT, REVIVIR.getLocation(), REVIVIR);
      PayloadTypeRegistry.playS2C().register(OpenMenuS2C.ID, OpenMenuS2C.CODEC);
      PayloadTypeRegistry.playS2C().register(StartFxS2C.ID, StartFxS2C.CODEC);
      PayloadTypeRegistry.playC2S().register(ReviveC2S.ID, ReviveC2S.CODEC);
      ServerTickEvents.END_SERVER_TICK.register(FogataAnim::tick);
      ServerPlayConnectionEvents.JOIN.register((Join)(handler, sender, server) -> {
         ServerPlayer p = handler.getPlayer();
         FogataMod.Pending pend = PENDING.remove(p.getGameProfile().getName().toLowerCase());
         if (pend != null) {
            ServerLevel w = server.getLevel(pend.dim());
            if (w != null) {
               server.execute(() -> FogataAnim.start(p, w, pend.pos(), pend.rojo()));
            }
         }
      });
      UseBlockCallback.EVENT.register((UseBlockCallback)(player, world, hand, hit) -> {
         if (!world.isClientSide() && hand == InteractionHand.MAIN_HAND) {
            if (!world.getBlockState(hit.getBlockPos()).is(BlockTags.CAMPFIRES)) {
               return InteractionResult.PASS;
            }

            if (!player.getItemInHand(hand).is(CUBIERTOS)) {
               return InteractionResult.PASS;
            }

            ServerPlayer sp = (ServerPlayer)player;
            List<String> muertos = new ArrayList<>(List.of(sp.server.getPlayerList().getBans().getUserList()));
            ServerPlayNetworking.send(sp, new OpenMenuS2C(muertos, hit.getBlockPos()));
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      });
      ServerPlayNetworking.registerGlobalReceiver(ReviveC2S.ID, (payload, ctx) -> {
         ServerPlayer sp = ctx.player();
         MinecraftServer server = sp.server;
         server.execute(() -> {
            ServerLevel w = sp.serverLevel();
            BlockPos pos = payload.pos();
            if (!(sp.distanceToSqr(pos.getCenter()) > 100.0)) {
               if (w.getBlockState(pos).is(BlockTags.CAMPFIRES)) {
                  String nombre = payload.name();
                  boolean muerto = false;

                  for (String n : server.getPlayerList().getBans().getUserList()) {
                     if (n.equalsIgnoreCase(nombre)) {
                        muerto = true;
                     }
                  }

                  if (muerto) {
                     revivir(server, nombre, w, pos, sp.getMainHandItem().is(CUCHILLOS));
                  }
               }
            }
         });
      });
      CommandRegistrationCallback.EVENT
         .register(
            (CommandRegistrationCallback)(d, reg, env) -> d.register(
               (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("fogata").requires(s -> s.hasPermission(2)))
                     .then(Commands.literal("revivir").then(Commands.argument("nombre", StringArgumentType.word()).executes(c -> {
                        ServerPlayer p = ((CommandSourceStack)c.getSource()).getPlayerOrException();
                        BlockPos fogataPos = buscarFogata(p);
                        if (fogataPos == null) {
                           ((CommandSourceStack)c.getSource())
                              .sendFailure(Component.literal("No hay una Fogata personalizada cerca. Usa /fogata spawn primero."));
                           return 0;
                        } else {
                           revivir(
                              ((CommandSourceStack)c.getSource()).getServer(), StringArgumentType.getString(c, "nombre"), p.serverLevel(), fogataPos, false
                           );
                           ((CommandSourceStack)c.getSource()).sendSuccess(() -> Component.literal("Resurreccion preparada."), true);
                           return 1;
                        }
                     }))))
                  .then(Commands.literal("rojo").then(Commands.argument("nombre", StringArgumentType.word()).executes(c -> {
                     ServerPlayer p = ((CommandSourceStack)c.getSource()).getPlayerOrException();
                     BlockPos fogataPos = buscarFogata(p);
                     if (fogataPos == null) {
                        ((CommandSourceStack)c.getSource()).sendFailure(Component.literal("No hay una Fogata personalizada cerca. Usa /fogata spawn primero."));
                        return 0;
                     } else {
                        revivir(((CommandSourceStack)c.getSource()).getServer(), StringArgumentType.getString(c, "nombre"), p.serverLevel(), fogataPos, true);
                        return 1;
                     }
                  })))
            )
         );
      CommandRegistrationCallback.EVENT
         .register(
            (CommandRegistrationCallback)(dispatcher, registryAccess, environment) -> dispatcher.register(
               (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("fogata").requires(source -> source.hasPermission(2)))
                     .then(Commands.literal("spawn").executes(context -> {
                        ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
                        ServerLevel world = player.serverLevel();
                        double radians = Math.toRadians(player.getYRot());
                        double x = player.getX() - Math.sin(radians) * 2.0;
                        double z = player.getZ() + Math.cos(radians) * 2.0;
                        FogataSummonEntity fogata = (FogataSummonEntity)CreeperMetalicoMod.FOGATA_SUMMONER.create(world);
                        if (fogata == null) {
                           ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("No se pudo crear la fogata."));
                           return 0;
                        } else {
                           fogata.moveTo(x, player.getY(), z, player.getYRot(), 0.0F);
                           if (!world.addFreshEntity(fogata)) {
                              ((CommandSourceStack)context.getSource()).sendFailure(Component.literal("No se pudo invocar la fogata."));
                              return 0;
                           } else {
                              ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal("Fogata creada correctamente."), true);
                              return 1;
                           }
                        }
                     })))
                  .then(
                     Commands.literal("delete")
                        .executes(
                           context -> {
                              ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
                              ServerLevel world = player.serverLevel();
                              List<FogataSummonEntity> fogatas = new ArrayList<>();

                              for (Entity entity : world.getAllEntities()) {
                                 if (entity instanceof FogataSummonEntity fogata) {
                                    fogatas.add(fogata);
                                 }
                              }

                              for (FogataSummonEntity fogata : fogatas) {
                                 BlockPos pos = fogata.blockPosition();
                                 if (world.getBlockState(pos).is(BlockTags.CAMPFIRES)) {
                                    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                                 }

                                 fogata.discard();
                              }

                              int cantidad = fogatas.size();
                              ((CommandSourceStack)context.getSource())
                                 .sendSuccess(
                                    () -> Component.literal(
                                       cantidad == 0 ? "No hay fogatas personalizadas para eliminar." : "Se eliminaron " + cantidad + " fogata(s)."
                                    ),
                                    true
                                 );
                              return cantidad;
                           }
                        )
                  )
            )
         );
   }

   private static BlockPos buscarFogata(ServerPlayer player) {
      ServerLevel world = player.serverLevel();
      FogataSummonEntity cercana = null;
      double distanciaMinima = 4096.0;

      for (Entity entity : world.getAllEntities()) {
         if (entity instanceof FogataSummonEntity fogata) {
            double distancia = player.distanceToSqr(fogata);
            if (distancia < distanciaMinima) {
               distanciaMinima = distancia;
               cercana = fogata;
            }
         }
      }

      return cercana == null ? null : cercana.blockPosition();
   }

   public static void revivir(MinecraftServer server, String nombre, ServerLevel w, BlockPos pos, boolean rojo) {
      server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), String.format("pardon %s", nombre));
      server.getPlayerList().broadcastSystemMessage(Component.literal("Resurreccion de " + nombre + " preparada."), false);
      ServerPlayer online = server.getPlayerList().getPlayerByName(nombre);
      if (online != null) {
         FogataAnim.start(online, w, pos, rojo);
      } else {
         PENDING.put(nombre.toLowerCase(), new FogataMod.Pending(w.dimension(), pos, rojo));
      }
   }

   public record Pending(ResourceKey<Level> dim, BlockPos pos, boolean rojo) {
   }
}
