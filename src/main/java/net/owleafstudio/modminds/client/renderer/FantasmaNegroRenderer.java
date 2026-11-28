package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.FantasmaNegroEntity;
import net.owleafstudio.modminds.entity.layer.FantasmaNegroLayer;
import net.owleafstudio.modminds.entity.model.FantasmaNegroModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class FantasmaNegroRenderer extends GeoEntityRenderer<FantasmaNegroEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public FantasmaNegroRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new FantasmaNegroModel());
      this.shadowRadius = 0.6F;
      this.withRenderLayer(new FantasmaNegroLayer(this));
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   
}
