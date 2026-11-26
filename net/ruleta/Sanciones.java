package net.ruleta;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanListEntry;

public final class Sanciones {
   public static final int NADA = 0;
   public static final int BAN = 1;
   public static final int KICK = 2;
   private static final int TICKS_LEER = 200;
   private static final List<Sanciones.Pendiente> PENDIENTES = new ArrayList<>();
   private static long tick;

   private Sanciones() {
   }

   static boolean protegido(MinecraftServer server, ServerPlayer p) {
      return server.getProfilePermissions(p.getGameProfile()) >= 3;
   }

   public static long leerTiempo(String s) {
      if (s == null) {
         return 0L;
      } else {
         String t = s.trim().toLowerCase().replace(" ", "");
         if (t.isEmpty()) {
            return 0L;
         } else {
            try {
               if (t.matches("\\d+")) {
                  return Long.parseLong(t) * 60L;
               } else if (!t.matches("(\\d+h)?(\\d+m)?")) {
                  return 0L;
               } else {
                  long total = 0L;
                  int h = t.indexOf(104);
                  if (h >= 0) {
                     total += Long.parseLong(t.substring(0, h)) * 3600L;
                     t = t.substring(h + 1);
                  }

                  if (t.endsWith("m")) {
                     total += Long.parseLong(t.substring(0, t.length() - 1)) * 60L;
                  }

                  return total;
               }
            } catch (NumberFormatException var5) {
               return 0L;
            }
         }
      }
   }

   static String textoTiempo(long segundos) {
      long h = segundos / 3600L;
      long m = segundos % 3600L / 60L;
      StringBuilder b = new StringBuilder();
      if (h > 0L) {
         b.append(h).append(h == 1L ? " hora" : " horas");
      }

      if (m > 0L) {
         if (b.length() > 0) {
            b.append(" y ");
         }

         b.append(m).append(m == 1L ? " minuto" : " minutos");
      }

      return b.toString();
   }

   static void programar(ServerPlayer objetivo, int tipo, long segundos, String quien) {
      PENDIENTES.add(new Sanciones.Pendiente(tick + 200L, objetivo.getUUID(), tipo, segundos, textoTiempo(segundos), quien));
   }

   public static void alTick(MinecraftServer servidor) {
      tick++;
      if (!PENDIENTES.isEmpty()) {
         MinecraftServer server = Servidor.actual();
         Iterator<Sanciones.Pendiente> it = PENDIENTES.iterator();

         while (it.hasNext()) {
            Sanciones.Pendiente s = it.next();
            if (tick >= s.en()) {
               it.remove();
               ServerPlayer p = server == null ? null : server.getPlayerList().getPlayer(s.jugador());
               if (p != null && !protegido(server, p)) {
                  if (s.tipo() == 1) {
                     Date ahora = new Date();
                     Date hasta = new Date(ahora.getTime() + s.segundos() * 1000L);
                     String razon = s.textoTiempo() + " baneado/a por la Ruleta Morada.";
                     server.getPlayerList().getBans().add(new UserBanListEntry(p.getGameProfile(), ahora, s.quien(), hasta, razon));
                     p.connection.disconnect(Component.literal("Desconectado/a.\n" + s.textoTiempo() + " baneado/a."));
                  } else if (s.tipo() == 2) {
                     p.connection.disconnect(Component.literal("GG"));
                  }
               }
            }
         }
      }
   }

   public static void alApagar(MinecraftServer servidor) {
      PENDIENTES.clear();
   }

   private record Pendiente(long en, UUID jugador, int tipo, long segundos, String textoTiempo, String quien) {
   }
}
