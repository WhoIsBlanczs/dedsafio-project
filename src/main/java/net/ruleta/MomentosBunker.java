package net.ruleta;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.ruleta.agujero.AgujeroPayload;
import net.ruleta.agujero.ComandoAgujero;
import net.ruleta.bunker.Bunkers;
import net.ruleta.compat.EntityTravelToDimensionEvent;
import net.ruleta.compat.SubscribeEvent;
import net.ruleta.meteoritos.MomentoMeteoritos;
import net.ruleta.puerta.PuertaBunkerBloque;
import net.ruleta.tornado.ComandoTornado;
import org.jetbrains.annotations.Nullable;

public final class MomentosBunker {
   private static long encerrados = Ajustes.ticks("bunker.cerrado");
   private static final long ESPERA_ECLIPSE = 600L;
   private static final int TOTALIDAD_ECLIPSE = Math.max(5, Math.round(61.0F));
   static final String TEXTO_ECLIPSE_2 = "¡Ha llegado el Eclipse! Deben utilizar linternas para abrirse paso por el mundo. CUIDADO: Herobrine está entre nosotros. NO lo mires a los ojos o te atacará y te hará mucho daño. Puedes apuntarle con la Linterna para que desaparezca.";
   static final String TITULO_ECLIPSE_2 = "Momento Reviil";
   @Nullable
   private static Bunkers.Bunker bunker;
   private static int puerta = -1;
   @Nullable
   private static String activo;
   private static long cierra = -1L;
   private static long abre = -1L;
   private static boolean cerrada;
   private static boolean netherFuera;
   private static long eclipseEn = -1L;
   private static long cierreFinal = -1L;
   @Nullable
   private static Bunkers.Bunker bunkerFinal;
   private static int puertaFinal = -1;
   @Nullable
   private static Boolean puertaPedida;
   private static String eclipseQuien = "";
   private static long tick;
   public static final String SEP = "\u001f";
   private static int pasoEclipse;
   private static boolean eclipseElegido;
   private static boolean eclipseHastaBunker;

   private MomentosBunker() {
   }

   private static boolean esDelBunker(String id) {
      return Momentos.delBunker(id);
   }

   @Nullable
   public static double[] centro() {
      return bunker == null ? null : new double[]{bunker.mx(), bunker.mz()};
   }

   private static boolean usaBunker(String id) {
      return esDelBunker(id) || "eclipse".equals(id);
   }

   private static String base(String extra) {
      int i = extra.indexOf("\u001f");
      return i < 0 ? extra : extra.substring(0, i);
   }

   static String mensajeEclipse(int i) {
      String[] p = MomentosReviil.extra("eclipse").split("\u001f", -1);
      return i + 1 < p.length && !p[i + 1].isBlank() ? p[i + 1].trim() : Momentos.ECLIPSE_TEXTOS[i];
   }

   private static String bunkerElegido(String id) {
      String e = base(MomentosReviil.extra(id));
      if ("eclipse".equals(id)) {
         String[] p = e.split(";", -1);
         return p.length >= 6 ? p[5] : "";
      } else {
         return e;
      }
   }

   private static int[] tiemposEclipse() {
      int[] r = new int[]{
         Ajustes.entero("eclipse.espera1"),
         Ajustes.entero("eclipse.espera2"),
         Ajustes.entero("eclipse.espera3"),
         Ajustes.entero("bunker.contador"),
         Ajustes.entero("bunker.cerrado")
      };
      String[] p = base(MomentosReviil.extra("eclipse")).split(";", -1);

      for (int i = 0; i < 5 && i < p.length; i++) {
         try {
            int v = Integer.parseInt(p[i].trim());
            if (v > 0) {
               r[i] = Math.min(86400, v);
            }
         } catch (NumberFormatException var4) {
         }
      }

      return r;
   }

