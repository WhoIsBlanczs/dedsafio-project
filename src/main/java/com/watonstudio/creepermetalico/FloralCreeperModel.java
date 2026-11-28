package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

@Environment(EnvType.CLIENT)
public class FloralCreeperModel extends GeoModel<FloralCreeperEntity> {
   private static final com.geckolib.constant.dataticket.DataTicket<Identifier> TEXTURE$ = com.geckolib.constant.dataticket.DataTicket.create("watonstudio_floralcreepermodel_texture", Identifier.class);

   @Override
   public void addAdditionalStateData(FloralCreeperEntity entidad$, Object relacionado$, com.geckolib.renderer.base.GeoRenderState estado$) {
      super.addAdditionalStateData(entidad$, relacionado$, estado$);
      { FloralCreeperEntity entity = entidad$; estado$.addGeckolibData(TEXTURE$, Identifier.fromNamespaceAndPath("creeper_metalico", "textures/entity/floral.png")); }
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("creeper_metalico", "floral");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return estado$.getGeckolibData(TEXTURE$);
   }

   public Identifier getAnimationResource(FloralCreeperEntity entity) {
      return Identifier.fromNamespaceAndPath("creeper_metalico", "floral");
   }
}
