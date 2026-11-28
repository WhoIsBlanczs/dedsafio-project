package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

@Environment(EnvType.CLIENT)
public final class FogataSummonModel extends GeoModel<FogataSummonEntity> {
   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("fogata", "custom_oak_spruce_stack");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("fogata", "textures/entity/custom_oak_spruce_stack.png");
   }

   public Identifier getAnimationResource(FogataSummonEntity animatable) {
      return Identifier.fromNamespaceAndPath("fogata", "fogata");
   }
}
