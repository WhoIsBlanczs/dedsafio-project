package net.owleafstudio.modminds.item.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.item.ArmaCortaEspacialItem;
import com.geckolib.model.GeoModel;

public class ArmaCortaEspacialItemModel extends GeoModel<ArmaCortaEspacialItem> {
   public Identifier getAnimationResource(ArmaCortaEspacialItem animatable) {
      return Identifier.fromNamespaceAndPath("modminds", "armaborgcorta");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "armaborgcorta");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "textures/item/arma_coreta_borg.png");
   }
}
