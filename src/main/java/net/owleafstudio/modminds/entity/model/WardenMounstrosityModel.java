package net.owleafstudio.modminds.entity.model;

import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.WardenMounstrosityEntity;
import com.geckolib.model.GeoModel;

public class WardenMounstrosityModel extends GeoModel<WardenMounstrosityEntity> {
   private static final com.geckolib.constant.dataticket.DataTicket<Identifier> TEXTURE$ = com.geckolib.constant.dataticket.DataTicket.create("modminds_wardenmounstrositymodel_texture", Identifier.class);

   @Override
   public void addAdditionalStateData(WardenMounstrosityEntity entidad$, Object relacionado$, com.geckolib.renderer.base.GeoRenderState estado$) {
      super.addAdditionalStateData(entidad$, relacionado$, estado$);
      { WardenMounstrosityEntity entity = entidad$; estado$.addGeckolibData(TEXTURE$, Identifier.fromNamespaceAndPath("modminds", "textures/entities/" + entity.getTexture() + ".png")); }
   }

   public Identifier getAnimationResource(WardenMounstrosityEntity entity) {
      return Identifier.fromNamespaceAndPath("modminds", "owleafentity.wardenmounstrosity");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "owleafentity.wardenmounstrosity");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return estado$.getGeckolibData(TEXTURE$);
   }
}
