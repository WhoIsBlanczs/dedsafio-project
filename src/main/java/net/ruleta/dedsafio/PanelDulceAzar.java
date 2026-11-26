package net.ruleta.dedsafio;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class PanelDulceAzar {
   private PanelDulceAzar() {
   }

   public static void recibir(ServerPlayer origen, DulceAzarAccionPayload m) {
      if (origen != null && origen.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)) {
         MinecraftServer servidor = origen.getServer();
         if (servidor != null) {
            List<ServerPlayer> elegidos = null;
            if (m.jugador() != null && !m.jugador().isEmpty()) {
               ServerPlayer uno = servidor.getPlayerList().getPlayerByName(m.jugador());
               if (uno == null) {
                  return;
               }

               elegidos = List.of(uno);
            }

            int acc = m.accion();
            boolean necesitaDimension = acc >= 2 && acc != 2 && acc != 4 && acc != 9;
            if (necesitaDimension && !origen.level().dimension().equals(ComandoDedsafio.DIMENSION)) {
               origen.sendSystemMessage(Component.literal("No estás en la dimensión de Dulce Azar, inténtalo allá").withStyle(ChatFormatting.RED));
            } else {
               switch (m.accion()) {
                  case 0:
                     ComandoDedsafio.viajar(servidor, elegidos, true);
                     break;
                  case 1:
                     ComandoDedsafio.viajar(servidor, elegidos, false);
                  case 2:
                  case 3:
                  case 4:
                  default:
                     break;
                  case 5:
                     EscenaServidor.iniciar(servidor);
                     break;
                  case 6:
                     EscenaServidor.parar(servidor);
                     break;
                  case 7:
                     OcultarServidor.ocultarTodos(servidor);
                     break;
                  case 8:
                     if (elegidos == null) {
                        OcultarServidor.mostrarTodos(servidor);
                     } else {
                        OcultarServidor.mostrar(servidor, elegidos.get(0));
                     }
               }
            }
         }
      }
   }
}
