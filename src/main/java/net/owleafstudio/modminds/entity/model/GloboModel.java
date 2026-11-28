package net.owleafstudio.modminds.entity.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.GloboEntity;
import com.geckolib.model.GeoModel;

public class GloboModel extends GeoModel<GloboEntity> {
   private static final com.geckolib.constant.dataticket.DataTicket<Identifier> TEXTURE$ = com.geckolib.constant.dataticket.DataTicket.create("modminds_globomodel_texture", Identifier.class);

   @Override
   public void addAdditionalStateData(GloboEntity entidad$, Object relacionado$, com.geckolib.renderer.base.GeoRenderState estado$) {
      super.addAdditionalStateData(entidad$, relacionado$, estado$);
      { GloboEntity entity = entidad$; estado$.addGeckolibData(TEXTURE$, Identifier.fromNamespaceAndPath("modminds", "textures/entities/" + entity.getTexture() + ".png")); }
   }

   public Identifier getAnimationResource(GloboEntity entity) {
      return Identifier.fromNamespaceAndPath("modminds", "globo");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "globo");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return estado$.getGeckolibData(TEXTURE$);
   }
}
