package net.owleafstudio.modminds.entity.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.OrbEntity;
import com.geckolib.model.GeoModel;

public class OrbModel extends GeoModel<OrbEntity> {
   private static final com.geckolib.constant.dataticket.DataTicket<Identifier> TEXTURE$ = com.geckolib.constant.dataticket.DataTicket.create("modminds_orbmodel_texture", Identifier.class);

   @Override
   public void addAdditionalStateData(OrbEntity entidad$, Object relacionado$, com.geckolib.renderer.base.GeoRenderState estado$) {
      super.addAdditionalStateData(entidad$, relacionado$, estado$);
      { OrbEntity entity = entidad$; estado$.addGeckolibData(TEXTURE$, Identifier.fromNamespaceAndPath("modminds", "textures/entities/" + entity.getTexture() + ".png")); }
   }

   public Identifier getAnimationResource(OrbEntity entity) {
      return Identifier.fromNamespaceAndPath("modminds", "orb");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "orb");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return estado$.getGeckolibData(TEXTURE$);
   }
}
