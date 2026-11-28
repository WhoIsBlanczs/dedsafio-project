package net.owleafstudio.modminds.entity.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.BombaRojaEntity;
import com.geckolib.model.GeoModel;

public class BombaRojaModel extends GeoModel<BombaRojaEntity> {
   private static final com.geckolib.constant.dataticket.DataTicket<Identifier> TEXTURE$ = com.geckolib.constant.dataticket.DataTicket.create("modminds_bombarojamodel_texture", Identifier.class);

   @Override
   public void addAdditionalStateData(BombaRojaEntity entidad$, Object relacionado$, com.geckolib.renderer.base.GeoRenderState estado$) {
      super.addAdditionalStateData(entidad$, relacionado$, estado$);
      { BombaRojaEntity entity = entidad$; estado$.addGeckolibData(TEXTURE$, Identifier.fromNamespaceAndPath("modminds", "textures/entities/" + entity.getTexture() + ".png")); }
   }

   public Identifier getAnimationResource(BombaRojaEntity entity) {
      return Identifier.fromNamespaceAndPath("modminds", "bombanegraconalas");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "bombanegraconalas");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return estado$.getGeckolibData(TEXTURE$);
   }
}
