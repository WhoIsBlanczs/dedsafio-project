package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.HydraEntity;
import net.owleafstudio.modminds.entity.layer.HydraLayer;
import net.owleafstudio.modminds.entity.model.HydraModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class HydraRenderer extends GeoEntityRenderer<HydraEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public HydraRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new HydraModel());
      this.shadowRadius = 0.0F;
      this.withRenderLayer(new HydraLayer(this));
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   
}
