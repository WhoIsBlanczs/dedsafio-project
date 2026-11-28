package net.ruleta.puerta.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.ruleta.puerta.PuertaBunker;

public final class PuertaBunkerCliente {
   private PuertaBunkerCliente() {
   }

   public static void registrar() {
      BlockEntityRenderers.register(PuertaBunker.BE.get(), PuertaBunkerRenderer::new);
   }
}
