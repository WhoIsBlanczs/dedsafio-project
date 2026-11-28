package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@Environment(EnvType.CLIENT)
public class MetalCreeperModel extends GeoModel<MetalCreeperEntity> {
   public ResourceLocation getModelResource(MetalCreeperEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("creeper_metalico", "geo/metal_creeper.geo.json");
   }

   public ResourceLocation getTextureResource(MetalCreeperEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("creeper_metalico", "textures/entity/metal_creeper.png");
   }

   public ResourceLocation getAnimationResource(MetalCreeperEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("creeper_metalico", "animations/metal_creeper.animation.json");
   }
}
