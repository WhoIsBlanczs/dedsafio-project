package ar.hombrosafio.ruleta.casino.client;

import ar.hombrosafio.ruleta.casino.Casino;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public final class TragamonedasCliente {
   private TragamonedasCliente() {
   }

   public static void registrar() {
      BlockEntityRendererRegistry.register(Casino.BE, TragamonedasRenderer::new);
   }
}
