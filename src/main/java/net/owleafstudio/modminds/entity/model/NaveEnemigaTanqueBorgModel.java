package net.owleafstudio.modminds.entity.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.NaveEnemigaTanqueBorgEntity;
import com.geckolib.model.GeoModel;

public class NaveEnemigaTanqueBorgModel extends GeoModel<NaveEnemigaTanqueBorgEntity> {
   private static final com.geckolib.constant.dataticket.DataTicket<Identifier> TEXTURE$ = com.geckolib.constant.dataticket.DataTicket.create("modminds_naveenemigatanqueborgmodel_texture", Identifier.class);

   @Override
   public void addAdditionalStateData(NaveEnemigaTanqueBorgEntity entidad$, Object relacionado$, com.geckolib.renderer.base.GeoRenderState estado$) {
      super.addAdditionalStateData(entidad$, relacionado$, estado$);
      { NaveEnemigaTanqueBorgEntity entity = entidad$; estado$.addGeckolibData(TEXTURE$, Identifier.fromNamespaceAndPath("modminds", "textures/entities/" + entity.getTexture() + ".png")); }
   }

   public Identifier getAnimationResource(NaveEnemigaTanqueBorgEntity entity) {
      return Identifier.fromNamespaceAndPath("modminds", "spaceship1");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "spaceship1");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return estado$.getGeckolibData(TEXTURE$);
   }
}
