package com.novastudio.dedsafio4;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

@Environment(EnvType.CLIENT)
public final class GhostsClientMod implements ClientModInitializer {
   public void onInitializeClient() {
      GhostsMod.GHOST_TYPES.forEach((color, type) -> EntityRendererRegistry.register(type, ctx -> ghostRenderer(ctx, color)));
      EntityRendererRegistry.register(GhostsMod.REAPER, ctx -> translucent(ctx, "grim_reaper"));
      EntityRendererRegistry.register(MiniReaper.TYPE, ctx -> translucent(ctx, "gream_reaper_pequena"));
   }

   private static GeoEntityRenderer<ReaperEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> translucent(EntityRendererProvider.Context ctx, String id) {
      return new GeoEntityRenderer<ReaperEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState>(ctx, new DefaultedEntityGeoModel<>(Identifier.fromNamespaceAndPath("nexo", id))) {
         public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState state, Identifier texture) {
            return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
         }
      };
   }

   private static GeoEntityRenderer<FantasmaEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> ghostRenderer(EntityRendererProvider.Context ctx, String color) {
      final Identifier texture = Identifier.fromNamespaceAndPath("nexo", "textures/entity/fantasma_" + color + ".png");
      DefaultedEntityGeoModel<FantasmaEntity> model = new DefaultedEntityGeoModel<FantasmaEntity>(Identifier.fromNamespaceAndPath("nexo", "fantasma")) {
         public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState state) {
            return texture;
         }
      };
      return new GeoEntityRenderer<FantasmaEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState>(ctx, model) {
         {
            this.shadowRadius = 0.4F;
         }

         public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState state, Identifier tex) {
            return net.minecraft.client.renderer.rendertype.RenderTypes.eyes(tex);
         }
      };
   }
}
