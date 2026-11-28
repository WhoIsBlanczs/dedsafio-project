package net.owleafstudio.modminds.client.renderer;

import net.minecraft.resources.Identifier;
import net.minecraft.client.model.monster.piglin.PiglinModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.PiglinDelLimboEntity;

public class PiglinDelLimboRenderer extends MobRenderer<PiglinDelLimboEntity, PiglinModel<PiglinDelLimboEntity>> {
   public PiglinDelLimboRenderer(EntityRendererProvider.Context context) {
      super(context, new PiglinModel(context.bakeLayer(ModelLayers.PIGLIN)), 0.5F);
   }

   public Identifier getTextureLocation(PiglinDelLimboEntity entity) {
      return Identifier.parse("modminds:textures/entities/piglin_limbo.png");
   }
}
