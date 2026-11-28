package net.ruleta.client.compat;

import net.minecraft.network.chat.Component;

public final class ClientChatReceivedEvent extends Evento {
   private Component mensaje;
   private final boolean sistema;

   public ClientChatReceivedEvent(Component mensaje, boolean sistema) {
      this.mensaje = mensaje;
      this.sistema = sistema;
   }

   public Component getMessage() {
      return this.mensaje;
   }

   public void setMessage(Component mensaje) {
      this.mensaje = mensaje;
   }

   public boolean isSystem() {
      return this.sistema;
   }
}
