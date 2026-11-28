package net.ruleta.nuclear.client;

import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.ruleta.nuclear.NuclearCreeperMod;

public class ClientEvents {
   public static void registrar() {
      ModelLayerRegistry.registerModelLayer(NuclearCreeperModel.LAYER_LOCATION, NuclearCreeperModel::createBodyLayer);
      EntityRendererRegistry.register(NuclearCreeperMod.NUCLEAR_CREEPER.get(), NuclearCreeperRenderer::new);
   }
}
