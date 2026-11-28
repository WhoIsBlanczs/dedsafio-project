package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

@Environment(EnvType.CLIENT)
public class NautilusOseoModel extends GeoModel<NautilusOseoEntity> {
   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("watonstudio", "nautilus_oseo");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("watonstudio", "textures/entity/nautilus_oseo.png");
   }

   public Identifier getAnimationResource(NautilusOseoEntity animatable) {
      return Identifier.fromNamespaceAndPath("watonstudio", "nautilus_oseo");
   }
}
