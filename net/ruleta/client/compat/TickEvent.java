package net.ruleta.client.compat;

public abstract class TickEvent extends Evento {
   public final TickEvent.Phase phase;

   protected TickEvent(TickEvent.Phase fase) {
      this.phase = fase;
   }

   public static final class ClientTickEvent extends TickEvent {
      public ClientTickEvent(TickEvent.Phase fase) {
         super(fase);
      }
   }

   public static enum Phase {
      START,
      END;
   }
}
