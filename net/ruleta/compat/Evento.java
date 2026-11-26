package net.ruleta.compat;

public abstract class Evento {
   private boolean cancelado;

   public boolean isCanceled() {
      return this.cancelado;
   }

   public void setCanceled(boolean cancelado) {
      this.cancelado = cancelado;
   }

   public static enum Result {
      DENY,
      DEFAULT,
      ALLOW;
   }
}
