package net.ruleta.compat;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class ServerChatEvent extends Evento {
   private final ServerPlayer jugador;
   private final Component mensaje;

   public ServerChatEvent(ServerPlayer jugador, Component mensaje) {
      this.jugador = jugador;
      this.mensaje = mensaje;
   }

   public ServerPlayer getPlayer() {
      return this.jugador;
   }

   public Component getMessage() {
      return this.mensaje;
   }
}
