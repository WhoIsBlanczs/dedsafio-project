package net.ruleta.client.dedsafio;

import net.ruleta.client.RedCliente;
import net.ruleta.dedsafio.DulceAzarAccionPayload;

public final class PanelEnvio {
   private PanelEnvio() {
   }

   public static void enviar(DulceAzarAccionPayload m) {
      RedCliente.enviar(m);
   }
}
