package com.novastudio.dedsafio4;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.resources.Identifier;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

@Environment(EnvType.CLIENT)
public final class MemeClientMod implements ClientModInitializer {
   public void onInitializeClient() {
      EntityRendererRegistry.register(
         MemeMod.MEME_CREEPER, ctx -> new GeoEntityRenderer(ctx, new DefaultedEntityGeoModel(Identifier.fromNamespaceAndPath("nexo", "meme_creeper"))).withScale(0.5F)
      );
      MemeTrapClient.register();
   }
}
