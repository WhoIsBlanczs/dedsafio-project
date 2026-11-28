package net.owleafstudio.modminds.entity.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.NaveEnemigaPerforadoraDelVacioBorgEntity;
import com.geckolib.model.GeoModel;

public class NaveEnemigaPerforadoraDelVacioBorgModel extends GeoModel<NaveEnemigaPerforadoraDelVacioBorgEntity> {
   private static final com.geckolib.constant.dataticket.DataTicket<Identifier> TEXTURE$ = com.geckolib.constant.dataticket.DataTicket.create("modminds_naveenemigaperforadoradelvacioborgmodel_texture", Identifier.class);

   @Override
   public void addAdditionalStateData(NaveEnemigaPerforadoraDelVacioBorgEntity entidad$, Object relacionado$, com.geckolib.renderer.base.GeoRenderState estado$) {
      super.addAdditionalStateData(entidad$, relacionado$, estado$);
      { NaveEnemigaPerforadoraDelVacioBorgEntity entity = entidad$; estado$.addGeckolibData(TEXTURE$, Identifier.fromNamespaceAndPath("modminds", "textures/entities/" + entity.getTexture() + ".png")); }
   }

   public Identifier getAnimationResource(NaveEnemigaPerforadoraDelVacioBorgEntity entity) {
      return Identifier.fromNamespaceAndPath("modminds", "spaceship6");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "spaceship6");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return estado$.getGeckolibData(TEXTURE$);
   }
}
