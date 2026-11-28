package net.owleafstudio.modminds.entity.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.Taxi23DeCuatroAsientosEntity;
import com.geckolib.model.GeoModel;

public class Taxi23DeCuatroAsientosModel extends GeoModel<Taxi23DeCuatroAsientosEntity> {
   private static final com.geckolib.constant.dataticket.DataTicket<Identifier> TEXTURE$ = com.geckolib.constant.dataticket.DataTicket.create("modminds_taxi23decuatroasientosmodel_texture", Identifier.class);

   @Override
   public void addAdditionalStateData(Taxi23DeCuatroAsientosEntity entidad$, Object relacionado$, com.geckolib.renderer.base.GeoRenderState estado$) {
      super.addAdditionalStateData(entidad$, relacionado$, estado$);
      { Taxi23DeCuatroAsientosEntity entity = entidad$; estado$.addGeckolibData(TEXTURE$, Identifier.fromNamespaceAndPath("modminds", "textures/entities/" + entity.getTexture() + ".png")); }
   }

   public Identifier getAnimationResource(Taxi23DeCuatroAsientosEntity entity) {
      return Identifier.fromNamespaceAndPath("modminds", "taxi4x4");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "taxi4x4");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return estado$.getGeckolibData(TEXTURE$);
   }
}
