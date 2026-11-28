package com.novastudio.dedsafio4;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.resources.Identifier;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

@Environment(EnvType.CLIENT)
public final class CreeperVariantsClient {
   private CreeperVariantsClient() {
   }

   public static void register() {
      CreeperVariantsContent.TYPES
         .forEach(
            (id, type) -> EntityRendererRegistry.register(
               type, ctx -> new GeoEntityRenderer(ctx, new DefaultedEntityGeoModel(Identifier.fromNamespaceAndPath("nexo", id)))
            )
         );
   }
}
