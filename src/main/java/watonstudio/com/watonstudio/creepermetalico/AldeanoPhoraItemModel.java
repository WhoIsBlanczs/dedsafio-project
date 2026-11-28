package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@Environment(EnvType.CLIENT)
public class AldeanoPhoraItemModel extends GeoModel<AldeanoPhoraItem> {
   public ResourceLocation getModelResource(AldeanoPhoraItem item) {
      return ResourceLocation.fromNamespaceAndPath("watonstudio", "geo/robot_monitor.geo.json");
   }

   public ResourceLocation getTextureResource(AldeanoPhoraItem item) {
      return ResourceLocation.fromNamespaceAndPath("watonstudio", "textures/entity/robot_monitor.png");
   }

   public ResourceLocation getAnimationResource(AldeanoPhoraItem item) {
      return ResourceLocation.fromNamespaceAndPath("watonstudio", "animations/robot_monitor.animation.json");
   }
}
