package net.ruleta.dedsafio;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;

public final class ComandoDedsafio {
   public static final ResourceKey<Level> DIMENSION = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("ruleta", "dedsafio"));
   private static final double X = 37.5;
   private static final double Y = -2.0;
   private static final double Z = -17.5;
   private static final int ESPERA = 30;
   private static final Map<UUID, ComandoDedsafio.Previo> previos = new HashMap<>();
   private static final Map<UUID, ComandoDedsafio.Previo> ultimas = new HashMap<>();
   private static final Set<UUID> estabanDentro = new HashSet<>();
   private static boolean previosCargados;
   private static final List<UUID> viajeros = new ArrayList<>();
   private static boolean yendo;
   private static int cuenta = -1;

   private ComandoDedsafio() {
   }

   public static void alRegistrar(CommandDispatcher<CommandSourceStack> d) {
      registrarEn(d);
   }

   private static void registrarEn(CommandDispatcher<CommandSourceStack> d) {
      d.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("dedsafio").requires(s -> s.hasPermission(2)))
               .then(
                  ((LiteralArgumentBuilder)Commands.literal("ir").executes(c -> viaje(c, null, true)))
                     .then(Commands.argument("jugadores", EntityArgument.players()).executes(c -> viaje(c, EntityArgument.getPlayers(c, "jugadores"), true)))
               ))
            .then(
               ((LiteralArgumentBuilder)Commands.literal("volver").executes(c -> viaje(c, null, false)))
                  .then(Commands.argument("jugadores", EntityArgument.players()).executes(c -> viaje(c, EntityArgument.getPlayers(c, "jugadores"), false)))
            )
      );
      d.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("dedsafioescena").requires(s -> s.hasPermission(2)))
               .then(
                  Commands.literal("iniciar")
                     .executes(
                        c -> {
                           int n = EscenaServidor.iniciar(((CommandSourceStack)c.getSource()).getServer());
                           ((CommandSourceStack)c.getSource())
                              .sendSuccess(() -> Component.literal("Escena iniciada para " + n + " jugador(es) en la dimensión Dulce Azar."), true);
                           return 1;
                        }
                     )
               ))
            .then(
               Commands.literal("parar")
                  .executes(
                     c -> {
                        int n = EscenaServidor.parar(((CommandSourceStack)c.getSource()).getServer());
                        ((CommandSourceStack)c.getSource())
                           .sendSuccess(() -> Component.literal("Escena parada para " + n + " jugador(es) en la dimensión Dulce Azar."), true);
                        return 1;
                     }
                  )
            )
      );
   }

   private static boolean enDimension(ServerPlayer p) {
      return p.level().dimension().equals(DIMENSION);
   }

   public static int viajar(MinecraftServer servidor, Collection<ServerPlayer> elegidos, boolean ir) {
      if (ir && servidor.getLevel(DIMENSION) == null) {
         return -2;
      } else if (cuenta >= 0) {
         return -1;
      } else {
         Collection<ServerPlayer> base = (Collection<ServerPlayer>)(elegidos != null ? elegidos : servidor.getPlayerList().getPlayers());
         viajeros.clear();

         for (ServerPlayer p : base) {
            if (enDimension(p) != ir) {
               if (ir) {
                  previos.put(p.getUUID(), new ComandoDedsafio.Previo(p.level().dimension(), p.getX(), p.getY(), p.getZ(), p.getYRot(), p.getXRot()));
                  guardarPrevios(servidor);
               }

               viajeros.add(p.getUUID());
               EscenaServidor.ojo(p);
            }
         }

         if (viajeros.isEmpty()) {
            return 0;
         } else {
            yendo = ir;
            cuenta = 30;
            return viajeros.size();
         }
      }
   }

   private static int viaje(CommandContext<CommandSourceStack> c, Collection<ServerPlayer> elegidos, boolean ir) {
      int n = viajar(((CommandSourceStack)c.getSource()).getServer(), elegidos, ir);
      if (n == -2) {
         ((CommandSourceStack)c.getSource())
            .sendFailure(Component.literal("La dimensión ruleta:dedsafio no está cargada (revisa que data/ruleta/dimension/dedsafio.json esté en el mod)."));
         return 0;
      } else if (n == -1) {
         ((CommandSourceStack)c.getSource()).sendFailure(Component.literal("Ya hay un viaje en curso, espera un segundo."));
         return 0;
      } else if (n == 0) {
         ((CommandSourceStack)c.getSource())
            .sendFailure(Component.literal(ir ? "No hay nadie a quien llevar (todos ya están en Dulce Azar)." : "No hay nadie en Dulce Azar a quien devolver."));
         return 0;
      } else {
         ((CommandSourceStack)c.getSource())
            .sendSuccess(() -> Component.literal((ir ? "Llevando a Dulce Azar: " : "Devolviendo del Dulce Azar: ") + n + " jugador(es)"), true);
         return n;
      }
   }

   public static void tick(MinecraftServer servidor) {
      seguirPrevios(servidor);
      EscenaServidor.vigilarFin(servidor);
      OlaServidor.tick(servidor);
      OcultarServidor.tick(servidor);
      if (cuenta >= 0 && --cuenta <= 0) {
         cuenta = -1;
         ServerLevel dedsafio = servidor.getLevel(DIMENSION);

         for (UUID id : viajeros) {
            ServerPlayer p = servidor.getPlayerList().getPlayer(id);
            if (p != null) {
               if (yendo) {
                  if (dedsafio != null) {
                     p.teleportTo(dedsafio, 37.5, -2.0, -17.5, 0.0F, -4.0F);
                  }
               } else {
                  devolver(servidor, p);
               }
            }
         }

         viajeros.clear();
      }
   }

   static void devolver(MinecraftServer servidor, ServerPlayer p) {
      OlaServidor.alSalir(p);
      ComandoDedsafio.Previo antes = previos.remove(p.getUUID());
      estabanDentro.remove(p.getUUID());
      guardarPrevios(servidor);
      ServerLevel nivel = antes != null && !antes.dimension().equals(DIMENSION) ? servidor.getLevel(antes.dimension()) : null;
      if (antes != null && nivel != null) {
         p.teleportTo(nivel, antes.x(), antes.y(), antes.z(), antes.giro(), antes.cabeceo());
      } else {
         ServerLevel mundo = servidor.overworld();
         BlockPos cama = p.getRespawnPosition();
         BlockPos pos = cama != null && p.getRespawnDimension() == Level.OVERWORLD ? cama : mundo.getSharedSpawnPos();
         p.teleportTo(mundo, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, p.getYRot(), p.getXRot());
      }
   }

   private static Path archivoPrevios(MinecraftServer servidor) {
      return servidor.getWorldPath(LevelResource.ROOT).resolve("dedsafio_previos.txt");
   }

   private static void cargarPrevios(MinecraftServer servidor) {
      previosCargados = true;
      Path f = archivoPrevios(servidor);
      if (Files.exists(f)) {
         try {
            for (String linea : Files.readAllLines(f, StandardCharsets.UTF_8)) {
               String[] t = linea.split(";");
               if (t.length == 7) {
                  previos.put(
                     UUID.fromString(t[0]),
                     new ComandoDedsafio.Previo(
                        ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(t[1])),
                        Double.parseDouble(t[2]),
                        Double.parseDouble(t[3]),
                        Double.parseDouble(t[4]),
                        Float.parseFloat(t[5]),
                        Float.parseFloat(t[6])
                     )
                  );
               }
            }
         } catch (RuntimeException | IOException var5) {
         }
      }
   }

   private static void guardarPrevios(MinecraftServer servidor) {
      List<String> l = new ArrayList<>();

      for (Entry<UUID, ComandoDedsafio.Previo> e : previos.entrySet()) {
         ComandoDedsafio.Previo v = e.getValue();
         l.add(e.getKey() + ";" + v.dimension().location() + ";" + v.x() + ";" + v.y() + ";" + v.z() + ";" + v.giro() + ";" + v.cabeceo());
      }

      try {
         Files.write(archivoPrevios(servidor), l, StandardCharsets.UTF_8);
      } catch (IOException var5) {
      }
   }

   private static void seguirPrevios(MinecraftServer servidor) {
      if (!previosCargados) {
         cargarPrevios(servidor);
      }

      boolean cambio = false;
      boolean anotar = servidor.getTickCount() % 10 == 0;

      for (ServerPlayer p : servidor.getPlayerList().getPlayers()) {
         UUID id = p.getUUID();
         if (enDimension(p)) {
            if (estabanDentro.add(id) && !previos.containsKey(id)) {
               ComandoDedsafio.Previo u = ultimas.get(id);
               if (u != null) {
                  previos.put(id, u);
                  cambio = true;
               }
            }
         } else {
            if (estabanDentro.remove(id)) {
               previos.remove(id);
               cambio = true;
            }

            if (anotar && !p.isDeadOrDying()) {
               ultimas.put(id, new ComandoDedsafio.Previo(p.level().dimension(), p.getX(), p.getY(), p.getZ(), p.getYRot(), p.getXRot()));
            }
         }
      }

      if (cambio) {
         guardarPrevios(servidor);
      }
   }

   public static void reiniciar() {
      ultimas.clear();
      estabanDentro.clear();
      previosCargados = false;
      previos.clear();
      viajeros.clear();
      cuenta = -1;
   }

   private record Previo(ResourceKey<Level> dimension, double x, double y, double z, float giro, float cabeceo) {
   }
}
