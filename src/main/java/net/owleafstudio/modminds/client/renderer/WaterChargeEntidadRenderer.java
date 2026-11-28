package net.owleafstudio.modminds.client.renderer;

import net.minecraft.resources.Identifier;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.silverfish.SilverfishModel;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.WaterChargeEntidadEntity;

public class WaterChargeEntidadRenderer extends MobRenderer<WaterChargeEntidadEntity, SilverfishModel<WaterChargeEntidadEntity>> {
   public WaterChargeEntidadRenderer(EntityRendererProvider.Context context) {
      super(context, new SilverfishModel(context.bakeLayer(ModelLayers.SILVERFISH)), 0.0F);
   }

   public Identifier getTextureLocation(WaterChargeEntidadEntity entity) {
      return Identifier.parse("modminds:textures/entities/hydrabrillante.png");
   }
}
