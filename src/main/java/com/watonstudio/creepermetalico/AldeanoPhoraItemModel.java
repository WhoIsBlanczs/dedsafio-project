package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

@Environment(EnvType.CLIENT)
public class AldeanoPhoraItemModel extends GeoModel<AldeanoPhoraItem> {
   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("watonstudio", "robot_monitor");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("watonstudio", "textures/entity/robot_monitor.png");
   }

   public Identifier getAnimationResource(AldeanoPhoraItem item) {
      return Identifier.fromNamespaceAndPath("watonstudio", "robot_monitor");
   }
}
