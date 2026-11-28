package net.owleafstudio.modminds.entity.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.PuertaBorgRosaEntity;
import com.geckolib.model.GeoModel;

public class PuertaBorgRosaModel extends GeoModel<PuertaBorgRosaEntity> {
   private static final com.geckolib.constant.dataticket.DataTicket<Identifier> TEXTURE$ = com.geckolib.constant.dataticket.DataTicket.create("modminds_puertaborgrosamodel_texture", Identifier.class);

   @Override
   public void addAdditionalStateData(PuertaBorgRosaEntity entidad$, Object relacionado$, com.geckolib.renderer.base.GeoRenderState estado$) {
      super.addAdditionalStateData(entidad$, relacionado$, estado$);
      { PuertaBorgRosaEntity entity = entidad$; estado$.addGeckolibData(TEXTURE$, Identifier.fromNamespaceAndPath("modminds", "textures/entities/" + entity.getTexture() + ".png")); }
   }

   public Identifier getAnimationResource(PuertaBorgRosaEntity entity) {
      return Identifier.fromNamespaceAndPath("modminds", "puertaborg");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "puertaborg");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return estado$.getGeckolibData(TEXTURE$);
   }
}
