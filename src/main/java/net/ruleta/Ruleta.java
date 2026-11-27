package net.ruleta;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.ruleta.compat.EntityTeleportEvent;
import net.ruleta.compat.EventPriority;
import net.ruleta.compat.LivingDeathEvent;
import net.ruleta.compat.LivingUseTotemEvent;
import net.ruleta.compat.PlayerEvent;
import net.ruleta.compat.ServerChatEvent;
import net.ruleta.compat.SubscribeEvent;
import net.ruleta.meteoritos.MomentoMeteoritos;
import net.ruleta.phora.MomentoPhora;
import net.ruleta.tornado.ComandoTornado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ruleta {
   public static final String MOD_ID = "ruleta";
   public static final int PERMISSION_LEVEL = 2;
   public static final String LICENCIA = "DEDsafio 4 Ruleta Fan Project (c) 2026 DarkPlayer y colaboradores. Licencia CC BY-NC-SA 4.0 (https://creativecommons.org/licenses/by-nc-sa/4.0/): hay que dar crédito, no se puede usar con fines comerciales y las obras derivadas (ports, recreaciones) deben conservar esta misma licencia y los créditos de CREDITS.txt. Serie DEDsafio 4: Eufonia Studios.";
   static final String TEXTO_PHORA = "Los phoras han llegado, ponte a salvo!.";
   private static final int COLOR_NOMBRE = 43520;
   private static final Map<ServerPlayer, Long> CHAT_VERDE = new ConcurrentHashMap<>();
   private static final long TICKS_CHAT_VERDE = 10L;
   private static long tickChat;
   private static final Map<UUID, Map<Item, Integer>> INVENTARIO_ANTES = new HashMap<>();
   private static final Map<UUID, Long> ULTIMO_AVISO_TOTEM = new HashMap<>();
   public static final Logger LOG = LoggerFactory.getLogger("ruleta");
   static final int TICKS_RULETA = Math.round(286.0F);
   private static final float DANO_MAGMA = 1.0E8F;
   private static Ruleta.DificultadDatos cambios;
   private static final TextColor COLOR_TOTEM = TextColor.fromRgb(16777045);

   public static void girar(MinecraftServer server, RuletaColor color, String quien, boolean mini) {
      girar(server, color, quien, mini, null);
   }

   static void girar(MinecraftServer server, RuletaColor color, String quien, boolean mini, List<ServerPlayer> destino) {
      ThreadLocalRandom rnd = ThreadLocalRandom.current();
      float startAngle = rnd.nextFloat() * 360.0F;
      float offset = (rnd.nextFloat() * 2.0F - 1.0F) * 15.0F;
      RuletaPayload p = new RuletaPayload(color.ordinal(), startAngle, 0, offset, quien, mini);
      if (destino == null) {
         Red.enviarATodos(server, p);
      } else {
         for (ServerPlayer j : destino) {
            Red.enviar(j, p);
         }
      }
   }

   private static String limpio(String s) {
      if (s == null) {
         return null;
      }

      String r = s.replace("\r", "").trim();
      return r.isEmpty() ? null : r;
   }

   public static void lanzar(ServerPlayer jugador, LanzarPayload m) {
      if (jugador != null && m != null) {
         if (!jugador.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)) {
            jugador.sendSystemMessage(Component.literal("Solo los operadores pueden lanzar la ruleta."));
         } else {
            MinecraftServer server = jugador.level().getServer();
            if (server != null) {
               if (m.color() == -1) {
                  parar(server);
                  jugador.sendSystemMessage(Component.literal("Ruleta parada."));
               } else if (m.color() >= 0 && m.color() < RuletaColor.VALUES.length) {
                  RuletaColor color = RuletaColor.byIndex(m.color());
                  if (Mensajes.ocupado()) {
                     jugador.sendSystemMessage(
                        Component.literal(
                              Mensajes.nutriaEnCurso()
                                 ? "Hay un Momento Nutria en proceso: espera a que termine o pulsa DETENER RULETA."
                                 : "Ya hay una ruleta girando: espera a que termine o pulsa DETENER RULETA."
                           )
                           .withStyle(ChatFormatting.RED)
                     );
                  } else if (color != RuletaColor.ROSA
                     || !m.animacion()
                     || !MomentoReviil.enMarcha()
                     || !MomentoReviil.enMarchaConCielo() && MomentosReviil.soloLuna()) {
                     if (color == RuletaColor.ROSA && m.animacion() && MomentoPhora.enMarcha()) {
                        jugador.sendSystemMessage(
                           Component.literal("No se puede lanzar el Momento Nutria con el Momento Phora en curso: espera a que se termine.")
                              .withStyle(ChatFormatting.RED)
                        );
                     } else {
                        String titulo = limpio(m.titulo());
                        String texto = limpio(m.mensaje());
                        String tiempo = limpio(m.tiempo());
                        boolean esRoja = color == RuletaColor.ROJO;
                        boolean phora = esRoja && m.phora();
                        boolean reviil = !phora && (m.reviil() || esRoja && m.cielo());
                        if (phora && MomentoPhora.enMarcha()) {
                           jugador.sendSystemMessage(
                              Component.literal("Ya hay un Momento Phora en curso: no se puede lanzar otro hasta que se termine.")
                                 .withStyle(ChatFormatting.RED)
                           );
                        } else if ((phora || esRoja && reviil && (m.para() == null || m.para().isBlank())) && ComandoEclipse.enMarcha()) {
                           jugador.sendSystemMessage(
                              Component.literal("No se puede lanzar un Momento Reviil ni un Momento Phora con un eclipse en curso: espera a que se termine.")
                                 .withStyle(ChatFormatting.RED)
                           );
                        } else if (phora && MomentoReviil.enMarcha()) {
                           jugador.sendSystemMessage(
                              Component.literal("No se puede lanzar el Momento Phora con un Momento Reviil en curso: espera a que se termine.")
                                 .withStyle(ChatFormatting.RED)
                           );
                        } else if (esRoja && reviil && !phora && (m.para() == null || m.para().isBlank()) && MomentoPhora.enMarcha()) {
                           jugador.sendSystemMessage(
                              Component.literal("No se puede lanzar un Momento Reviil con el Momento Phora en curso: espera a que se termine.")
                                 .withStyle(ChatFormatting.RED)
                           );
                        } else {
                           if (esRoja && !reviil && !phora) {
                              tiempo = null;
                           }

                           if (esRoja && reviil && MomentosReviil.activo("eclipse")) {
                              tiempo = null;
                           }

                           List<Cambios.Entrada> nuevos = new ArrayList<>();
                           if (color == RuletaColor.NARANJA || color == RuletaColor.AMARILLO) {
                              List<Cambios.Entrada> pedidos = Cambios.elegidos(m.cambios());
                              if (!pedidos.isEmpty()) {
                                 Cambios.Entrada elegido = pedidos.get(0);
                                 if (cambios(server).activo(elegido.id())) {
                                    jugador.sendSystemMessage(
                                       Component.literal(
                                          color == RuletaColor.AMARILLO
                                             ? "Ese mob ya está activo y es permanente."
                                             : "Ese cambio de dificultad ya está activo y es permanente."
                                       )
                                    );
                                    return;
                                 }

                                 if (elegido.id().equals(Mensajes.cambioPendiente())) {
                                    jugador.sendSystemMessage(
                                       Component.literal("Ese cambio de dificultad ya está lanzado: se activa cuando salga el anuncio en el chat.")
                                    );
                                    return;
                                 }

                                 nuevos.add(elegido);
                              }
                           }

                           if (texto == null && !nuevos.isEmpty()) {
                              texto = Cambios.unirTextos(nuevos);
                           }

                           boolean paraUno = m.para() != null && !m.para().isBlank();
                           if (paraUno && !nuevos.isEmpty()) {
                              jugador.sendSystemMessage(Component.literal("Los cambios de dificultad y los mobs nuevos son para todos: quita el PARA."));
                           } else {
                              if (esRoja && reviil && !paraUno) {
                                 MomentosReviil.antesDeLanzar(jugador);
                                 texto = MomentosBunker.conCoordenadas(texto);
                              }

                              if (esRoja && reviil && !paraUno && texto == null) {
                                 String[] momentos = MomentosReviil.textoArmados();
                                 if (momentos != null) {
                                    texto = momentos[1];
                                 }
                              }

                              if (titulo == null && !nuevos.isEmpty() && color == RuletaColor.NARANJA) {
                                 titulo = "Cambios de dificultad";
                              }

                              int segundos = -1;
                              if (tiempo != null) {
                                 segundos = Tiempo.leer(tiempo);
                                 if (segundos <= 0 || segundos > 86400) {
                                    jugador.sendSystemMessage(Component.literal("Temporizador no válido: " + tiempo + ". Usa 59:59 o hasta 24:00:00."));
                                    return;
                                 }
                              }

                              List<ServerPlayer> destino = null;
                              boolean conSancion = color == RuletaColor.MORADO && m.sancion() != 0;
                              boolean paraTodos = color == RuletaColor.ROSA || color == RuletaColor.NARANJA || color == RuletaColor.AMARILLO;
                              if (!conSancion && !paraTodos && m.para() != null && !m.para().isBlank()) {
                                 ServerPlayer elegido = server.getPlayerList().getPlayerByName(m.para());
                                 if (elegido == null) {
                                    jugador.sendSystemMessage(Component.literal("El jugador " + m.para() + " no está conectado."));
                                    return;
                                 }

                                 destino = List.of(elegido);
                              }

                              Runnable sancion = null;
                              if (color == RuletaColor.MORADO && m.sancion() != 0) {
                                 ServerPlayer obj = m.objetivo() == null ? null : server.getPlayerList().getPlayerByName(m.objetivo());
                                 if (obj == null) {
                                    jugador.sendSystemMessage(Component.literal("Elige un jugador conectado para la sanción."));
                                    return;
                                 }

                                 if (Sanciones.protegido(server, obj)) {
                                    jugador.sendSystemMessage(
                                       Component.literal("No se puede sancionar a " + obj.getGameProfile().name() + ": es operador de nivel 3 o 4.")
                                    );
                                    return;
                                 }

                                 long segs = Sanciones.leerTiempo(m.tiempoBan());
                                 if (m.sancion() == 1 && segs <= 0L) {
                                    jugador.sendSystemMessage(Component.literal("Tiempo de ban no válido: usa minutos (30 o 30m) u horas (2h, 1h30m)."));
                                    return;
                                 }

                                 int tipoSancion = m.sancion();
                                 String quienSanciona = jugador.getGameProfile().name();
                                 sancion = () -> Sanciones.programar(obj, tipoSancion, segs, quienSanciona);
                              }

                              boolean mini = m.tipo() == 0;
                              String idCambio = nuevos.isEmpty() ? null : nuevos.get(0).id();
                              Runnable alEnviar = nuevos.isEmpty() ? null : () -> activarCambios(server, nuevos);
                              Runnable antesCielo = null;
                              boolean eclipseNuevo = esRoja && reviil && !paraUno && MomentosReviil.activo("eclipse") && !MomentoReviil.enMarcha();
                              if (esRoja && reviil && !paraUno) {
                                 int duracion = segundos;
                                 boolean conCielo = (m.cielo() || MomentosReviil.hayArmados()) && !MomentosReviil.sinCielo();
                                 if (conCielo) {
                                    antesCielo = () -> MomentoReviil.empezar(server, duracion, true);
                                    alEnviar = () -> MomentosReviil.lanzarElegidos(server);
                                 } else {
                                    alEnviar = () -> {
                                       MomentoReviil.empezar(server, duracion, false, !eclipseNuevo);
                                       MomentosReviil.lanzarElegidos(server);
                                    };
                                 }
                              }

                              if (phora) {
                                 int duracion = segundos;
                                 alEnviar = () -> MomentoPhora.empezar(server, duracion);
                                 antesCielo = null;
                                 if (texto == null) {
                                    texto = "Los phoras han llegado, ponte a salvo!.";
                                 }
                              }

                              if (sancion != null) {
                                 Runnable antes = alEnviar;
                                 Runnable s2 = sancion;
                                 alEnviar = antes == null ? s2 : () -> {
                                    antes.run();
                                    s2.run();
                                 };
                              }

                              boolean conAnimacion = !phora && esRoja && (m.animacion() || reviil && !paraUno && MomentosReviil.hayArmados());
                              boolean sinRuleta = m.sinRuleta() && (color == RuletaColor.NARANJA || color == RuletaColor.AMARILLO);
                              if (!sinRuleta) {
                                 girar(server, color, jugador.getGameProfile().name(), mini, destino);
                              }

                              Mensajes.programar(
                                 color, jugador.getGameProfile().name(), titulo, texto, segundos, destino, idCambio, alEnviar, conAnimacion, sinRuleta
                              );
                              if (color == RuletaColor.ROSA && !m.animacion()) {
                                 Mensajes.sinNutria();
                              }

                              if (conAnimacion && esRoja && reviil && !paraUno && !MomentoReviil.enMarcha()) {
                                 Mensajes.marcarTema(eclipseNuevo);
                              }

                              if (antesCielo != null) {
                                 Mensajes.antesDelAnuncio(antesCielo);
                              }

                              if (phora) {
                                 Mensajes.marcarPhora();
                              }
                           }
                        }
                     }
                  } else {
                     jugador.sendSystemMessage(
                        Component.literal("No se puede lanzar el Momento Nutria con un Momento Reviil en marcha (solo con la Maldición de la Luna).")
                           .withStyle(ChatFormatting.RED)
                     );
                  }
               }
            }
         }
      }
   }

   private static void activarCambios(MinecraftServer server, List<Cambios.Entrada> nuevos) {
      Ruleta.DificultadDatos d = cambios(server);

      for (Cambios.Entrada nuevo : nuevos) {
         d.activar(nuevo.id());
         CambiosEfectos.alActivar(server, nuevo.id());
      }

      d.setDirty();
      Red.enviarATodos(server, new CambiosPayload(d.csv()));
   }

   public static void alRegistrarComandos(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("totem").requires(s -> s.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)))
            .then(
               ((LiteralArgumentBuilder)Commands.literal("mensaje")
                     .then(Commands.literal("on").executes(c -> mensajeTotem((CommandSourceStack)c.getSource(), true))))
                  .then(Commands.literal("off").executes(c -> mensajeTotem((CommandSourceStack)c.getSource(), false)))
            )
      );
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("cambios").requires(s -> s.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER))).executes(c -> {
         ServerPlayer p = ((CommandSourceStack)c.getSource()).getPlayerOrException();
         Red.enviar(p, new CambiosPayload(cambios(p.level().getServer()).csv()));
         Red.enviar(p, new ReviilPayload("abrir_cambios", 1, Component.empty(), Component.empty()));
         return 1;
      }));
   }

   private static int mensajeTotem(CommandSourceStack fuente, boolean on) {
      Ruleta.DificultadDatos d = cambios(fuente.getServer());
      d.sinMensajeTotem = !on;
      d.setDirty();
      fuente.sendSuccess(() -> Component.literal(on ? "Mensaje de los tótems: ACTIVADO." : "Mensaje de los tótems: DESACTIVADO."), true);
      return 1;
   }

   static void desactivarCambio(ServerPlayer jugador, String id) {
      if (jugador != null && jugador.getServer() != null && jugador.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)) {
         MinecraftServer server = jugador.getServer();
         Ruleta.DificultadDatos d = cambios(server);
         if (d.activo(id)) {
            d.desactivar(id);
            d.setDirty();
            if ("sin-regeneracion".equals(id)) {
               ((BooleanValue)server.getGameRules().getRule(GameRules.RULE_NATURAL_REGENERATION)).set(true, server);
            }

            Red.enviarATodos(server, new CambiosPayload(d.csv()));
            Cambios.Entrada e = Cambios.porId(id);
            jugador.sendSystemMessage(Component.literal("Desactivado: " + (e == null ? id : e.titulo())).withStyle(ChatFormatting.YELLOW));
         }
      }
   }

   private static void parar(MinecraftServer server) {
      Mensajes.parar(server);
      MomentoPhora.terminar(server);
      MomentoMeteoritos.terminar(server);
      ComandoTornado.pararTodo(server);
      MomentoReviil.terminar(server);
      if (MomentosReviil.hayArmados()) {
         MomentosReviil.alTerminar(server);
      }

      Pantalla.sacarAviso(server, null);
      Red.enviarATodos(server, new ReviilPayload("parar", 1, Component.empty(), Component.empty()));

      for (ServerPlayer p : server.getPlayerList().getPlayers()) {
         p.removeEffect(Efectos.TEMBLOR.get());
         p.removeEffect(Efectos.RULETA_ROJA.get());
      }
   }

   private static DamageSource fuenteMagma(ServerPlayer p) {
      return new DamageSource(p.damageSources().starve().typeHolder()) {
         public String getMsgId() {
            return "hotFloor";
         }
      };
   }

   private static DamageSource fuentePerla(ServerPlayer p) {
      return new DamageSource(p.damageSources().starve().typeHolder()) {
         public String getMsgId() {
            return "fall";
         }
      };
   }

   public static DamageSource fuenteLetalPublica(ServerPlayer p, String id, String frase) {
      return fuenteLetal(p, id, frase);
   }

   static DamageSource fuenteLetal(ServerPlayer p, String id, String frase) {
      return new DamageSource(p.damageSources().starve().typeHolder()) {
         public String getMsgId() {
            return id;
         }

         public Component getLocalizedDeathMessage(LivingEntity victima) {
            return Component.empty().append(victima.getDisplayName()).append(Component.literal(frase));
         }
      };
   }

   public static boolean cambioActivoPublico(String id) {
      return cambioActivo(id);
   }

   public static boolean cambioActivo(String id) {
      MinecraftServer server = Servidor.actual();
      return server != null && server.overworld() != null && cambios(server).activo(id);
   }

   private static Ruleta.DificultadDatos cambios(MinecraftServer server) {
      if (cambios == null) {
         cambios = (Ruleta.DificultadDatos)server.overworld()
            .getDataStorage()
            .computeIfAbsent(new Factory(Ruleta.DificultadDatos::new, Ruleta.DificultadDatos::cargar, DataFixTypes.LEVEL), "ruleta_perla");
      }

      return cambios;
   }

   static boolean perlaX2() {
      MinecraftServer server = Servidor.actual();
      return server != null && cambios(server).perla;
   }

   static boolean magmaDano() {
      MinecraftServer server = Servidor.actual();
      return server != null && cambios(server).magma;
   }

   public static boolean totemGastadoAhora(ServerPlayer p) {
      Long t = ULTIMO_AVISO_TOTEM.get(p.getUUID());
      return t != null && p.level().getGameTime() - t <= 1L;
   }

   public static void avisarTotem(ServerPlayer p, DamageSource fuente, Component nombreTotem) {
      if (p.level().getServer() != null && !cambios(p.level().getServer()).sinMensajeTotem) {
         long ahora = p.level().getGameTime();
         Long previo = ULTIMO_AVISO_TOTEM.put(p.getUUID(), ahora);
         if (previo == null || ahora - previo > 1L) {
            Component mensaje = Component.empty()
               .append(Component.literal("\ue000").setStyle(Style.EMPTY.withFont(new net.minecraft.network.chat.FontDescription.Resource(Identifier.fromNamespaceAndPath("ruleta", "totem")))))
               .append(Component.literal(" "))
               .append(p.getName().copy())
               .append(Component.literal(" consumió "))
               .append(nombreTotem.copy().withStyle(estilo -> estilo.withColor(COLOR_TOTEM)))
               .append(Component.literal(" ("))
               .append(razonTotem(fuente, p))
               .append(Component.literal(")"))
               .withStyle(estilo -> estilo.withColor(COLOR_TOTEM));
            p.level().getServer().getPlayerList().broadcastSystemMessage(mensaje, false);
         }
      }
   }

   public static Component nombreTotem(ItemStack stack) {
      return stack.getHoverName().copy();
   }

   private static Component razonTotem(DamageSource fuente, ServerPlayer victima) {
      if ("supernova.onda".equals(fuente.getMsgId())) {
         return Component.literal("Onda Expansiva");
      }

      if ("supernova".equals(fuente.getMsgId())) {
         return Component.literal("Supernova");
      }

      if ("ruleta.herobrine".equals(fuente.getMsgId())) {
         return Component.literal("Herobrine");
      }

      if ("nuclear_explosion".equals(fuente.getMsgId())) {
         return Component.literal("Explosión de Creeper Nuclear");
      }

      String base = switch (fuente.getMsgId()) {
         case "fall" -> "caída";
         case "lava" -> "lava";
         case "inFire", "onFire" -> "fuego";
         case "hotFloor" -> "bloque de magma";
         case "ruleta.boton" -> "botón";
         case "ruleta.placa" -> "placa de presión";
         case "ruleta.puerta" -> "puerta";
         case "ruleta.gas" -> "Gas Tóxico de Reviil";
         case "ruleta.luna" -> "Luna maldita";
         case "ruleta.rayo" -> "Rayo mortal de Reviil";
         case "ruleta.agujero" -> "Agujeros Negros de Reviil";
         case "ruleta.tornado" -> "Tornado";
         case "ruleta.bomba_warden" -> "Bomba Warden";
         case "ruleta.meteorito" -> "Meteorito";
         case "ruleta.meteoritos" -> "Lluvia de Meteoritos de Reviil";
         case "ruleta.meteoritos_tornados" -> "Tornados de Reviil";
         case "drown" -> "ahogamiento";
         case "explosion", "explosion.player" -> "explosión";
         case "arrow" -> "flecha";
         case "trident" -> "tridente";
         case "fireball", "fireworks" -> "bola de fuego";
         case "thrown", "indirectMagic", "magic" -> "magia";
         case "wither", "witherSkull" -> "Wither";
         case "starve" -> "hambre";
         case "cactus" -> "cactus";
         case "sweetBerryBush" -> "arbusto de bayas";
         case "fallingBlock", "anvil" -> "bloque que cae";
         case "fallingStalactite" -> "estalactita";
         case "stalagmite" -> "estalagmita";
         case "flyIntoWall" -> "choque al volar";
         case "freeze" -> "congelación";
         case "lightningBolt" -> "rayo";
         case "dragonBreath" -> "aliento de dragón";
         case "thorns" -> "espinas";
         case "sonic_boom" -> "grito del Warden";
         case "cramming" -> "aplastamiento";
         case "inWall" -> "asfixia en un bloque";
         case "dryout" -> "deshidratación";
         case "mob", "player", "mob_projectile" -> "ataque";
         case "generic" -> "daño";
         default -> fuente.getMsgId();
      };
      Entity autor = fuente.getEntity();
      return autor != null && autor != victima ? Component.literal(base + " de ").append(autor.getName()) : Component.literal(base);
   }

   static final class DificultadDatos extends SavedData {
      static final String NOMBRE = "ruleta_perla";
      boolean perla;
      boolean magma;
      boolean sinMensajeTotem;
      final Set<String> otros = new LinkedHashSet<>();

      static Ruleta.DificultadDatos cargar(CompoundTag tag, Provider registros) {
         Ruleta.DificultadDatos d = new Ruleta.DificultadDatos();
         d.perla = tag.getBoolean("activo");
         d.magma = tag.getBoolean("magma");
         d.sinMensajeTotem = tag.getBoolean("sin_mensaje_totem");
         ListTag lista = tag.getList("cambios", 8);

         for (int i = 0; i < lista.size(); i++) {
            d.otros.add(lista.getString(i));
         }

         return d;
      }

      public CompoundTag save(CompoundTag tag, Provider registros) {
         tag.putBoolean("activo", this.perla);
         tag.putBoolean("magma", this.magma);
         tag.putBoolean("sin_mensaje_totem", this.sinMensajeTotem);
         ListTag lista = new ListTag();

         for (String id : this.otros) {
            lista.add(StringTag.valueOf(id));
         }

         tag.put("cambios", lista);
         return tag;
      }

      boolean activo(String id) {
         if ("perla".equals(id)) {
            return this.perla;
         } else {
            return "magma".equals(id) ? this.magma : this.otros.contains(id);
         }
      }

      void desactivar(String id) {
         if ("perla".equals(id)) {
            this.perla = false;
         } else if ("magma".equals(id)) {
            this.magma = false;
         } else {
            this.otros.remove(id);
         }
      }

      void activar(String id) {
         if ("perla".equals(id)) {
            this.perla = true;
         } else if ("magma".equals(id)) {
            this.magma = true;
         } else {
            this.otros.add(id);
         }
      }

      String csv() {
         StringJoiner j = new StringJoiner(",");
         if (this.perla) {
            j.add("perla");
         }

         if (this.magma) {
            j.add("magma");
         }

         for (String id : this.otros) {
            j.add(id);
         }

         return j.toString();
      }
   }

   public static final class Eventos {
      private Eventos() {
      }

      public static void alTick(MinecraftServer servidor) {
         Ruleta.tickChat++;
         if (!Ruleta.CHAT_VERDE.isEmpty()) {
            List<ServerPlayer> escribieron = new ArrayList<>();
            Ruleta.CHAT_VERDE.forEach((px, hasta) -> {
               if (Ruleta.tickChat >= hasta) {
                  escribieron.add(px);
               }
            });

            for (ServerPlayer p : escribieron) {
               Ruleta.CHAT_VERDE.remove(p);
            }
         }

         MinecraftServer server = Servidor.actual();
         if (server != null) {
            Mensajes.tick(server);
            MomentoReviil.tick(server);
         }
      }

      public static void alApagar(MinecraftServer servidor) {
         Ruleta.cambios = null;
         Mensajes.parar();
         MomentoReviil.limpiar();
         Pantalla.aviso = null;
         Pantalla.temporizador = null;
      }

      @SubscribeEvent
      public static void alTeletransportarPerla(EntityTeleportEvent.EnderPearl e) {
         float dano = e.getAttackDamage();
         if (Ruleta.perlaX2()) {
            dano *= 2.0F;
         }

         ServerPlayer jugador = e.getPlayer();
         MinecraftServer servidor = jugador == null ? null : jugador.level().getServer();
         if (jugador != null) {
            CambiosEfectos.marcarPerla(jugador);
         }

         if (servidor != null && dano > 0.0F && Ruleta.cambioActivo("perla-ignora-armadura")) {
            e.setAttackDamage(0.0F);
            float finalDano = dano;
            servidor.execute(() -> {
               if (jugador.isAlive()) {
                  jugador.hurt(Ruleta.fuentePerla(jugador), finalDano);
               }
            });
         } else {
            e.setAttackDamage(dano);
         }

         if (jugador != null && !e.isCanceled()) {
            CambiosEfectos.perlaAterrizo(jugador, e.getTargetX(), e.getTargetY(), e.getTargetZ());
         }
      }

      @SubscribeEvent(priority = EventPriority.LOWEST)
      public static void alUsarTotem(LivingUseTotemEvent e) {
         if (e.getEntity() instanceof ServerPlayer p) {
            Ruleta.avisarTotem(p, e.getSource(), Ruleta.nombreTotem(e.getTotem()));
         }
      }

      @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
      public static void antesDeMorir(LivingDeathEvent e) {
         if (e.getEntity() instanceof ServerPlayer p) {
            Ruleta.INVENTARIO_ANTES.put(p.getUUID(), contarObjetos(p));
         }
      }

      @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
      public static void despuesDeMorir(LivingDeathEvent e) {
         if (e.getEntity() instanceof ServerPlayer p) {
            Map<Item, Integer> antes = Ruleta.INVENTARIO_ANTES.remove(p.getUUID());
            if (antes != null && e.isCanceled() && p.isAlive() && !(p.getHealth() <= 0.0F)) {
               Map<Item, Integer> ahora = contarObjetos(p);
               Item gastado = null;

               for (Entry<Item, Integer> en : antes.entrySet()) {
                  if (en.getValue() > ahora.getOrDefault(en.getKey(), 0)) {
                     Identifier id = BuiltInRegistries.ITEM.getKey(en.getKey());
                     if (gastado == null || id != null && id.getPath().contains("totem")) {
                        gastado = en.getKey();
                     }
                  }
               }

               if (gastado != null) {
                  Ruleta.avisarTotem(p, e.getSource(), Ruleta.nombreTotem(new ItemStack(gastado)));
               }
            }
         }
      }

      private static Map<Item, Integer> contarObjetos(ServerPlayer p) {
         Map<Item, Integer> r = new HashMap<>();
         int n = p.getInventory().getContainerSize();

         for (int i = 0; i < n; i++) {
            ItemStack st = p.getInventory().getItem(i);
            if (!st.isEmpty()) {
               r.merge(st.getItem(), st.getCount(), Integer::sum);
            }
         }

         return r;
      }

      @SubscribeEvent
      public static void alChat(ServerChatEvent e) {
         ServerPlayer p = e.getPlayer();
         Ruleta.CHAT_VERDE.put(p, Ruleta.tickChat + 10L);
      }

      @SubscribeEvent
      public static void alNombreJugador(PlayerEvent.NameFormat e) {
         if (e.getEntity() instanceof ServerPlayer p && Ruleta.CHAT_VERDE.containsKey(p)) {
            e.setDisplayname(e.getDisplayname().copy().withStyle(estilo -> estilo.withColor(TextColor.fromRgb(43520))));
         }
      }

      public static void alTickJugador(ServerPlayer p) {
         if (Ruleta.magmaDano()) {
            if (p.isAlive() && !p.isSpectator() && !p.isCreative()) {
               AABB caja = p.getBoundingBox().inflate(0.002);
               MutableBlockPos pos = new MutableBlockPos();

               for (int x = Mth.floor(caja.minX); x <= Mth.floor(caja.maxX); x++) {
                  for (int y = Mth.floor(caja.minY); y <= Mth.floor(caja.maxY); y++) {
                     for (int z = Mth.floor(caja.minZ); z <= Mth.floor(caja.maxZ); z++) {
                        if (p.level().getBlockState(pos.set(x, y, z)).is(Blocks.MAGMA_BLOCK)) {
                           p.hurt(Ruleta.fuenteMagma(p), 1.0E8F);
                           return;
                        }
                     }
                  }
               }
            }
         }
      }

      public static void alEntrar(ServerPlayer p) {
         if (p.level().getServer() != null) {
            Red.enviar(p, new CambiosPayload(Ruleta.cambios(p.level().getServer()).csv()));
            MomentoReviil.alEntrar(p);
            Pantalla.alEntrar(p);
         }
      }
   }
}
