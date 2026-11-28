package net.owleafstudio.modminds.entity.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.PuertaBorgAmarilloEntity;
import com.geckolib.model.GeoModel;

public class PuertaBorgAmarilloModel extends GeoModel<PuertaBorgAmarilloEntity> {
   private static final com.geckolib.constant.dataticket.DataTicket<Identifier> TEXTURE$ = com.geckolib.constant.dataticket.DataTicket.create("modminds_puertaborgamarillomodel_texture", Identifier.class);

   @Override
   public void addAdditionalStateData(PuertaBorgAmarilloEntity entidad$, Object relacionado$, com.geckolib.renderer.base.GeoRenderState estado$) {
      super.addAdditionalStateData(entidad$, relacionado$, estado$);
      { PuertaBorgAmarilloEntity entity = entidad$; estado$.addGeckolibData(TEXTURE$, Identifier.fromNamespaceAndPath("modminds", "textures/entities/" + entity.getTexture() + ".png")); }
   }

   public Identifier getAnimationResource(PuertaBorgAmarilloEntity entity) {
      return Identifier.fromNamespaceAndPath("modminds", "puertaborg");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "puertaborg");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return estado$.getGeckolibData(TEXTURE$);
   }
}
