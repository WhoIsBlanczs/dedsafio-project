package net.owleafstudio.modminds.entity.model;

import java.util.Set;
import net.minecraft.resources.Identifier;
import net.owleafstudio.modminds.entity.ReviilEntity;
import com.geckolib.model.GeoModel;

public class ReviilModel extends GeoModel<ReviilEntity> {
   private static final Set<String> PAINTED = Set.of("reviil_boss", "revilprime");

   public Identifier getAnimationResource(ReviilEntity entity) {
      return Identifier.fromNamespaceAndPath("modminds", "reviil");
   }

   public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return Identifier.fromNamespaceAndPath("modminds", "reviil");
   }

   private static final com.geckolib.constant.dataticket.DataTicket<Identifier> TEXTURE$ = com.geckolib.constant.dataticket.DataTicket.create("modminds_reviilmodel_texture", Identifier.class);

   @Override
   public void addAdditionalStateData(ReviilEntity entidad$, Object relacionado$, com.geckolib.renderer.base.GeoRenderState estado$) {
      super.addAdditionalStateData(entidad$, relacionado$, estado$);
      estado$.addGeckolibData(TEXTURE$, this.texturaDe(entidad$));
   }

   public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado$) {
      return estado$.getGeckolibData(TEXTURE$);
   }

   private Identifier texturaDe(ReviilEntity entity) {
      String skin = entity.getTexture();
      if (entity.isFury() && PAINTED.contains(skin)) {
         skin = skin + "_furia";
      }

      return Identifier.fromNamespaceAndPath("modminds", "textures/entities/" + skin + ".png");
   }
}
