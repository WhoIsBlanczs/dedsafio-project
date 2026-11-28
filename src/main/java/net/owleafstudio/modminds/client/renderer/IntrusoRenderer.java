package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.IntrusoEntity;
import net.owleafstudio.modminds.entity.layer.IntrusoLayer;
import net.owleafstudio.modminds.entity.model.IntrusoModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class IntrusoRenderer extends GeoEntityRenderer<IntrusoEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public IntrusoRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new IntrusoModel());
      this.shadowRadius = 0.0F;
      this.withRenderLayer(new IntrusoLayer(this));
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   
}
