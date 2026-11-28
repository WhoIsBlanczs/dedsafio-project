package net.owleafstudio.modminds.entity.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.NaveEnemigaCortaBorgEntity;
import com.geckolib.model.GeoModel;

public class NaveEnemigaCortaBorgModel extends GeoModel<NaveEnemigaCortaBorgEntity> {
   private static final com.geckolib.constant.dataticket.DataTicket<Identifier> TEXTURE$ = com.geckolib.constant.dataticket.DataTicket.create("modminds_naveenemigacortaborgmodel_texture", Identifier.class);

   @Override
   public void addAdditionalStateData(NaveEnemigaCortaBorgEntity entidad$, Object relacionado$, com.geckolib.renderer.base.GeoRenderState estado$) {
      super.addAdditionalStateData(entidad$, relacionado$, estado$);
      { NaveEnemigaCortaBorgEntity entity = entidad$; estado$.addGeckolibData(TEXTURE$, Identifier.fromNamespaceAndPath("modminds", "textures/entities/" + entity.getTexture() + ".png")); }
   }

   public Identifier getAnimationResource(NaveEnemigaCortaBorgEntity entity) {
      return Identifier.fromNamespaceAndPath("modminds", "spaceship4");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "spaceship4");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return estado$.getGeckolibData(TEXTURE$);
   }
}
