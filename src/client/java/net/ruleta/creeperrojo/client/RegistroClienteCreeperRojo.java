package net.ruleta.creeperrojo.client;

import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.ruleta.creeperrojo.CreeperRojo;

public final class RegistroClienteCreeperRojo {
   private RegistroClienteCreeperRojo() {
   }

   public static void registrar() {
      ModelLayerRegistry.registerModelLayer(CreeperRojoModel.CAPA, CreeperRojoModel::crearCapa);
      EntityRendererRegistry.register(CreeperRojo.TIPO.get(), contexto -> new CreeperRojoRenderer<>(contexto));
   }
}
