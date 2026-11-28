package net.ruleta.herobrine.client;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.ruleta.herobrine.Herobrine;

public final class RegistroClienteHerobrine {
   private RegistroClienteHerobrine() {
   }

   public static void registrar() {
      EntityRendererRegistry.register(Herobrine.HEROBRINE.get(), HerobrineRenderer::new);
   }
}
