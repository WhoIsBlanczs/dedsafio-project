package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@Environment(EnvType.CLIENT)
public class AldeanoPhoraModel extends GeoModel<AldeanoPhoraEntity> {
   public ResourceLocation getModelResource(AldeanoPhoraEntity entity) {
      return ResourceLocation.fromNamespaceAndPath("watonstudio", "geo/robot_monitor.geo.json");
   }

   public ResourceLocation getTextureResource(AldeanoPhoraEntity entity) {
      return ResourceLocation.fromNamespaceAndPath("watonstudio", "textures/entity/robot_monitor.png");
   }

   public ResourceLocation getAnimationResource(AldeanoPhoraEntity entity) {
      return ResourceLocation.fromNamespaceAndPath("watonstudio", "animations/robot_monitor.animation.json");
   }
}
