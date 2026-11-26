package net.ruleta.client.compat;

public abstract class Evento {
   private boolean cancelado;

   public boolean isCanceled() {
      return this.cancelado;
   }

   public void setCanceled(boolean cancelado) {
      this.cancelado = cancelado;
   }
}