   @Nullable
   static String preparar(ServerPlayer admin, Iterable<String> elegidos) {
      eclipseQuien = admin.getGameProfile().name();
      String quien = null;

      for (String id : elegidos) {
         if (usaBunker(id)) {
            quien = id;
         }
      }

      if (quien == null) {
         return null;
      }

      if (bunker == null || activo == null) {
         Bunkers.Bunker elegido = Bunkers.porClave(admin.getServer(), bunkerElegido(quien));
         bunker = elegido != null ? elegido : Bunkers.elegirOConstruir(admin);
         puerta = admin.getRandom().nextInt(Bunkers.PUERTAS.length);
      }

      if (bunker == null) {
         admin.sendSystemMessage(Component.literal("No se pudo preparar el bunker."));
         return "?";
      } else {
         return "X: " + (int)Math.floor(bunker.mx()) + " // Z: " + (int)Math.floor(bunker.mz());
      }
   }

   static String conCoordenadas(String texto) {
      if (texto != null && texto.contains("{bunker}")) {
         String c = bunker == null ? "?" : "X: " + (int)Math.floor(bunker.mx()) + " // Z: " + (int)Math.floor(bunker.mz());
         return texto.replace("{bunker}", c);
      } else {
         return texto;
      }
   }

   static void empezar(MinecraftServer server, String id, int segundos, int segundosCerrada) {
      if ("eclipse".equals(id)) {
         int[] t = tiemposEclipse();
         pasoEclipse = 2;
         eclipseEn = tick + t[0] * 20L;
      } else if (esDelBunker(id) && bunker != null) {
         activo = id;
         encerrados = segundosCerrada > 0 ? segundosCerrada * 20L : Ajustes.ticks("bunker.cerrado");
         cierreFinal = -1L;
         Bunkers.mostrarMarcador(server, bunker);
         cierra = tick + (segundos > 0 ? segundos : Ajustes.entero("bunker.contador")) * 20L;
         abre = -1L;
         cerrada = false;
         ponerPuerta(server, true);
         if ("bunker_agujeros".equals(id)) {
            netherFuera = true;
            ComandoAgujero.cielo(server, true);
            agujeros(server);
         }

         if ("bunker_meteoritos".equals(id)) {
            MomentoMeteoritos.empezarLluvia(server);
         }

         if ("bunker_meteoritos_tornados".equals(id)) {
            ComandoTornado.momentoTornados(server);
            Red.enviarATodos(server, new ReviilPayload("reviil_activo", 3, Component.empty(), Component.empty()));
         }
      }
   }

   private static void ponerPuerta(MinecraftServer server, boolean abierta) {
      puertaPedida = abierta;
      intentarPuerta(server, bunker, puerta);
   }

   private static void intentarPuerta(MinecraftServer server, @Nullable Bunkers.Bunker b, int i) {
      if (puertaPedida != null && b != null && i >= 0) {
         ServerLevel nivel = Bunkers.nivel(server, b);
         if (nivel != null && PuertaBunkerBloque.ponerPuerta(nivel, Bunkers.puerta(b, i), puertaPedida)) {
            puertaPedida = null;
         }
      }
   }

