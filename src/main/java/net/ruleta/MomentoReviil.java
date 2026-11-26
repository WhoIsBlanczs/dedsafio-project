package net.ruleta;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class MomentoReviil {
   private static long tick;
   private static long desde = -1L;
   private static long hasta = -1L;
   private static boolean cielo;
   private static long desdeCielo = -1L;
   private static boolean solo;
   private static boolean piezaTormenta;
   private static boolean piezaGrieta;
   private static long desdeTormenta = -1L;
   private static long desdeGrieta = -1L;
   static final float GRIETA_ABIERTA = 16.623932F;

   private MomentoReviil() {
   }

   static void empezar(MinecraftServer server, int segundos) {
      empezar(server, segundos, true);
   }

   static void empezar(MinecraftServer server, int segundos, boolean conCielo) {
      empezar(server, segundos, conCielo, true);
   }

   static void empezar(MinecraftServer server, int segundos, boolean conCielo, boolean conMusica) {
      if (desde >= 0L) {
         if (segundos > 0) {
            hasta = tick + segundos * 20L;
         }

         if (conCielo && !cielo) {
            cielo = true;
            desdeCielo = tick;
            Red.enviarATodos(server, mensaje(0));
         }
      } else {
         desde = tick;
         hasta = segundos > 0 ? tick + segundos * 20L : -1L;
         Red.enviarATodos(server, new ReviilPayload("reviil_activo", valorLluvia(), Component.empty(), Component.empty()));
         cielo = conCielo;
         desdeCielo = tick;
         if (cielo) {
            Red.enviarATodos(server, mensaje(0));
         } else if (conMusica) {
            Red.enviarATodos(server, new ReviilPayload("musica_reviil", 1, Component.empty(), Component.empty()));
         }
      }
   }

   private static int valorLluvia() {
      return !MomentosReviil.activo("bunker_meteoritos_tornados") && !MomentosReviil.activo("eclipse") ? 1 : 3;
   }

   private static void abrirPiezas(MinecraftServer server) {
      if (desde < 0L) {
         desde = tick;
         hasta = -1L;
         Red.enviarATodos(server, new ReviilPayload("reviil_activo", valorLluvia(), Component.empty(), Component.empty()));
      }

      cielo = true;
      solo = true;
   }

   public static boolean piezaCielo(MinecraftServer server) {
      if (!piezaTormenta && (desde < 0L || !cielo || solo)) {
         abrirPiezas(server);
         piezaTormenta = true;
         desdeTormenta = tick;
         Red.enviarATodos(server, new ReviilPayload("cielo_pieza", 0, Component.empty(), Component.empty()));
         return true;
      } else {
         return false;
      }
   }

   public static boolean piezaGrieta(MinecraftServer server) {
      if (!piezaGrieta && (desde < 0L || !cielo || solo)) {
         abrirPiezas(server);
         piezaGrieta = true;
         desdeGrieta = tick;
         Red.enviarATodos(server, new ReviilPayload("grieta_pieza", 0, Component.empty(), Component.empty()));
         return true;
      } else {
         return false;
      }
   }

   public static boolean grietaAbierta() {
      return desde >= 0L && cielo && (!solo || piezaGrieta);
   }

   static int ticksHastaGrieta() {
      if (desde >= 0L && cielo) {
         long falta = desdeCielo + Math.round(332.47864F) + 5L - tick;
         return (int)Math.max(0L, falta);
      } else {
         return 0;
      }
   }

   static void alargar(int ticks) {
      if (desde >= 0L && hasta >= 0L) {
         hasta += ticks;
      }
   }

   public static boolean enMarchaConCielo() {
      return desde >= 0L && cielo;
   }

   public static boolean enMarcha() {
      return desde >= 0L;
   }

   static void terminar(MinecraftServer server) {
      if (desde >= 0L) {
         desde = -1L;
         hasta = -1L;
         Red.enviarATodos(server, new ReviilPayload("reviil_activo", 0, Component.empty(), Component.empty()));
         if (cielo) {
            Red.enviarATodos(server, mensaje(-1));
         }

         cielo = false;
         solo = false;
         piezaTormenta = false;
         piezaGrieta = false;
         desdeTormenta = -1L;
         desdeGrieta = -1L;
         MomentosReviil.alTerminar(server);
         if (RuletaColor.ROJO.id.equals(Pantalla.aviso)) {
            Pantalla.sacarAviso(server, null);
         }
      }
   }

   static void tick(MinecraftServer server) {
      tick++;
      if (hasta >= 0L && tick >= hasta) {
         terminar(server);
      }
   }

   static void alEntrar(ServerPlayer jugador) {
      Red.enviar(jugador, new ReviilPayload("reviil_activo", desde >= 0L ? valorLluvia() : 0, Component.empty(), Component.empty()));
      if (desde >= 0L && cielo && !solo) {
         Red.enviar(jugador, mensaje((int)((tick - desdeCielo) / 20L)));
      }

      if (desde >= 0L && solo) {
         if (piezaTormenta) {
            Red.enviar(jugador, new ReviilPayload("cielo_pieza", (int)((tick - desdeTormenta) / 20L), Component.empty(), Component.empty()));
         }

         if (piezaGrieta) {
            Red.enviar(jugador, new ReviilPayload("grieta_pieza", (int)((tick - desdeGrieta) / 20L), Component.empty(), Component.empty()));
         }
      }
   }

   static void limpiar() {
      desde = -1L;
      hasta = -1L;
      cielo = false;
      desdeCielo = -1L;
      solo = false;
      piezaTormenta = false;
      piezaGrieta = false;
      desdeTormenta = -1L;
      desdeGrieta = -1L;
   }

   private static ReviilPayload mensaje(int valor) {
      return new ReviilPayload("cielo", valor, Component.empty(), Component.empty());
   }
}
