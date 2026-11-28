package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.TorretaBorgEntity;
import net.owleafstudio.modminds.entity.layer.TorretaBorgLayer;
import net.owleafstudio.modminds.entity.model.TorretaBorgModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class TorretaBorgRenderer extends GeoEntityRenderer<TorretaBorgEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public TorretaBorgRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new TorretaBorgModel());
      this.shadowRadius = 0.0F;
      this.withRenderLayer(new TorretaBorgLayer(this));
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   
}
