package net.owleafstudio.modminds.item.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.item.PerforadorDelVacioItem;
import com.geckolib.model.GeoModel;

public class PerforadorDelVacioItemModel extends GeoModel<PerforadorDelVacioItem> {
   public Identifier getAnimationResource(PerforadorDelVacioItem animatable) {
      return Identifier.fromNamespaceAndPath("modminds", "perdoradordelvacio");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "perdoradordelvacio");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "textures/item/perforador_del_vacio.png");
   }
}
