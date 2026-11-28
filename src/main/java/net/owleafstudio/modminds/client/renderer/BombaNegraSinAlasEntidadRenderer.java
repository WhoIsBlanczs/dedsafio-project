package net.owleafstudio.modminds.client.renderer;

import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.client.model.Modelunknown;
import net.owleafstudio.modminds.entity.BombaNegraSinAlasEntidadEntity;

public class BombaNegraSinAlasEntidadRenderer extends MobRenderer<BombaNegraSinAlasEntidadEntity, Modelunknown<BombaNegraSinAlasEntidadEntity>> {
   public BombaNegraSinAlasEntidadRenderer(EntityRendererProvider.Context context) {
      super(context, new Modelunknown(context.bakeLayer(Modelunknown.LAYER_LOCATION)), 0.0F);
   }

   public Identifier getTextureLocation(BombaNegraSinAlasEntidadEntity entity) {
      return Identifier.parse("modminds:textures/entities/bombanegra.png");
   }
}
