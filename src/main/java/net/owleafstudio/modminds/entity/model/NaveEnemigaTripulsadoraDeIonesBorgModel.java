package net.owleafstudio.modminds.entity.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.NaveEnemigaTripulsadoraDeIonesBorgEntity;
import com.geckolib.model.GeoModel;

public class NaveEnemigaTripulsadoraDeIonesBorgModel extends GeoModel<NaveEnemigaTripulsadoraDeIonesBorgEntity> {
   private static final com.geckolib.constant.dataticket.DataTicket<Identifier> TEXTURE$ = com.geckolib.constant.dataticket.DataTicket.create("modminds_naveenemigatripulsadoradeionesborgmodel_texture", Identifier.class);

   @Override
   public void addAdditionalStateData(NaveEnemigaTripulsadoraDeIonesBorgEntity entidad$, Object relacionado$, com.geckolib.renderer.base.GeoRenderState estado$) {
      super.addAdditionalStateData(entidad$, relacionado$, estado$);
      { NaveEnemigaTripulsadoraDeIonesBorgEntity entity = entidad$; estado$.addGeckolibData(TEXTURE$, Identifier.fromNamespaceAndPath("modminds", "textures/entities/" + entity.getTexture() + ".png")); }
   }

   public Identifier getAnimationResource(NaveEnemigaTripulsadoraDeIonesBorgEntity entity) {
      return Identifier.fromNamespaceAndPath("modminds", "spaceship8");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "spaceship8");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return estado$.getGeckolibData(TEXTURE$);
   }
}
