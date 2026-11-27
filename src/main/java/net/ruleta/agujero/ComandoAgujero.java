package net.ruleta.agujero;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.ruleta.Mirada;
import net.ruleta.Red;
import net.ruleta.Servidor;

public final class ComandoAgujero {
   private static boolean activo;
   private static final List<AgujeroPayload.Agujero> AGUJEROS = new ArrayList<>();
   private static final Set<UUID> VIVOS = new HashSet<>();
   private static final Map<UUID, AgujeroPayload.Agujero> POR_ID = new HashMap<>();
   private static long tick;
   private static long horaInicio = -1L;
   private static long desdeHora;
   private static long cortadoEn = -1L;
   private static long horaCortada;
   private static long relojHora;
   private static final long MEDIANOCHE = 18000L;
   private static final float ENTRADA = 60.0F;

   public static boolean vivo(UUID id) {
      return VIVOS.contains(id);
   }

   private ComandoAgujero() {
   }

   public static void alRegistrar(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                           "blackhole"
                        )
                        .requires(s -> s.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)))
                     .then(Commands.literal("run").executes(ctx -> empezar((CommandSourceStack)ctx.getSource()))))
                  .then(Commands.literal("stop").executes(ctx -> parar((CommandSourceStack)ctx.getSource()))))
               .then(
                  ((LiteralArgumentBuilder)Commands.literal("eliminar").executes(ctx -> eliminarMirado((CommandSourceStack)ctx.getSource())))
                     .then(Commands.literal("todos").executes(ctx -> eliminarTodos((CommandSourceStack)ctx.getSource())))
               ))
            .then(
               ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("agujero")
                           .executes(ctx -> agujero((CommandSourceStack)ctx.getSource(), null, 15.0F)))
                        .then(tamano("tamaño", null)))
                     .then(tamano("tamano", null)))
                  .then(
                     ((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3())
                              .executes(ctx -> agujero((CommandSourceStack)ctx.getSource(), Vec3Argument.getVec3(ctx, "pos"), 15.0F)))
                           .then(tamano("tamaño", "pos")))
                        .then(tamano("tamano", "pos"))
                  )
            )
      );
   }

   private static int eliminarMirado(CommandSourceStack fuente) {
      AgujeroNegroEntity a = Mirada.alQueMira(fuente, AgujeroNegro.TIPO.get(), e -> Math.max(3.0, e.radio() * 2.2), 4000.0);
      if (a == null) {
         fuente.sendFailure(Component.literal("No estás mirando a ningún agujero negro. Míralo, o usa /blackhole eliminar todos."));
         return 0;
      }

      AgujeroPayload.Agujero dato = POR_ID.remove(a.getUUID());
      if (dato != null) {
         AGUJEROS.remove(dato);
      }

      VIVOS.remove(a.getUUID());
      a.discard();
      enviarATodos(fuente.getServer());
      fuente.sendSuccess(() -> Component.literal("Agujero negro eliminado."), true);
      return 1;
   }

   private static int eliminarTodos(CommandSourceStack fuente) {
      if (AGUJEROS.isEmpty() && VIVOS.isEmpty()) {
         fuente.sendFailure(Component.literal("No hay ningún agujero negro que eliminar."));
         return 0;
      } else {
         int n = AGUJEROS.size();
         quitarTodos(fuente.getServer());
         fuente.sendSuccess(() -> Component.literal("Agujeros negros eliminados (" + n + ")."), true);
         return n;
      }
   }

   private static int empezar(CommandSourceStack fuente) {
      if (activo) {
         fuente.sendFailure(Component.literal("El agujero negro ya está en marcha."));
         return 0;
      } else {
         activo = true;
         horaInicio = fuente.getServer().overworld().getOverworldClockTime();
         desdeHora = relojHora;
         cortadoEn = -1L;
         enviarATodos(fuente.getServer());
         fuente.sendSuccess(() -> Component.literal("Agujero negro: el cielo ha cambiado. Usa /blackhole agujero para que aparezca uno."), true);
         return 1;
      }
   }

   private static int parar(CommandSourceStack fuente) {
      if (!activo && AGUJEROS.isEmpty()) {
         fuente.sendFailure(Component.literal("No hay ningún agujero negro en marcha."));
         return 0;
      }

      activo = false;
      AGUJEROS.clear();
      quitarAgujeros(fuente.getServer());
      if (horaInicio >= 0L) {
         cortadoEn = relojHora;
         horaCortada = fuente.getServer().overworld().getOverworldClockTime();
      }

      enviarATodos(fuente.getServer());
      fuente.sendSuccess(() -> Component.literal("Agujero negro detenido."), true);
      return 1;
   }

   private static LiteralArgumentBuilder<CommandSourceStack> tamano(String palabra, String pos) {
      return (LiteralArgumentBuilder<CommandSourceStack>)Commands.literal(palabra)
         .then(
            Commands.argument("tam", FloatArgumentType.floatArg(1.0F, 64.0F))
               .executes(
                  ctx -> agujero(
                     (CommandSourceStack)ctx.getSource(), pos == null ? null : Vec3Argument.getVec3(ctx, pos), FloatArgumentType.getFloat(ctx, "tam")
                  )
               )
         );
   }

   private static int agujero(CommandSourceStack fuente, Vec3 sitio, float radio) {
      if (sitio == null) {
         Vec3 ojos = fuente.getPosition();
         Vec3 mira = new Vec3(0.0, 0.0, 1.0);
         if (fuente.getEntity() != null) {
            ojos = fuente.getEntity().getEyePosition();
            mira = fuente.getEntity().getLookAngle();
         }

         sitio = ojos.add(mira.scale(radio * 8.0));
         double minimo = ojos.y + radio * 2.2;
         if (sitio.y < minimo) {
            sitio = new Vec3(sitio.x, minimo, sitio.z);
         }
      }

      Vec3 donde = sitio;
      ServerLevel mundo = fuente.getLevel();
      AgujeroNegroEntity agujero = (AgujeroNegroEntity)AgujeroNegro.TIPO.get().create(mundo, net.minecraft.world.entity.EntitySpawnReason.EVENT);
      if (agujero == null) {
         fuente.sendFailure(Component.literal("No se pudo crear el agujero negro."));
         return 0;
      } else {
         agujero.fijarRadio(radio);
         agujero.snapTo(donde.x, donde.y, donde.z, 0.0F, 0.0F);
         mundo.addFreshEntity(agujero);
         VIVOS.add(agujero.getUUID());
         AgujeroPayload.Agujero dato = new AgujeroPayload.Agujero(
            donde, agujero.radio() > 0.0 ? (float)agujero.radio() : radio, mundo.dimension().identifier().toString()
         );
         AGUJEROS.add(dato);
         POR_ID.put(agujero.getUUID(), dato);
         enviarATodos(fuente.getServer());
         fuente.sendSuccess(
            () -> Component.literal(String.format("Ha aparecido un agujero negro (tamaño %.0f) en %.0f, %.0f, %.0f.", radio, donde.x, donde.y, donde.z)), true
         );
         return 1;
      }
   }

   public static void cielo(MinecraftServer server, boolean poner) {
      if (poner && !activo) {
         activo = true;
         horaInicio = server.overworld().getOverworldClockTime();
         desdeHora = relojHora;
         cortadoEn = -1L;
      } else if (!poner && activo) {
         activo = false;
         if (horaInicio >= 0L) {
            cortadoEn = relojHora;
            horaCortada = server.overworld().getOverworldClockTime();
         }
      }

      enviarATodos(server);
   }

   public static void crearAgujero(ServerLevel mundo, Vec3 donde, float radio) {
      AgujeroNegroEntity agujero = (AgujeroNegroEntity)AgujeroNegro.TIPO.get().create(mundo, net.minecraft.world.entity.EntitySpawnReason.EVENT);
      if (agujero != null) {
         agujero.fijarRadio(radio);
         agujero.snapTo(donde.x, donde.y, donde.z, 0.0F, 0.0F);
         mundo.addFreshEntity(agujero);
         VIVOS.add(agujero.getUUID());
         AGUJEROS.add(new AgujeroPayload.Agujero(donde, agujero.radio() > 0.0 ? (float)agujero.radio() : radio, mundo.dimension().identifier().toString()));
         enviarATodos(mundo.getServer());
      }
   }

   public static List<AgujeroPayload.Agujero> existentes() {
      return new ArrayList<>(AGUJEROS);
   }

   public static int quitarLejos(MinecraftServer server, List<Vec3> puntos, double distancia) {
      int quitados = 0;
      Iterator<Entry<UUID, AgujeroPayload.Agujero>> it = POR_ID.entrySet().iterator();

      while (it.hasNext()) {
         Entry<UUID, AgujeroPayload.Agujero> e = it.next();
         Vec3 a = e.getValue().pos();
         boolean cerca = false;

         for (Vec3 p : puntos) {
            cerca |= Math.hypot(p.x - a.x, p.z - a.z) <= distancia;
         }

         if (!cerca) {
            it.remove();
            VIVOS.remove(e.getKey());
            AGUJEROS.remove(e.getValue());

            for (ServerLevel mundo : server.getAllLevels()) {
               Entity ent = mundo.getEntity(e.getKey());
               if (ent != null) {
                  ent.discard();
               }
            }

            quitados++;
         }
      }

      if (quitados > 0) {
         enviarATodos(server);
      }

      return quitados;
   }

   public static void quitarTodos(MinecraftServer server) {
      AGUJEROS.clear();
      POR_ID.clear();
      quitarAgujeros(server);
      enviarATodos(server);
   }

   private static void quitarAgujeros(MinecraftServer server) {
      VIVOS.clear();
      POR_ID.clear();

      for (ServerLevel mundo : server.getAllLevels()) {
         List<Entity> lista = new ArrayList<>();

         for (Entity e : mundo.getAllEntities()) {
            if (e instanceof AgujeroNegroEntity) {
               lista.add(e);
            }
         }

         for (Entity e : lista) {
            e.discard();
         }
      }
   }

   private static long corto(long de, long a) {
      long d = Math.floorMod(a - de, 24000L);
      return d > 12000L ? d - 24000L : d;
   }

   private static float suave(float x) {
      x = Math.max(0.0F, Math.min(1.0F, x));
      return x * x * (3.0F - 2.0F * x);
   }

   private static void hora(MinecraftServer server) {
      if (horaInicio >= 0L) {
         ServerLevel mundo = server.overworld();
         boolean ciclo = mundo.getGameRules().get(GameRules.ADVANCE_TIME);
         boolean termina = false;
         long hora;
         if (cortadoEn >= 0L) {
            long destino = ciclo ? horaInicio + (relojHora - desdeHora) : horaInicio;
            float u = suave((float)(relojHora - cortadoEn) / 60.0F);
            termina = relojHora - cortadoEn >= 60L;
            hora = termina ? destino : horaCortada + Math.round((float)corto(horaCortada, destino) * u);
         } else {
            long hasta = horaInicio + corto(horaInicio, 18000L);
            hora = horaInicio + Math.round((float)(hasta - horaInicio) * suave((float)(relojHora - desdeHora) / 60.0F));
         }
         if (termina) {
            horaInicio = -1L;
            cortadoEn = -1L;
         }

      }
   }

   public static void alTick(MinecraftServer servidor) {
      relojHora++;
      MinecraftServer server = Servidor.actual();
      if (server != null) {
         hora(server);
      }
   }

   private static AgujeroPayload estado() {
      return new AgujeroPayload(activo, new ArrayList<>(AGUJEROS));
   }

   private static void enviarATodos(MinecraftServer server) {
      Red.enviarATodos(server, estado());
   }

   public static void alEntrar(ServerPlayer p) {
      if (activo || !AGUJEROS.isEmpty()) {
         Red.enviar(p, estado());
      }
   }

   public static void alApagar(MinecraftServer servidor) {
      activo = false;
      horaInicio = -1L;
      cortadoEn = -1L;
      AGUJEROS.clear();
      POR_ID.clear();
      VIVOS.clear();
   }
}
