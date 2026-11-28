package net.owleafstudio.modminds.item.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.item.CerbatanaDeGoblinItem;
import com.geckolib.model.GeoModel;

public class CerbatanaDeGoblinItemModel extends GeoModel<CerbatanaDeGoblinItem> {
   public Identifier getAnimationResource(CerbatanaDeGoblinItem animatable) {
      return Identifier.fromNamespaceAndPath("modminds", "cerbatanav2");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "cerbatanav2");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "textures/item/cerbatanav2texture.png");
   }
}
