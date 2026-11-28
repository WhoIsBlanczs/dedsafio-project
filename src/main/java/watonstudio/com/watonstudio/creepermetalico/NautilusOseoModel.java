package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@Environment(EnvType.CLIENT)
public class NautilusOseoModel extends GeoModel<NautilusOseoEntity> {
   public ResourceLocation getModelResource(NautilusOseoEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("watonstudio", "geo/nautilus_oseo.geo.json");
   }

   public ResourceLocation getTextureResource(NautilusOseoEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("watonstudio", "textures/entity/nautilus_oseo.png");
   }

   public ResourceLocation getAnimationResource(NautilusOseoEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("watonstudio", "animations/nautilus_oseo.animation.json");
   }
}
