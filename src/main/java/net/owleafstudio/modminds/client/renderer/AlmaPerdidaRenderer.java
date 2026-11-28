package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.AlmaPerdidaEntity;
import net.owleafstudio.modminds.entity.layer.AlmaPerdidaLayer;
import net.owleafstudio.modminds.entity.model.AlmaPerdidaModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class AlmaPerdidaRenderer extends GeoEntityRenderer<AlmaPerdidaEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public AlmaPerdidaRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new AlmaPerdidaModel());
      this.shadowRadius = 0.0F;
      this.withRenderLayer(new AlmaPerdidaLayer(this));
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   
}
