package net.owleafstudio.modminds.entity.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.NaveEnemigaAcorazadaRojaBorgEntity;
import com.geckolib.model.GeoModel;

public class NaveEnemigaAcorazadaRojaBorgModel extends GeoModel<NaveEnemigaAcorazadaRojaBorgEntity> {
   private static final com.geckolib.constant.dataticket.DataTicket<Identifier> TEXTURE$ = com.geckolib.constant.dataticket.DataTicket.create("modminds_naveenemigaacorazadarojaborgmodel_texture", Identifier.class);

   @Override
   public void addAdditionalStateData(NaveEnemigaAcorazadaRojaBorgEntity entidad$, Object relacionado$, com.geckolib.renderer.base.GeoRenderState estado$) {
      super.addAdditionalStateData(entidad$, relacionado$, estado$);
      { NaveEnemigaAcorazadaRojaBorgEntity entity = entidad$; estado$.addGeckolibData(TEXTURE$, Identifier.fromNamespaceAndPath("modminds", "textures/entities/" + entity.getTexture() + ".png")); }
   }

   public Identifier getAnimationResource(NaveEnemigaAcorazadaRojaBorgEntity entity) {
      return Identifier.fromNamespaceAndPath("modminds", "spaceship7");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "spaceship7");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return estado$.getGeckolibData(TEXTURE$);
   }
}
