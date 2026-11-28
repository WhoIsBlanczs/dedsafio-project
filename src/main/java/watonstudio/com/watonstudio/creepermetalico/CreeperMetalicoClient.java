package com.watonstudio.creepermetalico;

import com.watonstudio.creepermetalico.client.AldeanoPhoraScreen;
import com.watonstudio.creepermetalico.client.EclipseLighting;
import com.watonstudio.creepermetalico.net.OpenPhoraS2C;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;

@Environment(EnvType.CLIENT)
public class CreeperMetalicoClient implements ClientModInitializer {
   public void onInitializeClient() {
      LinternaHud.register();
      AldeanoPhoraRegistration.ALDEANO_PHORA_ITEM.renderProviderHolder.setValue(new GeoRenderProvider() {
         private AldeanoPhoraItemRenderer renderer;

         public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
            if (this.renderer == null) {
               this.renderer = new AldeanoPhoraItemRenderer();
            }

            return this.renderer;
         }
      });
      ClientTickEvents.END_CLIENT_TICK.register(EclipseLighting::tick);
      EntityRendererRegistry.register(CreeperMetalicoMod.METAL_CREEPER, MetalCreeperRenderer::new);
      EntityRendererRegistry.register(CreeperMetalicoMod.NUCLEAR_CREEPER, CreeperNuclearRenderer::new);
      EntityRendererRegistry.register(FloralCreeperRegistration.FLORAL_CREEPER, FloralCreeperRenderer::new);
      EntityRendererRegistry.register(NautilusOseoRegistration.NAUTILUS_OSEO, NautilusOseoRenderer::new);
      EntityRendererRegistry.register(AldeanoPhoraRegistration.ALDEANO_PHORA, AldeanoPhoraRenderer::new);
      EntityRendererRegistry.register(CreeperMetalicoMod.FOGATA_SUMMONER, FogataSummonRenderer::new);
      ClientPlayNetworking.registerGlobalReceiver(
         TotemEonPayload.ID, (payload, context) -> context.client().execute(() -> TotemEonClientState.mark(payload.entityId()))
      );
      ClientPlayNetworking.registerGlobalReceiver(
         OpenPhoraS2C.ID,
         (payload, context) -> context.client().execute(() -> context.client().setScreen(new AldeanoPhoraScreen(payload.entityId(), payload.offers())))
      );
   }
}
