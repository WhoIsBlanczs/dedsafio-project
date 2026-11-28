package net.owleafstudio.modminds.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.owleafstudio.modminds.entity.LimboTitanEntity;
import net.owleafstudio.modminds.entity.layer.LimboTitanLayer;
import net.owleafstudio.modminds.entity.model.LimboTitanModel;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.renderer.GeoEntityRenderer;

public class LimboTitanRenderer extends GeoEntityRenderer<LimboTitanEntity, net.minecraft.client.renderer.entity.state.LivingEntityRenderState> {
   public LimboTitanRenderer(EntityRendererProvider.Context renderManager) {
      super(renderManager, new LimboTitanModel());
      this.withScale(1.5F);
      this.shadowRadius = 0.0F;
      this.withRenderLayer(new LimboTitanLayer(this));
   }

   public RenderType getRenderType(net.minecraft.client.renderer.entity.state.LivingEntityRenderState estado$, Identifier texture) {
      return net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucent(texture);
   }

   
}
