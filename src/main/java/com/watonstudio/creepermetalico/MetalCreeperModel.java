package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

@Environment(EnvType.CLIENT)
public class MetalCreeperModel extends GeoModel<MetalCreeperEntity> {
   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("creeper_metalico", "metal_creeper");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("creeper_metalico", "textures/entity/metal_creeper.png");
   }

   public Identifier getAnimationResource(MetalCreeperEntity animatable) {
      return Identifier.fromNamespaceAndPath("creeper_metalico", "metal_creeper");
   }
}
