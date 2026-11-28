package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@Environment(EnvType.CLIENT)
public class CreeperNuclearModel extends GeoModel<CreeperNuclearEntity> {
   public ResourceLocation getModelResource(CreeperNuclearEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("creeper_metalico", "geo/creeper_nuclear.geo.json");
   }

   public ResourceLocation getTextureResource(CreeperNuclearEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("creeper_metalico", "textures/entity/creeper_nuclear.png");
   }

   public ResourceLocation getAnimationResource(CreeperNuclearEntity animatable) {
      return ResourceLocation.fromNamespaceAndPath("creeper_metalico", "animations/creeper_nuclear.animation.json");
   }
}
