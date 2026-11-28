package net.owleafstudio.modminds.client.renderer;

import net.minecraft.resources.Identifier;
import net.minecraft.client.model.monster.piglin.PiglinModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.PiglinDelLimboEntity;

public class PiglinDelLimboRenderer extends MobRenderer<PiglinDelLimboEntity, net.minecraft.client.renderer.entity.state.PiglinRenderState, PiglinModel> {
   public PiglinDelLimboRenderer(EntityRendererProvider.Context context) {
      super(context, new net.minecraft.client.model.monster.piglin.AdultPiglinModel(context.bakeLayer(ModelLayers.PIGLIN)), 0.5F);
   }

   @Override
   public net.minecraft.client.renderer.entity.state.PiglinRenderState createRenderState() {
      return new net.minecraft.client.renderer.entity.state.PiglinRenderState();
   }

   public Identifier getTextureLocation(net.minecraft.client.renderer.entity.state.PiglinRenderState estado$) {
      return Identifier.parse("modminds:textures/entities/piglin_limbo.png");
   }
}
