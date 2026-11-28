package com.example.dedsafioimitationmod.client;

import com.example.dedsafioimitationmod.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class DedsafioImitationModClient implements ClientModInitializer {
   public void onInitializeClient() {
      EntityRendererRegistry.register(ModEntities.NAUTILUS_OSEO_G, NautilusOseoGRenderer::new);
      EntityRendererRegistry.register(ModEntities.CEREBRO_AMARILLO, CerebroAmarilloRenderer::new);
      EntityRendererRegistry.register(ModEntities.NAUTILUS_OSEO_P, NautilusOseoPRenderer::new);
      EntityRendererRegistry.register(ModEntities.CEREBRO_ROJO, CerebroRojoRenderer::new);
      // La animación de tensar los arcos ahora va en assets/dedsafioimitationmod/items/*.json
   }
}
