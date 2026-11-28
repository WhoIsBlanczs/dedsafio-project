package com.watonstudio.creepermetalico;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import com.geckolib.model.GeoModel;

@Environment(EnvType.CLIENT)
public class AldeanoPhoraModel extends GeoModel<AldeanoPhoraEntity> {
   private static final com.geckolib.constant.dataticket.DataTicket<Identifier> TEXTURE$ = com.geckolib.constant.dataticket.DataTicket.create("watonstudio_aldeanophoramodel_texture", Identifier.class);

   @Override
   public void addAdditionalStateData(AldeanoPhoraEntity entidad$, Object relacionado$, com.geckolib.renderer.base.GeoRenderState estado$) {
      super.addAdditionalStateData(entidad$, relacionado$, estado$);
      { AldeanoPhoraEntity entity = entidad$; estado$.addGeckolibData(TEXTURE$, Identifier.fromNamespaceAndPath("watonstudio", "textures/entity/robot_monitor.png")); }
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("watonstudio", "robot_monitor");
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return estado$.getGeckolibData(TEXTURE$);
   }

   public Identifier getAnimationResource(AldeanoPhoraEntity entity) {
      return Identifier.fromNamespaceAndPath("watonstudio", "robot_monitor");
   }
}
