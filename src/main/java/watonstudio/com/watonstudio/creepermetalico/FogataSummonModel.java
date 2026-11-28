package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@Environment(EnvType.CLIENT)
public final class FogataSummonModel extends GeoModel<FogataSummonEntity> {
   public ResourceLocation getModelResource(FogataSummonEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("fogata", "geo/custom_oak_spruce_stack.geo.json");
   }

   public ResourceLocation getTextureResource(FogataSummonEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("fogata", "textures/entity/custom_oak_spruce_stack.png");
   }

   public ResourceLocation getAnimationResource(FogataSummonEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("fogata", "animations/fogata.animation.json");
   }
}
