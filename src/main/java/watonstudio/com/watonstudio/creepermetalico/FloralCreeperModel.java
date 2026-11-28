package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@Environment(EnvType.CLIENT)
public class FloralCreeperModel extends GeoModel<FloralCreeperEntity> {
   public ResourceLocation getModelResource(FloralCreeperEntity entity) {
      return ResourceLocation.fromNamespaceAndPath("creeper_metalico", "geo/floral.geo.json");
   }

   public ResourceLocation getTextureResource(FloralCreeperEntity entity) {
      return ResourceLocation.fromNamespaceAndPath("creeper_metalico", "textures/entity/floral.png");
   }

   public ResourceLocation getAnimationResource(FloralCreeperEntity entity) {
      return ResourceLocation.fromNamespaceAndPath("creeper_metalico", "animations/floral.animation.json");
   }
}
