package net.owleafstudio.modminds.entity.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.BombaNegraSinAlasEntity;
import com.geckolib.model.GeoModel;

public class BombaNegraSinAlasModel extends GeoModel<BombaNegraSinAlasEntity> {
   private static final com.geckolib.constant.dataticket.DataTicket<Identifier> TEXTURE$ = com.geckolib.constant.dataticket.DataTicket.create("modminds_bombanegrasinalasmodel_texture", Identifier.class);

   @Override
   public void addAdditionalStateData(BombaNegraSinAlasEntity entidad$, Object relacionado$, com.geckolib.renderer.base.GeoRenderState estado$) {
      super.addAdditionalStateData(entidad$, relacionado$, estado$);
      { BombaNegraSinAlasEntity entity = entidad$; estado$.addGeckolibData(TEXTURE$, Identifier.fromNamespaceAndPath("modminds", "textures/entities/" + entity.getTexture() + ".png")); }
   }

   public Identifier getAnimationResource(BombaNegraSinAlasEntity entity) {
      return Identifier.fromNamespaceAndPath("modminds", "bombanegrasinalas");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "bombanegrasinalas");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return estado$.getGeckolibData(TEXTURE$);
   }
}
