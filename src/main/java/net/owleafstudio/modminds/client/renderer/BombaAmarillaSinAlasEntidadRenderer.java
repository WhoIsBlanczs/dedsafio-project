package net.owleafstudio.modminds.client.renderer;

import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.client.model.Modelunknown;
import net.owleafstudio.modminds.entity.BombaAmarillaSinAlasEntidadEntity;

public class BombaAmarillaSinAlasEntidadRenderer extends MobRenderer<BombaAmarillaSinAlasEntidadEntity, Modelunknown<BombaAmarillaSinAlasEntidadEntity>> {
   public BombaAmarillaSinAlasEntidadRenderer(EntityRendererProvider.Context context) {
      super(context, new Modelunknown(context.bakeLayer(Modelunknown.LAYER_LOCATION)), 0.0F);
   }

   public Identifier getTextureLocation(BombaAmarillaSinAlasEntidadEntity entity) {
      return Identifier.parse("modminds:textures/entities/bombaamarilla.png");
   }
}
