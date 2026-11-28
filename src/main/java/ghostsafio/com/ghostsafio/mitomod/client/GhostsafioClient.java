package com.ghostsafio.mitomod.client;

import com.ghostsafio.mitomod.ModDino;
import com.ghostsafio.mitomod.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class GhostsafioClient implements ClientModInitializer {
   public void onInitializeClient() {
      ItemProperties.register(ModItems.ARCO_GLEBANOIDE, ResourceLocation.withDefaultNamespace("pull"), (stack, level, entity, seed) -> {
         if (entity == null) {
            return 0.0F;
         } else {
            return entity.getUseItem() != stack ? 0.0F : (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F;
         }
      });
      ItemProperties.register(
         ModItems.ARCO_GLEBANOIDE,
         ResourceLocation.withDefaultNamespace("pulling"),
         (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F
      );
      BlockRenderLayerMap.INSTANCE.putBlock(ModDino.CICA, RenderType.cutout());
      BlockRenderLayerMap.INSTANCE.putBlock(ModDino.HELECHO_ROCA, RenderType.cutout());
      SupernovaClient.register();
      EclipseClient.register();
      NaveClient.register();
      MobsClient.register();
   }
}
