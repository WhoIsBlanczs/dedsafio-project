package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

@Environment(EnvType.CLIENT)
public class CreeperNuclearModel extends GeoModel<CreeperNuclearEntity> {
   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("creeper_metalico", "creeper_nuclear");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("creeper_metalico", "textures/entity/creeper_nuclear.png");
   }

   public Identifier getAnimationResource(CreeperNuclearEntity animatable) {
      return Identifier.fromNamespaceAndPath("creeper_metalico", "creeper_nuclear");
   }
}
