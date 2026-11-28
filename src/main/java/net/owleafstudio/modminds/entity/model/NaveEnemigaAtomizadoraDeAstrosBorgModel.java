package net.owleafstudio.modminds.entity.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.NaveEnemigaAtomizadoraDeAstrosBorgEntity;
import com.geckolib.model.GeoModel;

public class NaveEnemigaAtomizadoraDeAstrosBorgModel extends GeoModel<NaveEnemigaAtomizadoraDeAstrosBorgEntity> {
   private static final com.geckolib.constant.dataticket.DataTicket<Identifier> TEXTURE$ = com.geckolib.constant.dataticket.DataTicket.create("modminds_naveenemigaatomizadoradeastrosborgmodel_texture", Identifier.class);

   @Override
   public void addAdditionalStateData(NaveEnemigaAtomizadoraDeAstrosBorgEntity entidad$, Object relacionado$, com.geckolib.renderer.base.GeoRenderState estado$) {
      super.addAdditionalStateData(entidad$, relacionado$, estado$);
      { NaveEnemigaAtomizadoraDeAstrosBorgEntity entity = entidad$; estado$.addGeckolibData(TEXTURE$, Identifier.fromNamespaceAndPath("modminds", "textures/entities/" + entity.getTexture() + ".png")); }
   }

   public Identifier getAnimationResource(NaveEnemigaAtomizadoraDeAstrosBorgEntity entity) {
      return Identifier.fromNamespaceAndPath("modminds", "spaceship3");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "spaceship3");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return estado$.getGeckolibData(TEXTURE$);
   }
}
