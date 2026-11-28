package net.ruleta.meteoritos;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.ruleta.Ajustes;
import net.ruleta.ComandoEclipse;
import net.ruleta.MomentoReviil;
import net.ruleta.MomentosBunker;
import net.ruleta.Red;
import net.ruleta.ReviilPayload;
import net.ruleta.Servidor;
import net.ruleta.bunker.Bunkers;
import net.ruleta.phora.MomentoPhora;

public final class MomentoMeteoritos {
   private static boolean cielo;
   private static boolean lluvia;
   private static long tick;
   private static long siguiente;
   private static final List<double[]> RECIENTES = new ArrayList<>();

   private MomentoMeteoritos() {
   }

   public static boolean enMarcha() {
      return cielo;
   }

   public static void empezar(MinecraftServer server) {
      if (!cielo) {
         cielo = true;
         Red.enviarATodos(server, mensaje(0));
      }
   }

   public static void empezarLluvia(MinecraftServer server) {
      empezar(server);
      lluvia = true;
      siguiente = tick + 60L;
   }

   public static void terminar(MinecraftServer server) {
      for (ServerLevel n : server.getAllLevels()) {
         List<Entity> lista = new ArrayList<>();

         for (Entity e : n.getAllEntities()) {
            if (e instanceof MeteoritoEntity) {
               lista.add(e);
            }
         }

         for (Entity e : lista) {
            e.discard();
         }
      }

      lluvia = false;
      if (cielo) {
         cielo = false;
         Red.enviarATodos(server, mensaje(-1));
      }
   }

   public static void alTick(MinecraftServer servidor) {
      tick++;
      MinecraftServer server = Servidor.actual();
      if (server != null && lluvia && cielo && tick >= siguiente) {
         List<ServerPlayer> jugadores = new ArrayList<>();

         for (ServerPlayer p : server.getPlayerList().getPlayers()) {
            if (!p.isSpectator() && p.level().dimensionType().hasSkyLight()) {
               jugadores.add(p);
            }
         }

         if (!jugadores.isEmpty()) {
            ServerPlayer p = jugadores.get(server.overworld().random.nextInt(jugadores.size()));
            RandomSource r = p.getRandom();
            siguiente = tick + 200L + r.nextInt(240);
            int cuantos = 1;
            ServerLevel nivel = p.serverLevel();
            int alcance = Math.max(64, server.getPlayerList().getSimulationDistance() * 16 - 20);
            RECIENTES.removeIf(qx -> tick - (long)qx[3] > 2400L);
            boolean colocado = false;

            for (int k = 0; k < cuantos; k++) {
               for (int intento = 0; intento < 24; intento++) {
                  double ang = r.nextDouble() * Math.PI * 2.0;
                  double dist = 45.0 + r.nextDouble() * (alcance - 45.0);
                  int x = (int)Math.floor(p.getX() + Math.cos(ang) * dist);
                  int z = (int)Math.floor(p.getZ() + Math.sin(ang) * dist);
                  if (nivel.isPositionEntityTicking(new BlockPos(x, p.getBlockY(), z))) {
                     boolean malo = false;
                     double[] bun = MomentosBunker.centro();
                     if (bun != null && Math.hypot(bun[0] - x, bun[1] - z) < 200.0) {
                        malo = true;
                     }

                     if (Bunkers.cercaDeBunker(nivel, x, z, Ajustes.entero("bunker.zona_impactos"))) {
                        malo = true;
                     }

                     for (double[] q : RECIENTES) {
                        double dx = q[0] - x;
                        double dz = q[2] - z;
                        if (dx * dx + dz * dz < 12100.0) {
                           malo = true;
                           break;
                        }
                     }

                     if (!malo) {
                        int y = nivel.getHeight(Types.MOTION_BLOCKING, x, z);
                        MeteoritoEntity.lanzar(nivel, new Vec3(x + 0.5, y, z + 0.5), r, p.position());
                        RECIENTES.add(new double[]{x, y, z, tick});
                        colocado = true;
                        break;
                     }
                  }
               }
            }

            if (!colocado) {
               siguiente = tick + 40L;
            }
         }
      }
   }

