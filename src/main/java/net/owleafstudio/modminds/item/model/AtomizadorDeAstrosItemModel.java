package net.owleafstudio.modminds.item.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.item.AtomizadorDeAstrosItem;
import com.geckolib.model.GeoModel;

public class AtomizadorDeAstrosItemModel extends GeoModel<AtomizadorDeAstrosItem> {
   public Identifier getAnimationResource(AtomizadorDeAstrosItem animatable) {
      return Identifier.fromNamespaceAndPath("modminds", "atomizador_de_astros");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "atomizador_de_astros");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "textures/item/atomizador_de_astros.png");
   }
}
