package net.ruleta.client.compat;

public abstract class ClientPlayerNetworkEvent extends Evento {
   public static final class LoggingIn extends ClientPlayerNetworkEvent {
   }

   public static final class LoggingOut extends ClientPlayerNetworkEvent {
   }
}