   private static ReviilPayload mensaje(int valor) {
      return new ReviilPayload("meteoritos", valor, Component.empty(), Component.empty());
   }

   public static void alRegistrar(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                              "meteoritos"
                           )
                           .requires(s -> s.hasPermission(2)))
                        .then(
                           Commands.literal("cielo")
                              .executes(
                                 ctx -> {
                                    if (ComandoEclipse.enMarcha() || MomentoReviil.enMarcha() || MomentoPhora.enMarcha()) {
                                       ((CommandSourceStack)ctx.getSource())
                                          .sendFailure(
                                             Component.literal(
                                                "No se puede poner el cielo de meteoritos con un eclipse, un Momento Reviil o el Momento Phora en curso."
                                             )
                                          );
                                       return 0;
                                    } else if (cielo) {
                                       ((CommandSourceStack)ctx.getSource())
                                          .sendFailure(Component.literal("El cielo de los meteoritos ya está puesto (/meteoritos detener para quitarlo)."));
                                       return 0;
                                    } else {
                                       empezar(((CommandSourceStack)ctx.getSource()).getServer());
                                       ((CommandSourceStack)ctx.getSource())
                                          .sendSuccess(() -> Component.literal("Cielo de los meteoritos puesto (/meteoritos detener para quitarlo)."), true);
                                       return 1;
                                    }
                                 }
                              )
                        ))
                     .then(
                        Commands.literal("lluvia")
                           .executes(
                              ctx -> {
                                 if (cielo || !ComandoEclipse.enMarcha() && !MomentoReviil.enMarcha() && !MomentoPhora.enMarcha()) {
                                    empezar(((CommandSourceStack)ctx.getSource()).getServer());
                                    lluvia = true;
                                    siguiente = tick + 60L;
                                    ((CommandSourceStack)ctx.getSource())
                                       .sendSuccess(
                                          () -> Component.literal(
                                             "Lluvia de meteoritos: cae uno cada 10-22 s, lejos del anterior (/meteoritos detener para pararla)."
                                          ),
                                          true
                                       );
                                    return 1;
                                 } else {
                                    ((CommandSourceStack)ctx.getSource())
                                       .sendFailure(Component.literal("No se puede con un eclipse, un Momento Reviil o el Momento Phora en curso."));
                                    return 0;
                                 }
                              }
                           )
                     ))
                  .then(Commands.literal("meteorito").executes(ctx -> {
                     ServerPlayer p = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
                     Vec3 objetivo = MeteoritoEntity.dondeMira(p);
                     MeteoritoEntity.lanzar(p.serverLevel(), objetivo, p.getRandom(), p.position());
                     ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("Cae un meteorito donde miras."), false);
                     return 1;
                  })))
               .then(Commands.literal("detener").executes(ctx -> {
                  terminar(((CommandSourceStack)ctx.getSource()).getServer());
                  ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("Meteoritos detenidos."), true);
                  return 1;
               })))
            .then(
               Commands.literal("palo")
                  .executes(
                     ctx -> {
                        ServerPlayer p = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
                        ItemStack palo = new ItemStack((ItemLike)MeteoritosMod.PALO.get());
                        if (!p.getInventory().add(palo)) {
                           p.drop(palo, false);
                        }

                        ((CommandSourceStack)ctx.getSource())
                           .sendSuccess(() -> Component.literal("Palo de los meteoritos: clic derecho para hacer caer un meteorito donde miras."), false);
                        return 1;
                     }
                  )
            )
      );
   }

   public static void alEntrar(ServerPlayer p) {
      if (cielo) {
         Red.enviar(p, mensaje(0));
      }
   }

   public static void alApagar(MinecraftServer servidor) {
      cielo = false;
      lluvia = false;
   }
}
