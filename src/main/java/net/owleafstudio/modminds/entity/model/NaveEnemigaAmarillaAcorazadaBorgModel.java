package net.owleafstudio.modminds.entity.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.NaveEnemigaAmarillaAcorazadaBorgEntity;
import com.geckolib.model.GeoModel;

public class NaveEnemigaAmarillaAcorazadaBorgModel extends GeoModel<NaveEnemigaAmarillaAcorazadaBorgEntity> {
   private static final com.geckolib.constant.dataticket.DataTicket<Identifier> TEXTURE$ = com.geckolib.constant.dataticket.DataTicket.create("modminds_naveenemigaamarillaacorazadaborgmodel_texture", Identifier.class);

   @Override
   public void addAdditionalStateData(NaveEnemigaAmarillaAcorazadaBorgEntity entidad$, Object relacionado$, com.geckolib.renderer.base.GeoRenderState estado$) {
      super.addAdditionalStateData(entidad$, relacionado$, estado$);
      { NaveEnemigaAmarillaAcorazadaBorgEntity entity = entidad$; estado$.addGeckolibData(TEXTURE$, Identifier.fromNamespaceAndPath("modminds", "textures/entities/" + entity.getTexture() + ".png")); }
   }

   public Identifier getAnimationResource(NaveEnemigaAmarillaAcorazadaBorgEntity entity) {
      return Identifier.fromNamespaceAndPath("modminds", "spaceship5");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "spaceship5");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return estado$.getGeckolibData(TEXTURE$);
   }
}
