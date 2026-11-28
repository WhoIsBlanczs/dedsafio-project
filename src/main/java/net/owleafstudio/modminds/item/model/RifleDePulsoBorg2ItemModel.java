package net.owleafstudio.modminds.item.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.item.RifleDePulsoBorg2Item;
import com.geckolib.model.GeoModel;

public class RifleDePulsoBorg2ItemModel extends GeoModel<RifleDePulsoBorg2Item> {
   public Identifier getAnimationResource(RifleDePulsoBorg2Item animatable) {
      return Identifier.fromNamespaceAndPath("modminds", "armaborgverde");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "armaborgverde");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "textures/item/armaborgverde.png");
   }
}