   private static void agujeros(MinecraftServer server) {
      ServerLevel mundo = server.overworld();
      List<ServerPlayer> jugadores = new ArrayList<>();

      for (ServerPlayer p : mundo.players()) {
         if (!p.isSpectator()) {
            jugadores.add(p);
         }
      }

      List<Vec3> posiciones = new ArrayList<>();

      for (ServerPlayer p : jugadores) {
         posiciones.add(p.position());
      }

      if (!posiciones.isEmpty()) {
         ComandoAgujero.quitarLejos(server, posiciones, Ajustes.entero("agujeros.olvidar"));
      }

      Vec3 centro = bunker == null ? null : new Vec3(bunker.mx(), bunker.ey(), bunker.mz());
      float radioBase = Ajustes.decimal("agujeros.radio");
      int maxAg = Math.max(Ajustes.entero("agujeros.min"), Ajustes.entero("agujeros.max"));
      int cuantos = Math.max(Ajustes.entero("agujeros.min"), Math.min(maxAg, jugadores.size() * Ajustes.entero("agujeros.por_jugador")));
      double lejos = Ajustes.entero("agujeros.lejos_bunker");
      double separacion = Ajustes.entero("agujeros.separacion");
      double capaMin = Ajustes.entero("agujeros.capa_min");
      int probMed = Ajustes.entero("agujeros.prob_mediano");
      int probGig = Ajustes.entero("agujeros.prob_gigante");
      List<AgujeroPayload.Agujero> puestos = ComandoAgujero.existentes();
      RandomSource rnd = mundo.getRandom();
      int faltan = cuantos - puestos.size();

      for (int intento = 0; intento < Math.max(1, faltan) * 40 && faltan > 0; intento++) {
         int dado = rnd.nextInt(100);
         float radio = dado < probGig
            ? radioBase * Ajustes.decimal("agujeros.gigante")
            : (dado < probGig + probMed ? radioBase * Ajustes.decimal("agujeros.mediano") : radioBase);
         Vec3 base;
         if (!jugadores.isEmpty()) {
            ServerPlayer p = jugadores.get(rnd.nextInt(jugadores.size()));
            base = p.position();
         } else {
            base = Vec3.atCenterOf(mundo.getRespawnData().pos());
         }

         double ang = rnd.nextDouble() * Math.PI * 2.0;
         double d = 80.0 + rnd.nextDouble() * 320.0;
         double x = base.x + Math.cos(ang) * d;
         double z = base.z + Math.sin(ang) * d;
         if (centro == null || !(Math.hypot(x - centro.x, z - centro.z) < lejos + radio)) {
            boolean junto = false;

            for (AgujeroPayload.Agujero otro : puestos) {
               junto |= Math.hypot(x - otro.pos().x, z - otro.pos().z) < separacion + radio + otro.radio();
            }

            if (!junto) {
               int suelo = superficie(mundo, (int)Math.floor(x), (int)Math.floor(z));
               Vec3 sitio = new Vec3(x, Math.max(suelo + radio * 2.2 + 6.0, capaMin + radio * 2.2), z);
               ComandoAgujero.crearAgujero(mundo, sitio, radio);
               puestos = ComandoAgujero.existentes();
               faltan = cuantos - puestos.size();
            }
         }
      }
   }

   private static int superficie(ServerLevel mundo, int x, int z) {
      mundo.getChunk(x >> 4, z >> 4);
      MutableBlockPos p = new MutableBlockPos(x, mundo.getMaxY() - 1, z);

      while (p.getY() > mundo.getMinY()) {
         BlockState s = mundo.getBlockState(p);
         if (!s.isAir() && s.isSolid() && !s.is(BlockTags.LEAVES)) {
            return p.getY() + 1;
         }

         p.move(0, -1, 0);
      }

      return mundo.getSeaLevel();
   }

   static void tick(MinecraftServer server) {
      tick++;
      eclipse(server);
      if (puertaPedida != null) {
         if (activo != null) {
            intentarPuerta(server, bunker, puerta);
         } else {
            intentarPuerta(server, bunkerFinal, puertaFinal);
         }
      }

      if (activo == null) {
         despues(server);
      } else {
         if (!cerrada && tick >= cierra) {
            cerrada = true;
            abre = tick + encerrados;
            ponerPuerta(server, false);
         }

         if (cerrada && tick >= abre) {
            terminar(server);
         } else {
            if ("bunker_agujeros".equals(activo) && tick % (Math.max(2, Ajustes.entero("agujeros.reponer")) * 20L) == 0L) {
               agujeros(server);
            }

            if ("bunker_meteoritos_tornados".equals(activo) && tick % (Math.max(2, Ajustes.entero("tornado.momento_reponer")) * 20L) == 0L) {
               ComandoTornado.momentoTornados(server);
            }

            long cada = Math.max(1, Ajustes.entero("dano.bunker_intervalo")) * 20L;
            if (cerrada && tick % cada == 0L) {
               float dano = Ajustes.decimal("dano.bunker") * 2.0F;

               for (ServerPlayer p : new ArrayList(server.getPlayerList().getPlayers())) {
                  if (p.isAlive() && !p.isCreative() && !p.isSpectator() && !dentro(p)) {
                     p.setInvulnerableTime(0);
                     if ("bunker_rayo".equals(activo)) {
                        p.hurt(Ruleta.fuenteLetal(p, "ruleta.rayo", " murió por el Rayo mortal de Reviil"), dano);
                        if (Ajustes.bool("rayo.activo") && p.getRandom().nextInt(100) < Ajustes.entero("rayo.prob")) {
                           rayo(p);
                        }
                     } else if ("bunker_meteoritos".equals(activo)) {
                        p.hurt(Ruleta.fuenteLetal(p, "ruleta.meteoritos", " murió por la Lluvia de Meteoritos de Reviil"), dano);
                     } else if ("bunker_meteoritos_tornados".equals(activo)) {
                        p.hurt(Ruleta.fuenteLetal(p, "ruleta.meteoritos_tornados", " murió por los Tornados de Reviil"), dano);
                     } else {
                        p.hurt(Ruleta.fuenteLetal(p, "ruleta.agujero", " fue absorbido por los Agujeros Negros de Reviil"), dano);
                     }
                  }
               }
            }
         }
      }
   }

