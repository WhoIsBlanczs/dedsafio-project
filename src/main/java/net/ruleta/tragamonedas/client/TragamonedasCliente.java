package net.ruleta.tragamonedas.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.ruleta.tragamonedas.Tragamonedas;

public final class TragamonedasCliente {
   private TragamonedasCliente() {
   }

   public static void registrar() {
      BlockEntityRenderers.register(Tragamonedas.BE.get(), TragamonedasRenderer::new);
   }
}
