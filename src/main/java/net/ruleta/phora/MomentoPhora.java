package net.ruleta.phora;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.ruleta.Ajustes;
import net.ruleta.ComandoEclipse;
import net.ruleta.MomentoReviil;
import net.ruleta.Red;
import net.ruleta.ReviilPayload;
import net.ruleta.Servidor;
import net.ruleta.bunker.Bunkers;

public final class MomentoPhora {
   private static final List<double[]> RECIENTES = new ArrayList<>();
   private static long siguiente;
   private static final long ESPERA_VOZ = 100L;
   private static long tick;
   private static long desde = -1L;
   private static long hasta = -1L;

   private static int minEspera() {
      return Ajustes.entero("phora.espera_min") * 20;
   }

   private static int maxEspera() {
      return Math.max(minEspera() + 20, Ajustes.entero("phora.espera_max") * 20);
   }

   private static double separacion() {
      return Ajustes.entero("phora.separacion");
   }

   private MomentoPhora() {
   }

   public static boolean enMarcha() {
      return desde >= 0L;
   }

   public static void empezar(MinecraftServer server, int segundos) {
      if (desde < 0L) {
         desde = tick;
         siguiente = 0L;
         RECIENTES.clear();
         Red.enviarATodos(server, mensaje(0));
      }

      hasta = segundos > 0 ? tick + segundos * 20L : -1L;
   }

   public static void terminar(MinecraftServer server) {
      if (desde >= 0L) {
         desde = -1L;
         hasta = -1L;
         Red.enviarATodos(server, mensaje(-1));
      }
   }

   private static ReviilPayload mensaje(int valor) {
      return new ReviilPayload("phora", valor, Component.empty(), Component.empty());
   }

   public static void alTick(MinecraftServer servidor) {
      tick++;
      Crater.avanzar();
      MinecraftServer server = Servidor.actual();
      if (server != null && desde >= 0L) {
         if (hasta >= 0L && tick >= hasta) {
            terminar(server);
         } else if (tick - desde >= 100L && tick >= siguiente) {
            List<ServerPlayer> jugadores = new ArrayList<>();

            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
               if (!p.isSpectator() && p.level().dimensionType().hasSkyLight()) {
                  jugadores.add(p);
               }
            }

            if (!jugadores.isEmpty()) {
               ServerPlayer p = jugadores.get(server.overworld().getRandom().nextInt(jugadores.size()));
               RandomSource r = p.getRandom();
               siguiente = tick + minEspera() + r.nextInt(Math.max(1, maxEspera() - minEspera()));
               ServerLevel nivel = p.level();
               int alcance = Math.max(64, server.getPlayerList().getSimulationDistance() * 16 - 20);
               RECIENTES.removeIf(qx -> tick - (long)qx[3] > 3600L);

               for (int intento = 0; intento < 12; intento++) {
                  double ang = r.nextDouble() * Math.PI * 2.0;
                  double dist = 45.0 + r.nextDouble() * (alcance - 45.0);
                  int x = (int)Math.floor(p.getX() + Math.cos(ang) * dist);
                  int z = (int)Math.floor(p.getZ() + Math.sin(ang) * dist);
                  BlockPos donde = new BlockPos(x, p.getBlockY(), z);
                  if (nivel.isPositionEntityTicking(donde)) {
                     boolean malo = false;

                     for (ServerPlayer otro : jugadores) {
                        if (otro.level() == nivel && otro.position().distanceToSqr(x, otro.getY(), z) < 2025.0) {
                           malo = true;
                           break;
                        }
                     }

                     if (Bunkers.cercaDeBunker(nivel, x, z, Ajustes.entero("bunker.zona_impactos"))) {
                        malo = true;
                     }

                     for (double[] q : RECIENTES) {
                        double dx = q[0] - x;
                        double dz = q[2] - z;
                        if (dx * dx + dz * dz < separacion() * separacion()) {
                           malo = true;
                           break;
                        }
                     }

                     if (!malo) {
                        int y = nivel.getHeight(Types.MOTION_BLOCKING, x, z);
                        MisilEntity.lanzar(nivel, new Vec3(x + 0.5, y, z + 0.5), r, p.position());
                        RECIENTES.add(new double[]{x, y, z, tick});
                        return;
                     }
                  }
               }
            }
         }
      }
   }

   public static void alRegistrar(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("phora")
                     .requires(s -> s.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)))
                  .then(
                     Commands.literal("lluvia")
                        .executes(
                           ctx -> {
                              if (ComandoEclipse.enMarcha()) {
                                 ((CommandSourceStack)ctx.getSource())
                                    .sendFailure(Component.literal("No se puede lanzar el Momento Phora con un eclipse en curso."));
                                 return 0;
                              } else if (MomentoReviil.enMarcha()) {
                                 ((CommandSourceStack)ctx.getSource())
                                    .sendFailure(Component.literal("No se puede lanzar el Momento Phora con un Momento Reviil en curso."));
                                 return 0;
                              } else {
                                 empezar(((CommandSourceStack)ctx.getSource()).getServer(), -1);
                                 ((CommandSourceStack)ctx.getSource())
                                    .sendSuccess(() -> Component.literal("Momento Phora: empieza la lluvia de misiles (/phora detener para pararla)."), true);
                                 return 1;
                              }
                           }
                        )
                  ))
               .then(Commands.literal("detener").executes(ctx -> {
                  if (!enMarcha()) {
                     ((CommandSourceStack)ctx.getSource()).sendFailure(Component.literal("No hay ningún Momento Phora en marcha."));
                     return 0;
                  } else {
                     terminar(((CommandSourceStack)ctx.getSource()).getServer());
                     ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal("Momento Phora detenido."), true);
                     return 1;
                  }
               })))
            .then(
               Commands.literal("palo")
                  .executes(
                     ctx -> {
                        ServerPlayer p = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
                        ItemStack palo = new ItemStack((ItemLike)PhoraMod.PALO.get());
                        if (!p.getInventory().add(palo)) {
                           net.ruleta.compat.Util.dar(p, palo);
                        }

                        ((CommandSourceStack)ctx.getSource())
                           .sendSuccess(() -> Component.literal("Palo de los Phora: clic derecho para hacer caer un misil donde miras."), false);
                        return 1;
                     }
                  )
            )
      );
   }

   public static void alEntrar(ServerPlayer p) {
      if (desde >= 0L) {
         Red.enviar(p, mensaje((int)((tick - desde) / 20L)));
      }
   }

   public static void alApagar(MinecraftServer servidor) {
      desde = -1L;
      hasta = -1L;
      Crater.limpiar();
   }

   static List<String> nada() {
      return List.of();
   }
}