   public static boolean usa(Bunkers.Bunker b) {
      return b == null
         ? false
         : bunker != null && bunker.clave().equals(b.clave()) || bunkerFinal != null && cierreFinal >= 0L && bunkerFinal.clave().equals(b.clave());
   }

   public static boolean gestionaPuerta(Level nivel, BlockPos origen) {
      Bunkers.Bunker b = null;
      int i = -1;
      if (activo != null && !cerrada) {
         b = bunker;
         i = puerta;
      } else if (activo == null && cierreFinal >= 0L) {
         b = bunkerFinal;
         i = puertaFinal;
      }

      return b != null && i >= 0 && nivel.dimension().identifier().toString().equals(b.dimension()) && Bunkers.puerta(b, i).equals(origen);
   }

   private static boolean dentro(ServerPlayer p) {
      return Bunkers.interiorDe(p.level(), p.blockPosition()) != null
         || Bunkers.interiorDe(p.level(), BlockPos.containing(p.getEyePosition())) != null;
   }

   private static void rayo(ServerPlayer p) {
      ServerLevel nivel = p.level();
      LightningBolt r = (LightningBolt)net.minecraft.world.entity.EntityTypes.LIGHTNING_BOLT.create(nivel);
      if (r != null) {
         double x = p.getX() + (p.getRandom().nextDouble() - 0.5) * 2.0 * Ajustes.entero("rayo.radio");
         double z = p.getZ() + (p.getRandom().nextDouble() - 0.5) * 2.0 * Ajustes.entero("rayo.radio");
         int y = nivel.getHeight(Types.MOTION_BLOCKING, (int)Math.floor(x), (int)Math.floor(z));
         r.snapTo(x, y, z);
         r.setVisualOnly(true);
         nivel.addFreshEntity(r);
      }
   }

   static void terminar(MinecraftServer server) {
      String era = activo;
      if (era != null) {
         ponerPuerta(server, true);
      }

      acabarEclipse(server);
      if ("bunker_meteoritos".equals(era)) {
         MomentoMeteoritos.terminar(server);
      }

      if ("bunker_meteoritos_tornados".equals(era)) {
         ComandoTornado.pararTodo(server);
      }

      if ("bunker_agujeros".equals(era)) {
         ComandoAgujero.quitarTodos(server);
         ComandoAgujero.cielo(server, false);
      }

      if (era != null) {
         bunkerFinal = bunker;
         puertaFinal = puerta;
         cierreFinal = tick + Ajustes.ticks("bunker.final_abierto");
         Bunkers.mostrarMarcador(server, null);
      }

      activo = null;
      cerrada = false;
      cierra = -1L;
      abre = -1L;
      netherFuera = false;
      bunker = null;
      puerta = -1;
      if (era != null) {
         MomentosReviil.terminarUno(server, era);
         if (!eclipseElegido && MomentosReviil.nadaEnEfecto() && MomentoReviil.enMarcha()) {
            MomentoReviil.terminar(server);
         }
      }

      if (eclipseElegido) {
         eclipseElegido = false;
         MomentosReviil.terminarUno(server, "eclipse");
         MomentoReviil.terminar(server);
      }
   }

