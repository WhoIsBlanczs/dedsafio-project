package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.NereidaEntity;
import net.owleafstudio.modminds.entity.model.NereidaModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class NereidaRenderer extends GeoEntityRenderer<NereidaEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public NereidaRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new NereidaModel());
      this.withScale(0.7F);
      this.shadowRadius = 0.0F;
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   
}
