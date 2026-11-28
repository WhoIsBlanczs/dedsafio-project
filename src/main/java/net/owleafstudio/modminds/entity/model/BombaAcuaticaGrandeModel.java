package net.owleafstudio.modminds.entity.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.BombaAcuaticaGrandeEntity;
import com.geckolib.model.GeoModel;

public class BombaAcuaticaGrandeModel extends GeoModel<BombaAcuaticaGrandeEntity> {
   private static final com.geckolib.constant.dataticket.DataTicket<Identifier> TEXTURE$ = com.geckolib.constant.dataticket.DataTicket.create("modminds_bombaacuaticagrandemodel_texture", Identifier.class);

   @Override
   public void addAdditionalStateData(BombaAcuaticaGrandeEntity entidad$, Object relacionado$, com.geckolib.renderer.base.GeoRenderState estado$) {
      super.addAdditionalStateData(entidad$, relacionado$, estado$);
      { BombaAcuaticaGrandeEntity entity = entidad$; estado$.addGeckolibData(TEXTURE$, Identifier.fromNamespaceAndPath("modminds", "textures/entities/" + entity.getTexture() + ".png")); }
   }

   public Identifier getAnimationResource(BombaAcuaticaGrandeEntity entity) {
      return Identifier.fromNamespaceAndPath("modminds", "bomba_acuatica_grande");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "bomba_acuatica_grande");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return estado$.getGeckolibData(TEXTURE$);
   }
}