   private static void despues(MinecraftServer server) {
      if (cierreFinal >= 0L) {
         if (tick < cierreFinal) {
            return;
         }

         cierreFinal = -1L;
         puertaPedida = false;
         intentarPuerta(server, bunkerFinal, puertaFinal);
      }

      if (tick % 40L == 0L && Ajustes.bool("bunker.expulsar")) {
         for (ServerPlayer p : new ArrayList(server.getPlayerList().getPlayers())) {
            if (p.isAlive() && !p.isCreative() && !p.isSpectator()) {
               Bunkers.Bunker b = Bunkers.interiorDe(p.level(), p.blockPosition());
               if (b != null) {
                  Bunkers.sacar(p, b);
               }
            }
         }
      }
   }

   private static void acabarEclipse(MinecraftServer server) {
      if (eclipseHastaBunker) {
         eclipseHastaBunker = false;
         ComandoEclipse.terminarMomento(server);
      }
   }

   private static void avisarSiguiente(MinecraftServer server, int segundos) {
      Red.enviarATodos(server, new ReviilPayload("temporizador", segundos, Component.empty(), Component.empty()));
   }

   private static void eclipse(MinecraftServer server) {
      if (eclipseEn >= 0L && tick >= eclipseEn && !Mensajes.ocupado()) {
         eclipseEn = -1L;
         int paso = pasoEclipse;
         int[] t = tiemposEclipse();
         String quien = eclipseQuien;
         String titulo = "Momento Reviil";
         String texto = mensajeEclipse(paso - 1);
         Runnable alSalir;
         if (paso == 2) {
            alSalir = () -> {
               pasoEclipse = 3;
               eclipseEn = tick + t[1] * 20L;
            };
         } else if (paso == 3) {
            alSalir = () -> {
               ComandoEclipse.empezarMomento(server, 86400);
               eclipseHastaBunker = true;
               pasoEclipse = 4;
               eclipseEn = tick + t[2] * 20L;
            };
         } else {
            texto = conCoordenadas(texto);
            alSalir = () -> {
               pasoEclipse = 0;
               if (bunker != null) {
                  activo = "bunker_rayo";
                  encerrados = t[4] * 20L;
                  cierreFinal = -1L;
                  Bunkers.mostrarMarcador(server, bunker);
                  cierra = tick + t[3] * 20L;
                  abre = -1L;
                  cerrada = false;
                  ponerPuerta(server, true);
                  avisarSiguiente(server, t[3]);
                  eclipseElegido = true;
               } else {
                  acabarEclipse(server);
                  MomentosReviil.terminarUno(server, "eclipse");
                  MomentoReviil.terminar(server);
               }
            };
         }

         Ruleta.girar(server, RuletaColor.ROJO, quien, false);
         Mensajes.programar(RuletaColor.ROJO, quien, titulo, texto, -1, null, null, alSalir, false);
      }
   }

   static void alTerminarReviil(MinecraftServer server) {
      eclipseEn = -1L;
      pasoEclipse = 0;
      eclipseElegido = false;
      acabarEclipse(server);
      if (activo != null) {
         terminar(server);
      }
   }

   @SubscribeEvent
   public static void alViajar(EntityTravelToDimensionEvent e) {
      if (netherFuera && e.getDimension() == Level.NETHER) {
         e.setCanceled(true);
         if (e.getEntity() instanceof ServerPlayer p) {
            p.sendOverlayMessage(Component.literal("El Nether ha sido desactivado."));
         }
      }
   }

   public static void alApagar(MinecraftServer servidor) {
      bunkerFinal = null;
      puertaFinal = -1;
      cierreFinal = -1L;
      puertaPedida = null;
      bunker = null;
      puerta = -1;
      activo = null;
      cerrada = false;
      netherFuera = false;
      eclipseEn = -1L;
      pasoEclipse = 0;
      eclipseHastaBunker = false;
      eclipseElegido = false;
   }
}
