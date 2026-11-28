package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.GloboEntity;
import net.owleafstudio.modminds.entity.model.GloboModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class GloboRenderer extends GeoEntityRenderer<GloboEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public GloboRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new GloboModel());
      this.shadowRadius = 0.0F;
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   
}
